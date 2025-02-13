# app/core/ocr_processor.py
from datetime import datetime
import calendar
from typing import List, Dict
import statistics
import logging

logger = logging.getLogger(__name__)

class OCRProcessor:
   NAME_COLUMN_THRESHOLD = 50
   
   def __init__(self):
       # 현재 연월 정보와 해당 월 일수 초기화
       now = datetime.now()
       self.current_year = now.year
       self.current_month = now.month
       self.expected_days = calendar.monthrange(self.current_year, self.current_month)[1]
   
   @staticmethod
   def fix_text(text: str) -> str:
       # OCR로 인식된 근무 유형 텍스트 보정 (OFF -> OF)
       if text in ["D", "N", "E"]:
           return text
       return "OF"

   def analyze_and_compensate_gaps(self, ocr_result):
       """
       <후처리로직> OCR 결과에서 텍스트 간 간격을 분석해 누락된 셀 보완하는 로직
       """
       Y_TOLERANCE = 5
       GAP_THRESHOLD = 5
       MIN_GAP_COUNT = 3
       MISSING_CELL_OFFSET = 1.0
       MISSING_CELL_BASE = 1

       def determine_replacement_text(prev_text, next_text):
           shift_patterns = ['D', 'E', 'N', 'OF']
           if prev_text == next_text and prev_text in shift_patterns:
               return self.fix_text(prev_text)
           return "OF"
       
       def group_by_row(fields):
           if not fields:
               return {}
           rows = {}
           for field in fields:
               vertices = field.get("boundingPoly", {}).get("vertices", [])
               if not vertices:
                   continue
               try:
                   center_y = sum(v.get("y", 0) for v in vertices) / len(vertices)
               except ZeroDivisionError:
                   continue
               matched_key = None
               for key in rows.keys():
                   if abs(key - center_y) <= Y_TOLERANCE:
                       matched_key = key
                       break
               if matched_key is None:
                   matched_key = center_y
                   rows[matched_key] = []
               try:
                   left_x = min(v.get("x", 0) for v in vertices)
                   right_x = max(v.get("x", 0) for v in vertices)
                   width = right_x - left_x
               except ValueError:
                   continue
               raw_text = field.get("inferText", "").strip()
               if left_x < self.NAME_COLUMN_THRESHOLD:
                   text_val = raw_text
               else:
                   text_val = self.fix_text(raw_text)
               rows[matched_key].append({
                   'text': text_val,
                   'x': left_x,
                   'left_x': left_x,
                   'right_x': right_x,
                   'width': width,
                   'center_y': center_y,
                   'vertices': vertices,
                   'y': center_y
               })
           return rows
       
       def analyze_row_gaps(row_texts):
           if not row_texts:
               return None, []
           sorted_texts = sorted(row_texts, key=lambda x: x['left_x'])
           gaps = []
           for i in range(len(sorted_texts) - 1):
               gap = sorted_texts[i + 1]['left_x'] - sorted_texts[i]['right_x']
               if gap > 0:
                   gaps.append(gap)
           if len(gaps) < MIN_GAP_COUNT:
               return None, sorted_texts
           try:
               normal_gap = statistics.median(gaps)
           except statistics.StatisticsError:
               return None, sorted_texts
           return normal_gap, sorted_texts

       compensated_results = []
       for image in ocr_result.get("images", []):
           fields = image.get("fields", [])
           if not fields:
               continue
           rows = group_by_row(fields)
           for row_y, row_texts in rows.items():
               normal_gap, sorted_texts = analyze_row_gaps(row_texts)
               if normal_gap is None:
                   continue
               compensated_row = []
               for i in range(len(sorted_texts)):
                   compensated_row.append(sorted_texts[i])
                   if i < len(sorted_texts) - 1:
                       gap = sorted_texts[i+1]['left_x'] - sorted_texts[i]['right_x']
                       if gap > normal_gap * GAP_THRESHOLD:
                           missing_cells = int(round((gap / normal_gap) * MISSING_CELL_OFFSET)) - MISSING_CELL_BASE
                           if missing_cells < 1:
                               missing_cells = 1
                           replacement_text = determine_replacement_text(sorted_texts[i]['text'], sorted_texts[i+1]['text'])
                           step = gap / (missing_cells + 1)
                           for k in range(1, missing_cells+1):
                               new_cell = {
                                   'text': replacement_text,
                                   'left_x': sorted_texts[i]['right_x'] + 2 + step * (k - 1),
                                   'right_x': sorted_texts[i]['right_x'] + 2 + step * k,
                                   'width': step,
                                   'center_y': row_y,
                                   'y': row_y,
                                   'is_compensated': True,
                                   'estimated_chars': 1,
                                   'replacement_text': replacement_text,
                                   'vertices': [
                                       {"x": sorted_texts[i]['right_x'] + 2 + step * (k - 1), "y": row_y - 10},
                                       {"x": sorted_texts[i]['right_x'] + 2 + step * k, "y": row_y - 10},
                                       {"x": sorted_texts[i]['right_x'] + 2 + step * k, "y": row_y + 10},
                                       {"x": sorted_texts[i]['right_x'] + 2 + step * (k - 1), "y": row_y + 10}
                                   ]
                               }
                               new_cell['x'] = new_cell['left_x']
                               compensated_row.append(new_cell)
               if compensated_row:
                   compensated_results.append({
                       'y_coord': row_y,
                       'texts': compensated_row
                   })
       
       return {
           'rows': compensated_results,
           'stats': {
               'total_rows': len(compensated_results),
               'compensated_count': sum(
                   sum(1 for text in row['texts'] if text.get('is_compensated', False))
                   for row in compensated_results
               )
           }
       }

   def process_ocr_result(self, ocr_result: dict) -> List[Dict]:
       """
       후처리 로직 2
       텍스트높이 분석 및 비정상 높이 처리, 날짜열 위치 식별, 간호사 이름 추출, 각 간호사별 스케줄구성 
       """
       if not ocr_result or "images" not in ocr_result:
           logger.error("OCR 결과가 유효하지 않습니다.")
           return []

       cells = []
       header_y = float('inf')
       
       # 모든 필드의 높이 수집
       all_heights = []
       for image in ocr_result["images"]:
           if "fields" in image:
               for field in image["fields"]:
                   vertices = field["boundingPoly"]["vertices"]
                   height = max(v["y"] for v in vertices) - min(v["y"] for v in vertices)
                   all_heights.append(height)
       
       if not all_heights:
           logger.error("처리할 높이 데이터가 없습니다.")
           return []
       
       # 전체의 중앙값 계산
       median_height = sorted(all_heights)[len(all_heights)//2]
       normal_candidates = [h for h in all_heights if h <= median_height * 1.5]
       if normal_candidates:
           normal_height = sum(normal_candidates) / len(normal_candidates)
       else:
           normal_height = median_height
       abnormal_threshold = normal_height * 2.1

       # OCR 텍스트 처리
       for image in ocr_result["images"]:
           if "fields" in image:
               for field in image["fields"]:
                   vertices = field["boundingPoly"]["vertices"]
                   left_x = min(v["x"] for v in vertices)
                   right_x = max(v["x"] for v in vertices)
                   min_y = min(v["y"] for v in vertices)
                   max_y = max(v["y"] for v in vertices)
                   height = max_y - min_y
                   width = right_x - left_x
                   raw_text = field["inferText"].strip()
                   
                   if left_x < self.NAME_COLUMN_THRESHOLD:
                       text = raw_text
                   else:
                       text = self.fix_text(raw_text)

                   if height > abnormal_threshold:
                       num_cells = max(1, int(height / normal_height))
                       cell_height = height / num_cells
                       for i in range(num_cells):
                           cell_info = {
                               'text': '',
                               'x': left_x,
                               'y': min_y + i * cell_height,
                               'height': cell_height,
                               'width': width,
                               'right_x': right_x
                           }
                           cells.append(cell_info)
                   else:
                       cell_info = {
                           'text': text,
                           'x': left_x,
                           'y': min_y,
                           'height': height,
                           'width': width,
                           'right_x': right_x
                       }
                       cells.append(cell_info)

                   if raw_text.isdigit() and len(raw_text) <= 2:
                       header_y = min(header_y, min_y)

       if not cells:
           logger.error("처리된 셀 데이터가 없습니다.")
           return []

       # 날짜 열 위치 식별
       date_row_tolerance = 9
       date_rows = {}
       for cell in cells:
           if (cell['text'].isdigit() and len(cell['text']) <= 2) or cell['text'] == '':
               added = False
               for row_y in list(date_rows.keys()):
                   if abs(cell['y'] - row_y) < date_row_tolerance:
                       date_rows[row_y].append(cell)
                       added = True
                       break
               if not added:
                   date_rows[cell['y']] = [cell]
       
       column_x_coords = []
       if date_rows:
           candidate_row_y = max(date_rows, key=lambda y: len(date_rows[y]))
           date_columns = sorted(date_rows[candidate_row_y], key=lambda c: c['x'])
           date_columns = [col for col in date_columns if col['x'] > 100]
           column_x_coords = [col['x'] for col in date_columns]

           if len(column_x_coords) < self.expected_days:
               schedule_area_right = max(cell['x'] for cell in cells)
               if len(column_x_coords) >= 2:
                   gaps = [column_x_coords[i+1] - column_x_coords[i] for i in range(len(column_x_coords)-1)]
                   avg_gap = statistics.median(gaps)
               else:
                   avg_gap = 20
               last_x = column_x_coords[-1]
               while len(column_x_coords) < self.expected_days:
                   last_x += avg_gap
                   column_x_coords.append(last_x)

       # 간호사 정보 추출
       stop_words = ["상근조", "야간", "전담조", "교대조", "교대", "전담"]
       row_tolerance = 15
       nurse_candidates = [cell for cell in cells if cell['y'] > (header_y + 40) and cell['x'] < self.NAME_COLUMN_THRESHOLD]
       
       nurse_groups = {}
       for cell in nurse_candidates:
           added = False
           for group_y in list(nurse_groups.keys()):
               if abs(cell['y'] - group_y) < row_tolerance:
                   nurse_groups[group_y].append(cell)
                   added = True
                   break
           if not added:
               nurse_groups[cell['y']] = [cell]

       nurse_cells = []
       for group in nurse_groups.values():
           group_sorted = sorted(group, key=lambda c: c['x'])
           filtered = [c for c in group_sorted if not any(stop in c['text'].strip() for stop in stop_words)]
           if filtered:
               selected = filtered[0]
           else:
               selected = group_sorted[0]
           nurse_cells.append(selected)

       # 스케줄 데이터 구성
       schedule_data = []
       
       for nurse_cell in nurse_cells:
           nurse_y = nurse_cell['y']
           row_cells = [cell for cell in cells if abs(cell['y'] - nurse_y) <= row_tolerance and cell['x'] > nurse_cell['x']]
           row_cells_sorted = sorted(row_cells, key=lambda c: c['x'])
           
           schedule = []
           for day in range(1, self.expected_days+1):
               if day - 1 < len(row_cells_sorted):
                   work_type = row_cells_sorted[day - 1]['text']
                   if work_type.strip() == "":
                       work_type = "OF"
               else:
                   work_type = "OF"
               schedule.append({"day": day, "type": work_type})
           
           schedule_data.append({
               "name": nurse_cell["text"],
               "schedule": schedule
           })

       return schedule_data
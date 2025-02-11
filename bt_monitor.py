# bt_monitor.py
import os
import re
import time
from db_utils import get_db_connection
from config import BUTTON_MAPPING

# 버튼 상태와 마지막 이벤트 시간을 저장 (디바운스용)
button_state = {}
last_event_time = {}
DEBOUNCE_INTERVAL = 0.5  # 초 단위

# 정규표현식으로 Handle과 Data 값을 추출
handle_re = re.compile(r'Handle:\s+(0x[0-9a-fA-F]+)')
data_re = re.compile(r'Data:\s+([0-9a-fA-F]+)')


def get_bed_name(mac_address):
    """DB에서 MAC 주소로 기기 이름을 조회"""
    with get_db_connection("devices") as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT bed_name FROM Bed WHERE mac_address = ?", (mac_address,))
        result = cursor.fetchone()
        return result[0] if result else None


def register_bed(mac_address):
    """새로운 리모컨을 DB에 등록"""
    bed_name = f"리모컨-{mac_address[-4:]}"
    with get_db_connection("devices") as conn:
        cursor = conn.cursor()
        cursor.execute(
            "INSERT OR IGNORE INTO Bed (mac_address, bed_name) VALUES (?, ?)",
            (mac_address, bed_name)
        )
        conn.commit()
        print(f"✅ 새 리모컨 등록됨: {mac_address} -> {bed_name}", flush=True)
    return bed_name


def monitor_btmon(alert_queue):
    """
    btmon을 사용하여 Bluetooth HCI 로그를 파싱합니다.
    - "Address:" 줄에서 MAC 주소를 업데이트합니다.
    - "Data:" 줄에서 정규표현식으로 Handle과 Data 값을 추출하고,
      Handle이 0x001f인 경우에만 BUTTON_MAPPING에 따라 이벤트를 처리합니다.
    - 디바운스 로직을 적용해 같은 버튼 이벤트가 짧은 시간 내에 중복 처리되지 않도록 합니다.

    또한, 디버그 목적으로 매핑 대상 데이터가 있는 라인만 출력합니다.
    """
    print("🔵 블루투스 리모컨 입력 감지 중...", flush=True)
    with os.popen("sudo btmon") as btmon_output:
        current_mac = None
        for line in btmon_output:
            line = line.strip()

            # MAC 주소 업데이트: "Address:"가 있는 줄
            if "Address:" in line:
                parts = line.split()
                current_mac = parts[-1]
                if not get_bed_name(current_mac):
                    register_bed(current_mac)

            # "Data:"가 포함된 줄에서 데이터 값을 추출하고, 매핑 대상이면 디버그 출력
            if "Data:" in line and current_mac:
                data_match = data_re.search(line)
                if not data_match:
                    continue
                data_value = data_match.group(1)

                # Handle 값 추출 및 필터링 (우리가 관심 있는 Handle은 0x001f)
                handle_match = handle_re.search(line)
                if handle_match:
                    handle_val = handle_match.group(1)
                    if handle_val.lower() != "0x001f":
                        continue
                else:
                    continue

                # 만약 이 data_value가 매핑 대상이라면 디버그 메시지 출력
                if data_value in BUTTON_MAPPING:
                    print(f"DEBUG (Mapped): {line}", flush=True)

                now = time.time()
                if data_value in BUTTON_MAPPING:
                    # 디바운스: 동일한 data_value가 짧은 시간 내에 반복되면 무시
                    if data_value in last_event_time and (now - last_event_time[data_value]) < DEBOUNCE_INTERVAL:
                        continue
                    last_event_time[data_value] = now

                    response_text = BUTTON_MAPPING[data_value]
                    bed_name = get_bed_name(current_mac)
                    if data_value not in button_state or not button_state[data_value]:
                        print(f"🎯 버튼 감지: {bed_name} - {response_text}", flush=True)
                        alert_queue.put((bed_name, response_text))
                        button_state[data_value] = True

                # 버튼 해제(놓임) 시 "000000" 데이터로 상태 초기화
                elif data_value == "000000":
                    button_state.clear()

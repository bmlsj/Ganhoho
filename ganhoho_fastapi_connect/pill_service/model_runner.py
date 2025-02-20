import cv2
import torch
import json
import os
import numpy as np
from rembg import remove
from PIL import Image
from ultralytics import YOLO
from google.cloud import vision
import difflib
from dotenv import load_dotenv
from io import BytesIO


# 환경 변수 로드 (.env 파일에서 설정값 불러오기)
load_dotenv()

# 현재 파일이 위치한 디렉토리를 기준으로 상대 경로 설정
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# 모델 및 JSON 파일 경로 설정
MODEL_PATH = os.path.join(BASE_DIR, "models", "best.pt")  # YOLO 모델 가중치 파일
JSON_PATH = os.path.join(BASE_DIR,"data", "pill_text_val.json")  # 알약 정보 JSON 파일

# Google Cloud Vision API 환경 변수 설정
GOOGLE_CREDENTIALS_PATH = os.getenv("GOOGLE_APPLICATION_CREDENTIALS", os.path.join(BASE_DIR, "googleOCR", "ganhoho.json"))
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = GOOGLE_CREDENTIALS_PATH
vision_client = vision.ImageAnnotatorClient()

# GPU 설정 (환경 변수에서 가져오도록 설정)
GPU_ID = os.getenv("CUDA_VISIBLE_DEVICES", "2")  # 2번 GPU 사용
os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"
os.environ["CUDA_VISIBLE_DEVICES"] = GPU_ID

# YOLO 모델 로드
model = YOLO(MODEL_PATH)

# JSON 파일 로드 (알약 정보)
with open(JSON_PATH, "r", encoding="utf-8") as f:
    pill_labels = json.load(f)


# OCR 텍스트 자동 수정 (오류 교정)
def correct_ocr_text(detected_text):
    """
    OCR 결과에서 공백 제거 후 JSON 데이터와 가장 유사한 값 반환
    """
    cleaned_text = detected_text.replace("\n", " ").strip()
    possible_texts = [pill_info["text"] for pill_info in pill_labels.values()]
    closest_match = difflib.get_close_matches(cleaned_text, possible_texts, n=1, cutoff=0.6)
    return closest_match[0] if closest_match else cleaned_text


# 배경 제거 및 밝기 조정
def remove_background_and_adjust(image_bytes):
    """
    rembg를 사용해 배경 제거 후, 이미지의 밝기 및 대비 조정
    """
    input_image = Image.open(BytesIO(image_bytes))
    output_image = remove(input_image) #rembg 배경 제거

    
    white_bg = Image.new("RGBA", output_image.size, (255, 255, 255, 255))
    white_bg.paste(output_image, mask=output_image.split()[3])

    img = np.array(white_bg)
    img = cv2.cvtColor(img, cv2.COLOR_RGBA2RGB)

    # 밝기 조정 (alpha: 대비, beta: 밝기)
    adjusted_img = cv2.convertScaleAbs(img, alpha=1.0, beta=10)
    return adjusted_img


# YOLO를 사용한 알약 감지
def detect_pills_with_yolo(image):
    """
    YOLO 모델을 사용하여 알약 감지 (Bounding Box 반환)
    """
    results = model(image)

    # YOLO가 감지 실패하면 밝기 조정 후 재시도
    if len(results[0].boxes) == 0:
        image = cv2.convertScaleAbs(image, alpha=1.8, beta=70)  # 밝기 조정 후 재시도
        results = model(image)
    return results


# Google OCR 실행
def detect_text_google_ocr(image):
    """
    Google Cloud Vision OCR을 사용하여 이미지에서 텍스트 추출
    """
    _, encoded_image = cv2.imencode(".png", image)
    image_bytes = encoded_image.tobytes()

    image = vision.Image(content=image_bytes)
    response = vision_client.text_detection(image=image)

    texts = response.text_annotations
    if texts:
        return texts[0].description.strip()
    return "인식 실패"


# Levenshtein 유사도 비교 (OCR 텍스트와 JSON 데이터 비교)
def calculate_similarity(text1, text2):
    """
    두 개의 텍스트 간 유사도(%)를 계산 (Levenshtein 거리 사용)
    """
    return difflib.SequenceMatcher(None, text1, text2).ratio() * 100


# FastAPI에서 호출할 핵심 함수
def detect_pill(image_bytes):
    """
    FastAPI에서 호출하는 알약 인식 함수

    1. 이미지의 배경을 제거하고 밝기 조정
    2. YOLO 모델을 사용하여 알약을 감지
    3. Google Cloud Vision OCR을 통해 텍스트를 추출
    4. JSON 데이터와 비교하여 가장 유사한 알약 이름 반환
    """
    img = remove_background_and_adjust(image_bytes)
    img = cv2.cvtColor(img, cv2.COLOR_RGBA2RGB)

    results = detect_pills_with_yolo(img)
    detected_pills = []

    for result in results:
        for box in result.boxes:
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            cropped_img = img[y1:y2, x1:x2]

            if cropped_img.shape[0] == 0 or cropped_img.shape[1] == 0:
                continue

            raw_text = detect_text_google_ocr(cropped_img)
            detected_text = correct_ocr_text(raw_text)

            matched_pill = None
            matched_pill_name = "알 수 없음"
            max_similarity = 0

            for pill_id, pill_info in pill_labels.items():
                similarity = calculate_similarity(detected_text, pill_info["text"])
                if similarity > max_similarity:
                    max_similarity = similarity
                    matched_pill = pill_id
                    matched_pill_name = pill_info["name"]

            if max_similarity >= 70:
                detected_pills.append(matched_pill_name)

    return detected_pills if detected_pills else ["인식 실패"]


# 테스트 코드 (FastAPI 서버에서 실행할 때는 실행 안 됨)
if __name__ == "__main__":
    # 테스트용 이미지 경로
    TEST_IMAGE_PATH = os.path.join(BASE_DIR, "test", "diff03.png")
    
    with open(TEST_IMAGE_PATH, "rb") as f:
        image_bytes = f.read()

    result = detect_pill(image_bytes)
    print(f"예측된 알약: {result}")

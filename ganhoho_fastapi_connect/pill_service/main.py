from fastapi import FastAPI, File, UploadFile
import uvicorn
import io
from PIL import Image
from model_runner import detect_pill
from typing import Dict, Any

app = FastAPI()

@app.post("/predict/")
async def predict_pill(image: UploadFile = File(...)) -> Dict[str, Any]:
    try:
        # 1. 이미지 파일을 바이너리로 읽기
        image_bytes = await image.read()

        # 2. PIL.Image로 변환
        image_pil = Image.open(io.BytesIO(image_bytes))

        # 3. YOLO 모델 실행
        detections = detect_pill(image_bytes)
        
        # 4. 응답 구조화
        if not detections or detections[0] == "인식 실패":
            return {
                "success": False,
                "message": "의약품을 인식하지 못했습니다",
                "detections": []
            }
        
        return {
            "success": True,
            "message": "Prediction successful",
            "detections": {
                "name": detections[0]  # 첫 번째 감지된 의약품 이름
            }
        }

    except Exception as e:
        return {
            "success": False,
            "message": str(e),
            "detections": []
        }

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
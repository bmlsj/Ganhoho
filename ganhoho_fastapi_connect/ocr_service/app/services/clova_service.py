import requests
import json
from fastapi import HTTPException
# from ..config.yml_config import CLOVA_API_URL, CLOVA_SECRET_KEY
from ..config.settings import settings

class ClovaOCRService:
    @staticmethod
    async def process_image(ocrImg) -> dict :
        if not settings.API_URL or not settings.SECRET_KEY:
            raise HTTPException(status_code=500, detail="OCR 설정값이 없습니다.")
        
        headers = {"X-OCR-SECRET": settings.SECRET_KEY}
        file_content = await ocrImg.read()

        files = {"file": ("image.png", file_content, "image/png")}
        payload = {
            "images": [{"format":"png", "name": "schedule_image"}],
            "requestId": "unique_request_id",
            "version": "V2",
            "timestamp": 0
        }

        try: 
            response = requests.post(
                settings.API_URL,
                headers=headers,
                data={"message": json.dumps(payload)},
                files=files
            )
            response.raise_for_status()
            return response.json()
        
        except requests.RequestException as e:
            raise HTTPException(status_code= 500, detail= f"OCR API 호출 실패: {str(e)}")
        
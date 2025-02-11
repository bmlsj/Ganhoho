import requests
import json
from ..config.settings import settings
from fastapi import HTTPException

class ClovaOCRService:
    @staticmethod
    async def process_image(ocrImg) -> dict :
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
        
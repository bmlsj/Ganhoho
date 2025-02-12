from fastapi import FastAPI
from .api.routes import ocr

app = FastAPI(title="Nurse Schedule OCR API")


app.include_router(ocr.router, prefix="/api/schedules", tags=["OCR"])

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .api.routes import ocr

app = FastAPI(title="Nurse Schedule OCR API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080"],  # Spring Boot 서버 주소
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(ocr.router, prefix="/api", tags=["OCR"])

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
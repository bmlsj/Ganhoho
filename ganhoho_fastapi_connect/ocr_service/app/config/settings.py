from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    API_URL: str 
    SECRET_KEY: str
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        # 환경 변수 이름 매핑
        env_prefix = "CLOVA_OCR_"
        extra = "allow"

settings = Settings()
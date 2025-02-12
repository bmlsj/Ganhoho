from fastapi import APIRouter, UploadFile, File, HTTPException
from ...services.clova_service import ClovaOCRService
from ...core.ocr_processor import OCRProcessor
from ...core.schedule_builder import ScheduleBuilder
from ...schemas.schedule import ScheduleResponse

router = APIRouter()

@router.post("/ocr", response_model=ScheduleResponse)
async def process_schedule(ocrImg: UploadFile = File(...)):
    try:
        # OCR 처리
        ocr_service = ClovaOCRService()
        ocr_result = await ocr_service.process_image(ocrImg)
        
        # OCR 결과 처리
        processor = OCRProcessor()
        schedule_data = processor.process_ocr_result(ocr_result)
        
        # 최종 스케줄 생성
        builder = ScheduleBuilder()
        final_schedule = builder.build_schedule(schedule_data)
        
        return final_schedule
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
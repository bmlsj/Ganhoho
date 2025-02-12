package com.ssafy.ganhoho.domain.schedule.service;

import com.ssafy.ganhoho.domain.schedule.entity.OCRSchedule;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OCRScheduleService {

    // ocr 이미지 처리 및 스케줄 저장
    OCRSchedule processANDSaveSchedule(MultipartFile ocrImg, Long memberId);

    // 회원 ID와 이름으로 OCR 스케줄 조회
    List<OCRSchedule> getScheduleByMemberIdAndName(Long memberId, String name);

}

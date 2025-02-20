package com.ssafy.ganhoho.domain.schedule.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.ganhoho.domain.schedule.entity.OCRSchedule;

public interface OCRScheduleService {

    // ocr 이미지 처리 및 스케줄 저장
    List<OCRSchedule> processANDSaveSchedule(MultipartFile ocrImg, Long memberId);

    // 회원 ID와 이름으로 OCR 스케줄 조회
    List<OCRSchedule> getScheduleByMemberId(Long memberId);

}

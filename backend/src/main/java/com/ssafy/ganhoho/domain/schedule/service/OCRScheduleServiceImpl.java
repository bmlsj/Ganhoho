package com.ssafy.ganhoho.domain.schedule.service;

import com.ssafy.ganhoho.domain.auth.AuthRepository;
import com.ssafy.ganhoho.domain.schedule.entity.OCRSchedule;
import com.ssafy.ganhoho.domain.schedule.repository.OCRScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OCRScheduleServiceImpl implements OCRScheduleService {

    private final OCRScheduleRepository ocrScheduleRepository;
    private final WorkScheduleService workScheduleService;
    private final AuthRepository authRepository;

    @Value("${ocr.fastapi.url}")
    private String fastApiUrl;


    @Override
    @Transactional
    // 이미지 처리 및 스케줄 저장(결과 MongoDB, 근무 스케줄 정보 함께 저장)
    public OCRSchedule processAndSaveSchedule(MultipartFile ocrImg, Long memberId) {
        try {

        }
    }

}

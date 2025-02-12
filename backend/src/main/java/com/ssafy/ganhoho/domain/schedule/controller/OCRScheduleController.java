package com.ssafy.ganhoho.domain.schedule.controller;

import com.ssafy.ganhoho.domain.schedule.entity.OCRSchedule;
import com.ssafy.ganhoho.domain.schedule.service.OCRScheduleService;
import com.ssafy.ganhoho.global.auth.SecurityUtil;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import com.ssafy.ganhoho.global.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class OCRScheduleController {

    private final OCRScheduleService ocrScheduleService;

    // 이미지 처리 api
    @PostMapping("/ocr")
    public ResponseEntity<?> processOCRSchedule(
            @RequestParam("ocrImg") MultipartFile ocrImg
    ) {
        try {
            // 토큰 추출
            Long memberId = SecurityUtil.getCurrentMemberId();
            List<OCRSchedule> result = ocrScheduleService.processANDSaveSchedule(ocrImg, memberId);
            return ResponseEntity.ok(result);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            log.error("OCR 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(ErrorCode.SERVER_ERROR));
        }
    }

    // OCR 스케줄 조회
    @GetMapping("/ocr")
    public ResponseEntity<?> getOCRSchedule() {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            return ResponseEntity.ok(ocrScheduleService.getScheduleByMemberId(memberId));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            log.error("OCR 스케줄 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(ErrorCode.SERVER_ERROR));
        }
    }
}

package com.ssafy.ganhoho.domain.schedule.service;

import com.ssafy.ganhoho.domain.auth.AuthRepository;
import com.ssafy.ganhoho.domain.member.entity.Member;
import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleRequestDto;
import com.ssafy.ganhoho.domain.schedule.entity.OCRSchedule;
import com.ssafy.ganhoho.domain.schedule.entity.WorkType;
import com.ssafy.ganhoho.domain.schedule.repository.OCRScheduleRepository;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OCRScheduleServiceImpl implements OCRScheduleService {

    private final OCRScheduleRepository ocrScheduleRepository;
    private final WorkScheduleService workScheduleService;
    private final AuthRepository authRepository;

    // FastAPI URL 주석 처리
    // @Value("${ocr.fastapi.url}")
    // private String fastApiUrl;

    @Override
    @Transactional
    public List<OCRSchedule> processANDSaveSchedule(MultipartFile ocrImg, Long memberId) {
        // FastAPI 서버 비활성화 상태 알림
        throw new CustomException(ErrorCode.SERVER_ERROR);

        /* 기존 FastAPI 관련 코드 주석 처리
        try {
            Member member = authRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

            RestTemplate restTemplate = new RestTemplate();
            String url = fastApiUrl + "/api/schedules/ocr";
            ...
        } catch (Exception e) {
            log.error("OCR 스케줄 처리 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
        */
    }

    @Override
    public List<OCRSchedule> getScheduleByMemberId(Long memberId) {
        // 회원 존재 확인
        if (!authRepository.existsById(memberId)) {
            throw new CustomException(ErrorCode.NOT_EXIST_MEMBER);
        }
        return ocrScheduleRepository.findByMemberId(memberId);
    }

    // 나머지 helper 메서드들은 그대로 유지
    private OCRSchedule convertToOCRSchedule(Map<String, Object> data, Member member) {
        List<Map<String, Object>> scheduleList = (List<Map<String, Object>>) data.get("schedule");
        List<OCRSchedule.ScheduleDay> scheduleDays = scheduleList.stream()
                .map(day -> OCRSchedule.ScheduleDay.builder()
                    .day((Integer) day.get("day"))
                    .type((String) day.get("type"))
                    .build())
                .collect(Collectors.toList());

        return OCRSchedule.builder()
                .memberId(member.getMemberId())
                .name((String) data.get("name"))
                .year((Integer) data.get("year"))
                .month((Integer) data.get("month"))
                .scheduleData(scheduleDays)
                .build();
    }

    private void saveToWorkSchedule(OCRSchedule schedule) {
        List<WorkScheduleRequestDto> workSchedules = schedule.getScheduleData().stream()
                .map(day -> {
                    Date workDate = java.sql.Timestamp.valueOf(
                            LocalDateTime.of(
                                    schedule.getYear(),
                                    schedule.getMonth(),
                                    day.getDay(),
                                    0, 0
                            )
                    );

                    return WorkScheduleRequestDto.builder()
                            .memberId(schedule.getMemberId())
                            .workType(WorkType.valueOf(day.getType()))
                            .workDate(workDate)
                            .workScheduleDetailId(null) // 그룹정보 x null처리. 추후 그룹쪽에서 그룹가입시 바꿔주는걸로.
                            .build();
                })
                .collect(Collectors.toList());
        workScheduleService.updateOrAddWorkSchedules(workSchedules, schedule.getMemberId());
    }
}

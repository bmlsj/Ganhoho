package com.ssafy.ganhoho.domain.schedule.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ssafy.ganhoho.domain.group.GroupParticipationRepository;
import com.ssafy.ganhoho.domain.group.GroupService;
import com.ssafy.ganhoho.domain.group.entity.GroupParticipation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OCRScheduleServiceImpl implements OCRScheduleService {

    private final OCRScheduleRepository ocrScheduleRepository;
    private final WorkScheduleService workScheduleService;
    private final AuthRepository authRepository;
    private final GroupParticipationRepository groupParticipationRepository;
    private final GroupService groupService;

    @Value("${ocr.fastapi.url}")
    private String fastApiUrl;


    @Override
    @Transactional
    // 이미지 처리 및 스케줄 저장(결과 MongoDB, 근무 스케줄 정보 함께 저장)
    public List<OCRSchedule> processANDSaveSchedule(MultipartFile ocrImg, Long memberId) {
        try {

            // 예외처리 존재하지 않는 회원일시
            Member member = authRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

            // 기존 OCR 스케줄 삭제
            ocrScheduleRepository.deleteByMemberId(memberId);

            //FastAPI 서버로 이미지 전송
            RestTemplate restTemplate = new RestTemplate();
            String url = fastApiUrl + "/fastapi/ocr";
            log.info("FastAPI 서버 요청: {}", url);

            // 이미지 파일 변환
            ByteArrayResource imageResource = new ByteArrayResource(ocrImg.getBytes()) {
                @Override
                public String getFilename() {
                    return ocrImg.getOriginalFilename();
                }
            };

            //multipart/form-data 형식 구성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("ocrImg", imageResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            //Fast api로 전송 및 응답 수신
            log.info("OCR 이미지 처리 요청");
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            log.info("OCR 이미지 처리 응답 수신: {}", response.getBody());

            //응답 검증
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new CustomException(ErrorCode.SERVER_ERROR); //OCR 처리중 오류 발생
            }

            // 스케줄 데이터 추출
            List<Map<String, Object>> scheduleDataList = (List<Map<String, Object>>) response.getBody().get("data");
            if (scheduleDataList.isEmpty()) {
                throw new CustomException(ErrorCode.SERVER_ERROR);
            }

            // 모든 스케줄 MongoDB 저장
            List<OCRSchedule> savedSchedules = new ArrayList<>();
            for (Map<String, Object> scheduleData : scheduleDataList) {
                OCRSchedule schedule = convertToOCRSchedule(scheduleData, member);
                OCRSchedule savedSchedule = ocrScheduleRepository.save(schedule);
                savedSchedules.add(savedSchedule);

                // jwt 제공 토큰과 일치하는 회원의 이름 workSChedule에 추가
                if (savedSchedule.getName().equals(member.getName())) {
                    saveToWorkSchedule(savedSchedule);
                }

            }

            return savedSchedules;

        } catch (Exception e) {
            log.error("OCR 스케줄 처리 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    @Override // 회원 ID와 이름으로 OCR 스케줄 조회 (추후 전체 근무줄 스케줄 조회에 사용)
    public List<OCRSchedule> getScheduleByMemberId(Long memberId) {
        // 회원 존재 확인
        if (!authRepository.existsById(memberId)) {
            throw new CustomException(ErrorCode.NOT_EXIST_MEMBER);
        }
        return ocrScheduleRepository.findByMemberId(memberId);
    }

    //FastAPI 응답 데이터 OCRSchedule 형태로 변환
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

    // OCR 스케줄 -> 근무 스케줄로 변환 저장, 머지시도
    private void saveToWorkSchedule(OCRSchedule schedule) {
        List<WorkScheduleRequestDto> workSchedules = schedule.getScheduleData().stream()
                .map(day -> {
                    Date workDate = java.sql.Timestamp.valueOf(
                            LocalDateTime.of(
                                    schedule.getYear(),
                                    schedule.getMonth(),
                                    day.getDay(),
                                    15, 0
                                    //UTC로 밀리는거 방지
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

        List<GroupParticipation> participations = groupParticipationRepository
            .findByMemberId(schedule.getMemberId());

        // 해당 멤버가 속한 모든 그룹에 스케줄 연동
        for (GroupParticipation participation : participations) {
            String yearMonth = String.format("%d-%02d", schedule.getYear(), schedule.getMonth());
            groupService.linkMemberSchedulesToGroup(
                    participation.getGroupId(),
                    schedule.getMemberId(),
                    yearMonth
            );
        }

    }

}

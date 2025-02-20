package com.ssafy.ganhoho.domain.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleRequestDto;
import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleResponseDto;
import com.ssafy.ganhoho.domain.schedule.entity.WorkSchedule;
import com.ssafy.ganhoho.domain.schedule.repository.WorkScheduleRepository;
import com.ssafy.ganhoho.global.auth.jwt.JWTUtil;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final JWTUtil jwtUtil;

    public List<WorkScheduleResponseDto> getWorkSchedules(Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETERS);
        }

        List<WorkSchedule> schedules = workScheduleRepository.findByMemberId(memberId);
        if (schedules.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXIST_DATA);
        }

        return schedules.stream()
                .map(schedule -> new WorkScheduleResponseDto(
                        schedule.getWorkType(),
                        schedule.getWorkDate()))
                .collect(Collectors.toList());
    }

    public WorkScheduleResponseDto updateWorkSchedule(Long workScheduleId, WorkScheduleRequestDto updatedSchedule, Long memberId) {
        if (workScheduleId == null || memberId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETERS);
        }

        if (updatedSchedule == null) {
            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
        }

        WorkSchedule schedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_DATA));

        if (!schedule.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        try {
            schedule.setWorkScheduleDetailId(updatedSchedule.getWorkScheduleDetailId());
            schedule.setWorkType(updatedSchedule.getWorkType());
            schedule.setWorkDate(updatedSchedule.getWorkDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

            workScheduleRepository.save(schedule);

            return new WorkScheduleResponseDto(schedule.getWorkType(), schedule.getWorkDate());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_DATA);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    public List<WorkScheduleResponseDto> addWorkSchedules(List<WorkScheduleRequestDto> requestDtos, Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETERS);
        }

        if (requestDtos == null || requestDtos.isEmpty()) {
            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
        }

        try {
            List<WorkSchedule> schedules = requestDtos.stream()
                    .map(requestDto -> {
                        validateWorkScheduleRequest(requestDto);
                        return WorkSchedule.builder()
                                .memberId(memberId)
                                .workScheduleDetailId(requestDto.getWorkScheduleDetailId())
                                .workType(requestDto.getWorkType())
                                .workDate(requestDto.getWorkDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                                .build();
                    })
                    .collect(Collectors.toList());

            workScheduleRepository.saveAll(schedules);

            return schedules.stream()
                    .map(schedule -> new WorkScheduleResponseDto(
                            schedule.getWorkType(),
                            schedule.getWorkDate()))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_DATA);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    private void validateWorkScheduleRequest(WorkScheduleRequestDto requestDto) {
        if (requestDto.getWorkType() == null) {
            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
        }
        if (requestDto.getWorkDate() == null) {
            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
        }
//        if (requestDto.getWorkScheduleDetailId() == null) {
//            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
//        }
    }

    public WorkSchedule saveWorkSchedule(WorkSchedule workSchedule) {
        return workScheduleRepository.save(workSchedule);
    }

    public List<WorkScheduleResponseDto> getWorkSchedulesByMemberIdAndDateRange(
            Long memberId, LocalDate startDate, LocalDate endDate) {
        if (memberId == null || startDate == null || endDate == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETERS);
        }

        if (startDate.isAfter(endDate)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_DATA);
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<WorkSchedule> schedules = workScheduleRepository.findByMemberIdAndWorkDateBetween(
                memberId, startDateTime, endDateTime);

        if (schedules.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXIST_DATA);
        }

        return schedules.stream()
                .map(schedule -> new WorkScheduleResponseDto(
                        schedule.getWorkType(),
                        schedule.getWorkDate()))
                .collect(Collectors.toList());
    }

    //기존 작업 일정 업데이트(ocr)
    @Transactional
    public List<WorkScheduleResponseDto> updateOrAddWorkSchedules(List<WorkScheduleRequestDto> requestDtos, Long memberId) {
        // memberId가 없을시
        if (memberId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETERS);
        }

        if (requestDtos == null || requestDtos.isEmpty()) {
            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
        }

        try {
            List<WorkSchedule> updatedSchedules = new ArrayList<>();

            for (WorkScheduleRequestDto requestDto : requestDtos) {
                validateWorkScheduleRequest(requestDto);
                LocalDateTime workDateTime = requestDto.getWorkDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
                // 같은 날짜 기존 스케줄 탐색
                WorkSchedule existingSchedule = workScheduleRepository
                        .findByMemberIdAndWorkDate(memberId, workDateTime)
                        .orElse(null);

                if (existingSchedule != null) {
                    // 기존 스케줄 업데이트
                    existingSchedule.setWorkType(requestDto.getWorkType());
                    existingSchedule.setWorkScheduleDetailId(requestDto.getWorkScheduleDetailId());
                    updatedSchedules.add(workScheduleRepository.save(existingSchedule));
                } else {
                    //새로운 스케줄 생성
                    WorkSchedule newSchedule = WorkSchedule.builder()
                            .memberId(memberId)
                            .workScheduleDetailId(requestDto.getWorkScheduleDetailId())
                            .workType(requestDto.getWorkType())
                            .workDate(workDateTime)
                            .build();
                    updatedSchedules.add(workScheduleRepository.save(newSchedule));
                }
            }

            return updatedSchedules.stream()
                .map(schedule -> new WorkScheduleResponseDto(
                    schedule.getWorkType(),
                    schedule.getWorkDate()))
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_DATA);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

}

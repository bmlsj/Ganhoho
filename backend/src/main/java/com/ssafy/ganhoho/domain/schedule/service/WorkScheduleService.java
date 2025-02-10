package com.ssafy.ganhoho.domain.schedule.service;

import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleRequestDto;
import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleResponseDto;
import com.ssafy.ganhoho.domain.schedule.entity.WorkSchedule;
import com.ssafy.ganhoho.domain.schedule.repository.WorkScheduleRepository;
import com.ssafy.ganhoho.global.auth.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final JWTUtil jwtUtil;

    public List<WorkScheduleResponseDto> getWorkSchedules(Long memberId) {
        List<WorkSchedule> schedules = workScheduleRepository.findByMemberId(memberId);
        return schedules.stream()
                .map(schedule -> new WorkScheduleResponseDto(
                        schedule.getWorkType(),
                        schedule.getWorkDate()))
                .collect(Collectors.toList());
    }

    public WorkScheduleResponseDto updateWorkSchedule(Long workScheduleId, WorkScheduleRequestDto updatedSchedule, Long memberId) {
        WorkSchedule schedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (!schedule.getMemberId().equals(memberId)) {
            throw new RuntimeException("Unauthorized access");
        }

        schedule.setWorkScheduleDetailId(updatedSchedule.getWorkScheduleDetailId());
        schedule.setWorkType(updatedSchedule.getWorkType());
        schedule.setWorkDate(updatedSchedule.getWorkDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        workScheduleRepository.save(schedule);

        return new WorkScheduleResponseDto(schedule.getWorkType(), schedule.getWorkDate());
    }
    public List<WorkScheduleResponseDto> addWorkSchedules(List<WorkScheduleRequestDto> requestDtos, Long memberId) {
        List<WorkSchedule> schedules = requestDtos.stream()
                .map(requestDto -> WorkSchedule.builder()
                        .memberId(memberId)
                        .workScheduleDetailId(requestDto.getWorkScheduleDetailId())
                        .workType(requestDto.getWorkType())
                        .workDate(requestDto.getWorkDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .build())
                .collect(Collectors.toList());

        workScheduleRepository.saveAll(schedules);

        return schedules.stream()
                .map(schedule -> new WorkScheduleResponseDto(
                        schedule.getWorkType(),
                        schedule.getWorkDate()))
                .collect(Collectors.toList());
    }

    public WorkSchedule saveWorkSchedule(WorkSchedule workSchedule) {
        return workScheduleRepository.save(workSchedule);
    }

    public List<WorkScheduleResponseDto> getWorkSchedulesByMemberIdAndDateRange(
            Long memberId, LocalDate startDate, LocalDate endDate) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return workScheduleRepository.findByMemberIdAndWorkDateBetween(
                memberId, startDateTime, endDateTime)
                .stream()
                .map(schedule -> new WorkScheduleResponseDto(
                        schedule.getWorkType(),
                        schedule.getWorkDate()))
                .collect(Collectors.toList());
    }
}

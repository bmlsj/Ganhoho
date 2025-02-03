package com.ssafy.ganhoho.domain.schedule.service;

import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleRequestDto;
import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleResponseDto;
import com.ssafy.ganhoho.domain.schedule.entity.WorkSchedule;
import com.ssafy.ganhoho.domain.schedule.repository.WorkScheduleRepository;
import com.ssafy.ganhoho.global.auth.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                        schedule.getWorkScheduleId(),
                        schedule.getMemberId(),
                        schedule.getGroupScheduleDetailId(),
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

        schedule.setGroupScheduleDetailId(updatedSchedule.getGroupScheduleDetailId());
        schedule.setWorkType(updatedSchedule.getWorkType());
        schedule.setWorkDate(updatedSchedule.getWorkDate());

        workScheduleRepository.save(schedule);

        return new WorkScheduleResponseDto(schedule.getWorkScheduleId(), schedule.getMemberId(), schedule.getGroupScheduleDetailId(), schedule.getWorkType(), schedule.getWorkDate());
    }

    public void deleteWorkSchedule(Long workScheduleId, Long memberId) {
        WorkSchedule schedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (!schedule.getMemberId().equals(memberId)) {
            throw new RuntimeException("Unauthorized access");
        }

        workScheduleRepository.delete(schedule);
    }
}
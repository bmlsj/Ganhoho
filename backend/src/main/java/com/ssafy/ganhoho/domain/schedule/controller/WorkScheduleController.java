package com.ssafy.ganhoho.domain.schedule.controller;

import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleRequestDto;
import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleResponseDto;
import com.ssafy.ganhoho.domain.schedule.service.WorkScheduleService;
import com.ssafy.ganhoho.global.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @GetMapping("/work")
    public ResponseEntity<List<WorkScheduleResponseDto>> getWorkSchedules() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getUserId();

        List<WorkScheduleResponseDto> schedules = workScheduleService.getWorkSchedules(memberId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/work/{memberId}")
    public ResponseEntity<List<WorkScheduleResponseDto>> getWorkSchedulesByMemberId(@PathVariable Long memberId) {
        List<WorkScheduleResponseDto> schedules = workScheduleService.getWorkSchedules(memberId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @PutMapping("/work/{workScheduleId}")
    public ResponseEntity<WorkScheduleResponseDto> updateWorkSchedule(
            @PathVariable Long workScheduleId,
            @RequestBody WorkScheduleRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = (Long) authentication.getPrincipal();

        WorkScheduleResponseDto updatedSchedule = workScheduleService.updateWorkSchedule(workScheduleId, requestDto, memberId);
        return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
    }
}

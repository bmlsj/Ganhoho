package com.ssafy.ganhoho.domain.schedule.controller;

import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleRequestDto;
import com.ssafy.ganhoho.domain.schedule.dto.WorkScheduleResponseDto;
import com.ssafy.ganhoho.domain.schedule.service.WorkScheduleService;
import com.ssafy.ganhoho.global.auth.dto.CustomUserDetails;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
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

    private CustomUserDetails validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new CustomException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (userDetails.getUserId() == null) {
            throw new CustomException(ErrorCode.MISSING_REQUIRED_USER_DATA);
        }

        return userDetails;
    }

    @GetMapping("/work")
    public ResponseEntity<List<WorkScheduleResponseDto>> getWorkSchedules() {
        CustomUserDetails userDetails = validateToken();
        Long memberId = userDetails.getUserId();
        
        List<WorkScheduleResponseDto> schedules = workScheduleService.getWorkSchedules(memberId);
        if (schedules.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXIST_DATA);
        }
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/work/{memberId}")
    public ResponseEntity<List<WorkScheduleResponseDto>> getWorkSchedulesByMemberId(
            @PathVariable Long memberId) {
        validateToken();  // JWT 토큰 검증

        if (memberId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETERS);
        }

        List<WorkScheduleResponseDto> schedules = workScheduleService.getWorkSchedules(memberId);
        if (schedules.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXIST_DATA);
        }
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @PutMapping("/work/{workScheduleId}")
    public ResponseEntity<WorkScheduleResponseDto> updateWorkSchedule(
            @PathVariable Long workScheduleId,
            @RequestBody WorkScheduleRequestDto requestDto) {
        CustomUserDetails userDetails = validateToken();

        if (workScheduleId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETERS);
        }

        if (requestDto == null) {
            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
        }

        Long memberId = userDetails.getUserId();
        WorkScheduleResponseDto updatedSchedule = workScheduleService.updateWorkSchedule(workScheduleId, requestDto, memberId);
        return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
    }

    @PostMapping("/work/create")
    public ResponseEntity<List<WorkScheduleResponseDto>> addWorkSchedules(
            @RequestBody List<WorkScheduleRequestDto> requestDtos) {
        CustomUserDetails userDetails = validateToken();

        if (requestDtos == null || requestDtos.isEmpty()) {
            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
        }

        Long memberId = userDetails.getUserId();
        List<WorkScheduleResponseDto> createdSchedules = workScheduleService.addWorkSchedules(requestDtos, memberId);
        return new ResponseEntity<>(createdSchedules, HttpStatus.CREATED);
    }
}

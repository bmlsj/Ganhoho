package com.ssafy.ganhoho.domain.schedule.controller;

import com.ssafy.ganhoho.domain.schedule.dto.PersonalScheduleRequestDto;
import com.ssafy.ganhoho.domain.schedule.dto.PersonalScheduleResponseDto;
import com.ssafy.ganhoho.domain.schedule.service.PersonalScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.ssafy.ganhoho.global.auth.dto.CustomUserDetails;
import com.ssafy.ganhoho.global.error.ErrorResponse;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

@RestController
@Slf4j
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class PersonalScheduleController {

    private final PersonalScheduleService personalScheduleService;

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

    @PostMapping("/personal")
    public ResponseEntity<PersonalScheduleResponseDto> addPersonalSchedule(
            @RequestBody PersonalScheduleRequestDto requestDto) {
        CustomUserDetails userDetails = validateToken();
        
        if (requestDto == null) {
            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
        }

        Long memberId = userDetails.getUserId();
        PersonalScheduleResponseDto createdSchedule = personalScheduleService.addPersonalSchedule(requestDto, memberId);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @GetMapping("/personal")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getPersonalSchedules() {
        CustomUserDetails userDetails = validateToken();
        Long memberId = userDetails.getUserId();

        Map<String, List<Map<String, Object>>> schedules = personalScheduleService.getFormattedPersonalSchedules(memberId);
        if (schedules.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXIST_DATA);
        }
        
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @PutMapping("/personal/{scheduleId}")
    public ResponseEntity<Map<String, Boolean>> updatePersonalSchedule(
            @PathVariable Long scheduleId,
            @RequestBody PersonalScheduleRequestDto requestDto) {
        CustomUserDetails userDetails = validateToken();

        if (scheduleId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETERS);
        }

        if (requestDto == null) {
            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
        }

        Long memberId = userDetails.getUserId();
        personalScheduleService.updatePersonalSchedule(scheduleId, requestDto, memberId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/personal/{memberId}")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getPersonalSchedulesByMemberId(
            @PathVariable Long memberId) {
        validateToken();  // 토큰 검증

        if (memberId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETERS);
        }

        Map<String, List<Map<String, Object>>> response = personalScheduleService.getPersonalSchedulesByMemberId(memberId);
        if (response.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXIST_DATA);
        }
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getSchedule(@PathVariable Long scheduleId) {
        validateToken();  // 토큰 검증

        if (scheduleId == null || scheduleId <= 0) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_PARAMETERS);
        }

        try {
            return ResponseEntity.ok(personalScheduleService.getSchedule(scheduleId));
        } catch (Exception e) {
            log.error("일정 조회 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }
}
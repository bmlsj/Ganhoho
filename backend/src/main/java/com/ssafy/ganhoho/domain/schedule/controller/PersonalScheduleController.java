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

    @PostMapping("/personal")
    public ResponseEntity<PersonalScheduleResponseDto> addPersonalSchedule(
            @RequestBody PersonalScheduleRequestDto requestDto) {
        // JWTFilter를 통해 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getUserId();  // getId() 대신 getUserId() 사용

        // 개인 스케줄 추가
        PersonalScheduleResponseDto createdSchedule = personalScheduleService.addPersonalSchedule(requestDto, memberId);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @GetMapping("/personal")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getPersonalSchedules() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getUserId();

        return new ResponseEntity<>(personalScheduleService.getFormattedPersonalSchedules(memberId), HttpStatus.OK);
    }

    @PutMapping("/personal/{scheduleId}")
    public ResponseEntity<Map<String, Object>> updatePersonalSchedule(
            @PathVariable Long scheduleId,
            @RequestBody PersonalScheduleRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getUserId();

        Map<String, Object> response = new HashMap<>();
        try {
            personalScheduleService.updatePersonalSchedule(scheduleId, requestDto, memberId);
            response.put("success", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/personal/{memberId}")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getPersonalSchedulesByMemberId(
            @PathVariable Long memberId) {
        Map<String, List<Map<String, Object>>> response = personalScheduleService.getPersonalSchedulesByMemberId(memberId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
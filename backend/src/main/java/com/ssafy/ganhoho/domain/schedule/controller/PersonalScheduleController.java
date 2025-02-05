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

@RestController
@Slf4j
@RequestMapping("/api/personal-schedules")
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
    public ResponseEntity<List<PersonalScheduleResponseDto>> getPersonalSchedules() {
        // JWTFilter를 통해 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getUserId();  // getId() 대신 getUserId() 사용

        List<PersonalScheduleResponseDto> schedules = personalScheduleService.getPersonalSchedules(memberId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @PutMapping("/personal/{scheduleId}")
    public ResponseEntity<PersonalScheduleResponseDto> updatePersonalSchedule(
            @PathVariable Long scheduleId,
            @RequestBody PersonalScheduleRequestDto requestDto) {
        // JWTFilter를 통해 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getUserId();  // getId() 대신 getUserId() 사용

        PersonalScheduleResponseDto updatedSchedule = personalScheduleService.updatePersonalSchedule(scheduleId, requestDto, memberId);
        return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
    }
}
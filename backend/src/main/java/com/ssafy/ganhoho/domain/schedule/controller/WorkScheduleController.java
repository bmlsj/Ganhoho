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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

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

    @Operation(summary = "근무 일정 조회", description = "인증된 사용자의 전체 근무 일정 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 조회 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "data": [
                                {
                                    "scheduleId": 1,
                                    "startDt": "2024-03-21T09:00:00",
                                    "endDt": "2024-03-21T18:00:00",
                                    "scheduleTitle": "주간근무",
                                    "scheduleColor": "#FF5733",
                                    "isTimeSet": true
                                }
                            ]
                        }
                        """))),
            @ApiResponse(responseCode = "200", description = "조회된 데이터가 없습니다.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "200",
                            "message": "데이터가 존재하지 않습니다."
                        }
                        """))),
            @ApiResponse(responseCode = "401", description = "토큰이 없는 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "401",
                            "message": "유효하지 않은 토큰입니다."
                        }
                        """))),
            @ApiResponse(responseCode = "401", description = "토큰이 만료된 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "401",
                            "message": "만료된 토큰입니다."
                        }
                        """))),
            @ApiResponse(responseCode = "401", description = "토큰에서 userId를 추출할 수 없는 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "401",
                            "message": "필수 사용자 데이터가 누락되었습니다."
                        }
                        """))),
            @ApiResponse(responseCode = "500", description = "서버에서 예외가 발생한 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "500",
                            "message": "서버 내부 오류가 발생했습니다."
                        }
                        """))),
    })
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

    @Operation(summary = "특정 멤버의 근무 일정 조회", description = "특정 멤버의 전체 근무 일정 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 조회 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "data": [
                                {
                                    "scheduleId": 1,
                                    "startDt": "2024-03-21T09:00:00",
                                    "endDt": "2024-03-21T18:00:00",
                                    "scheduleTitle": "주간근무",
                                    "scheduleColor": "#FF5733",
                                    "isTimeSet": true
                                }
                            ]
                        }
                        """))),
            @ApiResponse(responseCode = "200", description = "조회된 데이터가 없습니다.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "200",
                            "message": "데이터가 존재하지 않습니다."
                        }
                        """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "400",
                            "message": "잘못된 요청 파라미터입니다."
                        }
                        """))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "401",
                            "message": "인증되지 않은 요청입니다."
                        }
                        """))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "404",
                            "message": "데이터가 존재하지 않습니다."
                        }
                        """))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "500",
                            "message": "서버 내부 오류가 발생했습니다."
                        }
                        """))),
    })
    @GetMapping("/work/{memberId}")
    public ResponseEntity<List<WorkScheduleResponseDto>> getWorkSchedulesByMemberId(
            @Parameter(description = "조회할 멤버의 ID") @PathVariable Long memberId) {
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

    @Operation(summary = "근무 일정 수정", description = "특정 근무 일정을 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 수정 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "success": true
                        }
                        """))),
            @ApiResponse(responseCode = "400", description = "workScheduleId가 null인 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "400",
                            "message": "잘못된 요청 파라미터입니다."
                        }
                        """))),
            @ApiResponse(responseCode = "400", description = "requestDto가 null이거나 필수 필드 누락",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "400",
                            "message": "필수 항목이 누락되었습니다."
                        }
                        """))),
            @ApiResponse(responseCode = "401", description = "토큰이 없거나 유효하지 않은 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "401",
                            "message": "인증되지 않은 요청입니다."
                        }
                        """))),
            @ApiResponse(responseCode = "404", description = "해당 workScheduleId의 일정이 없는 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "404",
                            "message": "데이터가 존재하지 않습니다."
                        }
                        """))),
            @ApiResponse(responseCode = "500", description = "서버에서 예외가 발생한 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "500",
                            "message": "서버 내부 오류가 발생했습니다."
                        }
                        """))),
    })
    @PutMapping("/work/{workScheduleId}")
    public ResponseEntity<WorkScheduleResponseDto> updateWorkSchedule(
            @Parameter(description = "수정할 일정의 ID") @PathVariable Long workScheduleId,
            @Parameter(description = "수정할 일정 정보", required = true,
                schema = @Schema(example = """
                    {
                        "scheduleTitle": "수정된 근무",
                        "startDt": "2024-03-21T09:00:00",
                        "endDt": "2024-03-21T18:00:00",
                        "scheduleColor": "#FF5733",
                        "isTimeSet": true
                    }
                    """))
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

    @Operation(summary = "근무 일정 생성", description = "새로운 근무 일정 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "일정 생성 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "data": [
                                {
                                    "workScheduleDetailId": 1,
                                    "workType": "D",
                                    "workDate": "2024-02-01T09:00:00"
                                },
                                {
                                    "workScheduleDetailId": 1,
                                    "workType": "D",
                                    "workDate": "2024-02-02T09:00:00"
                                }
                            ]
                        }
                        """))),
            @ApiResponse(responseCode = "400", description = "필수 항목이 누락되었습니다.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "400",
                            "message": "필수 항목이 누락되었습니다."
                        }
                        """))),
            @ApiResponse(responseCode = "401", description = "토큰이 없는 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "401",
                            "message": "유효하지 않은 토큰입니다."
                        }
                        """))),
            @ApiResponse(responseCode = "401", description = "토큰이 만료된 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "401",
                            "message": "만료된 토큰입니다."
                        }
                        """))),
            @ApiResponse(responseCode = "401", description = "토큰에서 userId를 추출할 수 없는 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "401",
                            "message": "필수 사용자 데이터가 누락되었습니다."
                        }
                        """))),
            @ApiResponse(responseCode = "500", description = "서버에서 예외가 발생한 경우",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "status": "500",
                            "message": "서버 내부 오류가 발생했습니다."
                        }
                        """))),
    })
    @PostMapping("/work/create")
    public ResponseEntity<List<WorkScheduleResponseDto>> addWorkSchedules(
            @Parameter(description = "추가할 일정 정보", required = true,
                schema = @Schema(example = """
                    [
                        {
                            "workScheduleDetailId": 1,
                            "workType": "D",
                            "workDate": "2024-02-01T09:00:00"
                        },
                        {
                            "workScheduleDetailId": 1,
                            "workType": "D",
                            "workDate": "2024-02-02T09:00:00"
                        }
                    ]
                    """))
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

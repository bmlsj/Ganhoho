package com.ssafy.ganhoho.domain.schedule.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalScheduleResponseDto {
    private Long scheduleId;
    private Long memberId;
    private List<ScheduleDetailDto> details;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDetailDto {
        private Long detailId;
        private LocalDateTime startDt;
        private LocalDateTime endDt;
        private String scheduleTitle;
        private String scheduleColor;
        private Boolean isTimeSet;
        private Boolean isPublic;
    }
}
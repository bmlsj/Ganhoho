package com.ssafy.ganhoho.domain.schedule.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalScheduleRequestDto {
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private String scheduleTitle;
    private String scheduleColor;
    private Boolean isTimeSet;
    private Boolean isPublic;
}
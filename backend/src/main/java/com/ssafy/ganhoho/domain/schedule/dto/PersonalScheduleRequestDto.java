package com.ssafy.ganhoho.domain.schedule.dto;

import com.google.type.DateTime;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

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
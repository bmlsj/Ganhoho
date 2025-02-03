package com.ssafy.ganhoho.domain.schedule.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalScheduleRequestDto {
    private Date startDt;
    private Date endDt;
    private String scheduleTitle;
    private String scheduleColor;
    private Boolean isTimeSet;
    private Boolean isPublic;
}
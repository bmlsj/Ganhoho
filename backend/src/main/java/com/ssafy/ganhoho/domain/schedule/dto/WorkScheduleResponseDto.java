package com.ssafy.ganhoho.domain.schedule.dto;

import com.ssafy.ganhoho.domain.schedule.entity.WorkType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkScheduleResponseDto {

    private WorkType workType;
    private Date workDate;
    //s
} 
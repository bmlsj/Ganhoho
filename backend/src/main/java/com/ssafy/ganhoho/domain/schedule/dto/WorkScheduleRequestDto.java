package com.ssafy.ganhoho.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.ganhoho.domain.schedule.entity.WorkType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkScheduleRequestDto {
    private Long memberId;
    private Long workScheduleDetailId;
    private WorkType workType;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date workDate;
}
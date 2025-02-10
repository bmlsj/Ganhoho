package com.ssafy.ganhoho.domain.schedule.dto;

import com.ssafy.ganhoho.domain.schedule.entity.WorkType;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkScheduleResponseDto {

    private WorkType workType;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime workDate;
    //s
} 
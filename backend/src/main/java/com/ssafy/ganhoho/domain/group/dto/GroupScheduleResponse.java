package com.ssafy.ganhoho.domain.group.dto;

import com.ssafy.ganhoho.domain.schedule.entity.WorkType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupScheduleResponse {
    private Long memberId;
    private String name;
    private String loginId;
    private String hospital;
    private String ward;
    private List<ScheduleInfo> schedules;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ScheduleInfo {
        private WorkType workType;
        private LocalDateTime workDate;
    }
}

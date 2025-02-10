package com.ssafy.ganhoho.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_schedule")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 그룹 스케줄 정보
public class GroupSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_schedule_detail_id") // 추후 work_schedule_detail_id 로 수정해야함
    private Long workScheduleDetailId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "schedule_month", length = 10)
    private String scheduleMonth;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

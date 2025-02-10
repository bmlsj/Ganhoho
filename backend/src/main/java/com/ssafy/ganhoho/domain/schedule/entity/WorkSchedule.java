package com.ssafy.ganhoho.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "work_schedule")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workScheduleId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;


    @Column(name = "work_schedule_detail_id", nullable = false)
    private Long workScheduleDetailId;  // 필드명 수정

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false)
    private WorkType workType;

    @Column(name = "work_date", nullable = false)
    private Date workDate;
    //s
}
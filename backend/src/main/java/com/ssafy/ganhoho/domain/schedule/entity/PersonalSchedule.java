package com.ssafy.ganhoho.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "personal_schedule")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 추가적인 필드가 필요하면 여기에 정의
}
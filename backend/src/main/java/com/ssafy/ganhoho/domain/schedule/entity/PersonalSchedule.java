package com.ssafy.ganhoho.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;


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

    @Column(name = "is_public")
    private Boolean isPublic;

    @OneToMany(mappedBy = "personalSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleDetail> scheduleDetails = new ArrayList<>();

    // 추가적인 필드가 필요하면 여기에 정의
}
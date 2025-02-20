package com.ssafy.ganhoho.domain.schedule.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "schedule_detail")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ScheduleDetail {
    //ㄴㅇㄹ
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private PersonalSchedule personalSchedule;

    @Column(name = "start_dt", nullable = false)
    private LocalDateTime startDt;

    @Column(name = "end_dt")
    private LocalDateTime endDt;

    @Column(name = "schedule_title")
    private String scheduleTitle;

    @Column(name = "schedule_color")
    private String scheduleColor;

    @Column(name = "is_time_set")
    private Boolean isTimeSet;

    // 추가적인 필드가 필요하면 여기에 정의
}

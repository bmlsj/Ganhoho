package com.ssafy.ganhoho.domain.schedule.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "schedule_detail")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "start_dt", nullable = false)
    private Date startDt;

    @Column(name = "end_dt")
    private Date endDt;

    private String scheduleTitle;
    private String scheduleColor;
    private Boolean isTimeSet;
    private Boolean isPublic;

    // 추가적인 필드가 필요하면 여기에 정의
}
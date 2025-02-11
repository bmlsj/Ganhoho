package com.ssafy.ganhoho.domain.schedule.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "ocr_schedule")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OCRSchedule {
    @Id
    private String id; // MongoDB _id 필드와 매핑

    private String name;
    private int year;
    private int month;

    @Field(name = "schedule_data")
    private List<ScheduleDay> scheduleData;

    private Long memberId;

    // 생성시간 생성해놓기
    @CreatedDate
    @Field(name = "created_at")
    private LocalDateTime createdAt; //생성시점에 필요

    @Data
    @Builder
    public static class ScheduleDay {
        private int day;
        private String type;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}

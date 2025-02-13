package com.ssafy.ganhoho.domain.schedule.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "ocr_schedule")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OCRSchedule implements Persistable<String> {
    @Id
    private String id; // 몽고db 문서 식별자

    // 해당 스케줄 소유자
    private Long memberId;

    private String name;

    private int year;

    private int month;

    //상세 데이터
    @Field(name = "schedule_data")
    private List<ScheduleDay> scheduleData;

    @CreatedDate
    @Field(name = "created_at")
    @Indexed(expireAfter = "30d") // 30일 후 자동 삭제
    private LocalDateTime createdAt;

    @Getter
    @Builder
    public static class ScheduleDay {
        //근무 일자
        private int day;

        //근무타입 (D,E,N,OF)
        private String type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
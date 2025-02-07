package com.ssafy.ganhoho.domain.notification.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "notification")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification implements Persistable<String> {
    @Id
    private String id;

    private String message;
    private String title;
    private int type;
    private Long memberId;

    @CreatedDate
    @Indexed(name = "ttl_index", expireAfter = "1m") // 5분 데이터 유지
    @Field("created_at")
    private LocalDateTime createdAt;

    @Version
    private Long version;

    @Override
    public boolean isNew() {
        // createDate가 null이면 새로운 객체로 판별
        return createdAt == null;
    }
}

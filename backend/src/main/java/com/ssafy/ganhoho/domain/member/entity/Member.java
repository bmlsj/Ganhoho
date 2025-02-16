package com.ssafy.ganhoho.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="member")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "login_id")
    private String loginId;
    private String password;
    private String name;
    private String hospital;
    private String ward;

    @Column(name = "hospital_lat")
    private Double hospitalLat;

    @Column(name = "hospital_lng")
    private Double hospitalLng;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "app_fcm_token")
    private String appFcmToken;
    @Column(name = "watch_fcm_token")
    private String watchFcmToken;
}

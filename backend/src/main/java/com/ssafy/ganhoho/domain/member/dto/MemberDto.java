package com.ssafy.ganhoho.domain.member.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="member")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String loginId;
    private String password;
    private String name;
    private String hospital;
    private String ward;
    @Column(insertable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String appFcmToken;
    private String watchFcmToken;
}

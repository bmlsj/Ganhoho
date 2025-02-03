package com.ssafy.ganhoho.domain.auth.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {
    private String loginId;
    private String password;
    private String fcmToken;
    private int deviceType;
}

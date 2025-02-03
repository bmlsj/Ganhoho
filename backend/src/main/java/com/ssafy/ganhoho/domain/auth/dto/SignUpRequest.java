package com.ssafy.ganhoho.domain.auth.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequest {

    private String loginId;
    private String password;
    private String name;
    private String hospital;
    private String ward;
    private String fcmToken;
    private int deviceType; // 0이 앱, 1이 워치

}
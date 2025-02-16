package com.ssafy.ganhoho.domain.auth.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponse {
    private Long memberId;
    private String loginId;
    private String name;
    private String hospital;
    private String ward;
    private Double hospitalLng;
    private Double hospitalLat;
    private String accessToken;
    private String refreshToken;
}

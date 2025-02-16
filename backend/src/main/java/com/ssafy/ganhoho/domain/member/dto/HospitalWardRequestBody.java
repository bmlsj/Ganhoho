package com.ssafy.ganhoho.domain.member.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HospitalWardRequestBody {
    private String hospital;
    private String ward;
    private Double hospitalLat;
    private Double hospitalLng;
}

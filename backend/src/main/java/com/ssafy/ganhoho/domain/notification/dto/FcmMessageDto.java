package com.ssafy.ganhoho.domain.notification.dto;

import lombok.*;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmMessageDto {
    private String token;
    private Map<String, String> data;
}

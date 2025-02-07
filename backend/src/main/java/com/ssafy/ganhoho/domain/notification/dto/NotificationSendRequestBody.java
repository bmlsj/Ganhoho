package com.ssafy.ganhoho.domain.notification.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSendRequestBody {
    private String message;
    private String title;
    private int type;
}

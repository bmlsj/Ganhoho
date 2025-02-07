package com.ssafy.ganhoho.domain.notification.dto;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSubscribeRequestBody {
    private boolean isSubscribed;
}

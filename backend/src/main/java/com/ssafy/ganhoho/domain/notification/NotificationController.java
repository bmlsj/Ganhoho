package com.ssafy.ganhoho.domain.notification;

import com.ssafy.ganhoho.domain.notification.dto.NotificationSendRequestBody;
import com.ssafy.ganhoho.domain.notification.dto.NotificationSubscribeRequestBody;
import com.ssafy.ganhoho.domain.notification.service.NotificationService;
import com.ssafy.ganhoho.domain.notification.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ssafy.ganhoho.global.auth.SecurityUtil.getCurrentMemberId;

@RestController
@Slf4j
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/subscription")
    public void changeNotificationSubscription(@RequestBody NotificationSubscribeRequestBody notificationSubscribeRequestBody) {
        // 멤버의 알림 구독 여부를 변경하는 함수
        Long memberId = getCurrentMemberId();
        notificationService.changeSubscription(memberId, notificationSubscribeRequestBody.isSubscribed());
    }

    @PostMapping("/button-pattens")
    public void sendNotification(@RequestBody NotificationSendRequestBody notificationSendRequestBody) {
        notificationService.saveNotification(notificationSendRequestBody);
    }
}

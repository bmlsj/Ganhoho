package com.ssafy.ganhoho.domain.notification;

import com.ssafy.ganhoho.domain.notification.dto.NotificationDto;
import com.ssafy.ganhoho.domain.notification.dto.NotificationSubscribeRequestBody;
import com.ssafy.ganhoho.domain.notification.service.NotificationService;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ssafy.ganhoho.global.auth.SecurityUtil.getCurrentMemberId;

@RestController
@Slf4j
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Value("${iot.token}")
    private String iotToken;

    @PostMapping("/subscription")
    public void changeNotificationSubscription(@RequestBody NotificationSubscribeRequestBody notificationSubscribeRequestBody) {
        // 멤버의 알림 구독 여부를 변경하는 함수
        Long memberId = getCurrentMemberId();
        notificationService.changeSubscription(memberId, notificationSubscribeRequestBody.getIsSubscribed());
    }

    @PostMapping("/button-patterns")
    public void sendNotification(@RequestHeader("Authorization") String data, @RequestBody NotificationDto notificationDto) {
        log.info("button patterns access : {}",iotToken);
        if(data.equals(iotToken) == false) throw new CustomException(ErrorCode.INVALID_PI_TOKEN);
        notificationService.sendNotification(notificationDto);
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        Long memberId = getCurrentMemberId();
        return ResponseEntity.ok(notificationService.getNotifications(memberId));
    }
}

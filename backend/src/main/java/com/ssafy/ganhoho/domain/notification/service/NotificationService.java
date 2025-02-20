package com.ssafy.ganhoho.domain.notification.service;

import com.ssafy.ganhoho.domain.notification.dto.NotificationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {
    void changeSubscription(Long memberId, Boolean isSubscribed);

    List<NotificationDto> getNotifications(Long memberId);

    void sendNotification(NotificationDto notificationSendRequestBody);
}

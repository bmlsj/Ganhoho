package com.ssafy.ganhoho.domain.notification.service;

import com.ssafy.ganhoho.domain.notification.dto.NotificationSendRequestBody;
import com.ssafy.ganhoho.domain.notification.entity.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {
    void changeSubscription(Long memberId, Boolean isSubscribe);

    List<Notification> getAllNotifications(Long memberId);

    void saveNotification(NotificationSendRequestBody notificationSendRequestBody);
}

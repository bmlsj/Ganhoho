package com.ssafy.ganhoho.data.model.response.notification

import com.ssafy.ganhoho.data.model.dto.notification.Notification

data class NotificationResponse (
    val notifications: List<Notification>
)
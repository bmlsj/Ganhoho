package com.ssafy.ganhoho.presentation.notification

import com.ssafy.ganhoho.presentation.RetrofitInstance

class NotificationRepository {
    private val notificationService = RetrofitInstance.getInstance().create(NotificationService::class.java)

    // 알림 기록 조회
    suspend fun getNotifications(token: String) = notificationService.getNotifications("Bearer $token")
}
package com.ssafy.ganhoho.presentation.notification

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header


interface NotificationService {

    // 알림 기록 조회
    @GET("api/notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ) : List<Notification>

}
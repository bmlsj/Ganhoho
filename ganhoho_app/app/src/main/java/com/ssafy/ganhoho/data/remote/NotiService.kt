package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.data.model.dto.notification.Notification
import com.ssafy.ganhoho.data.model.response.notification.NotificationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface NotiService {

    // 알림 기록 조회
    @GET("api/noifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ) : Response<List<Notification>>

}
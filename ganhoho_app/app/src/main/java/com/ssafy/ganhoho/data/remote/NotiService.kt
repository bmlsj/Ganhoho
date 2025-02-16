package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.data.model.dto.notification.Notification
import com.ssafy.ganhoho.data.model.response.notification.NotificationResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface NotiService {

    // 알림 기록 조회
    @GET("api/notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ) : Response<List<Notification>>

    @POST("api/notifications/subscription")
    suspend fun changeSubscription(
        @Header("Authorization") token: String,
        @Body body : RequestBody
    ) : Response<ResponseBody>

}
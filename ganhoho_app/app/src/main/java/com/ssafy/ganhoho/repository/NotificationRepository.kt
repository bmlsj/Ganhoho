package com.ssafy.ganhoho.repository

import android.util.Log
import com.ssafy.ganhoho.data.model.dto.notification.Notification
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.model.response.notification.NotificationResponse
import com.ssafy.ganhoho.data.remote.RetrofitUtil

class NotificationRepository {

    // 알림 기록 조회
    suspend fun getNotifications(
        token: String
    ): Result<List<Notification>> {
        return try {
            val response = RetrofitUtil.notiService.getNotifications("Bearer $token")
            Log.d("noti", "getNotifications error: ${response.errorBody()?.string()}")
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
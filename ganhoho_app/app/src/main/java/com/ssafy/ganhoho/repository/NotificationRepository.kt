package com.ssafy.ganhoho.repository

import android.util.Log
import com.google.gson.JsonObject
import com.ssafy.ganhoho.data.model.dto.notification.Notification
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.model.response.notification.NotificationResponse
import com.ssafy.ganhoho.data.remote.RetrofitUtil
import okhttp3.RequestBody
import okhttp3.ResponseBody

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

    // 알림 구독 여부 수정
    suspend fun changeSubscription(
        token: String,
        body: RequestBody
    ): Result<ResponseBody> {
        return try {
            val response = RetrofitUtil.notiService.changeSubscription("Bearer $token", body)
            Log.d("noti", "changeNotification: ${response.errorBody()?.string()}")
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // 알림 구독 여부 수정
}
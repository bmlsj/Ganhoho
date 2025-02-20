package com.ssafy.ganhoho.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.dto.notification.Notification
import com.ssafy.ganhoho.data.model.response.notification.NotificationResponse
import com.ssafy.ganhoho.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotifiViewModel : ViewModel() {

    private val repository = NotificationRepository()

    private val _notifications = MutableStateFlow<Result<List<Notification>>?>(null)
    val notifcations: StateFlow<Result<List<Notification>>?> = _notifications

    // 알림 기록 조회
    fun getNotifications(token: String) {
        viewModelScope.launch {
            val response = repository.getNotifications(token)
            _notifications.value = response
            Log.d("noti", "$response")
        }
    }

}
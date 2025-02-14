package com.ssafy.ganhoho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.response.notification.NotificationResponse
import com.ssafy.ganhoho.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotifiViewModel : ViewModel() {

    private val repository = NotificationRepository()

    private val _notifications = MutableStateFlow<Result<NotificationResponse>?>(null)
    val notifcations: StateFlow<Result<NotificationResponse>?> = _notifications

    // 알림 기록 조회
    fun getNotifications(token: String) {
        viewModelScope.launch {
            val response = repository.getNotifications(token)
            _notifications.value = response
        }
    }

}
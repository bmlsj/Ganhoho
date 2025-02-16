package com.ssafy.ganhoho.presentation.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.presentation.UserDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "NotificationViewModel"
class NotificationViewModel : ViewModel() {
    private val notificationRepository = NotificationRepository()

    private val _notifications = MutableStateFlow<List<Notification>?>(null)
    val notifications: StateFlow<List<Notification>?> = _notifications

    fun getNotifications() = viewModelScope.launch {
        viewModelScope.launch {
            UserDataStore().getAccessToken()?.let {
                notificationRepository.getNotifications(it).let { response ->
                    _notifications.value = response
                }
            }
        }

    }
}
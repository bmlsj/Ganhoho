package com.ssafy.ganhoho.data.model.dto.auth

data class LoginRequest(
    val loginId: String,
    val password: String,
    val fcmToken: String?,
    val deviceType: Int = 0
)
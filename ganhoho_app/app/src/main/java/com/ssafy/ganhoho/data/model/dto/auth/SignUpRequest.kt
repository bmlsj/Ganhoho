package com.ssafy.ganhoho.data.model.dto.auth

data class SignUpRequest(
    val loginId: String,
    val password: String,
    val name: String,
    val hospital: String?,
    val ward: String?,
    val fcmToken: String?,
    val deviceType: Int = 0
)
package com.ssafy.ganhoho.data.model.dto.auth

// 로그인 요청
data class LoginRequest(
    val loginId: String,   // 로그인 ID
    val password: String,  // 비밀번호
    val fcmToken: String? = "", // FCM 토큰 (선택 사항)
    val deviceType: Int = 0   // 기기 타입
)
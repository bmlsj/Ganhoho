package com.ssafy.ganhoho.data.model.dto.auth

// 회원 가입 요청
data class SignUpRequest(
    val loginId: String,   // 로그인 ID
    val password: String,  // 비밀번호
    val name: String,      // 사용자 이름
    val hospital: String? = null,  // 병원 정보
    val ward: String? = null,      // 병동 정보
    val fcmToken: String? = null, // FCM 토큰 (선택 사항)
    val deviceType: Int = 0,   // 기기 타입(0: 폰, 1: 워치)
    val hospitalLat: Double? = null,
    val hospitalLng: Double? = null
)

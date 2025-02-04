package com.ssafy.ganhoho.data.model.dto.member

// 회원 가입 요청
data class SignUpRequest(
    val loginId: String,   // 로그인 ID
    val password: String,  // 비밀번호
    val name: String,      // 사용자 이름
    val hospital: String,  // 병원 정보
    val ward: String,      // 병동 정보
    val fcmToken: String?, // FCM 토큰 (선택 사항)
    val deviceType: Int    // 기기 타입
)

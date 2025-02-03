package com.ssafy.ganhoho.data.model.dto.member

// 회원 정보 DTO
data class MemberDTO(
    val memberId: Long,    // 멤버 ID
    val loginId: String,   // 로그인 ID
    val name: String,      // 사용자 이름
    val hospital: String?, // 병원 (선택 사항)
    val ward: String?,     // 병동 (선택 사항)
//    val fcmToken: String?, // FCM 토큰 (선택 사항)
//    val deviceType: Int    // 기기 타입 (1: Android, 2: 워치)
)

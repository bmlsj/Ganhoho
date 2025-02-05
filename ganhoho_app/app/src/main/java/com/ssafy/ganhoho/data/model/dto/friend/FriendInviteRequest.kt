package com.ssafy.ganhoho.data.model.dto.friend

// 친구 요청 승인 및 거절 Body
data class FriendInviteRequest(
    val requestStatus: String,  // "accepted" 또는 "declined"
)
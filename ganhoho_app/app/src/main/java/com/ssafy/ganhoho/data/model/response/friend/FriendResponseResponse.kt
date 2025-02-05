package com.ssafy.ganhoho.data.model.response.friend


data class FriendResponseResponse(
    val friendId: Long,
    val requestStatus: String  // accepted or declined
)

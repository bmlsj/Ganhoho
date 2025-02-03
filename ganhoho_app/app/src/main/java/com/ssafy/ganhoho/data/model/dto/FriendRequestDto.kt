package com.ssafy.ganhoho.data.model.dto

data class FriendRequestDto(
    val friendRequestId: Long,
    val friendLoginId: String,
    val name: String,
    val hospital: String,
    val ward: String,
    val requestStatus: String
)

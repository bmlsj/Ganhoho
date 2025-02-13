package com.ssafy.ganhoho.data.model.response.friend

data class FriendPersonalResponse(
    val scheduleId: Long,
    val startDt: String,
    val endDt: String,
    val scheduleTitle: String,
    val scheduleColor: String
)

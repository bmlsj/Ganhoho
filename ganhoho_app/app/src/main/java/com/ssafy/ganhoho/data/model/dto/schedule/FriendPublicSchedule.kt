package com.ssafy.ganhoho.data.model.dto.schedule

// 친구 공개 일정 스케쥴
data class FriendPublicSchedule(
    val scheduleId: Long,
    val startDt: String,
    val endDt: String,
    val scheduleTitle: String,
    val scheduleColor: String
)


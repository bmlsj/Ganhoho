package com.ssafy.ganhoho.data.model.dto.schedule

import java.time.LocalDateTime

// 친구 근무 스케쥴
data class FriendSchedule(
    val startDt: LocalDateTime,  // 시작 날짜
    val endDt: LocalDateTime,  // 종료 날짜
    val title: String,  // 일정 제목
    val color: String,  // 일정 색상(색상 코드)
) {
    private var scheduleId = -1L

    constructor(
        scheduleId: Long,
        startDt: LocalDateTime,
        endDt: LocalDateTime,
        title: String,
        color: String
    ) : this(startDt, endDt, title, color) {
        this.scheduleId = scheduleId
    }
}


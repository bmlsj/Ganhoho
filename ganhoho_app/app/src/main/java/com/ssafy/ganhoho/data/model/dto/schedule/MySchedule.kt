package com.ssafy.ganhoho.data.model.dto.schedule

import kotlinx.datetime.LocalDateTime

data class MySchedule(
    val startDt: LocalDateTime,  // 시작 날짜
    val endDt: LocalDateTime,  // 종료 날짜
    val title: String,  // 일정 제목
    val color: String,  // 일정 색상(색상 코드)
    val isPublic: Boolean,   // 공개 여부
    val isTimeSet: Boolean  // 시간 설정 여부
) {

    private var scheduleId = -1L

    constructor(
        scheduleId: Long,
        startDt: LocalDateTime,
        endDt: LocalDateTime,
        title: String,
        color: String,
        isPublic: Boolean,
        isTimeSet: Boolean
    ) : this(startDt, endDt, title, color, isPublic, isTimeSet) {
        this.scheduleId = scheduleId
    }
}

package com.ssafy.ganhoho.data.model.dto.schedule

import java.time.LocalDateTime

data class MyScheduleRequest(
    val startDt: LocalDateTime,
    val endDt: LocalDateTime,
    val scheduleTitle: String,
    val scheduleColor: String,
    val isPublic: Boolean,
    val isTimeSet: Boolean
)

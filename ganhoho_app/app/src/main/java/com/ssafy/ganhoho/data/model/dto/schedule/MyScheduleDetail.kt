package com.ssafy.ganhoho.data.model.dto.schedule

import java.time.LocalDateTime

data class MyScheduleDetail(
    val detailId: Long,
    val startDt: String,
    val endDt: String,
    val scheduleTitle: String,
    val scheduleColor: String,
    val isTimeSet: Boolean,
    val isPublic: Boolean
)
package com.ssafy.ganhoho.data.model.dto.group

import kotlinx.datetime.LocalDateTime

data class WorkScheduleDto (
    val workDate: LocalDateTime,
    val workType: String
)
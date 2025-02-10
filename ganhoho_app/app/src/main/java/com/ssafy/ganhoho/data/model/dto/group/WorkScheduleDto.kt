package com.ssafy.ganhoho.data.model.dto.group

import java.time.LocalDateTime

data class WorkScheduleDto (
    val workDate: LocalDateTime,
    val workType: String
)
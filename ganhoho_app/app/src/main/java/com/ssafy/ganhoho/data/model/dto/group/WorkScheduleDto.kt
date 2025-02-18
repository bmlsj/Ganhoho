package com.ssafy.ganhoho.data.model.dto.group

data class WorkScheduleDto(
    val workDate: String,
    val workType: String? // ENUM('D', 'N', 'E', 'OF')
)


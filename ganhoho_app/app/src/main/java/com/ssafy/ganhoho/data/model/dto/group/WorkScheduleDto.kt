package com.ssafy.ganhoho.data.model.dto.group

data class WorkScheduleDto(
    val workDate: java.time.LocalDateTime,
    val workType: String // ENUM('D', 'N', 'E', 'OF')
)


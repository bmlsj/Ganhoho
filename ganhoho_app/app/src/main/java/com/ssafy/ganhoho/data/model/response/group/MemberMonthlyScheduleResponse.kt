package com.ssafy.ganhoho.data.model.response.group

import com.ssafy.ganhoho.data.model.dto.group.WorkScheduleDto

data class MemberMonthlyScheduleResponse(
    val memberId: Long,
    val name: String,
    val loginId: String,
    val hospital: String?,
    val ward: String?,
    val schedules: List<WorkScheduleDto?>
)


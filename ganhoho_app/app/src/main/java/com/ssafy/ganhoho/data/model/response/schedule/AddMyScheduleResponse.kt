package com.ssafy.ganhoho.data.model.response.schedule

import com.ssafy.ganhoho.data.model.dto.schedule.MyScheduleDetail

data class AddMyScheduleResponse(
    val scheduleId: Long,
    val memberId: Long,
    val details: List<MyScheduleDetail>
)
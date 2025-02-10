package com.ssafy.ganhoho.data.model.dto.schedule

import com.google.gson.annotations.SerializedName

data class MySchedule(
    @SerializedName("scheduleId") val scheduleId: Long,
    @SerializedName("startDt") val startDt: String,  // 시작 날짜
    @SerializedName("endDt") val endDt: String,  // 종료 날짜
    val title: String,  // 일정 제목
    val color: String,  // 일정 색상(색상 코드)
    val isPublic: Boolean,   // 공개 여부
    val isTimeSet: Boolean  // 시간 설정 여부
)
package com.ssafy.ganhoho.data.model.dto.schedule

import androidx.compose.ui.graphics.Color

//data class TimelineEvent(
//    val startTime: String, // 시작 시간
//    val title: String,     // 이벤트 제목
//    val dateRange: String, // 날짜 범위
//    val color: Color,      // 점 색상
//    val isLast: Boolean    // 마지막 항목 여부
//)

data class TimelineEvent(
    val startTime: String, // 시작 시간
    val title: String,     // 이벤트 제목
    val dateRange: String, // 날짜 범위
    val color: Color,      // 점 색상
    val isLast: Boolean,   // 마지막 항목 여부
    val mySchedule: MySchedule // ⬅ 원본 일정 데이터 포함
)
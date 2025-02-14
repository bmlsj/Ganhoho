package com.ssafy.ganhoho.util

import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ISO-8601 형식의 날짜를 LocalDateTime으로 변환하는 함수
fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
}

fun String.toLocalDate(): LocalDate {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")  // ISO 8601 형식
        LocalDateTime.parse(this, formatter).toLocalDate()  // LocalDateTime으로 파싱 후 날짜만 반환
    } catch (e: Exception) {
        Log.e("DateError", "날짜 변환 실패: $this", e)
        LocalDate.MIN  // 기본 값 반환하여 오류 방지
    }
}

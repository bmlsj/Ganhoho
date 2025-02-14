package com.ssafy.ganhoho.util

import android.util.Log
import androidx.compose.ui.graphics.Color

fun parsedColor(colorString: String): Color {

    return try {
        // ✅ #AARRGGBB 형식이면 #RRGGBB로 변환
        if (colorString.length == 9) {
            val rgbColor = "#${colorString.substring(3)}" // ✅ 앞의 #FF 제거
            Color(android.graphics.Color.parseColor(rgbColor))
        } else {
            Color(android.graphics.Color.parseColor(colorString)) // ✅ 기존 #RRGGBB 처리
        }
    } catch (e: IllegalArgumentException) {
        Log.e("ColorError", "색상 코드 변환 실패: $colorString", e)
        Color.Gray // ✅ 기본 색상 적용
    }


}
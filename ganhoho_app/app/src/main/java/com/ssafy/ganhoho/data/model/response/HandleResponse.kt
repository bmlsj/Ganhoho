package com.ssafy.ganhoho.data.model.response

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response

inline fun <reified T> handleResponse(response: Response<T>): Result<T> {
    return if (response.isSuccessful) {
        response.body()?.let {
            Result.success(it) // ✅ 요청 성공 -> 결과 반환
        } ?: Result.failure(Exception("response is empty")) // ❌ 응답이 비어있는 경우
    } else {
        val errorBody = response.errorBody()?.string()

        // 🔹 errorBody가 null 또는 비어있으면 기본 메시지 반환
        if (errorBody.isNullOrEmpty()) {
            return Result.failure(Exception("request fail: unknown error (empty response)"))
        }

        Log.e("API_ERROR", "❌ 서버 응답 에러: $errorBody") // 🔥 JSON인지 확인

        return try {
            // 🔹 JSON 변환 시도
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)

            // 🔹 `status`와 `message`가 정상적으로 들어왔는지 확인
            val status = errorResponse?.status ?: "Unknown Status"
            val errorMessage = errorResponse?.message ?: "Unknown error from server"

            Result.failure(Exception("request fail ($status): $errorMessage"))
        } catch (e: JsonSyntaxException) {
            // ❌ JSON 파싱 실패 -> 원본 그대로 출력
            Result.failure(Exception("request fail: ${errorBody ?: "Unknown error"}"))
        }
    }
}

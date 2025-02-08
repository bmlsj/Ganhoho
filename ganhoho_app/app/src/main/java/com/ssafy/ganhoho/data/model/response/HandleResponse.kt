package com.ssafy.ganhoho.data.model.response

import com.google.gson.Gson
import retrofit2.Response

inline fun <reified T> handleResponse(response: Response<T>): Result<T> {
    return if (response.isSuccessful) {
        response.body()?.let {
            Result.success(it) // ✅ 요청 성공 -> 결과 반환
        } ?: Result.failure(Exception("response is empty")) // ❌ 응답이 비어있는 경우
    } else {
        val errorBody = response.errorBody()?.string() // 🔹 에러 메시지 가져오기
        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
        Result.failure(Exception("request fail: ${errorResponse.error}")) // ❌ 에러 메시지 반환
    }
}
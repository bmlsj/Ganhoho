package com.ssafy.ganhoho.repository

import android.content.Context
import android.util.Log
import com.ssafy.ganhoho.data.model.dto.auth.LoginRequest
import com.ssafy.ganhoho.data.model.dto.auth.SignUpRequest
import com.ssafy.ganhoho.data.model.response.auth.LoginResponse
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.remote.RetrofitUtil

class AuthRepository {

    // 로그인
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = RetrofitUtil.authService.login(loginRequest)
            Log.d("AuthRepository", "🔹 요청 성공 여부: ${response.isSuccessful}")
            Log.d("AuthRepository", "🔹 HTTP 코드: ${response.code()}")
            Log.d("AuthRepository", "🔹 응답 메시지: ${response.message()}")

            if (response.isSuccessful) {
                Log.d("AuthRepository", "✅ 로그인 성공: ${response.body()}")
            } else {
                Log.e("AuthRepository", "❌ 로그인 실패")
                Log.e("AuthRepository", "🔹 에러 바디: ${response.errorBody()?.string()}")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 회원 가입
    suspend fun signUp(signUpRequest: SignUpRequest): Result<Boolean> {
        return try {
            val response = RetrofitUtil.authService.signUp(signUpRequest)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 아이디 중복 확인 요청
    suspend fun isUsedId(loginId: String): Result<Boolean> {
        return try {
            val response = RetrofitUtil.authService.isUsedId(loginId)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 회원 탈퇴
    suspend fun withdrawalMember(token: String, context: Context): Result<Void> {
        return try {
            val response = RetrofitUtil.authService.withdrawalMember(token)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
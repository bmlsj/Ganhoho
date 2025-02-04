package com.ssafy.ganhoho.repository

import com.ssafy.ganhoho.data.model.dto.member.LoginRequest
import com.ssafy.ganhoho.data.model.dto.member.SignUpRequest
import com.ssafy.ganhoho.data.model.response.LoginResponse
import com.ssafy.ganhoho.data.model.response.SignUpResponse
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.model.remote.RetrofitUtil

class MemberRepository {

    // 로그인
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = RetrofitUtil.memberService.login(loginRequest)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 회원 가입
    suspend fun signUp(signUpRequest: SignUpRequest): Result<String> {
        return try {
            val response = RetrofitUtil.memberService.signUp(signUpRequest)
            handleResponse(response) // 🔹 공통 응답 처리 함수 호출
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 아이디 중복 확인 요청
    suspend fun isUsedId(loginId: Long): Result<Boolean> {
        return try {
            val response = RetrofitUtil.memberService.isUsedId(loginId)
            handleResponse(response) // 🔹 공통 응답 처리 함수 호출
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
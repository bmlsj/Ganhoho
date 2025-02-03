package com.ssafy.ganhoho.repository

import com.google.gson.Gson
import com.ssafy.ganhoho.data.model.dto.member.LoginRequest
import com.ssafy.ganhoho.data.model.dto.member.MemberDTO
import com.ssafy.ganhoho.data.model.dto.member.SignUpRequest
import com.ssafy.ganhoho.data.model.response.ErrorResponse
import com.ssafy.ganhoho.data.model.response.LoginResponse
import com.ssafy.ganhoho.data.model.response.SignUpResponse
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.model.service.RetrofilUtil
import retrofit2.Response

class MemberRepository {

    // 로그인
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = RetrofilUtil.memberService.login(loginRequest)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 회원 가입
    suspend fun signUp(signUpRequest: SignUpRequest): Result<SignUpResponse> {
        return try {
            val response = RetrofilUtil.memberService.signUp(signUpRequest)
            handleResponse(response) // 🔹 공통 응답 처리 함수 호출
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 아이디 중복 확인 요청
    suspend fun isUsedId(loginId: Long): Result<Boolean> {
        return try {
            val response = RetrofilUtil.memberService.isUsedId(loginId)
            handleResponse(response) // 🔹 공통 응답 처리 함수 호출
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
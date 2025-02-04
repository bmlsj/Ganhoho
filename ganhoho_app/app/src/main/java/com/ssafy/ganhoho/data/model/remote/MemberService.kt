package com.ssafy.ganhoho.data.model.remote

import com.ssafy.ganhoho.data.model.dto.member.LoginRequest
import com.ssafy.ganhoho.data.model.dto.member.SignUpRequest
import com.ssafy.ganhoho.data.model.response.LoginResponse
import com.ssafy.ganhoho.data.model.response.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface MemberService {

    // 회원 가입
    // @Headers("Content-Type: application/json")
    @POST("api/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<String>

    // 로그인
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // 아이디 존재 여부
    // true일 때 중복, false일때 사용 가능
    @GET("api/auth/duplicate-check")
    suspend fun isUsedId(@Query("loginId") id: Long): Response<Boolean>

}
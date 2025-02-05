package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.data.model.dto.member.MemberDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import kotlin.jvm.Throws

interface MemberService {

    // 회원 검색
    @GET("api/member/search")
    suspend fun searchFriend(
        @Header("Authorization") token: String,
        @Query("friendLoginId") friendLoginId: String
    ): Response<List<MemberDto>>

}
package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.data.model.dto.member.MemberDto
import com.ssafy.ganhoho.data.model.response.member.MyPageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import kotlin.jvm.Throws

interface MemberService {

    // 회원 검색
    @GET("api/members/search")
    suspend fun searchFriend(
        @Header("Authorization") token: String,
        @Query("friendLoginId") memberLoginId: String
    ): Response<List<MemberDto>>

    // 마이페이지 정보
    @GET("api/members/mypage")
    suspend fun getMyPageInfo(
        @Header("Authorization") token: String
    ) : Response<MyPageResponse>

}
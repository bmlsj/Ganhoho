package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.data.model.response.friend.FriendRequestResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface FriendService {

    // 친구 요청 목록 조회 API (Authorization 헤더 포함)
    @GET("api/friends/requests/list")
    suspend fun getFriendRequests(
        @Header("Authorization") token: String
    ): Response<FriendRequestResponse>

}
package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.data.model.dto.friend.FriendAddRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendApproveRequest
import com.ssafy.ganhoho.data.model.response.friend.FriendApproveResponse
import com.ssafy.ganhoho.data.model.dto.friend.FriendDto
import com.ssafy.ganhoho.data.model.dto.friend.FriendFavoriteRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendInviteDto
import com.ssafy.ganhoho.data.model.response.friend.FriendAddResponse
import com.ssafy.ganhoho.data.model.response.schedule.FriendPersonalResponse
import com.ssafy.ganhoho.data.model.response.schedule.FriendWorkResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendService {

    // 친구 목록 조회
    @GET("api/friends/list")
    suspend fun getFriendList(@Header("Authorization") token: String): Response<List<FriendDto>>

    // 친구 삭제
    @DELETE("api/friends/{friendId}")
    suspend fun deleteFriend(
        @Header("Authorization") token: String, @Path("friendId") friendId: Long
    ): Response<Long>

    // 친구 요청 목록 조회 API
    @GET("api/friends/requests/list")
    suspend fun getFriendInvite(
        @Header("Authorization") token: String
    ): Response<List<FriendInviteDto>>

    // 친구 요청 승인 및 거절
    @POST("api/friends/{friendId}/response")
    suspend fun respondToFriendInvite(
        @Header("Authorization") token: String,
        @Path("friendId") friendId: Long,
        @Body requestStatus: FriendApproveRequest
    ): Response<FriendApproveResponse>

    // 친구 추가
    @POST("api/friends/request")
    suspend fun addFriendList(
        @Header("Authorization") token: String,
        @Body friendLoginId: FriendAddRequest
    ): Response<FriendAddResponse>


    // 친구 즐겨찾기 수정
    @PATCH("api/friends/favorite")
    suspend fun updateFriendFavorite(
        @Header("Authorization") token: String, @Body request: FriendFavoriteRequest  // 즐겨찾기 수정 데이터
    ): Response<Boolean>

    // 친구 근무 스케쥴 조회
    @GET("api/schedules/work/{memberId}")
    suspend fun getFriendWorkSchedule(
        @Header("Authorization") token: String,
        @Path("memberId") memberId: Long
    ): Response<FriendWorkResponse>

    // 친구의 공개된 스케쥴
    @GET("api/schedules/personal/{memberId}")
    suspend fun getFriendPublicSchedule(
        @Header("Authorization") token: String,
        @Path("memberId") memberId: Long
    ): Response<FriendPersonalResponse>

}
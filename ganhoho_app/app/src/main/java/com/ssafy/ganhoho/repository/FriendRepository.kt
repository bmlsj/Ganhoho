package com.ssafy.ganhoho.repository

import android.util.Log
import com.ssafy.ganhoho.data.model.dto.friend.FriendAddRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendDto
import com.ssafy.ganhoho.data.model.dto.friend.FriendFavoriteRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendInviteRequest
import com.ssafy.ganhoho.data.model.response.friend.FriendInviteResponse
import com.ssafy.ganhoho.data.model.response.friend.FriendResponseResponse
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.remote.RetrofitUtil

class FriendRepository {

    // 친구 목록 조회
    suspend fun getFriendList(token: String): Result<List<FriendDto>> {
        return try {
            val response = RetrofitUtil.friendService.getFriendList("Bearer $token")
            Log.d("repo test", "test")
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 친구 삭제
    suspend fun deleteFriend(token: String, friendId: Long): Result<Long> {
        return try {
            val response = RetrofitUtil.friendService.deleteFriend("Bearer $token", friendId)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 친구 요청 목록 조회
    suspend fun getFriendInvite(token: String): Result<FriendInviteResponse> {
        return try {
            val response = RetrofitUtil.friendService.getFriendInvite("Bearer $token")
            Log.d("test", "토큰: $token")
            Log.d("test", "응답 코드: ${response.code()}")  // 응답 코드 확인
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("test", "네트워크 에러 발생: ${e.message}")
            Result.failure(e)
        }
    }

    // 친구 요청 승인 및 거절
    suspend fun respondToFriendInvite(token: String, friendId: Long, request: FriendInviteRequest)
            : Result<FriendResponseResponse> {
        return try {
            val response =
                RetrofitUtil.friendService.respondToFriendInvite("Bearer $token", friendId, request)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 친구 추가
    suspend fun addFriend(
        token: String,
        friendLoginId: FriendAddRequest
    ): Result<Boolean> {
        return try {
            val response = RetrofitUtil.friendService.addFriend("Bearer $token", friendLoginId)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 친구 즐겨찾기 수정
    suspend fun updateFriendFavorite(
        token: String,
        request: FriendFavoriteRequest
    ): Result<Boolean> {
        return try {
            val response = RetrofitUtil.friendService.updateFriendFavorite("Bearer $token", request)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
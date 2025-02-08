package com.ssafy.ganhoho.repository

import android.util.Log
import com.ssafy.ganhoho.data.model.dto.friend.FriendAddRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendApproveRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendDto
import com.ssafy.ganhoho.data.model.dto.friend.FriendFavoriteRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendInviteDto
import com.ssafy.ganhoho.data.model.response.friend.FriendAddResponse
import com.ssafy.ganhoho.data.model.response.friend.FriendApproveResponse
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.remote.RetrofitUtil
import retrofit2.Response

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
    suspend fun getFriendInvite(token: String): Result<List<FriendInviteDto>> {
        return try {
            val response = RetrofitUtil.friendService.getFriendInvite("Bearer $token")
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 친구 요청 승인 및 거절
    suspend fun respondToFriendInvite(token: String, friendId: Long, request: FriendApproveRequest)
            : Result<FriendApproveResponse> {
        return try {
            val response =
                RetrofitUtil.friendService.respondToFriendInvite("Bearer $token", friendId, request)

            handleResponse(response)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 친구 추가
    suspend fun addFriendList(
        token: String,
        friendLoginId: FriendAddRequest
    ): Result<FriendAddResponse> {
        return try {
            val response = RetrofitUtil.friendService.addFriendList("Bearer $token", friendLoginId)
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
//            if (response.isSuccessful) {
//                response.body()?.let {
//                    Result.success(it)  // ✅ 성공 시 정상적으로 데이터 반환
//                } ?: Result.failure(Exception("Response body is null"))
//            } else {
//                val errorBody = response.errorBody()?.string()
//                Log.e("FriendRepository", "Error Body: $errorBody") // 에러 로그 추가
//                Result.failure(Exception("Request failed: $errorBody")) // ❌ 실패 시 메시지 반환
//            }
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
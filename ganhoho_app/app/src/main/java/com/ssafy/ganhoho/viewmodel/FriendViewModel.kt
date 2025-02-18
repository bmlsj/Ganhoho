package com.ssafy.ganhoho.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.dto.friend.FriendAddRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendApproveRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendDto
import com.ssafy.ganhoho.data.model.dto.friend.FriendFavoriteRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendInviteDto
import com.ssafy.ganhoho.data.model.response.friend.FriendAddResponse
import com.ssafy.ganhoho.data.model.response.friend.FriendApproveResponse
import com.ssafy.ganhoho.data.model.response.schedule.FriendPersonalResponse
import com.ssafy.ganhoho.data.model.response.schedule.FriendWorkResponse
import com.ssafy.ganhoho.repository.FriendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FriendViewModel() : ViewModel() {

    // 레포지토리
    private val friendRepository = FriendRepository()

    // 친구 목록 조회 결과
    private val _friendList = MutableStateFlow<Result<List<FriendDto>>?>(
        Result.success(
            emptyList()
        )
    )
    val friendList: StateFlow<Result<List<FriendDto>>?> = _friendList

    // 친구 삭제 결과
    private val _deleteResult = MutableStateFlow<Result<Long>?>(null)
    val deleteResult: StateFlow<Result<Long>?> = _deleteResult

    // 친구 요청 목록 조회 결과
    private val _friendInviteList = MutableStateFlow<Result<List<FriendInviteDto>>?>(
        Result.success(
            emptyList()
        )
    )
    val friendInviteList: StateFlow<Result<List<FriendInviteDto>>?> = _friendInviteList

    // 친구 요청 승인/거절 결과
    private val _friendResponse = MutableStateFlow<Result<FriendApproveResponse>?>(null)
    val friendResponse: StateFlow<Result<FriendApproveResponse>?> = _friendResponse

    // 친구 추가 결과
    private val _addFriendResult = MutableStateFlow<Result<FriendAddResponse>?>(null)
    val addFriendResult: StateFlow<Result<FriendAddResponse>?> = _addFriendResult

    // 친구 즐겨찾기 수정 결과
    private val _updateFavoriteResult = MutableStateFlow<Result<Boolean>?>(null)
    val updateFavoriteResult: StateFlow<Result<Boolean>?> = _updateFavoriteResult

    // 친구 근무 일정 조회
    private val _friendWorkSchedule = MutableStateFlow<Result<FriendWorkResponse>?>(null)
    val friendWorkSchedule: StateFlow<Result<FriendWorkResponse>?> = _friendWorkSchedule

    // 친구 개인 일정 조회
    private val _friendPersonalSchedule = MutableStateFlow<Result<FriendPersonalResponse>?>(null)
    val friendPersonalResponse: StateFlow<Result<FriendPersonalResponse>?> = _friendPersonalSchedule

    // 친구 목록 조회
    fun getFriendList(token: String) {
        viewModelScope.launch {
            val result = friendRepository.getFriendList(token)
            Log.d("friend", "success $result")
            _friendList.value = result
        }
    }

    // 친구 삭제
    fun deleteFriend(token: String, friendId: Long) {
        viewModelScope.launch {
            val result = friendRepository.deleteFriend(token, friendId)
            _deleteResult.value = result
        }
    }

    // 친구 요청 목록 조회
    fun getFriendInvite(token: String) {
        viewModelScope.launch {
            val result = friendRepository.getFriendInvite(token)
            _friendInviteList.value = result
        }
    }

    // 친구 요청 승인 및 거절
    fun respondToFriendInvite(token: String, friendId: Long, request: FriendApproveRequest) {
        viewModelScope.launch {
            // val request = FriendApproveRequest(requestStatus = "ACCEPTED")
            val result = friendRepository.respondToFriendInvite(token, friendId, request)

            result.onSuccess { response ->
                Log.d("FriendViewModel", "친구 요청 승인 성공: $response")
                _friendResponse.value = Result.success(response)
            }.onFailure { error ->
                Log.e("FriendViewModel", "친구 요청 실패: ${error.message}")
                _friendResponse.value = Result.failure(error)
            }
        }
    }

    // 친구 추가
    fun addFriendList(token: String, friendLoginId: String) {
        viewModelScope.launch {

            val request = FriendAddRequest(friendLoginId)
            val result = friendRepository.addFriendList(token, request)
            Log.d("FriendAdd", "$friendLoginId $result")
            _addFriendResult.value = result // 실패 시 false 반환
        }
    }

    // 친구 즐겨찾기 수정
    fun updateFriendFavorite(token: String, friendMemberId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            val request = FriendFavoriteRequest(friendMemberId, isFavorite)
            val result = friendRepository.updateFriendFavorite(token, request)
            getFriendList(token)
            _updateFavoriteResult.value = result
        }
    }


    fun clearAddFriendResult() {
        _addFriendResult.value = null
    }

}

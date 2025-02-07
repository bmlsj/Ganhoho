package com.ssafy.ganhoho.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.dto.friend.FriendAddRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendDto
import com.ssafy.ganhoho.data.model.dto.friend.FriendFavoriteRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendInviteDto
import com.ssafy.ganhoho.data.model.response.friend.FriendResponseResponse
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
    private val _friendResponse = MutableStateFlow<Result<FriendResponseResponse>?>(null)
    val friendResponse: StateFlow<Result<FriendResponseResponse>?> = _friendResponse

    // 친구 추가 결과
    private val _addFriendResult = MutableStateFlow<Result<Boolean>?>(null)
    val addFriendResult: StateFlow<Result<Boolean>?> = _addFriendResult

    // 친구 즐겨찾기 수정 결과
    private val _updateFavoriteResult = MutableStateFlow<Result<Boolean>?>(null)
    val updateFavoriteResult: StateFlow<Result<Boolean>?> = _updateFavoriteResult

    // 친구 목록 조회
    fun getFriendList(token: String) {
        viewModelScope.launch {
            val result = friendRepository.getFriendList(token)
            Log.d("test", "success $result")
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
    fun respondToFriendInvite(token: String, friendId: Long, request: String) {
        viewModelScope.launch {
            val result = friendRepository.respondToFriendInvite(token, friendId, request)
            Log.d("respondToFriend", "$friendId $request")
            _friendResponse.value = result
        }
    }

    // 친구 추가
    fun addFriendList(token: String, friendLoginId: String) {
        viewModelScope.launch {
            val request = FriendAddRequest(friendLoginId)
            val result = friendRepository.addFriendList(token, request)
            _addFriendResult.value = result
        }
    }

    // 친구 즐겨찾기 수정
    fun updateFriendFavorite(token: String, friendId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            val request = FriendFavoriteRequest(friendId, isFavorite)
            val result = friendRepository.updateFriendFavorite(token, request)
            _updateFavoriteResult.value = result
        }
    }
}

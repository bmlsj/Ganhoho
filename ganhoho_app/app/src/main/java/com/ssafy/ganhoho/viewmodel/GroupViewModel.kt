package com.ssafy.ganhoho.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.base.TokenManager
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.response.group.GroupMemberResponse
import com.ssafy.ganhoho.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel(
    private val repository: GroupRepository = GroupRepository(), // 레포지토리 인스턴스 생성
    private val tokenManager: TokenManager
) : ViewModel() {

    // API 에러 상태 관리
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 그룹 목록 상태 관리
    private val _groupList = MutableStateFlow<List<GroupDto>>(emptyList())
    val groupList: StateFlow<List<GroupDto>> = _groupList

    // 그룹 초대 링크
    private val _groupMembers = MutableStateFlow<List<GroupMemberResponse>>(emptyList())
    val groupMembers: StateFlow<List<GroupMemberResponse>> = _groupMembers

    //그룹 목록 출력
    fun fetchGroupList() {
        viewModelScope.launch {
            val result = repository.getGroupList() // API 호출
            result.onSuccess { groups ->
                _groupList.value = groups
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
        }
    }


    // 그룹 추가
    fun addGroup(groupName: String, groupIconType: Int) {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken() ?: run {
                Log.e("GroupViewModel", "no token!!!")
                return@launch
            }
            Log.e("GroupViewModel", "token : $token")

            val result = repository.addGroup(token, groupName, groupIconType)

            result.onSuccess { newGroup ->
                Log.d("GroupViewModel", "그룹 추가 성공: ${newGroup.groupName}")
            }.onFailure { error ->
                Log.e("GroupViewModel", "그룹 추가 실패: ${error.message}")
            }
        }
    }




}

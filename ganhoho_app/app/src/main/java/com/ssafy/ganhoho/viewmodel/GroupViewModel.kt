package com.ssafy.ganhoho.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.base.TokenManager
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.response.group.GroupMemberResponse
import com.ssafy.ganhoho.data.model.response.group.MemberMonthlyScheduleResponse
import com.ssafy.ganhoho.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel(
    private val repository: GroupRepository,
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

    // 그룹 스케줄 관련
    private val _memberSchedules = MutableStateFlow<List<MemberMonthlyScheduleResponse>>(emptyList())
    val memberSchedules: StateFlow<List<MemberMonthlyScheduleResponse>> = _memberSchedules


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
                Log.d("GroupViewModel", "그룹 추가 성공")
            }.onFailure { error ->
                Log.e("GroupViewModel", "그룹 추가 실패: ${error.message}")
            }
        }
    }


    //그룹 탈퇴
    fun leaveGroup(groupId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken() ?: run {
                Log.e("GroupViewModel", "No token available!")
                onResult(false)
                return@launch
            }

            Log.d("GroupViewModel", "그룹 탈퇴 요청: groupId=$groupId, token=$token")

            val result = repository.leaveGroup(token, groupId)

            result.onSuccess {
                Log.d("GroupViewModel", "그룹 탈퇴 성공!")
                onResult(true)
            }.onFailure { error ->
                Log.e("GroupViewModel",  "그룹 탈퇴 실패: ${error.message}")
                onResult(false)
            }
        }
    }


    // 그룹별 스케줄 조회
    fun fetchMemberSchedules(groupId: Int, yearMonth: String) {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken() ?: return@launch
            Log.d("API_REQUEST", "Fetching schedule for groupId: $groupId, yearMonth: $yearMonth")

            val result = repository.getEachMemberMonthlySchedule(token, groupId, yearMonth)
            result.onSuccess {
                _memberSchedules.value = it
            }.onFailure {
                Log.e("GroupViewModel", "스케줄 가져오기 실패: ${it.message}")
            }
        }
    }

    // 그룹원 리스트 불러오기
    fun fetchMemberList(groupId: Int) {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken() ?: return@launch
            Log.d("API_REQUEST", "Fetching members for groupId: $groupId")

            val result = repository.getGroupMembers(token, groupId)
            result.onSuccess {
                _groupMembers.value = it
            }.onFailure {
                Log.e("GroupViewModel", "그룹 멤버 가져오기 실패: ${it.message}")
            }
        }
    }



}

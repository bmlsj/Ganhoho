package com.ssafy.ganhoho.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.dto.member.MemberDto
import com.ssafy.ganhoho.data.model.dto.member.UpdateHospitalWardRequest
import com.ssafy.ganhoho.data.model.response.member.MyPageResponse
import com.ssafy.ganhoho.repository.MemberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "MemberViewModel"
class MemberViewModel() : ViewModel() {

    // 레포지토리
    private val memberRepository = MemberRepository()

    // 회원 검색 결과
    private val _memberList = MutableStateFlow<Result<List<MemberDto>>>(
        Result.success(
            emptyList()
        )
    )
    val memberList: StateFlow<Result<List<MemberDto>>> = _memberList

    // 마이페이지 정보
    private val _mypageInfo = MutableStateFlow<Result<MyPageResponse>?>(null)
    val mypageInfo: StateFlow<Result<MyPageResponse>?> = _mypageInfo

    // 회원 병동/병원 정보 수정
    private val _updateHospitalWardInfo = MutableStateFlow<Result<MyPageResponse>?>(null)
    val updateHospitalWardInfo: StateFlow<Result<MyPageResponse>?> = _updateHospitalWardInfo

    // 회원 검색 조회
    fun searchFriend(token: String, memberLoginId: String) {
        viewModelScope.launch {
            val result = memberRepository.searchFriend(token, memberLoginId)
            Log.d("searchFriend", "$result, $memberLoginId")
            _memberList.value = result
        }
    }

    // 마이페이지 정보 조회
    fun getMyPageInfo(token: String) {
        viewModelScope.launch {
            val result = memberRepository.getMyPageInfo(token)
            _mypageInfo.value = result
            Log.d(TAG, "getMyPageInfo: ${_mypageInfo.value}")
        }
    }

    // 병원 정보 수정(회원 정보 수정)
    fun updateHospitalAndWardInfo(token: String, request: UpdateHospitalWardRequest) {
        viewModelScope.launch {
            val result = memberRepository.updateHospitalAndWardInfo(token, request)
            _updateHospitalWardInfo.value = result
        }
    }

}
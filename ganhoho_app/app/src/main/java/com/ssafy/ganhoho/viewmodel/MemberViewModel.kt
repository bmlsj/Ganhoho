package com.ssafy.ganhoho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.dto.member.MemberDto
import com.ssafy.ganhoho.repository.MemberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    // 회원 검색 조회
    fun searchFriend(token: String, friendLoginId: String) {
        viewModelScope.launch {
            val result = memberRepository.searchFriend(token, friendLoginId)
            _memberList.value = result
        }
    }


}
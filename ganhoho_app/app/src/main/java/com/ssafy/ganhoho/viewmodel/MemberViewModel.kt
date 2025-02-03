package com.ssafy.ganhoho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.dto.member.LoginRequest
import com.ssafy.ganhoho.data.model.dto.member.SignUpRequest
import com.ssafy.ganhoho.repository.MemberRepository
import kotlinx.coroutines.launch

class MemberViewModel : ViewModel() {

    private val memberRepository = MemberRepository()

    // 로그인
    fun login(member: MemberDTO) {
        viewModelScope.launch {

        }
    }

    // 회원가입
    fun signUp(
        signUpRequest: SignUpRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = memberRepository.signUp(signUpRequest)

            result.onSuccess {
                onSuccess()
            }.onFailure { error ->
                onError(error.message ?: "회원가입 실패")
            }
        }
    }

}
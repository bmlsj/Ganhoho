package com.ssafy.ganhoho.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.dto.member.SignUpRequest
import com.ssafy.ganhoho.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val memberRepository by lazy { AuthRepository() }
    // 로그인
//    fun login(member: MemberDTO) {
//        viewModelScope.launch {
//
//        }
//    }

    fun signUpTest() {
        val signUpRequest = SignUpRequest(
            loginId = "testuser123",
            password = "password123!",
            name = "John Doe",
            hospital = "General Hospital",
            ward = "Emergency Ward",
            fcmToken = "test-fcm-token",
            deviceType = 1
        )

        viewModelScope.launch {
            val response = memberRepository.signUp(signUpRequest)
            if (response.isSuccess) {
                Log.d("SignUpTest", "signup success: ${response.getOrNull()}")
            } else {
                Log.e("SignUpTest", "signup fail : ${response.exceptionOrNull()?.message}")
            }
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
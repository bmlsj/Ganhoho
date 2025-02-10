package com.ssafy.ganhoho.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.data.model.dto.member.LoginRequest
import com.ssafy.ganhoho.data.model.dto.member.SignUpRequest
import com.ssafy.ganhoho.data.model.response.auth.LoginResponse
import com.ssafy.ganhoho.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository by lazy { AuthRepository() }

    // 🔹 로그인 결과 상태 관리
    private val _loginResult = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginResult: StateFlow<Result<LoginResponse>?> = _loginResult

    // 🔹 회원가입 결과 상태 관리
    private val _signUpResult = MutableStateFlow<Result<Boolean>?>(null)
    val signUpResult: StateFlow<Result<Boolean>?> = _signUpResult

    // 아이디 중복 확인 상태
    private val _isIdUsed = MutableStateFlow<Result<Boolean>?>(null)
    val isIdUsed: StateFlow<Result<Boolean>?> = _isIdUsed

    // 🔹 저장된 Access Token (로그인 유지 확인용)
    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken

    // 🔹 저장된 Refresh Token (재로그인 처리용)
    private val _refreshToken = MutableStateFlow<String?>(null)
    val refreshToken: StateFlow<String?> = _refreshToken

    /**
     * 🔹 로그인 요청
     */
    fun login(loginRequest: LoginRequest, context: Context) {
        viewModelScope.launch {
            val result = authRepository.login(loginRequest)
            _loginResult.value = result

            result.onSuccess { response ->
                Log.d("AuthViewModel", "Login Success: ${response.accessToken}")

                // ✅ JWT & Refresh Token 저장
                SecureDataStore.saveAccessToken(context, response.accessToken)
                SecureDataStore.saveRefreshToken(context, response.refreshToken)

                // ✅ 저장된 토큰 상태 업데이트
                _accessToken.value = response.accessToken
                _refreshToken.value = response.refreshToken

                // ✅ 저장 후 바로 불러와서 확인
                val savedAccessToken = SecureDataStore.getAccessToken(context).first()
                val savedRefreshToken = SecureDataStore.getRefreshToken(context).first()

                Log.d("AuthViewModel", "Saved Access Token: $savedAccessToken")
                Log.d("AuthViewModel", "Saved Refresh Token: $savedRefreshToken")

                // ✅ 저장된 토큰이 제대로 반영되었는지 확인
                loadTokens(context)

            }.onFailure { error ->
                Log.e("AuthViewModel", "Login Failed: ${error.message}")
            }
        }
    }



    /**
     * 🔹 회원가입 요청
     */
    fun signUp(signUpRequest: SignUpRequest) {
        viewModelScope.launch {
            val result = authRepository.signUp(signUpRequest)
            _signUpResult.value = result

            result.onSuccess {
                Log.d("AuthViewModel", "SignUp Success")
            }.onFailure { error ->
                Log.e("AuthViewModel", "SignUp Failed: ${error.message}")
            }
        }
    }

    /**
     * 🔹 저장된 토큰 불러오기
     */
    fun loadTokens(context: Context) {
        viewModelScope.launch {
            val access = SecureDataStore.getAccessToken(context).first()
            val refresh = SecureDataStore.getRefreshToken(context).first()

            _accessToken.value = access
            _refreshToken.value = refresh

            Log.d("AuthViewModel", "Loaded Access Token: $access")
            Log.d("AuthViewModel", "Loaded Refresh Token: $refresh")
        }
    }

    /**
     * 아이디 중복 확인
     */
    fun checkIsIdUsed(loginId: String) {
        viewModelScope.launch {
            val result = authRepository.isUsedId(loginId)
            _isIdUsed.value = result

            result.onSuccess { isUsed ->
                if (isUsed) {
                    Log.d("AuthViewModel", "사용 가능한 아이디입니다 $loginId")
                } else {
                    Log.d("AuthViewModel", "이미 존재하는 아이디입니다 $loginId")
                }
            }.onFailure { error ->
                Log.d("AuthViewModel", "아이디 중복확인 실패 $error")
            }
        }
    }

    /**
     * 🔹 로그아웃 (토큰 삭제)
     */
    fun logout(context: Context) {
        viewModelScope.launch {
            SecureDataStore.clearTokens(context)
            _accessToken.value = null
            _refreshToken.value = null
            Log.d("AuthViewModel", "User logged out: Tokens cleared")
        }
    }
}

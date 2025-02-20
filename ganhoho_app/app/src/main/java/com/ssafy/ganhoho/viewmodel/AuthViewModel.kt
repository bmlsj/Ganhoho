package com.ssafy.ganhoho.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.data.model.dto.auth.LoginRequest
import com.ssafy.ganhoho.data.model.dto.auth.SignUpRequest
import com.ssafy.ganhoho.base.TokenManager
import com.ssafy.ganhoho.data.model.response.auth.LoginResponse
import com.ssafy.ganhoho.repository.AuthRepository
import com.ssafy.ganhoho.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class AuthViewModel : ViewModel() {

    private val authRepository by lazy { AuthRepository() }
    private val notificationRepository by lazy { NotificationRepository() }

    // 🔹 로그인 결과 상태 관리
    private val _loginResult = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginResult: StateFlow<Result<LoginResponse>?> = _loginResult

    private val _loginState = MutableStateFlow<String?>(null)  // 로그인 상태 추가 (로그인 실패 시 토스트 띄우기용)
    val loginState: StateFlow<String?> = _loginState

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

    // 🔹 저장된 사용자 정보
    private val _userInfo = MutableStateFlow<LoginResponse?>(null)
    val userInfo: StateFlow<LoginResponse?> = _userInfo


    /**
     * ✅ 앱 실행 시 저장된 토큰을 확인하여 자동 로그인
     */
    fun checkAutoLogin(context: Context) {
        viewModelScope.launch {
            val userInfo = SecureDataStore.getUserInfo(context).firstOrNull()
            if (userInfo != null && userInfo.accessToken.isNotEmpty()) {
                _userInfo.value = userInfo
                _loginResult.value = Result.success(userInfo)
                Log.d("AuthViewModel", "✅ 자동 로그인 성공: ${userInfo.loginId}")
            } else {
                Log.d("AuthViewModel", "⚠️ 자동 로그인 실패: 저장된 사용자 정보 없음")
            }
        }
    }

    /**
     * 🔹 로그인 요청
     */
    fun login(loginRequest: LoginRequest, context: Context) {
        viewModelScope.launch {
            val result = authRepository.login(loginRequest)
            _loginResult.value = result

            result.onSuccess { response ->
                Log.d("AuthViewModel", "Login Success: ${response.accessToken}")

                // ✅ 로그인한 사용자 정보 저장
                SecureDataStore.saveUserInfo(context, response)
                // ✅ TokenManager에도 저장
                TokenManager.saveAccessToken(response.accessToken)


                viewModelScope.launch {
                    SecureDataStore.getAccessToken(context).collect { savedAccessToken ->
                        Log.d("AuthViewModel", "🔑 저장 후 불러온 Access Token: $savedAccessToken")
                    }
                    SecureDataStore.getRefreshToken(context).collect { savedRefreshToken ->
                        Log.d("AuthViewModel", "🔑 저장 후 불러온 Refresh Token: $savedRefreshToken")
                    }
                }

                // ✅ 로그인한 사용자의 알림 구독 여부 저장 --> 로그아웃 시 구독 해제
                SecureDataStore.saveSubscriptionInfo(context, false)

                // ✅ 로그인한 사용자의 병원 위치 정보 저장
                response.hospitalLat?.let {
                    SecureDataStore.saveHospitalLocation(context, response.hospitalLat, response.hospitalLng!!)
                }
                Log.d("TAG", "login: ${response.hospital} ${response.hospitalLat} ${response.hospitalLng}")

                // ✅ 저장된 토큰 상태 업데이트
                _accessToken.value = response.accessToken
                _refreshToken.value = response.refreshToken
                _userInfo.value = response

                _loginState.value = "success" //로그인 성공 상태로 업데이트
                

                // ✅ 저장 후 바로 불러와서 확인
                val savedAccessToken = SecureDataStore.getAccessToken(context).first()
                val savedRefreshToken = SecureDataStore.getRefreshToken(context).first()

                Log.d("AuthViewModel", "Saved Access Token: $savedAccessToken")
                Log.d("AuthViewModel", "Saved Refresh Token: $savedRefreshToken")

                // ✅ 저장된 토큰이 제대로 반영되었는지 확인
                loadTokens(context)

            }.onFailure { error ->
                Log.e("AuthViewModel", "Login Failed: ${error.message}")
                _loginState.value = "failure" //로그인 실패 상태 업데이트
            }
        }
    }

    fun resetLoginState(){
        _loginState.value = null // 상태 초기화해서 여러 번 시도해도 성공 여부가 감지되도록 함
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
    fun logout(token: String, context: Context, requestBody: RequestBody) {
        viewModelScope.launch {
            if(SecureDataStore.getSubscriptionInfo(context).first() == true) notificationRepository.changeSubscription(token, requestBody)
            SecureDataStore.clearTokens(context)
            _userInfo.value = null
            _accessToken.value = null
            _refreshToken.value = null
            Log.d("AuthViewModel", "User logged out: Tokens cleared")
        }
    }

    /**
     * 🔹 회원 탈퇴(토큰 삭제)
     */
    fun withdrawalMember(token: String, context: Context) {
        viewModelScope.launch {
            val result = authRepository.withdrawalMember(token, context)
            if (result.isSuccess) {
                SecureDataStore.clearTokens(context)
                _userInfo.value = null
                _accessToken.value = null
                _refreshToken.value = null
            }

        }
    }
}

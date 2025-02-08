package com.ssafy.ganhoho.base

import android.content.Context
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenManager.getAccessToken()

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        val response = chain.proceed(request)

        // 401 Unauthorized → Access Token이 만료됨 → Refresh Token을 사용하여 새 토큰 요청
        if (response.code == 401) {
            runBlocking {
                val refreshToken = SecureDataStore.getRefreshToken(context)
                refreshToken.collect { storedToken ->
                    if (!storedToken.isNullOrEmpty()) {
                        val newToken = refreshAccessToken(storedToken) // 서버에서 새 Access Token 받기
                        if (!newToken.isNullOrEmpty()) {
                            TokenManager.saveAccessToken(newToken)
                            chain.proceed(
                                request.newBuilder()
                                    .header("Authorization", "Bearer $newToken")
                                    .build()
                            )
                        }
                    }
                }
            }
        }

        return response
    }

    private fun refreshAccessToken(refreshToken: String): String? {
        // 🔹 서버에 Refresh Token을 보내서 새 Access Token을 받아오는 로직 추가
        return null
    }
}

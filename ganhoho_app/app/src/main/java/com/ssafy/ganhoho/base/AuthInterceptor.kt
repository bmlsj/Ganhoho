package com.ssafy.ganhoho.base

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        // 로그인 요청 시, token을 헤더에 추가x
        val originalRequest = chain.request()

        // ✅ 로그인 API 요청인 경우 `Authorization` 헤더를 추가하지 않음
        if (originalRequest.url.encodedPath.contains("api/auth")) {
            return chain.proceed(originalRequest)
        }

        // 로그인 요청이 아닌 경우, 토큰을 가져와서 헤더에 추가
        val token = runBlocking { SecureDataStore.getAccessToken(context).first() }
        Log.d("AuthInterceptor", "Token before request: $token")

        if (!token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "Loaded Access Token: $token")
        } else {
            Log.d("AuthInterceptor", "Access Token is NULL!!")
        }

        val request = chain.request().newBuilder()
            //.addHeader("Authorization", "Bearer $token")
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

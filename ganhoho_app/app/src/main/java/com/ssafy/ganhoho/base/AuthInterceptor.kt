package com.ssafy.ganhoho.base

import com.ssafy.ganhoho.util.SharedPreferencesUtil
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sharedPreferencesUtil: SharedPreferencesUtil) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreferencesUtil.getJwtToken()

        val request = if (token.isNotEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
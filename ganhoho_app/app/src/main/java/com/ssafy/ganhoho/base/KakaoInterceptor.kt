package com.ssafy.ganhoho.base

import com.ssafy.ganhoho.BuildConfig.KAKAO_NATIVE_APP_KEY
import okhttp3.Interceptor
import okhttp3.Response

class KakaoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "KakaoAK $KAKAO_NATIVE_APP_KEY") // ✅ 카카오 인증 헤더 추가
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}

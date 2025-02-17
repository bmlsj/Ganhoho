package com.ssafy.ganhoho.base

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.ganhoho.BuildConfig
import com.ssafy.ganhoho.util.SharedPreferencesUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {

    companion object {
        lateinit var retrofit: Retrofit
        lateinit var kakaoRetrofit: Retrofit // ✅ 카카오 API Retrofit 추가
        const val SERVER_URL = BuildConfig.SERVER_URL
        const val KAKAO_URL = "https://dapi.kakao.com/"
    }

    override fun onCreate() {
        super.onCreate()

        // 로그 찍기
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            try {
                val decodedMessage = message.toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)
                Log.e("POST", "log: message $decodedMessage")
            } catch (e: Exception) {
                Log.e("POST", "log: message (decode failed) $message")
            }
        }.setLevel(HttpLoggingInterceptor.Level.BODY)

        // ✅ 기본 서버 API용 클라이언트
        val client = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(AuthInterceptor(this))
            .addInterceptor(loggingInterceptor)
            .build()

        // ✅ 카카오 API용 클라이언트 (별도 Interceptor 추가)
        val kakaoClient = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(KakaoInterceptor()) // ✅ 카카오 API 전용 Interceptor 추가
            .addInterceptor(loggingInterceptor)
            .build()

        val gson: Gson = GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .create()

        // ✅ 서버 API Retrofit 인스턴스
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        // ✅ 카카오 API Retrofit 인스턴스 추가
        kakaoRetrofit = Retrofit.Builder()
            .baseUrl(KAKAO_URL)
            .client(kakaoClient)
       //     .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}

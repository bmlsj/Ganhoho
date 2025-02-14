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
        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        const val SERVER_URL = BuildConfig.SERVER_URL
    }

    override fun onCreate() {
        super.onCreate()

        // sharedPreferencesUtil 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        // 로그 찍기
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            try {
                val decodedMessage = message.toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)
                Log.e("POST", "log: message $decodedMessage")
            } catch (e: Exception) {
                Log.e("POST", "log: message (decode failed) $message")
            }
        }

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(AuthInterceptor(this))
            .addInterceptor(loggingInterceptor)
            .build()

        val gson: Gson = GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .create()

        // retrofit 인스턴스
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)
       //     .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    }
}
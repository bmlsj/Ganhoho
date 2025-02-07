package com.ssafy.ganhoho.base

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.ganhoho.BuildConfig
import com.ssafy.ganhoho.util.SharedPreferencesUtil
import okhttp3.OkHttpClient
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

        val client = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(AuthInterceptor(sharedPreferencesUtil))
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
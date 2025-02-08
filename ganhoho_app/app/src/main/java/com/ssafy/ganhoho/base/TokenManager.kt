package com.ssafy.ganhoho.base

import android.util.Log

object TokenManager {
    private var accessToken: String? = null

    fun saveAccessToken(token: String) {
        accessToken = token
        Log.d("TokenManager", "Access Token 저장됨: $accessToken")
    }

    fun getAccessToken(): String? {
        Log.d("TokenManager", "Access Token 반환: $accessToken")
        return accessToken
    }

    fun clearAccessToken() {
        accessToken = null
    }
}

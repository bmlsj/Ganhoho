package com.ssafy.ganhoho.base

object TokenManager {
    private var accessToken: String? = null

    fun saveAccessToken(token: String) {
        accessToken = token
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun clearAccessToken() {
        accessToken = null
    }
}

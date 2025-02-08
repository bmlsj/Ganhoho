package com.ssafy.ganhoho.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtil(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "ganhoho_prefs"  // SharedPreferences 파일 이름
        private const val KEY_JWT_TOKEN = "jwt_token"   // JWT 토큰 저장 키
    }

    /**
     * ✅ 값 저장
     */
    fun setValue(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    /**
     * ✅ 값 가져오기 (기본값 설정 가능)
     */
    fun getValue(key: String, defaultValue: String = ""): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * ✅ 값 삭제
     */
    fun removeValue(key: String) {
        prefs.edit().remove(key).apply()
    }

    /**
     * ✅ JWT 토큰 저장
     */
    fun saveJwtToken(token: String) {
        setValue(KEY_JWT_TOKEN, token)
    }

    /**
     * ✅ JWT 토큰 가져오기
     */
    fun getJwtToken(): String {
        return getValue(KEY_JWT_TOKEN)
    }

    /**
     * ✅ JWT 토큰 삭제 (로그아웃 시)
     */
    fun clearJwtToken() {
        removeValue(KEY_JWT_TOKEN)
    }
}

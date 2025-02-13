package com.ssafy.ganhoho.ui.auth

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("auth_prefs")

// feat: 로그인 한 번 한 계정은 자동 로그인을 하기 위해 DataStore 사용

class AuthDataStore(private val context: Context) {
    
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = stringPreferencesKey("user_id")
    }

    // 로그인 상태 가져오기
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false
    }

    // 로그인 정보 저장
    suspend fun saveLoginState(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_ID] = userId
        }
    }

    // 로그아웃 (저장된 로그인 정보 삭제)
    suspend fun clearLoginState() {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = false
            prefs.remove(USER_ID)
        }
    }
}

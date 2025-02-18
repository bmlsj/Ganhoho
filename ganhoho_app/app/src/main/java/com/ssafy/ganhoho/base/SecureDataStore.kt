package com.ssafy.ganhoho.base

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.ssafy.ganhoho.data.model.response.auth.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "secure_prefs")

object SecureDataStore {
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

    private val MEMBER_ID_KEY = longPreferencesKey("member_id")
    private val LOGIN_ID_KEY = stringPreferencesKey("login_id")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val HOSPITAL_KEY = stringPreferencesKey("hospital")
    private val WARD_KEY = stringPreferencesKey("ward")
    private val HOSPITAL_LOCATION_LAT = doublePreferencesKey("hospital_location_lat")
    private val HOSPITAL_LOCATION_LNG = doublePreferencesKey("hospital_location_lng")
    private val IS_SUBSCRIBED = booleanPreferencesKey("is_subscribed")

    // 🔹 JWT Access Token 저장
    suspend fun saveUserInfo(context: Context, response: LoginResponse) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = response.accessToken
            prefs[REFRESH_TOKEN_KEY] = response.refreshToken
            prefs[MEMBER_ID_KEY] = response.memberId
            prefs[LOGIN_ID_KEY] = response.loginId
            prefs[USER_NAME_KEY] = response.name
            response.hospital?.let { prefs[HOSPITAL_KEY] = it }
            response.ward?.let { prefs[WARD_KEY] = it }
            response.hospitalLat?.let { prefs[HOSPITAL_LOCATION_LAT] = it }
            response.hospitalLng?.let { prefs[HOSPITAL_LOCATION_LNG] = it }
        }
    }

    // 🔹 JWT Access Token 가져오기
    fun getAccessToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

//    // 🔹 Refresh Token 저장
//    suspend fun saveRefreshToken(context: Context) {
//        context.dataStore.edit { preferences ->
//            preferences[REFRESH_TOKEN_KEY]
//        }
//    }

    // 🔹 Refresh Token 가져오기
    fun getRefreshToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }

    // 🔹 모든 토큰 삭제 (로그아웃 시 사용)
    suspend fun clearTokens(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }

    // ✅ 저장된 사용자 정보 가져오기
    fun getUserInfo(context: Context): Flow<LoginResponse?> =
        context.dataStore.data.map { prefs ->
            val memberId = prefs[MEMBER_ID_KEY] ?: return@map null
            val loginId = prefs[LOGIN_ID_KEY] ?: return@map null
            val name = prefs[USER_NAME_KEY] ?: return@map null
            val hospital = prefs[HOSPITAL_KEY]
            val ward = prefs[WARD_KEY]
            val hospitalLat = prefs[HOSPITAL_LOCATION_LAT]
            val hospitalLng = prefs[HOSPITAL_LOCATION_LNG]

            LoginResponse(
                memberId,
                loginId,
                name,
                hospital,
                ward,
                prefs[ACCESS_TOKEN_KEY] ?: "",
                prefs[REFRESH_TOKEN_KEY] ?: "",
                hospitalLat,
                hospitalLng
            )
        }

    // 🔹 병원 위치 정보 저장
    suspend fun saveHospitalLocation(context: Context, lat: Double, lng: Double) {
        context.dataStore.edit { prefs ->
            prefs[HOSPITAL_LOCATION_LAT] = lat
            prefs[HOSPITAL_LOCATION_LNG] = lng
        }
    }

    // 🔹 병원 lat 값 가져오기
    fun getHospitalLocationLat(context: Context): Flow<Double?> {
        return context.dataStore.data.map { preferences ->
            preferences[HOSPITAL_LOCATION_LAT]
        }
    }

    // 🔹 병원 lng 값 가져오기
    fun getHospitalLocationLng(context: Context): Flow<Double?> {
        return context.dataStore.data.map { preferences ->
            preferences[HOSPITAL_LOCATION_LNG]
        }
    }

    suspend fun saveSubscriptionInfo(context: Context, isSubscribed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_SUBSCRIBED] = isSubscribed
        }
    }

    fun getSubscriptionInfo(context: Context): Flow<Boolean?>{
        return context.dataStore.data.map {
            it[IS_SUBSCRIBED]
        }
    }

    // ✅ 로그아웃 시 모든 데이터 삭제
    suspend fun clearAllUserData(context: Context) {
        context.dataStore.edit { it.clear() }
    }
}

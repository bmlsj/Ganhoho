package com.ssafy.ganhoho.repository

import android.util.Log
import com.ssafy.ganhoho.data.model.response.auth.SearchResultItem
import com.ssafy.ganhoho.data.remote.RetrofitUtil

class HospitalSearchRepository {

    suspend fun searchHospital(
        query: String,
        x: String,
        y: String
    ): Result<List<SearchResultItem>> {
        return try {
            val response = RetrofitUtil.kakaoSearchApi.searchHospital(query, x, y)
            if (response.isSuccessful) {
                Log.d("KakaoMap", "병원 검색 성공! 검색어: $query")
                val hospitalList = response.body()?.places ?: emptyList()
                Result.success(hospitalList)
            } else {
                Log.e("KakaoMap", "API 호출 실패: ${response.errorBody()?.string()}")
                Result.failure(Exception("API 호출 실패"))
            }
        } catch (e: Exception) {
            Log.e("KakaoMap", "API 호출 중 오류 발생", e)
            Result.failure(e)
        }
    }
}

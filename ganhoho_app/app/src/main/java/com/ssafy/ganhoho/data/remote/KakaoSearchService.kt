package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.data.model.response.auth.KakaoSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoSearchService {

    @GET("v2/local/search/keyword.json")
    suspend fun searchHospital(
        @Query("query") query: String,  // 검색어 (ex: "병원")
        @Query("x") x: String,          // 경도 (Longitude)
        @Query("y") y: String,          // 위도 (Latitude)
        @Query("category_group_code") category: String = "HP8"  // 병원 카테고리 코드
    ): Response<KakaoSearchResponse>

}

package com.ssafy.ganhoho.data.model.response.auth

import com.google.gson.annotations.SerializedName

data class KakaoSearchResponse(
    @SerializedName("documents") val places: List<SearchResultItem>
)

data class SearchResultItem(
    @SerializedName("id") val id: String,
    @SerializedName("place_name") val name: String,
    @SerializedName("address_name") val address: String,
    @SerializedName("road_address_name") val roadAddress: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("x") val x: String,
    @SerializedName("y") val y: String,
    @SerializedName("place_url") val placeUrl: String
)

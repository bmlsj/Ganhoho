package com.ssafy.ganhoho.data.model.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class KakaoSearchResponse(
    @SerializedName("documents") val places: List<SearchResultItem>
)

@Parcelize
data class SearchResultItem(
    @SerializedName("id") val id: String,
    @SerializedName("place_name") var name: String,
    @SerializedName("address_name") val address: String,
    @SerializedName("road_address_name") val roadAddress: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("x") val x: String,
    @SerializedName("y") val y: String,
    @SerializedName("place_url") val placeUrl: String
) : Parcelable

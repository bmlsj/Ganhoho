package com.ssafy.ganhoho.data.model.dto.friend

import com.google.gson.annotations.SerializedName

data class FriendAddRequest (
    @SerializedName("friendLoginId") val friendLoginId: String
)
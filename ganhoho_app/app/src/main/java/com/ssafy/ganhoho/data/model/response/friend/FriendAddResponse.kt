package com.ssafy.ganhoho.data.model.response.friend

import com.google.gson.annotations.SerializedName

data class FriendAddResponse(
    @SerializedName("success") val success: Boolean
)
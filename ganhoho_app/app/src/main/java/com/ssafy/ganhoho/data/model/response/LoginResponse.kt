package com.ssafy.ganhoho.data.model.response

data class LoginResponse(
    val memberId: Long,
    val loginId: String,
    val name: String,
    val hospital: String?,
    val ward: String?,
    val accessToken: String,
    val refreshToken: String
)
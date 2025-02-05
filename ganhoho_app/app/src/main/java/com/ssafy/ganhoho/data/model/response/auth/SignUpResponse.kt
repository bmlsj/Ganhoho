package com.ssafy.ganhoho.data.model.response.auth

data class SignUpResponse(
    val memberId: Long,
    val loginId: String,
    val name: String,
    val hospital: String?,
    val ward: String?
)
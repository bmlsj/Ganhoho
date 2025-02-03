package com.ssafy.ganhoho.data.model.response

import com.ssafy.ganhoho.data.model.dto.member.MemberDTO

data class LoginResponse(
    val memberId: Long,
    val loginId: String,
    val name: String,
    val hospital: String?,
    val ward: String?,
    val accessToken: String,
    val refreshToken: String
)
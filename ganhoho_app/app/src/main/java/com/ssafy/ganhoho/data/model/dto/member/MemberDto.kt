package com.ssafy.ganhoho.data.model.dto.member

data class MemberDto(
    val memberId: Long,
    val loginId: String,
    val name: String,
    val hospital: String?,
    val ward: String?
)

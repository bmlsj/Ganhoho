package com.ssafy.ganhoho.data.model.response.group

data class GroupMemberResponse(
    val memberId: String,
    val loginId: String,
    val name: String,
    val hospital: String?,
    val ward: String?
)
package com.ssafy.ganhoho.data.model.response.group

data class GroupInviteResponse(
    val groupId: Int,
    val groupName: String,
    val groupIconType: Int,
    val groupMemberCount: Int
)

package com.ssafy.ganhoho.data.model.response.friend

import com.ssafy.ganhoho.data.model.dto.friend.FriendInviteDto

data class FriendInviteResponse(
    val friendRequestList: List<FriendInviteDto>
)

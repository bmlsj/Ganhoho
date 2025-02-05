package com.ssafy.ganhoho.data.model.dto.friend

data class FriendFavoriteRequest(
    val friendMemberId: Long,
    val isFavorite: Boolean
)

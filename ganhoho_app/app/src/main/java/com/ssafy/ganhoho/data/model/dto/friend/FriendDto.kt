package com.ssafy.ganhoho.data.model.dto.friend

data class FriendDto(
    val friendId: Long,
    val memberId: Long,
    val friendLoginId: String,
    val name: String,
    val hospital: String?,
    val ward: String?,
    var isFavorite: Boolean
)
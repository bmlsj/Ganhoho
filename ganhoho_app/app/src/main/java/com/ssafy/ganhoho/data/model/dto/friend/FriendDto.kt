package com.ssafy.ganhoho.data.model.dto.friend

data class FriendDto(
    val friendLoginId: String,
    val name: String,
    val hospital: String?,
    val ward: String?,
    var isFavorite: Boolean
) {
    private var friendId = 1L

    constructor(
        friendId: Long,
        friendLoginId: String,
        name: String,
        hospital: String?,
        ward: String?,
        isFavorite: Boolean
    ) : this(friendLoginId, name, hospital, ward, isFavorite) {
        this.friendId = friendId
    }
}

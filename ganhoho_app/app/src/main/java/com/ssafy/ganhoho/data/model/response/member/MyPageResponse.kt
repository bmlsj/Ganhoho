package com.ssafy.ganhoho.data.model.response.member

data class MyPageResponse(
    val memberId: Long = -1,
    val loginId: String = "",
    val name: String = "",
    val hospital: String?,
    val ward: String?
) {

}
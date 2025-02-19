package com.ssafy.ganhoho.ui.nav_host

import android.net.Uri

sealed class Route(val route: String) {

    // Auth
    object Splash : Route("splash")
    object Login : Route("login")
    object Join : Route("join")
    object HospitalInfo : Route("hospitalInfo")
    object Main : Route("main")

    // 바텀 네비게이션
    object Work : Route("work")
    object Pill : Route("pill")
    object Home : Route("home")
    object Friend : Route("friend")
    object Group : Route("group")

    object GroupInvite : Route("group?groupCode={groupCode}") {
        fun createRoute(groupCode: String): String = "group?groupCode=$groupCode"
    }
    object Mypage : Route("mypage")
    object Notification : Route("noti")
    object UpdateInfo : Route("update")  // 마이페이지 수정

    // ✅ 그룹 관련 네비게이션 추가
    data class GroupDetail(val groupId: Int) : Route("group/$groupId")
    data class EachGroup(val groupJson: String) : Route("EachGroupScreen/$groupJson")

    companion object {
        fun GroupDetail.createRoute(groupId: Int): String {
            return "group/$groupId"
        }

        fun EachGroup.createRoute(groupJson: String): String {
            return "EachGroupScreen/${Uri.encode(groupJson)}"
        }
    }

}
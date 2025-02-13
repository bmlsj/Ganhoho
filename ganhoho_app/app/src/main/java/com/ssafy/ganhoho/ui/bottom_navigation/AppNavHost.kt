package com.ssafy.ganhoho.ui.bottom_navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ssafy.ganhoho.ui.friend.FriendScreen
import com.ssafy.ganhoho.ui.group.GroupScreen
import com.ssafy.ganhoho.ui.home.HomeScreen
import com.ssafy.ganhoho.ui.mypage.MyPageScreen
import com.ssafy.ganhoho.ui.mypage.UpdateMemberInfo
import com.ssafy.ganhoho.ui.pill.PillScreen
import com.ssafy.ganhoho.ui.work_schedule.WorkScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("work") { WorkScreen(navController) }
        composable("pill") { PillScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("group") { GroupScreen(navController) }
        composable("friend") { FriendScreen(navController) }
        composable("mypage") { MyPageScreen(navController) }
        composable("update") { UpdateMemberInfo(navController) }  // 회원정보 수정 화면
    }
}
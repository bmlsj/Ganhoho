package com.ssafy.ganhoho.ui.bottom_navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ssafy.ganhoho.ui.friend.FriendScreen
import com.ssafy.ganhoho.ui.group.EachGroupScreen
import com.ssafy.ganhoho.ui.group.GroupMemberScheduleScreen
import com.ssafy.ganhoho.ui.group.GroupScreen
import com.ssafy.ganhoho.ui.group.getSampleGroup
import com.ssafy.ganhoho.ui.group.getSampleMembers
import com.ssafy.ganhoho.ui.group.getSampleSchedules
import com.ssafy.ganhoho.ui.home.HomeScreen
import com.ssafy.ganhoho.ui.pill.PillScreen
import com.ssafy.ganhoho.ui.work_schedule.WorkScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = "home" //todo: 다른 걸로 바꿨을 시 다시 home으로 돌려놓기
    ) {
        composable("work") { WorkScreen(navController) }
        composable("pill") { PillScreen(navController) }
        composable("home") { HomeScreen(modifier) }
        composable("group") { GroupScreen(navController) }
        composable("friend") { FriendScreen(navController) }
        composable("GroupScreen") { GroupScreen(navController) }
        composable("EachGroupScreen/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
            if (groupId != null) {
                EachGroupScreen(
                    navController = navController,
                    group = getSampleGroup(), // todo:실제 데이터 연동 필요
                    groupMember = getSampleMembers(),
                    memberSchedule = getSampleSchedules(),
                    onToggleBottomNav = { isVisible -> /* 네비게이션 바 상태 변경 로직 추가 */ }
                )
            }
        }
        composable("eachGroupScreen") {
            EachGroupScreen(
                navController = navController,
                group = getSampleGroup(), // TODO: 실제 데이터 연동 필요
                groupMember = getSampleMembers(),
                memberSchedule = getSampleSchedules(),
                onToggleBottomNav = { isVisible -> /* 네비게이션 바 상태 변경 로직 추가 */ }
            )
        }

        composable("GroupMemberSchedule/{memberName}") { backStackEntry ->
            val memberName = backStackEntry.arguments?.getString("memberName") ?: "이름 없음"
            GroupMemberScheduleScreen(
                navController = navController,
                memberName = memberName,
                onToggleBottomNav = { isVisible -> /* 바텀 네비게이션 상태 변경 */ }
            )
        }

    }
}
package com.ssafy.ganhoho.ui.bottom_navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.gson.Gson
import com.ssafy.ganhoho.base.TokenManager
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.repository.GroupRepository
import com.ssafy.ganhoho.ui.friend.FriendScreen
import com.ssafy.ganhoho.ui.group.EachGroupScreen
import com.ssafy.ganhoho.ui.group.GroupMemberScheduleScreen
import com.ssafy.ganhoho.ui.group.GroupScreen
import com.ssafy.ganhoho.ui.group.getSampleMembers
import com.ssafy.ganhoho.ui.group.getSampleSchedules
import com.ssafy.ganhoho.ui.home.HomeScreen
import com.ssafy.ganhoho.ui.pill.PillScreen
import com.ssafy.ganhoho.ui.work_schedule.WorkScreen
import com.ssafy.ganhoho.viewmodel.BottomNavViewModel
import com.ssafy.ganhoho.viewmodel.GroupViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val bottomNavViewModel: BottomNavViewModel = viewModel()
    val groupRepository = GroupRepository()

    NavHost(
        navController = navController,
        startDestination = "home" //todo: 다른 걸로 바꿨을 시 다시 home으로 돌려놓기
    ) {
        composable("work") { WorkScreen(navController) }
        composable("pill") { PillScreen(navController) }
        composable("home") { HomeScreen() } // modifier 제거
        composable("group") {
            GroupScreen(
                navController = navController,
                bottomNavViewModel = bottomNavViewModel,
                repository = groupRepository,
                tokenManager = TokenManager
            )
        }
        composable("friend") { FriendScreen(navController) }

        composable("group/{groupIconType}") { backStackEntry ->
            val groupIconType = backStackEntry.arguments?.getString("groupIconType")?.toIntOrNull() ?: 1
            val groupViewModel: GroupViewModel = viewModel()

            GroupScreen(
                navController = navController,
                bottomNavViewModel = bottomNavViewModel,
                repository = groupRepository,
                tokenManager = TokenManager
            )
        }

        composable("EachGroupScreen/{groupJson}") { backStackEntry ->
            val groupJson = backStackEntry.arguments?.getString("groupJson")
            val group = groupJson?.let {
                try {
                    val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                    Gson().fromJson(decodedJson, GroupDto::class.java)
                } catch (e: Exception) {
                    Log.e("Navigation", "JSON 변환 실패: ${e.message}")
                    null
                }
            }

            if (group != null) {
                EachGroupScreen(
                    navController = navController,
                    group = group,
                    groupMember = getSampleMembers(),
                    memberSchedule = getSampleSchedules(),
                    repository = GroupRepository(),
                    tokenManager = TokenManager
                )
            } else {
                Log.e("Navigation", "group이 null이므로 EachGroupScreen을 열 수 없음")
            }
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

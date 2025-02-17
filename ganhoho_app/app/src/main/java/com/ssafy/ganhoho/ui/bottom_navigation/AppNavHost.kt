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
import com.ssafy.ganhoho.data.model.response.group.GroupViewModelFactory
import com.ssafy.ganhoho.data.repository.GroupRepository
import com.ssafy.ganhoho.ui.friend.FriendScreen
import com.ssafy.ganhoho.ui.group.EachGroupScreen
import com.ssafy.ganhoho.ui.group.GroupScreen
import com.ssafy.ganhoho.ui.group.getSampleMembers
import com.ssafy.ganhoho.ui.home.HomeScreen
import com.ssafy.ganhoho.ui.mypage.MyPageScreen
import com.ssafy.ganhoho.ui.mypage.UpdateMemberInfo
import com.ssafy.ganhoho.ui.pill.PillScreen
import com.ssafy.ganhoho.ui.work_schedule.WorkScreen
import com.ssafy.ganhoho.viewmodel.BottomNavViewModel
import com.ssafy.ganhoho.viewmodel.GroupViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val bottomNavViewModel: BottomNavViewModel = viewModel()
    val groupRepository = GroupRepository()

    val yearMonth: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("work") { WorkScreen(navController) }
        composable("pill") { PillScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("home") { HomeScreen(navController) } // modifier 제거
        composable("group") {
            GroupScreen(
                navController = navController,
                bottomNavViewModel = bottomNavViewModel,
                repository = groupRepository
            )
        }
        composable("friend") { FriendScreen(navController) }
        composable("mypage") { MyPageScreen(navController) }
        composable("update") { UpdateMemberInfo(navController) }  // 회원정보 수정 화면

        composable("group/{groupIconType}") { backStackEntry ->
            val groupIconType =
                backStackEntry.arguments?.getString("groupIconType")?.toIntOrNull() ?: 1
            val groupViewModel: GroupViewModel = viewModel()

            GroupScreen(
                navController = navController,
                bottomNavViewModel = bottomNavViewModel,
                repository = groupRepository
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
                val groupViewModel: GroupViewModel = viewModel(
                    factory = GroupViewModelFactory(groupRepository)
                )

                EachGroupScreen(
                    navController = navController,
                    group = group,
                    groupMember = getSampleMembers(),
                    repository = GroupRepository(),
                    groupId = group.groupId,
                    yearMonth = yearMonth
                )
            } else {
                Log.e("Navigation", "group이 null이므로 EachGroupScreen을 열 수 없음")
            }
        }

        composable("group/{groupId}") { backStackEntry ->
            var groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
            if (groupId == null) {
                Log.e("DEBUG_NAV", "groupId가 null이므로 네비게이션 실패")
                return@composable
            }

            Log.d("DEBUG_NAV", "EachGroupScreen으로 이동 - groupId: $groupId")

            EachGroupScreen(
                navController = navController,
                group = GroupDto(groupId, "Sample Group", 1, 5), // 필요시 수정
                groupMember = getSampleMembers(),
                repository = groupRepository,
                groupId = groupId,
                yearMonth = yearMonth
            )
        }
    }
}
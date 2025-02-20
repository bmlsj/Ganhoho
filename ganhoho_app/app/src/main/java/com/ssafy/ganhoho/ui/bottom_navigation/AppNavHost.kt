package com.ssafy.ganhoho.ui.bottom_navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.gson.Gson
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.response.group.GroupViewModelFactory
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.ui.AuthActivity
import com.ssafy.ganhoho.ui.auth.SearchHospital
import com.ssafy.ganhoho.ui.friend.FriendScreen
import com.ssafy.ganhoho.ui.group.EachGroupScreen
import com.ssafy.ganhoho.ui.group.GroupScreen
import com.ssafy.ganhoho.ui.group.getSampleMembers
import com.ssafy.ganhoho.ui.home.HomeScreen
import com.ssafy.ganhoho.ui.mypage.MyPageScreen
import com.ssafy.ganhoho.ui.mypage.NotificationScreen
import com.ssafy.ganhoho.ui.mypage.UpdateMemberInfo
import com.ssafy.ganhoho.ui.nav_host.Route
import com.ssafy.ganhoho.ui.pill.PillScreen
import com.ssafy.ganhoho.ui.work_schedule.WorkScreen
import com.ssafy.ganhoho.viewmodel.BottomNavViewModel
import com.ssafy.ganhoho.viewmodel.GroupViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 바텀 네비게이션을 나타내는 네비게이션
 */
@Composable
fun AppNavHost(navController: NavHostController, yearMonth: String) {
    val bottomNavViewModel: BottomNavViewModel = viewModel()
    val groupRepository = GroupRepository()

    NavHost(
        navController = navController,
        startDestination = Route.Home.route // ✅ 홈을 기본 화면으로 설정
    ) {
        composable(Route.Work.route) { WorkScreen(navController) }
        composable(Route.Pill.route) { PillScreen(navController) }
        composable(Route.Home.route) { HomeScreen(navController) }
        composable(Route.Group.route) {
            GroupScreen(
                navController = navController,
                bottomNavViewModel = bottomNavViewModel
            )
        }
        composable(Route.Friend.route) { FriendScreen(navController) }
        composable(Route.Mypage.route) { MyPageScreen(navController) }
        composable(Route.Notification.route) { NotificationScreen(navController) }
        composable(Route.UpdateInfo.route) { UpdateMemberInfo(navController) }
        composable(Route.HospitalInfo.route) { SearchHospital(navController) }

        // ✅ 그룹 화면 (딥링크 가능)
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
        // 병원 정보
        composable(Route.HospitalInfo.route) { SearchHospital(navController) }

    }
}

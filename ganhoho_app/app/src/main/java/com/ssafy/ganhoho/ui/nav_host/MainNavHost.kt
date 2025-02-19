package com.ssafy.ganhoho.ui.nav_host

import android.content.Context
import android.net.Uri
import android.util.Log
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.ui.MainScreen
import com.ssafy.ganhoho.ui.auth.JoinScreen
import com.ssafy.ganhoho.ui.auth.LoginScreen
import com.ssafy.ganhoho.ui.auth.SearchHospital
import com.ssafy.ganhoho.ui.friend.FriendScreen
import com.ssafy.ganhoho.ui.group.EachGroupScreen
import com.ssafy.ganhoho.ui.group.GroupScreen
import com.ssafy.ganhoho.ui.group.getSampleMembers
import com.ssafy.ganhoho.ui.home.HomeScreen
import com.ssafy.ganhoho.ui.mypage.MyPageScreen
import com.ssafy.ganhoho.ui.pill.PillScreen
import com.ssafy.ganhoho.ui.splash.AnimatedSplashScreen
import com.ssafy.ganhoho.ui.work_schedule.WorkScreen
import com.ssafy.ganhoho.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
/**
 * 최상위 네비게이션
 */
@Composable
fun MainNavHost(navController: NavHostController, deepLinkUri: Uri?) {

    val yearMonth: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    // ✅ 딥링크 감지 및 처리
    LaunchedEffect(deepLinkUri) {
        deepLinkUri?.let { uri ->
            val groupJson = uri.getQueryParameter("groupJson")
            if (!groupJson.isNullOrEmpty()) {
                val decodedJson = Uri.decode(groupJson)
                val group = try {
                    Gson().fromJson(decodedJson, GroupDto::class.java)
                } catch (e: Exception) {
                    Log.e("DeepLink", "JSON 변환 실패: ${e.message}")
                    null
                }

                if (group != null) {
                    Log.d("DeepLink", "딥링크 감지: 그룹 페이지로 이동 -> ${group.groupId}")
                    navController.navigate("EachGroupScreen/${Uri.encode(decodedJson)}") {
                        popUpTo(Route.Group.route) { inclusive = true }
                    }
                }
            }
        }
    }

    MainScreen(navController, yearMonth)

}

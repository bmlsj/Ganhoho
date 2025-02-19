package com.ssafy.ganhoho.ui.nav_host

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.response.group.GroupViewModelFactory
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.ui.AuthActivity
import com.ssafy.ganhoho.ui.MainScreen
import com.ssafy.ganhoho.ui.auth.JoinScreen
import com.ssafy.ganhoho.ui.auth.LoginScreen
import com.ssafy.ganhoho.ui.auth.SearchHospital
import com.ssafy.ganhoho.ui.group.EachGroupScreen
import com.ssafy.ganhoho.ui.group.GroupScreen
import com.ssafy.ganhoho.ui.group.getSampleMembers
import com.ssafy.ganhoho.ui.splash.AnimatedSplashScreen
import com.ssafy.ganhoho.viewmodel.GroupViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("ContextCastToActivity")
@Composable
fun AuthNavHost(navController: NavHostController, deepLinkUri: Uri?) {

    val context = LocalContext.current as AuthActivity
    val yearMonth: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    NavHost(
        navController = navController,
        startDestination = Route.Splash.route
    ) {
        // 스플래시
        composable(Route.Splash.route) { AnimatedSplashScreen(navController, deepLinkUri) }
        // 로그인
        composable(Route.Login.route) { LoginScreen(navController, deepLinkUri) }
        // 메인
        composable(Route.Main.route) { MainScreen(navController, yearMonth) }
        // 회원 가입
        composable(Route.Join.route) { JoinScreen(navController) }
        // 병원 정보
        composable(Route.HospitalInfo.route) { SearchHospital(navController) }

    }

}
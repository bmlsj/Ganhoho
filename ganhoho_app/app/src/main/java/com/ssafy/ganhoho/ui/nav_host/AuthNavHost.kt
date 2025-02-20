package com.ssafy.ganhoho.ui.nav_host

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ssafy.ganhoho.ui.auth.JoinScreen
import com.ssafy.ganhoho.ui.auth.LoginScreen
import com.ssafy.ganhoho.ui.auth.SearchHospital
import com.ssafy.ganhoho.ui.splash.AnimatedSplashScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("ContextCastToActivity")
@Composable
fun AuthNavHost(navController: NavHostController, deepLinkUri: Uri?) {

    val yearMonth: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    NavHost(
        navController = navController,
        startDestination = Route.Splash.route
    ) {
        // 스플래시
        composable(Route.Splash.route) { AnimatedSplashScreen(navController, deepLinkUri) }
        // 로그인
        composable(Route.Login.route) { LoginScreen(navController, deepLinkUri) }
        // 회원 가입
        composable(Route.Join.route) { JoinScreen(navController) }
        // 병원 정보
        composable(Route.HospitalInfo.route) { SearchHospital(navController) }

    }

}
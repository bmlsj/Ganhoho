package com.ssafy.ganhoho.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.ui.auth.JoinScreen
import com.ssafy.ganhoho.ui.auth.LoginScreen
import com.ssafy.ganhoho.ui.auth.SearchHospital
import com.ssafy.ganhoho.ui.splash.AnimatedSplashScreen
import com.ssafy.ganhoho.ui.theme.GANHOHOTheme

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainNavHost() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // 스플래시 화면
        composable("splash") { AnimatedSplashScreen(navController)}
        // 로그인 화면
        composable("login") { LoginScreen(navController) }
        // 메인 화면
        composable("main") { MainScreen() }
        // 회원 가입 화면
        composable("join") { JoinScreen(navController) }
        // 병원 정보 화면
        composable("hospitalInfo") { SearchHospital(navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    GANHOHOTheme {
        MainNavHost()
    }
}
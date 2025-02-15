package com.ssafy.ganhoho.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.ui.auth.AuthDataStore
import com.ssafy.ganhoho.ui.auth.HospitalInfoScreen
import com.ssafy.ganhoho.ui.auth.JoinScreen
import com.ssafy.ganhoho.ui.auth.LoginScreen
import com.ssafy.ganhoho.ui.bottom_navigation.CustomBottomNavigation
import com.ssafy.ganhoho.ui.theme.GANHOHOTheme

@Composable
fun MainNavHost(authDataStore: AuthDataStore) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // 로그인 화면
        composable("login") {
            LoginScreen(navController, authDataStore)
        }

        // 메인 화면
        composable("main") {
            CustomBottomNavigation(navController)
        }

        // 회원 가입 화면
        composable("join") {
            JoinScreen(navController)
        }

        // 병원 정보 화면
        composable("hospitalInfo") {
            HospitalInfoScreen(navController)
        }


    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val authDataStore = AuthDataStore(context) // Preview에서도 authDataStore 생성

    GANHOHOTheme {
        MainNavHost(authDataStore)
    }
}
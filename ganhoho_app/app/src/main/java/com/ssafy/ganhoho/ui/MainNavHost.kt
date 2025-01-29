package com.ssafy.ganhoho.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.ui.login.LoginScreen
import com.ssafy.ganhoho.ui.theme.GANHOHOTheme

@Composable
fun MainNavHost() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login") {

        // 로그인 화면
        composable("login") {
            LoginScreen(navController)
        }

        // 메인 화면
        composable("main") {
            MainScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    GANHOHOTheme {
        MainNavHost()
    }
}
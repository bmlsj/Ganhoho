package com.ssafy.ganhoho.ui.splash

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(navController: NavController) {

    val scale = remember {
        Animatable(0f)
    }

    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current
    val userInfo = authViewModel.userInfo.collectAsState().value

    // ✅ 앱 실행 시 자동 로그인 확인
    LaunchedEffect(Unit) {
        authViewModel.checkAutoLogin(context)
    }

    // ✅ 애니메이션 실행 (1초)
    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(1000)) // 1초 동안 확대 애니메이션 실행
    }

    // ✅ 스플래시 화면 최소 2초 유지 후 이동
    LaunchedEffect(userInfo) {
        delay(2000) // ⏳ 2초간 스플래시 화면 유지
        if (userInfo != null) {
            Log.d("SplashScreen", "자동 로그인 성공 → 메인 이동")
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            Log.d("SplashScreen", "자동 로그인 실패 → 로그인 이동")
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF79C7E3)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "GANHOHO",
            fontSize = 32.sp * scale.value, // 확대 애니메이션 적용
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }

}
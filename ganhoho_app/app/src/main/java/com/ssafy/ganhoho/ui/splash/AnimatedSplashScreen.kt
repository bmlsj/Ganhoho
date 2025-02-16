package com.ssafy.ganhoho.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(navController: NavController) {

    val scale = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(1000)) // 1초 동안 확대
        delay(1000)
        navController.navigate("main") {
            popUpTo("splash") { inclusive = true }
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
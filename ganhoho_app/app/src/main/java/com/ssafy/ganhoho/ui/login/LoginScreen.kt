package com.ssafy.ganhoho.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "로그인 화면")
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // ✅ 로그인 완료 후 MainScreen으로 이동
                navController.navigate("main") {
                    popUpTo("login") { inclusive = true } // 뒤로 가기 시 로그인 화면 제거
                }
            }) {
                Text("로그인")
            }
        }
    }

}
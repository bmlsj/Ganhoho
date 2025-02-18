package com.ssafy.ganhoho.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTopAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Icon(
                painter = painterResource(id = R.drawable.logo), // ✅ 여기에 logo 사용
                contentDescription = "로고",
                tint = Color.Unspecified,
                modifier = Modifier.size(80.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        actions = {

            IconButton(onClick = {
                navController.navigate("mypage")
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.mypage),
                    contentDescription = "마이 페이지",
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(onClick = {
                // 알림 조회 페이지로 이동
                navController.navigate("noti")
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.notification),
                    contentDescription = "알림 페이지",
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    val navController = rememberNavController()
    BasicTopAppBar(navController)
}
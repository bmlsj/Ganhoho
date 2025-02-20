package com.ssafy.ganhoho.ui.bottom_navigation//package com.example.calendar.ui.bottomnavigation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.R

@Composable
fun CustomBottomNavigation(navController: NavController) {

    // 리소스를 명확히 선언하여 수정
    val items = listOf(
        Triple("work", R.drawable.icon_nav_work, "근무일정"),
        Triple("pill", R.drawable.icon_nav_pill, "알약찾기"),
        Triple("home", R.drawable.icon_nav_home, "홈"),
        Triple("group", R.drawable.icon_nav_group, "그룹"),
        Triple("friend", R.drawable.icon_nav_friend, "친구")
    )

    val currentRoute = navController.currentBackStackEntryAsState().value

    Row(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (route, icon, title) ->

            when (route) {
                "work" -> stringResource(id = R.string.nav_work)
                "pill" -> stringResource(id = R.string.nav_pill)
                "home" -> stringResource(id = R.string.nav_home)
                "group" -> stringResource(id = R.string.nav_group)
                "friend" -> stringResource(id = R.string.nav_friend)
                else -> route
            }

            val isSelected = currentRoute?.destination?.route == route

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
                    .clickable {
                        if (!isSelected) {  // 클릭된 목적지가 현재 경로와 다를때만 실행(중복 클릭 방지)
                            navController.navigate(route) {
                                // 시작 화면으로 돌아가는 경로 저장
                                // 시작 화면 자체는 제거하지 않고, 그위의 스택만 제거
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }

                                // 스택의 맨 위에 현재 화면이 있는 경우, 재생성 하지 않음
                                // 중복된 화면 인스턴스 방지
                                launchSingleTop = true
                            }
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = if (isSelected) Color(0xFF79C7E3) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(  // 탭 이름
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF79C7E3) else Color.Gray
                )
            }

        }

    }
}


@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun BottomNav() {
    val navController = rememberNavController()
    CustomBottomNavigation(navController)
}

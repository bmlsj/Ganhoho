package com.ssafy.ganhoho.ui.bottom_navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.R

@Composable
fun CustomBottomNavigation(navController: NavController) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(65.dp)
                    .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
                cutoutShape = CircleShape, // FAB 컷아웃 적용
                backgroundColor = Color.White,
                elevation = 16.dp
            ) {
                BottomNav(navController = navController)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                shape = CircleShape,
                backgroundColor = Color(0xFF79C7E3)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.nav_home),
                    contentDescription = "홈",
                    Modifier.size(23.dp),
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavHost(navController)
        }
    }
}

@Composable
fun BottomNav(navController: NavController) {
    val currentRoute by navController.currentBackStackEntryAsState()

    val items = listOf(
        NavItem("work", R.drawable.bottom_calendar),
        NavItem("pill", R.drawable.bottom_pill),
        NavItem("home", R.drawable.bottom_home_white),
        NavItem("group", R.drawable.bottom_group),
        NavItem("friend", R.drawable.bottom_friend)
    )

    BottomNavigation(
        modifier = Modifier
            .padding(horizontal = 0.dp)
            .height(118.dp)
            .fillMaxWidth(),
        backgroundColor = Color.White,
        elevation = 10.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute?.destination?.route == item.route

            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.route,
                        modifier = Modifier.size(25.dp)
                            .padding(bottom = 5.dp),
                        tint = if (isSelected) Color(0xFFBCE0F5) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = routeToLabel[item.route] ?: item.route, // route 대신 한글 표시
                        fontSize = 12.sp,
                        color = if (isSelected) Color(0xFFBCE0F5) else Color.Gray
                    )
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

// route와 표시할 데이터 매핑
val routeToLabel = mapOf(
    "work" to "근무일정",
    "pill" to "알약찾기",
    "home" to "홈",
    "group" to "그룹",
    "friend" to "친구"
)

data class NavItem(val route: String, val icon: Int)

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    val navController = rememberNavController()
    Box(
        modifier = Modifier.fillMaxWidth() // Preview에서 너비를 가득 채우도록 설정
    ) {
        CustomBottomNavigation(navController)
    }
}
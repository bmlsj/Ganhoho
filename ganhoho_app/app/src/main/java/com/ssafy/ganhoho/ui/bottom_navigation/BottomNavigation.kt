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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.R

@Composable
fun CustomBottomNavigation(navController: NavController, modifier: Modifier = Modifier) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val items = listOf(
        Pair("work", R.drawable.nav_work),
        Pair("pill", R.drawable.nav_pill),
        Pair("home", R.drawable.nav_home),
        Pair("group", R.drawable.nav_group),
        Pair("friend", R.drawable.nav_friend)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),  // 간격 추가
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (route, icon) ->
            val isSelected = currentRoute?.startsWith(route) == true  // 동적 라우트 고려

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
                    .clickable {
                        if (!isSelected) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = route,
                    tint = Color.Gray,  // 선택된 아이콘 색상 수정
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = route.uppercase(),
                    fontSize = 12.sp,
                    color =  Color.Gray  // 가독성 향상
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavPreview() {
    val navController = rememberNavController()
    CustomBottomNavigation(navController, Modifier)
}



@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun BottomNav() {
    val navController = rememberNavController()
    CustomBottomNavigation(navController, Modifier.zIndex(0f))
}

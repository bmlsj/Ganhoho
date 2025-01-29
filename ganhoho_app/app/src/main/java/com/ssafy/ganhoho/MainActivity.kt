package com.ssafy.ganhoho

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.ganhoho.ui.bottom_navigation.CustomBottomNavigation
import com.ssafy.ganhoho.ui.friend.FriendScreen
import com.ssafy.ganhoho.ui.group.GroupScreen
import com.ssafy.ganhoho.ui.home.HomeScreen
import com.ssafy.ganhoho.ui.pill.PillScreen
import com.ssafy.ganhoho.ui.theme.GANHOHOTheme
import com.ssafy.ganhoho.ui.work_schedule.WorkScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GANHOHOTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun MainScreen() {
    val selectedItem = remember { mutableIntStateOf(2) } // 기본 Home

    BoxWithConstraints {
        val screenWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() }
        val itemWidth = screenWidth / 5   // 네비게이션 버튼 5개 기준

        Scaffold(
            floatingActionButton = {
                val fabOffsetX = calculateFabOffset(selectedItem.intValue, itemWidth)

                FloatingActionButton(
                    onClick = {
                        when (selectedItem.intValue) {
                            0 -> Log.d("FAB", "Work FAB clicked!")
                            1 -> Log.d("FAB", "Pill FAB clicked!")
                            2 -> Log.d("FAB", "Home FAB clicked!")
                            3 -> Log.d("FAB", "Group FAB clicked!")
                            4 -> Log.d("FAB", "Friend FAB clicked!")
                            else -> Log.d("FAB", "Default FAB clicked!")
                        }
                    },
                    containerColor = if (selectedItem.intValue == selectedItem.intValue) Color(
                        0xFF79C7E3
                    ) else Color(
                        0xFF5661FF
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .offset(x = fabOffsetX, y = (-10).dp) // FAB 이동
                        .size(70.dp)
                ) {
                    Icon(
                        painter = when (selectedItem.intValue) {
                            0 -> painterResource(id = R.drawable.nav_work)
                            1 -> painterResource(id = R.drawable.nav_pill)
                            3 -> painterResource(id = R.drawable.nav_group)
                            4 -> painterResource(id = R.drawable.nav_friend)
                            else -> painterResource(id = R.drawable.nav_home)
                        },
                        contentDescription = "FAB Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    // ✅ FAB 버튼 밑에 텍스트 추가
                    Text(
                        text = when (selectedItem.intValue) {
                            0 -> "근무"
                            1 -> "알약"
                            3 -> "그룹"
                            4 -> "친구"
                            else -> "홈"
                        },
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            },
            isFloatingActionButtonDocked = true,
            floatingActionButtonPosition = FabPosition.Center,
            bottomBar = {
                // CustomBottomAppBar(selectedItem = selectedItem.value, itemWidth = itemWidth) {
                CustomBottomNavigation(selectedItem)
                // }
            },
        ) { innerPadding ->
            when (selectedItem.intValue) {
                0 -> WorkScreen(modifier = Modifier.padding(innerPadding))
                1 -> PillScreen(modifier = Modifier.padding(innerPadding))
                2 -> HomeScreen(modifier = Modifier.padding(innerPadding))
                3 -> GroupScreen(modifier = Modifier.padding(innerPadding))
                4 -> FriendScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

// ✅ Cutout 이동을 동적으로 처리
//@SuppressLint("UnusedBoxWithConstraintsScope")
//@Composable
//fun CustomBottomAppBar(
//    selectedItem: Int,
//    itemWidth: Dp,
//    content: @Composable () -> Unit
//) {
//    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
//        val fabOffsetX = calculateFabOffset(selectedItem, itemWidth)
//        val density = LocalDensity.current
//
//        Box(modifier = Modifier.fillMaxWidth()) {
//            Canvas(modifier = Modifier.fillMaxWidth()) {
//                val offsetXPx = with(density) { fabOffsetX.toPx() }
//                drawCutout(offsetXPx)
//            }
//            content()
//        }
//    }
//}
//
//// ✅ Cutout을 FAB 위치에 맞춰 이동
//fun DrawScope.drawCutout(offsetX: Float) {
//    val cutoutRadius = 36.dp.toPx()
//    val cutoutY = size.height
//
//    drawIntoCanvas { canvas ->
//        val path = Path().apply {
//            moveTo(0f, cutoutY)
//            lineTo(offsetX - cutoutRadius, cutoutY)
//            cubicTo(
//                offsetX - cutoutRadius / 2, cutoutY - cutoutRadius,
//                offsetX + cutoutRadius / 2, cutoutY - cutoutRadius,
//                offsetX + cutoutRadius, cutoutY
//            )
//            lineTo(size.width, cutoutY)
//            lineTo(size.width, size.height)
//            lineTo(0f, size.height)
//            close()
//        }
//        canvas.drawPath(path, Paint().apply { color = Color.White })
//    }
//}

// ✅ FAB 버튼 위치 계산
@Composable
fun calculateFabOffset(selectedIndex: Int, itemWidth: Dp): Dp {
    return when (selectedIndex) {
        0 -> -itemWidth * 2 // 알약 찾기 위치 (왼쪽)
        1 -> -itemWidth // 알약 찾기 위치 (왼쪽)
        2 -> 0.dp // 홈 위치 (중앙)
        3 -> itemWidth  // 그룹 위치 (오른쪽)
        4 -> itemWidth * 2 // 그룹 위치 (오른쪽)
        else -> 0.dp
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    MainScreen()
}
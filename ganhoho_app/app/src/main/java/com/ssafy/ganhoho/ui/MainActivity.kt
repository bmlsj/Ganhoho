package com.ssafy.ganhoho.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.ui.auth.AuthDataStore
import com.ssafy.ganhoho.ui.auth.LoginScreen
import com.ssafy.ganhoho.ui.bottom_navigation.CustomBottomNavigation
import com.ssafy.ganhoho.ui.theme.GANHOHOTheme
import com.ssafy.ganhoho.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authDataStore = AuthDataStore(applicationContext) //DataStore 생성

        // 저장된 토큰 불러오기
        authViewModel.loadTokens(this)
        setContent {
            val navController = rememberNavController()
            val isLoggedIn by authDataStore.isLoggedIn.collectAsState(initial = false) // 자동 로그인 상태 확인

            GANHOHOTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) "main" else "login" // 자동 로그인 여부에 따라 시작 화면 결정
                        ///startDestination = "main"
                    ) {
                        composable("login") { LoginScreen(navController, authDataStore) }
                        composable("main") { CustomBottomNavigation(navController) }
                    }
                }
            }

            // 로그인 여부가 결정될 때까지 로딩 화면을 표시
//            if (isLoggedIn == null) {
//                // 로딩 UI를 보여줄 수 있음 (예: 스플래시 화면)
//                return@setContent
//            }
//
//            GANHOHOTheme {
//                Surface(color = MaterialTheme.colorScheme.background) {
//                    NavHost(
//                        navController = navController,
//                        startDestination = if (isLoggedIn == true) "main" else "login" // ✅ 자동 로그인 여부에 따라 시작 화면 결정
//                    ) {
//                        composable("login") { LoginScreen(navController, authDataStore) }
//                        composable("main") { MainScreen(navController, authDataStore) }
//                    }
//                }
//            }
        }
    }
}
//
//@SuppressLint("UseOfNonLambdaOffsetOverload")
//@Composable
//fun MainScreen(navController: NavHostController, authDataStore: AuthDataStore) {
//
//    val navController = rememberNavController()
//
//    // 현재 활성화된 경로(route)를 추적
//    val currentBackStackEntry =
//        navController.currentBackStackEntryAsState().value  // currentRoute 자동 업데이트
//    val currentRoute = currentBackStackEntry?.destination?.route ?: "home"
//
//    BoxWithConstraints {
//        val screenWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() }
//        val itemWidth = screenWidth / 5   // 네비게이션 버튼 5개 기준
//
//        Scaffold(
//            floatingActionButton = {
//
//                val fabOffsetX = calculateFabOffset(currentRoute, itemWidth)
//
//                FloatingActionButton(
//                    onClick = {
//                        if (currentRoute != "home") {
//                            navController.navigate("home")
//                        }
//                    },
//                    containerColor = Color(0xFF79C7E3),
//                    shape = CircleShape,
//                    modifier = Modifier
//                        .offset(x = fabOffsetX, y = (-13).dp) // FAB 이동
//                        .size(70.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(
//                            id = when (currentRoute) {
//                                "work" -> R.drawable.nav_work
//                                "pill" -> R.drawable.nav_pill
//                                "group" -> R.drawable.nav_group
//                                "friend" -> R.drawable.nav_friend
//                                else -> R.drawable.nav_home
//                            }
//                        ),
//                        contentDescription = "FAB Icon",
//                        tint = Color.White,
//                        modifier = Modifier.size(24.dp)
//                    )
//
//                }
//            },
//            isFloatingActionButtonDocked = true,
//            floatingActionButtonPosition = FabPosition.Center,
//            bottomBar = {
//                CustomBottomNavigation(navController)
//            },
//        ) { innerPadding ->
//
//            AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
//        }
//    }
//}

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
fun calculateFabOffset(currentRoute: String, itemWidth: Dp): Dp {
    return when (currentRoute) {
        "work" -> -itemWidth * 2 // 알약 찾기 위치 (왼쪽)
        "pill" -> -itemWidth // 알약 찾기 위치 (왼쪽)
        "home" -> 0.dp // 홈 위치 (중앙)
        "group" -> itemWidth  // 그룹 위치 (오른쪽)
        "friend" -> itemWidth * 2 // 그룹 위치 (오른쪽)
        else -> 0.dp
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authDataStore = AuthDataStore(context)

    CustomBottomNavigation(navController = navController)
}
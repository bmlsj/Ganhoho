package com.ssafy.ganhoho.ui

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.runtime.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.activity.viewModels
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.ui.bottom_navigation.AppNavHost
import com.ssafy.ganhoho.ui.bottom_navigation.CustomBottomNavigation
import com.ssafy.ganhoho.ui.theme.GANHOHOTheme
import com.ssafy.ganhoho.util.PermissionChecker
import com.ssafy.ganhoho.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 저장된 토큰 불러오기
        authViewModel.loadTokens(this)
        setContent {
            GANHOHOTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                   // MainScreen()
                    MainNavHost()
                }
            }
        }
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun MainScreen() {

    val navController = rememberNavController()

    // 현재 활성화된 경로(route)를 추적
    val currentBackStackEntry =
        navController.currentBackStackEntryAsState().value  // currentRoute 자동 업데이트
    val currentRoute = currentBackStackEntry?.destination?.route ?: "home"

    BoxWithConstraints {
        val screenWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() }
        val itemWidth = screenWidth / 5   // 네비게이션 버튼 5개 기준

        Scaffold(
            floatingActionButton = {

                val fabOffsetX = calculateFabOffset(currentRoute, itemWidth)

                FloatingActionButton(
                    onClick = {
                        if (currentRoute != "home") {
                            navController.navigate("home")
                        }
                    },
                    containerColor = Color(0xFF79C7E3),
                    shape = CircleShape,
                    modifier = Modifier
                        .offset(x = fabOffsetX, y = (-10).dp) // FAB 이동
                        .size(70.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = when (currentRoute) {
                                "work" -> R.drawable.nav_work
                                "pill" -> R.drawable.nav_pill
                                "group" -> R.drawable.nav_group
                                "friend" -> R.drawable.nav_friend
                                else -> R.drawable.nav_home
                            }
                        ),
                        contentDescription = "FAB Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                }
            },
            isFloatingActionButtonDocked = true,
            floatingActionButtonPosition = FabPosition.Center,
            bottomBar = {
                CustomBottomNavigation(navController)
            },
        ) { innerPadding ->

            AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
            CheckPermissionAndInitFCM()
        }
    }
}


// ✅ Compose에서 권한을 체크하고 FCM을 초기화하는 Composable 함수
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CheckPermissionAndInitFCM() {
    val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    val context = LocalContext.current // ✅ LocalContext 가져오기

    var permissionGranted by remember { mutableStateOf(false) } // ✅ 올바른 선언

    // 🔹 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allPermissionsGranted = result.values.all { it } // ✅ Boolean 값 저장
        permissionGranted = allPermissionsGranted // ✅ 직접 할당
        if (allPermissionsGranted) initFCM() // ✅ true일 때 실행
    }

    // 🔹 앱 실행 시 권한 자동 체크
    LaunchedEffect(Unit) {
        if (!PermissionChecker.hasPermissions(context, permissions)) { // ✅ context 전달
            permissionLauncher.launch(permissions)
        } else {
            permissionGranted = true
            initFCM()
        }
    }
}

// ✅ Firebase 메시징 초기화
fun initFCM() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("FCM Token", "FCM 토큰 가져오기 실패", task.exception)
            return@addOnCompleteListener
        }

        val token = task.result
        Log.d("FCM Token", "FCM Token: $token")
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
    MainScreen()
}
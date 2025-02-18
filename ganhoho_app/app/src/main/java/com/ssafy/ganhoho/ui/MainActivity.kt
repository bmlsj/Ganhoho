package com.ssafy.ganhoho.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.activity.viewModels
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.vectormap.KakaoMapSdk
import com.ssafy.ganhoho.BuildConfig.KAKAO_NATIVE_APP_KEY
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.data.model.response.group.GroupViewModelFactory
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.ui.auth.AuthDataStore
import com.ssafy.ganhoho.fcm.LocationWorker
import com.ssafy.ganhoho.ui.bottom_navigation.AppNavHost
import com.ssafy.ganhoho.ui.bottom_navigation.CustomBottomNavigation
import com.ssafy.ganhoho.ui.theme.GANHOHOTheme
import com.ssafy.ganhoho.util.PermissionChecker
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.GroupViewModel
import java.util.concurrent.TimeUnit

private const val TAG = "MainActivity"
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authDataStore = AuthDataStore(applicationContext) //DataStore 생성
        val repository = GroupRepository()

        // 앱 실행 시 딥링크 처리
        val groupViewModel: GroupViewModel = ViewModelProvider(
            this,
            GroupViewModelFactory(repository)
        )[GroupViewModel::class.java]

        val token = SecureDataStore.getAccessToken(applicationContext)
        //  handleDeepLink(intent, groupViewModel, token.toString())
        val deepLinkUri = intent?.data  // 딥링크 데이터

        // 저장된 토큰 불러오기
        authViewModel.loadTokens(this)
        // 카카오 맵
        KakaoMapSdk.init(
            this@MainActivity,
            KAKAO_NATIVE_APP_KEY
        )
        setContent {
            GANHOHOTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavHost(deepLinkUri)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun MainScreen() {
    val context = LocalContext.current // ✅ LocalContext 가져오기
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
                        .offset(x = fabOffsetX, y = (10).dp) // FAB 이동
                        .size(70.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = when (currentRoute) {
                                "work" -> R.drawable.icon_nav_work
                                "pill" -> R.drawable.icon_nav_pill
                                "group" -> R.drawable.icon_nav_group
                                "friend" -> R.drawable.icon_nav_friend
                                else -> R.drawable.icon_nav_home
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AppNavHost(
                    navController = navController
                )
                CheckPermissionAndInitFCM()
            }

        }
    }
}


// ✅ Compose에서 권한을 체크하고 FCM을 초기화하는 Composable 함수
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CheckPermissionAndInitFCM() {
    val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current // ✅ LocalContext 가져오기

    var permissionGranted by remember { mutableStateOf(false) } // ✅ 올바른 선언
    var backgroundPermissionGranted by remember { mutableStateOf(false) }
    // 🔹 권한 요청 런처
    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        backgroundPermissionGranted = isGranted

        if (backgroundPermissionGranted) {
            // 📌 3. 백그라운드 권한 승인 시 원하는 기능 실행
            scheduleLocationWorker(context)

        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allPermissionsGranted = result.values.all { it }
        if (allPermissionsGranted) {
            permissionGranted = allPermissionsGranted // ✅ 직접 할당
            Toast.makeText(context, "원활한 알림 on/off를 위해 항상 허용을 선택해주세요", Toast.LENGTH_SHORT).show()
            backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    // 🔹 앱 실행 시 권한 자동 체크
    LaunchedEffect(Unit) {
        if (!PermissionChecker.hasPermissions(context, permissions)) { // ✅ context 전달
            permissionLauncher.launch(permissions)
        } else {
            permissionGranted = true
            initFCM()
            if(!PermissionChecker.hasPermissions(context, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))) backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            else scheduleLocationWorker(context)
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

fun scheduleLocationWorker(context: Context) {
    Log.d(TAG, "scheduleLocationWorker: ")
    val workManager = WorkManager.getInstance(context)
    // 기존 작업이 등록되어 있는지 확인
    workManager.getWorkInfosByTag("LocationWorker").get().let { workInfos ->
//        if (workInfos.isNullOrEmpty()) {
            // 기존에 등록된 작업이 없으면 새로 등록
            val workRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .addTag("LocationWorker") // 중복 실행 방지용 태그 추가
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED) // 네트워크 연결 필요 (선택)
                        .build()
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                "LocationWorker",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
//        } else{
//            Log.d(TAG, "scheduleLocationWorker: already scheduled")
//        }
    }
}


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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    MainScreen()
}


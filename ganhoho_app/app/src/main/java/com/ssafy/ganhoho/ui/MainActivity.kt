package com.ssafy.ganhoho.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.data.model.response.group.GroupViewModelFactory
import com.ssafy.ganhoho.fcm.LocationWorker
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.ui.bottom_navigation.AppNavHost
import com.ssafy.ganhoho.ui.bottom_navigation.CustomBottomNavigation
import com.ssafy.ganhoho.ui.nav_host.Route
import com.ssafy.ganhoho.ui.pill.AndroidCameraInterface
import com.ssafy.ganhoho.ui.theme.GANHOHOTheme
import com.ssafy.ganhoho.util.PermissionChecker
import com.ssafy.ganhoho.util.bitmapToBase64
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

private const val TAG = "MainActivity"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : ComponentActivity() {

    private lateinit var androidCameraInterface: AndroidCameraInterface
    private lateinit var webView: WebView
    val yearMonth: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deepLinkUri = intent?.data  // 딥링크 데이터

        val repository = GroupRepository()

        // 앱 실행 시 딥링크 처리
        val groupViewModel: GroupViewModel = ViewModelProvider(
            this,
            GroupViewModelFactory(repository)
        )[GroupViewModel::class.java]

        setContent {
            GANHOHOTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MainScreen(navController, yearMonth, deepLinkUri, groupViewModel)
                }
            }

            val webView = WebView(this)
            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview(),
                onResult = { bitmap: Bitmap? ->
                    bitmap?.let {
                        val base64Image = bitmapToBase64(it) // Base64 변환
                        Log.d("webview", "캡처 이미지 변환 완료 ")
                        webView.evaluateJavascript(
                            "window.onImageCaptured('$base64Image')", null
                        )
                    }
                }
            )

            androidCameraInterface = AndroidCameraInterface(this, webView, cameraLauncher)
            webView.addJavascriptInterface(androidCameraInterface, "AndroidCameraInterface")

        }

    }

    // ✅ Compose에서는 권한 요청을 ActivityResultLauncher를 통해 처리해야 함
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "✅ 카메라 권한 허용됨! 카메라 실행")
            androidCameraInterface.launchCamera() // 🚀 카메라 실행
        } else {
            Log.e("MainActivity", "🚨 카메라 권한 거부됨!")
        }
    }

    fun requestCameraPermission() {

        Log.d("MainActivity", "📌 requestCameraPermission() 호출됨")
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            androidCameraInterface.launchCamera() // ✅ 이미 권한 있으면 바로 실행
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun MainScreen(
    navController: NavHostController,
    yearMonth: String,
    deepLinkUri: Uri?,
    groupViewModel: GroupViewModel
) {

    val context = LocalContext.current

    // 현재 활성화된 경로(route)를 추적
    val currentBackStackEntry =
        navController.currentBackStackEntryAsState().value  // currentRoute 자동 업데이트
    val currentRoute = currentBackStackEntry?.destination?.route ?: Route.Home.route

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
                                "home" -> R.drawable.icon_nav_home
                                "friend" -> R.drawable.icon_nav_friend
                                "noti" -> R.drawable.icon_nav_home
                                else -> R.drawable.icon_nav_group
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
                AppNavHost(navController, yearMonth)
                CheckPermissionAndInitFCM()
            }

        }
    }

    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            SecureDataStore.getAccessToken(context).collect { token ->
                Log.d("DisposableEffect", "HandleDeepLink: $token")
                val inviteCode = deepLinkUri?.getQueryParameter("groupCode")

                if (!inviteCode.isNullOrEmpty()) {
                    Log.d("DeepLink", "초대코드 감지: $inviteCode")

                    if (token != null) {
                        Log.d("DeepLink", "토큰 확인 완료 : $token")

                        // 그룹 가입 후 그룹 화면으로 이동
                        groupViewModel.joinGroupByInviteCode(token, inviteCode,
                            onSuccess = {
                                Log.d("DeepLink", "초대 수락 성공! 그룹 화면으로 이동")

                                navController.navigate(Route.Home.route) {
                                    popUpTo(Route.Splash.route) { inclusive = true }
                                }

                                // ✅ Main 이동 후 AppNavHost에서 그룹 화면으로 이동
                                navController.navigate(Route.Group.route)
                            },
                            onFailure = { error ->
                                Log.e("DeepLink", "초대 수락 실패: $error")
                                navController.navigate(Route.Group.route)

                            })
                    } else {  // 토큰 없으니 내쫓기
                        val intent = Intent(context, AuthActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                }
            }
        }
        onDispose { job.cancel() }
    }

}


// ✅ Compose에서 권한을 체크하고 FCM을 초기화하는 Composable 함수
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CheckPermissionAndInitFCM() {
    val permissions = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
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
            if (!PermissionChecker.hasPermissions(
                    context,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                )
            ) backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
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
        if (workInfos.isNullOrEmpty()) {
            // 기존에 등록된 작업이 없으면 새로 등록
            val workRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .addTag("LocationWorker") // 중복 실행 방지용 태그 추가
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                "LocationWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        } else {
            Log.d(TAG, "scheduleLocationWorker: already scheduled")
        }
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
        "noti" -> 0.dp
        else -> itemWidth
    }
}
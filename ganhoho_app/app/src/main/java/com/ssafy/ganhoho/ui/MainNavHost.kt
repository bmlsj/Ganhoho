package com.ssafy.ganhoho.ui

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.ui.auth.JoinScreen
import com.ssafy.ganhoho.ui.auth.LoginScreen
import com.ssafy.ganhoho.ui.auth.SearchHospital
import com.ssafy.ganhoho.ui.group.GroupScreen
import com.ssafy.ganhoho.ui.splash.AnimatedSplashScreen
import com.ssafy.ganhoho.viewmodel.GroupViewModel

@Composable
fun MainNavHost(deepLinkUri: Uri?) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val groupViewModel: GroupViewModel = viewModel()

    val token =
        SecureDataStore.getAccessToken(context).collectAsState(initial = null).value
    Log.d("DeepLink", "토큰 가져오기 완료: $token")

    val deepLinkState by remember { mutableStateOf(deepLinkUri) }

    // ✅ 딥링크 처리 함수 호출
    HandleDeepLink(deepLinkState, token, navController, groupViewModel)

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // 스플래시 화면
        composable("splash") { AnimatedSplashScreen(navController) }
        // 로그인 화면
        composable("login") { LoginScreen(navController) }
        // 메인 화면
        composable("main") { MainScreen() }
        // 회원 가입 화면
        composable("join") { JoinScreen(navController) }
        // 병원 정보 화면
        composable("hospitalInfo") { SearchHospital(navController) }
        // ✅ 그룹 화면 추가
        composable("group") { GroupScreen(navController) }


    }

}

/**
 * ✅ 딥링크를 처리하는 함수
 * - 초대 코드(`groupCode`)가 감지되면 해당 그룹에 자동 가입 시도
 * - 성공 시 `group` 화면으로 이동
 * - 실패 시 `login` 화면으로 이동
 */
@Composable
fun HandleDeepLink(
    deepLinkUri: Uri?,
    token: String?,
    navController: NavController,
    groupViewModel: GroupViewModel
) {
    // ✅ 이미 처리한 초대 코드 추적하여 중복 실행 방지
    val processedInviteCode by remember { mutableStateOf<String?>(null) }

    // 딥링크 처리
    LaunchedEffect(deepLinkUri, token) {

        val inviteCode = deepLinkUri?.getQueryParameter("groupCode")

        if (!inviteCode.isNullOrEmpty() && inviteCode != processedInviteCode) {
            Log.d("DeepLink", "초대코드 감지: $inviteCode")

            if (token != null) {
                Log.d("DeepLink", "토큰 확인 완료 : $token")

                // 그룹 가입 후 그룹 화면으로 이동
                groupViewModel.joinGroupByInviteCode(token, inviteCode,
                    onSuccess = {
                        Log.d("DeepLink", "초대 수락 성공! 그룹 화면으로 이동")
                        navController.navigate("group") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    onFailure = { error ->
                        Log.e("DeepLink", "초대 수락 실패: $error")
                    })
            } else {
                Log.e("DeepLink", "토큰 없음. 로그인 필요")
                navController.navigate("login")
            }
        }
    }
}
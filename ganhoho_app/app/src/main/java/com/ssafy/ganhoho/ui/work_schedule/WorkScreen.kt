package com.ssafy.ganhoho.ui.work_schedule

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.ganhoho.BuildConfig.WEBVIEW_WORK_URL
import com.ssafy.ganhoho.data.model.response.member.MyPageResponse
import com.ssafy.ganhoho.util.WebViewWithToken
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.MemberViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WorkScreen(navController: NavController) {

    val authViewModel: AuthViewModel = viewModel()
    val memberViewModel: MemberViewModel = viewModel()

    // 토큰 로드하기
    val token = authViewModel.accessToken.collectAsState().value
    val refreshToken = authViewModel.refreshToken.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        } else {
            Log.d("token", token)
        }
    }


    // 웹뷰 구성
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (token != null && refreshToken != null) {
            WebViewWithToken(
                url = WEBVIEW_WORK_URL,
                token = token,
                refreshToken = refreshToken,
                enableCamera = false // ✅ 카메라 기능 비활성화
            )
        }
    }

}

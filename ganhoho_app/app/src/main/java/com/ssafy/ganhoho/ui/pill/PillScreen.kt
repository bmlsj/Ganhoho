package com.ssafy.ganhoho.ui.pill

import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.ganhoho.BuildConfig.WEBVIEW_PILL_URL
import com.ssafy.ganhoho.data.model.response.member.MyPageResponse
import com.ssafy.ganhoho.util.WebViewWithToken
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.MemberViewModel

@Composable
fun PillScreen(navController: NavController) {

    val authViewModel: AuthViewModel = viewModel()
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

    // ✅ Base64 이미지 상태 (카메라 촬영 후 저장됨)
    var capturedImageBase64 by remember { mutableStateOf<String?>(null) }
    // ✅ WebView 상태 관리
    val webViewState = remember { mutableStateOf<WebView?>(null) }

    // 웹뷰 구성
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (token != null && refreshToken != null ) {
            WebViewWithToken(
                url = WEBVIEW_PILL_URL,
                token = token,
                refreshToken = refreshToken
            )
        }
    }

    // ✅ Base64 이미지가 생성되면 웹뷰에 전달
    LaunchedEffect(capturedImageBase64) {
        capturedImageBase64?.let { base64 ->
            Log.d("chromium", "Captured Image: $base64")
            webViewState.value?.evaluateJavascript(
                "window.onImageCaptured('$base64')", null
            )
        }
    }


}

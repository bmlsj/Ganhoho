package com.ssafy.ganhoho.util

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.ssafy.ganhoho.ui.LoadingScreen
import com.ssafy.ganhoho.ui.MainActivity
import com.ssafy.ganhoho.ui.pill.AndroidCameraInterface
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream

@SuppressLint("SetJavaScriptEnabled", "UnrememberedMutableState", "ContextCastToActivity")
@Composable
fun WebViewWithToken(
    url: String,
    token: String,
    refreshToken: String,
    enableCamera: Boolean = true
) {

    // ✅ 파일 선택 콜백 변수 선언 (WebView -> Activity Result로 전달)
    var fileChooserCallback by remember {
        mutableStateOf<ValueCallback<Array<Uri>>?>(null)
    }

    // ✅ 파일 선택 ActivityResultLauncher
    val fileChooserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        fileChooserCallback?.onReceiveValue(uri?.let { arrayOf(it) } ?: emptyArray())
        fileChooserCallback = null
    }

    // ✅ 웹뷰 상태 관리
    val webViewState = remember { mutableStateOf<WebView?>(null) }

    // ✅ 카메라 실행 및 결과 받기
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? ->
            bitmap?.let {
                val base64Image = bitmapToBase64(it) // Base64 변환
                Log.d("webview", "캡처 이미지 변환 완료 ")
                webViewState.value?.evaluateJavascript(
                    "window.onImageCaptured('$base64Image')", null
                )
            }
        }
    )


    // 로딩
    var isLoading by remember { mutableStateOf(true) }  // 로딩 상태 관리
    val loadingState by rememberUpdatedState(isLoading)

    LaunchedEffect(Unit) {
        delay(500)
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            LoadingScreen(isLoading = loadingState)
        } else {
            AndroidView(
                factory = { context ->
                    WebView.setWebContentsDebuggingEnabled(true)
                    WebView(context).apply {

                        webViewState.value = this // ✅ 웹뷰 상태 저장

                        // ✅ enableCamera가 true일 때만 JavaScript 인터페이스 추가
                        if (enableCamera) {
                            addJavascriptInterface(AndroidCameraInterface(context as MainActivity, this, cameraLauncher),
                                "AndroidCameraInterface")
                        }

                        settings.apply {
                            javaScriptEnabled = true // ✅ JavaScript 활성화
                            domStorageEnabled = true // ✅ DOM 스토리지 활성화
                            cacheMode = WebSettings.LOAD_NO_CACHE // ✅ 캐시 기본 설정
                            allowContentAccess = true
                            allowFileAccess = true  // 파일 접근 허용
                            loadsImagesAutomatically = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                        }

                        // ✅ 파일 선택 (갤러리, 문서 업로드 지원)
                        webChromeClient = object : WebChromeClient() {
                            override fun onShowFileChooser(
                                webView: WebView?,
                                filePathCallback: ValueCallback<Array<Uri>>?,
                                fileChooserParams: FileChooserParams?
                            ): Boolean {
                                fileChooserCallback = filePathCallback
                                fileChooserLauncher.launch(arrayOf("*/*")) // ✅ 모든 파일 선택 가능
                                return true
                            }
                        }


                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                evaluateJavascript(
                                    "javascript:receiveToken('$token', '$refreshToken')",
                                    null
                                )
                            }
                        }

                        loadUrl(url) // ✅ URL 로드
                    }


                },
                modifier = Modifier.fillMaxSize()

            )
        }
    }

}

// ✅ Bitmap → Base64 변환 함수
fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}
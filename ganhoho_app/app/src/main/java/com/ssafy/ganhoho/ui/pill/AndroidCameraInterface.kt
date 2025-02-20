package com.ssafy.ganhoho.ui.pill

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ssafy.ganhoho.ui.MainActivity
import java.io.ByteArrayOutputStream

class AndroidCameraInterface(
    private val activity: MainActivity,
    private val webView: WebView,
    private val cameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?> // 📌 결과를 Compose에 전달
) {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1001
    }

    // ✅ 웹에서 openNativeCamera() 호출 시 실행
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @JavascriptInterface
    fun openCamera() {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 🚨 카메라 권한 요청
            activity.requestCameraPermission()
            Log.e("AndroidCameraInterface", "🚨 카메라 권한이 없어 요청함.")


        } else {
            launchCamera()
        }
    }

    // ✅ 카메라 실행을 따로 함수로 분리
    fun launchCamera() {
        try {
            Log.d("AndroidCameraInterface", "📸 카메라 실행됨!")
            cameraLauncher.launch(null) // 카메라 실행
        } catch (e: Exception) {
            Log.e("AndroidCameraInterface", "🚨 카메라 실행 중 오류 발생", e)
        }
    }

    // ✅ 카메라 촬영 후 결과 처리
    fun onImageCaptured(data: Intent?) {
        val bitmap = data?.extras?.get("data") as? Bitmap ?: return

        // ✅ Bitmap → PNG 압축 후 Base64 변환
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64Image = Base64.encodeToString(byteArray, Base64.NO_WRAP)

        // ✅ 웹뷰에 Base64 이미지 전달
        webView.post {
            webView.evaluateJavascript("window.onImageCaptured('$base64Image')", null)
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ✅ 사용자가 카메라 권한을 허용함 -> 카메라 실행
                Log.d("AndroidCameraInterface", "✅ 카메라 권한이 허용됨!")
                launchCamera()  // 🚨 카메라 다시 실행
            } else {
                // ❌ 사용자가 카메라 권한을 거부함
                Log.e("AndroidCameraInterface", "🚨 카메라 권한이 거부됨!")
            }
        }
    }
}



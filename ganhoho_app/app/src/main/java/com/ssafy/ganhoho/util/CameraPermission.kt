package com.ssafy.ganhoho.util

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun CameraPermission() {
    val context = LocalContext.current

    // Android 13 이상에서만 알림 권한 요청
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("CameraPermission", "카메라 권한 허용됨")
        } else {
            Log.e("CameraPermission", "카메라 권한 거부됨")
        }
    }

        // 권한 요청 실행
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

}

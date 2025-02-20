package com.ssafy.ganhoho.util

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun RequestMultiplePermissions() {
    // 요청할 권한 목록 생성
    val permissionsToRequest = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // 여러 권한 요청을 위한 launcher 생성
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (permission, isGranted) ->
            if (isGranted) {
                Log.d("Permissions", "$permission 권한이 허용되었습니다.")
            } else {
                Log.e("Permissions", "$permission 권한이 거부되었습니다.")
            }
        }
    }

    // 최초 컴포지션 시 권한 요청 실행
    LaunchedEffect(Unit) {
        permissionsLauncher.launch(permissionsToRequest.toTypedArray())
    }
}
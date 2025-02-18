package com.ssafy.ganhoho.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

fun requestBackgroundLocationPermission(context: Context, onResult: (Boolean) -> Unit) {
    val backgroundPermissionLauncher =
        (context as? ComponentActivity)?.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onResult(isGranted)
        }

    backgroundPermissionLauncher?.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
}
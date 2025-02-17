package com.ssafy.ganhoho.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun requestBackgroundLocationPermission(context: Context) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            (context as android.app.Activity),
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            10
        )
    }
}

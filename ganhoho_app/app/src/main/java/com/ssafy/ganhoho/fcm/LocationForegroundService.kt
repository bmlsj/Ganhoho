package com.ssafy.ganhoho.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.ssafy.ganhoho.R

class LocationForegroundService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "onCreate: ")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d("LocationService", "onCreate2: ")
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()
        Log.d("LocationService", "onCreate3: ")
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.d("LocationService", "onLocationResult: ")
                locationResult.lastLocation?.let { location ->
                    Log.d("LocationService", "Lat: ${location.latitude}, Lng: ${location.longitude}")
                }
                Log.d("LocationService", "onLocationResult22: ")
            }
        }


        Log.d("LocationService", "onCreate33333: ")
        startForeground(1, createNotification())
        Log.d("LocationService", "AfterStartForeground: ")
        startLocationUpdates()
        Log.d("LocationService", "RMX~~~: ")
    }


    private fun createNotification(): Notification {
        val channelId = "location_service"
        val channelName = "Location Service"
        Log.d("LocationService", "createNotification: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        Log.d("LocationService", "beforecreateNotification: ")
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("위치 추적 중")
            .setContentText("백그라운드에서 위치 정보를 업데이트합니다.")
            .setSmallIcon(R.drawable.nav_pill)
            .build()
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

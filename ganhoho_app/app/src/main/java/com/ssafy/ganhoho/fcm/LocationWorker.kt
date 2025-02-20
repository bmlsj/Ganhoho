package com.ssafy.ganhoho.fcm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.JsonObject
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TAG = "LocationWorker"

class LocationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return Result.failure()
        }

        val distance = requestLocationUpdates()
        val subscriptionInfo = getSubscriptionInfo()
        val token = getAccessToken()
        token?.let {
            if(distance > 0) {
                if(distance > 300 && subscriptionInfo == true) { // 이제 퇴근함
                    // 구독해제하는 걸로 api 던지기
                    val jsonObject = JsonObject().apply {
                        addProperty("isSubscribed", false)
                    }
                    val requestBody = RequestBody.create(
                        "application/json".toMediaTypeOrNull(),
                        jsonObject.toString()
                    )
                    repository.changeSubscription(token, requestBody).let {
                        it.onSuccess {
                            SecureDataStore.saveSubscriptionInfo(mContext, false)
                            createNotification("퇴근","집가자")
                        }
                    }
                } else if(distance <= 300 && subscriptionInfo == false) { // 이제 출근함
                    // 구독하는 걸로 api 던지기
                    val jsonObject = JsonObject().apply {
                        addProperty("isSubscribed", true)
                    }
                    val requestBody = RequestBody.create(
                        "application/json".toMediaTypeOrNull(),
                        jsonObject.toString()
                    )
                    repository.changeSubscription(token, requestBody).let {
                        it.onSuccess {
                            SecureDataStore.saveSubscriptionInfo(mContext, true)
                            createNotification("출근","일하자")
                        }
                    }
                } else {
//                    createNotification("api 요청 안함","같은 상태 :${subscriptionInfo}")
                }
            } else {
//                createNotification("위치 찾기 불가능","${subscriptionInfo}")
            }
        }
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestLocationUpdates(): Float = suspendCancellableCoroutine { cont ->
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 900000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    if (location == null) {
                        cont.resume(0F)
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                mLocation = location
                                val distance = calculateDistanceSuspend(location)
                                cont.resume(distance)
                            } catch (e: Exception) {
                                cont.resumeWithException(e)
                            }
                        }
                    }
                    Log.d("LocationService", "Lat: ${location.latitude}, Lng: ${location.longitude}")
                }
                Log.d("LocationService", "onLocationResult22: ")
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val mContext = context
    private lateinit var mLocation: Location
    private val repository = NotificationRepository()

    suspend fun getLocation(): Float = suspendCancellableCoroutine { cont ->
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)

        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,null)
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    cont.resume(0F)
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            mLocation = location
                            val distance = calculateDistanceSuspend(location)
                            cont.resume(distance)
                        } catch (e: Exception) {
                            cont.resumeWithException(e)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

    suspend fun calculateDistanceSuspend(location: Location): Float = withContext(Dispatchers.IO) {
        val hospitalLat = SecureDataStore.getHospitalLocationLat(mContext).firstOrNull()
        val hospitalLng = SecureDataStore.getHospitalLocationLng(mContext).firstOrNull()
        if (hospitalLat != null && hospitalLng != null) {
            val results = FloatArray(1)
            Location.distanceBetween(
                location.latitude,
                location.longitude,
                hospitalLat,
                hospitalLng,
                results
            )
            results[0]
        } else {
            0F
        }
    }

    private suspend fun getSubscriptionInfo(): Boolean? {
        var result: Boolean? = null
        val subscriptionInfo = SecureDataStore.getSubscriptionInfo(mContext).firstOrNull()
        subscriptionInfo?.let {
            result = it
        }
        return result
    }

    private suspend fun getAccessToken(): String? {
        return SecureDataStore.getAccessToken(mContext).first()
    }

    @SuppressLint("MissingPermission")
    private fun createNotification(title: String, message: String) {
        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널 설정 (Android 8.0 이상 필수)
        val channel = NotificationChannel(
            "default",
            "기본 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "앱 기본 푸시 알림"
        }
        notificationManager.createNotificationChannel(channel)

        // 🔹 작은 아이콘 설정 (이 아이콘이 없으면 앱이 크래시 발생!)
        val smallIcon = R.drawable.icon_notification // 🚨 여기에 작은 아이콘을 설정해야 함!

        val notificationBuilder = NotificationCompat.Builder(mContext, "default")
            .setSmallIcon(smallIcon)  // 🔥 작은 아이콘 추가 (필수)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setLocalOnly(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .extend(
                NotificationCompat.WearableExtender()
                .setBridgeTag("testOne"))
            .build()

        NotificationManagerCompat.from(mContext).notify(0, notificationBuilder);

    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        val cancellationToken = CancellationTokenSource().token
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken)
            .addOnSuccessListener { location ->
                location?.let {
                    Log.d("LocationWorker", "Lat: ${it.latitude}, Lng: ${it.longitude}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocationWorker", "Failed to get location", e)
            }
    }

}
package com.ssafy.ganhoho.fcm

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.repository.NotificationRepository
import com.ssafy.ganhoho.util.requestLocationPermission
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

/*
해야할 일
1. workmanager success, fail 분기 처리하기
2. 거리 비교하기 300m보다 큰지 비교해서 클 경우, 구독 해제
3. 300m보다 작을 경우, 구독하기
--> 계속 구독요청보내기도 좀 그런데....
--> 구독했는지 안했는지 여부를 datastore에 저장해두고, 가져와서 값이 반대일 때만 던지기?
4. 회원가입 시, 병원 lat, lng 값 같이 보내기
5. 병원병동 정보 수정 시, 병원 lat, lng 값 같이 보내기
6. 로그인 시, 병원 lat, lng 값 같이 받아서 datastore에 저장해두기
 */

/*
체크할 사항
1. 현재 위치를 받아오는 걸로 로직 수정
2. 함수 호출 및 처리 순서 정리
 */

class LocationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {
        val distance = getLocation()
        val subscriptionInfo = getSubscriptionInfo()
        val token = getAccessToken()
        token?.let {
            Log.d(TAG, "token check in doWork: ${token}")
            if(distance > 0) {
                if(distance > 300 && subscriptionInfo == true) { // 이제 퇴근함
                    // 구독해제하는 걸로 api 던지기
                    Log.d(TAG, "doWork: xhlrmsgka ")
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
                        }
                    }
                } else if(distance <= 300 && subscriptionInfo == false) { // 이제 출근함
                    // 구독하는 걸로 api 던지기
                    Log.d(TAG, "doWork: cnfrmsgka ")
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
                        }
                    }
                } else {
                    Log.d(TAG, "doWork: no api request ")
                }
            } else {
                Log.d(TAG, "doWork: cannot check distance")
            }
        }
        return Result.success()
    }
    private val mContext = context
    private lateinit var mLocation: Location
    private val repository = NotificationRepository()
    private var distance = -1f

    suspend fun getLocation(): Float = suspendCancellableCoroutine { cont ->
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        // Worker에서 권한 처리는 보통 이미 처리된 상태라고 가정합니다.
        requestLocationPermission(mContext)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    Log.d(TAG, "getLocation: location is null")
                    cont.resume(0F)
                } else {
                    Log.d(TAG, "getLocation: 위치 정보 수신 - lat: ${location.latitude}, lng: ${location.longitude}")
                    // calculateDistanceSuspend를 호출하여 병원과의 거리를 계산하고 결과를 반환
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            mLocation = location
                            val distance = calculateDistanceSuspend(location)
                            Log.d(TAG, "getLocation: 계산된 거리 = $distance")
                            cont.resume(distance)
                        } catch (e: Exception) {
                            cont.resumeWithException(e)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "getLocation: 실패 - ${exception.message}")
                cont.resumeWithException(exception)
            }
    }

    suspend fun calculateDistanceSuspend(location: Location): Float = withContext(Dispatchers.IO) {
        // SecureDataStore가 Flow<Float?>를 반환한다고 가정. (실제 구현에 맞게 수정)
        val hospitalLat = SecureDataStore.getHospitalLocationLat(mContext).firstOrNull()
        val hospitalLng = SecureDataStore.getHospitalLocationLng(mContext).firstOrNull()
        Log.d(TAG, "calculateDistanceSuspend: ${hospitalLat} ${hospitalLng} ")
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
            Log.d(
                TAG,
                "getSubscriptionInfo: it is not null it: ${it} result: ${result}"
            )
        }
        return result
    }

    private suspend fun getAccessToken(): String? {
        return SecureDataStore.getAccessToken(mContext).first()
    }

}
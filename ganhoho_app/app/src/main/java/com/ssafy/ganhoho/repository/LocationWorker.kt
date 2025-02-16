package com.ssafy.ganhoho.repository

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.ssafy.ganhoho.base.SecureDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

private const val TAG = "LocationWorker"
class LocationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {
        val token = getAccessToken()
        val distance = getLocation()
        val subscriptionInfo = getSubscriptionInfo()
        token?.let {
            Log.d(TAG, "token check in doWork: ${token}")
            if(distance > 0) {
                if(distance > 300 && subscriptionInfo == true) { // 이제 퇴근함
                    // 구독해제하는 걸로 api 던지기
                    Log.d(TAG, "doWork: xhlrmsgka ")
                    val jsonObject = JsonObject().apply {
                        addProperty("isSubscribed", false)
                    }
                    val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
                    repository.changeSubscription(token, requestBody).let {
                        it.onSuccess {
                            SecureDataStore.saveSubscriptionInfo(mContext,false)
                        }
                    }
                } else if(distance <= 300 && subscriptionInfo == false) { // 이제 출근함
                    // 구독하는 걸로 api 던지기
                    Log.d(TAG, "doWork: cnfrmsgka ")
                    val jsonObject = JsonObject().apply {
                        addProperty("isSubscribed", true)
                    }
                    val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
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


    private fun getLocation(): Float {
        var distance = -1f
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(mContext)

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    mLocation = location
                    distance = calculateDistance()
                    Log.d(TAG, "getLocation: ${distance}")
                }
            }
            .addOnFailureListener { fail ->
                Log.d(TAG, "getLocation: fail ${fail.message}")
            }
        return distance
    }

    private fun calculateDistance(): Float{
        var distance = -1f
        if(::mLocation.isInitialized) {
            mLocation.let { location ->
                CoroutineScope(Dispatchers.IO).launch {
                    val hospitalLat = SecureDataStore.getHospitalLocationLat(mContext).firstOrNull()
                    val hospitalLng = SecureDataStore.getHospitalLocationLng(mContext).firstOrNull()
                    val results = FloatArray(1)
                    hospitalLng?.let {
                        Location.distanceBetween(location.latitude, location.longitude, hospitalLat!!, hospitalLng, results)
                        distance = results[0]
                        Log.d(TAG, "calculateDistance: ${distance}")
                    }
                }
            }
        } else{
            Log.d(TAG, "calculateDistance: is not initialized")
        }
        return distance
    }

    private suspend fun getSubscriptionInfo(): Boolean? {
        var result: Boolean? = null
        val subscriptionInfo = SecureDataStore.getSubscriptionInfo(mContext).firstOrNull()
        subscriptionInfo?.let {
            result = it
            Log.d(TAG, "getSubscriptionInfo: it is not null it: ${it} result: ${result}")
        }
        return result
    }

    private suspend fun getAccessToken(): String? {
        return SecureDataStore.getAccessToken(mContext).first()
    }

}

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
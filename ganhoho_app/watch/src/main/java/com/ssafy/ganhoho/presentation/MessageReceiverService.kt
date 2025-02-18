package com.ssafy.ganhoho.presentation

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MessageReceiverService"
class MessageReceiverService : WearableListenerService() {
    private val dataStore = UserDataStore()

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/jwt_token") {
            val receivedData = String(messageEvent.data)
            // 받은 데이터 처리
            Log.d(TAG, "onMessageReceived: receivedData: $receivedData")

            CoroutineScope(Dispatchers.IO).launch {
                dataStore.saveAccessToken(receivedData)
            }
        }
    }
}

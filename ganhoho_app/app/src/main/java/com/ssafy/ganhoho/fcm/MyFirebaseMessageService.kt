package com.ssafy.ganhoho.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.ganhoho.R

private const val TAG = "MyFirebaseMsgSvc"

class MyFirebaseMessageService : FirebaseMessagingService() {

    // 새로운 토큰이 생성될 때마다 호출
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
        // 서버로 토큰 업로드
        // MainActivity.uploadToken(token)
    }

    // 메시지 수신 시 호출
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var messageTitle = ""
        var messageContent = ""

        Log.d("remoteMessage", remoteMessage.toString())
        // Notification이 있는 경우 (Foreground 처리)
        if (remoteMessage.notification != null) {

            messageTitle = remoteMessage.notification!!.title.toString()
            messageContent = remoteMessage.notification!!.body.toString()
        } else { // Data 메시지 처리 (Foreground와 Background 모두)
            val data = remoteMessage.data
            Log.d(TAG, "Data received: $data")
            messageTitle = data["title"].orEmpty()
            messageContent = data["body"].orEmpty()
        }

        // 알림 생성 및 표시
        createNotification(messageTitle, messageContent)

        // 서버에 알림 저장
        //  saveNotificationToServer(messageTitle, messageContent, messageChannel)

    }

    private fun createNotification(title: String, message: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

        val notificationBuilder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(smallIcon)  // 🔥 작은 아이콘 추가 (필수)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(0, notificationBuilder.build())
    }

    //      private fun saveNotificationToServer(title: String, content: String, c

}
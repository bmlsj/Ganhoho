package com.ssafy.ganhoho.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.ganhoho.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        var messageChannel = ""

        Log.d("remoteMessage", remoteMessage.toString())
        // Notification이 있는 경우 (Foreground 처리)
        if (remoteMessage.notification != null) {

            messageTitle = remoteMessage.notification!!.title.toString()
            messageContent = remoteMessage.notification!!.body.toString()
            messageChannel = remoteMessage.data["channel"] ?: "broad"
        } else { // Data 메시지 처리 (Foreground와 Background 모두)
            val data = remoteMessage.data
            Log.d(TAG, "Data received: $data")
            messageTitle = data["myTitle"].orEmpty()
            messageContent = data["myBody"].orEmpty()
            messageChannel = data["channel"].orEmpty()
        }

        // 알림 생성 및 표시
      //  createNotification(messageChannel, messageTitle, messageContent)

        // 서버에 알림 저장
      //  saveNotificationToServer(messageTitle, messageContent, messageChannel)

    }
//      private fun createNotification(channel: String, title: String, content: String) {
//          val intent = Intent(this, NotificationActivity::class.java).apply {
//              flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//              putExtra("channel", channel) // 필요한 데이터 전달
//              Log.d(TAG, "createNotification: $channel")
//          }
//
//          val mainPendingIntent: PendingIntent = PendingIntent.getActivity(
//              this, 0,
//              intent, PendingIntent.FLAG_IMMUTABLE
//          )
//
//          // 아이콘 설정
//          val smallIcon = when (channel) {
//              MainActivity.DELIVERY_CHANNEL -> R.drawable.notification_notice_ib // 배송 채널 아이콘
//              MainActivity.BROAD_CHANNEL -> R.drawable.notification_notice_ib // 일반 알림 채널 아이콘
//              else -> R.drawable.notification_notice_ib // 기본 아이콘
//
//
//          }
//
//          val builder = NotificationCompat.Builder(
//              this,
//              if (channel == MainActivity.DELIVERY_CHANNEL) MainActivity.DELIVERY_CHANNEL else MainActivity.BROAD_CHANNEL
//          )
//              .setSmallIcon(smallIcon)
//              .setContentTitle(title)
//              .setContentText(content)
//              .setAutoCancel(true)
//              .setContentIntent(mainPendingIntent)
//
//          val notificationManager: NotificationManager =
//              getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//          notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
//      }

//      private fun saveNotificationToServer(title: String, content: String, c

}
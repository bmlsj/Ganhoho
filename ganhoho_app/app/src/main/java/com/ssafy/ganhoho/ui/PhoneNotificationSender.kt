package com.ssafy.ganhoho.ui

//import android.content.Context
//import android.util.Log
//import com.google.android.gms.wearable.PutDataMapRequest
//import com.google.android.gms.wearable.Wearable

//fun sendDataToWatch(context: Context, key: String, value: String) {
//    val dataClient = Wearable.getDataClient(context)
//
//    val putDataReq = PutDataMapRequest.create("/data_path").apply {
//        dataMap.putString(key, value)
//        dataMap.putLong("timestamp", System.currentTimeMillis()) // 최신 데이터 유지
//    }.asPutDataRequest()
//
//    dataClient.putDataItem(putDataReq)
//        .addOnSuccessListener {
//            Log.d("WearData", "데이터 전송 성공: $key = $value")
//        }
//        .addOnFailureListener { e ->
//            Log.e("WearData", "데이터 전송 실패", e)
//        }
//}


/*
fun sendNotification(context: Context, title: String, message: String) {
    val channelId = "phone_notification_channel"

    // Android 8.0 (Oreo) 이상에서는 Notification Channel 필요
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "일반 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "스마트폰 & 워치 알림 채널"
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    // 알림 클릭 시 실행될 인텐트 (MainActivity로 이동)
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // 알림 생성
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true) // 알림 클릭 시 자동 삭제
        .setPriority(NotificationCompat.PRIORITY_HIGH) // 우선순위 높게 설정
        .setVibrate(longArrayOf(100, 200, 100, 200)) // 진동 설정
        .setLocalOnly(false)
        .extend(
            NotificationCompat.WearableExtender()
                .setBridgeTag("tagOne")
        )
        .build()

    // 알림 표시
    NotificationManagerCompat.from(context).notify(1, notification)
}
 */
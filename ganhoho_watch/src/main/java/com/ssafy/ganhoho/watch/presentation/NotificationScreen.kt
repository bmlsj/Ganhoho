package com.ssafy.ganhoho.watch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.tooling.preview.devices.WearDevices


// 가짜 알림 데이터
data class Notification(val type: Int, val title: String, val bedNum: String)

// 샘플 알림 리스트
val notifications = listOf(
    Notification(1, "긴급", "A-2"),
    Notification(2, "일반", "A-6"),
    Notification(3, "긴급", "A-4")
)

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotificationScreen()
        }
    }
}

@Composable
fun NotificationScreen() {
    // val navController = rememberSwipeDismissableNavController()
    var selectedNotification by remember { mutableStateOf<Notification?>(null) }

    Scaffold(
        timeText = { TimeText() }
    ) {
        if (selectedNotification == null) {
            NotificationListScreen(
                notifications = notifications,
                onNotificationClick = { selectedNotification = it }
            )
        } else {
            NotificationDetailScreen(
                notification = selectedNotification!!,
                onBack = { selectedNotification = null }
            )
        }
    }
}

@Composable
fun NotificationListScreen(
    notifications: List<Notification>,
    onNotificationClick: (Notification) -> Unit
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(notifications) { notification ->
            Card(
                onClick = { onNotificationClick(notification) },
                backgroundPainter = CardDefaults.cardBackgroundPainter(
                    startBackgroundColor = Color(0xFF1E88E5),
                    endBackgroundColor = Color(0xFF42A5F5)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        notification.type.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(notification.title, fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(notification.bedNum, fontSize = 12.sp, color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun NotificationDetailScreen(notification: Notification, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(notification.type.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(notification.title, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(notification.bedNum, fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            IconButton(onClick = onBack, modifier = Modifier.clip(CircleShape)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로 가기", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(20.dp))
            IconButton(onClick = { /* 알림 닫기 로직 추가 가능 */ }, modifier = Modifier.clip(CircleShape)) {
                Icon(Icons.Filled.Close, contentDescription = "닫기", tint = Color.Red)
            }
        }
    }
}

@Preview(showBackground = true, device = WearDevices.SMALL_ROUND)
@Composable
fun NotificationListPreview() {
    NotificationListScreen(
        notifications = notifications,
        onNotificationClick = {}
    )
}

@Preview(showBackground = true, device = WearDevices.SMALL_ROUND)
@Composable
fun NotificationDetailPreview() {
    NotificationDetailScreen(
        notification = notifications.first(),
        onBack = {}
    )
}
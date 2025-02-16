package com.ssafy.ganhoho.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.IconButton
import androidx.wear.tooling.preview.devices.WearDevices

@Composable
fun NotificationScreen() {
    val listState = rememberScalingLazyListState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val notificationViewModel: NotificationViewModel = viewModel()

    val notificationState = notificationViewModel.notifications.collectAsState().value
    val notifications = notificationState ?: emptyList()


    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        state = listState,
    ) {
        items(notifications.size) { index ->
            val selected = listState.centerItemIndex == index
            NotificationItem(notifications[index], selected)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                notificationViewModel.getNotifications()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, isSelected: Boolean) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val width = if (isSelected) screenWidth * 0.9f else screenWidth * 0.7f
    val height = screenHeight * 0.25f
    val fontSize = if (isSelected) screenWidth * 0.1f else screenWidth * 0.07f

    var borderColor = Color.Transparent
    val backgroundColor = Color.White.copy(0.3f)
    if (notification.type == 1) { // 긴급
        borderColor = Color.Red.copy(0.5f)
    }

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .size(width, height)
            .clip(RoundedCornerShape(30.dp))
            .background(if (notification.type == 1) borderColor else backgroundColor)
            .border(1.dp, color = borderColor, shape = RoundedCornerShape(30.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(start = 20.dp)
        ) {

            Text(
                text = notification.title,
                fontSize = fontSize.value.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = notification.message,
                fontSize = fontSize.value.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true, device = WearDevices.SMALL_ROUND)
@Composable
fun NotificationScreenPreview() {
    NotificationScreen()
}


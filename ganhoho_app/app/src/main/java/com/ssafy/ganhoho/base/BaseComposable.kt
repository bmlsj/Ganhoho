package com.ssafy.ganhoho.base

import android.content.Context
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat.MessagingStyle.Message

@Composable
fun BaseComposable(
    onBackPressedDispatcher: OnBackPressedDispatcher? = null,
    onBackPress: () -> Unit = { },
    content: @Composable () -> Unit
) {

    val context = LocalContext.current

    // 백 버튼 핸들러 설정
    if (onBackPressedDispatcher != null) {
        BackHandler {
            onBackPress()
        }
    }

    // 기본 UI Content
    content()

}

// Toast 메세지
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
package com.ssafy.ganhoho.ui.mypage

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.ganhoho.data.model.dto.notification.Notification
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.NotifiViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun NotificationScreen(navController: NavController) {

    val notifiViewModel: NotifiViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val notificationState = notifiViewModel.notifcations.collectAsState().value
    val notifications = notificationState?.getOrNull() ?: emptyList()

    // fcm 토큰 불러오기
    val fcmToken = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken.value = task.result
            } else {
                fcmToken.value = null
            }
        }
    }

    // 토큰 로드하기
    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(token, fcmToken) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        } else if (!fcmToken.value.isNullOrEmpty()) {
            Log.d("token", token)
            // 알림 기록 불러오기
            notifiViewModel.getNotifications(token)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (notifications.isEmpty()) {
            // ✅ 알림이 없을 때
            Text(
                text = "조회된 알림이 없습니다.",
                fontSize = 18.sp,
                color = Color.Gray
            )
        } else {
            // ✅ 알림이 있을 때
            for (notis in notifications) {
                NotiDetail(notis)
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun NotiDetail(notice: Notification) {

    val boxColor = mutableStateOf(Color(0xff8DC6D9))
    Row(
        modifier = Modifier
            .padding(10.dp)
            .shadow(10.dp, shape = RoundedCornerShape(10.dp)) // ✅ 먼저 적용
            .border(
                1.dp,
                color = if (notice.type != 1) Color.Transparent else Color.Red.copy(0.2f),
                shape = RoundedCornerShape(10.dp)
            )
            .background(Color.White) // ✅ 배경 추가 (그림자가 더 뚜렷해짐)
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp) // ✅ 크기 조정 (너무 커지지 않게)
                    .aspectRatio(1f)  // 1:1 비율 유지
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        2.dp,
                        (if (notice.type != 1) boxColor.value else Color.Red),
                        RoundedCornerShape(5.dp)
                    )
                    .background(if (notice.type != 1) boxColor.value else Color.Red),
                contentAlignment = Alignment.Center  // 텍스트 중앙 정렬
            ) {
                Text(
                    text = notice.type.toString(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,  // ✅ 폰트 크기 조정
                    color = Color.White
                )
            }

            Column {
                Text(
                    notice.title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Text(
                    text = notice.message,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // ✅ 우측 정렬을 위해 Spacer 추가
        }
    }

}

@Preview(showBackground = true)
@Composable
fun NotifiPreview() {

    val notification = listOf<Notification>(
        Notification("의료요청", 2, "4번 베드"),
        Notification("긴급사항", 1, "4번 베드"),
        Notification("긴급사항", 1, "4번 베드"),
        Notification("기타요청", 4, "4번 베드")

    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        for (notice in notification) {
            NotiDetail(notice = notice)
        }

    }

}
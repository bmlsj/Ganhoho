package com.ssafy.ganhoho.ui.group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController




@Composable
fun GroupMemberScheduleScreen(
    navController: NavController,
    memberName: String,
    onToggleBottomNav: (Boolean) -> Unit
) {
    // 화면이 나타날 때 바텀 네비게이션 숨김
    LaunchedEffect(Unit) {
        onToggleBottomNav(false)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        // 상단 좌측 이름
        Text(
            text = memberName,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 공개 스케줄 추가하기
        Text("공개 스케줄 추가하기 (개인 스케줄-홈화면 참고해서 공개인 것 + 근무만 불러오기)", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}


@Preview(showBackground = true, name="Interactive")
@Composable
fun PreviewGroupMemberScheduleScreen() {
    val navController = TestNavHostController(LocalContext.current)

    GroupMemberScheduleScreen(
        navController = navController,
        memberName = "서정후",
        onToggleBottomNav = {}
    )
}
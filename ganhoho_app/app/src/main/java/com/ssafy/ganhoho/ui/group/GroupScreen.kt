package com.ssafy.ganhoho.ui.group

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.ganhoho.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Preview()
@Composable
fun GroupScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 상단 타이틀 & 아이콘 -> 추후 따로 뺄 예정!
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "GANHOHO",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF79C7E3),
            )
            Row {
                Image(
                    painter = painterResource(id = R.drawable.icon_profile),
                    contentDescription = "Profile Image",
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.icon_notification),
                    contentDescription = "Notification Icon",
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "나의 그룹" 텍스트 제목
        Text(
            text = "나의 그룹",
            fontSize = 25.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 그룹 리스트 - 추후 구현 예정
        //GroupList()

        Spacer(modifier = Modifier.height(16.dp))

        // 그룹 추가 버튼
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62B5E5)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("그룹 추가하기", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupScreen() {
    MaterialTheme {
        GroupScreen()
    }
}
package com.ssafy.ganhoho.ui.group

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.ganhoho.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.ui.group.common.GroupList


@Composable
fun GroupScreen(navController: NavController) {
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
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(15.dp))

        // 그룹 리스트 - 추후 구현 예정
        val groupList = listOf(
            GroupDto(
                groupId = 1,
                groupName = "백엔드 마스터즈",
                groupIconType = R.drawable.icon_profile,
                groupMemberCount = 15
            ),
            GroupDto(
                groupId = 2,
                groupName = "프론트엔드 위저드",
                groupIconType = R.drawable.hospital_room,
                groupMemberCount = 12
            ),
            GroupDto(
                groupId = 3,
                groupName = "디자인 감성단",
                groupIconType = R.drawable.nav_pill,
                groupMemberCount = 8
            )
        )

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            for (group in groupList) {
                GroupList(group = group, navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // 그룹 추가 버튼
        Button(
            onClick = { /*TODO 그룹 추가 기능*/ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF79C7E3)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text("그룹 추가", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupScreen() {
    val navController = rememberNavController()
    MaterialTheme {
        GroupScreen(navController)
    }
}
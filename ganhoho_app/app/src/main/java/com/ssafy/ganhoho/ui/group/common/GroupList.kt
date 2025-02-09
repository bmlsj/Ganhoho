package com.ssafy.ganhoho.ui.group.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.data.model.dto.group.GroupDto

@Composable
fun GroupList(
    group: GroupDto,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            // 각 그룹 상세페이지로 이동
            navController.navigate("")
        },
        shape = RoundedCornerShape(16.dp), // 둥근 모서리
        elevation = CardDefaults.cardElevation(6.dp), // 그림자 적용
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp) // 내부 패딩 조정
        ) {
            // 아이콘
            Icon(
                painter = painterResource(id = group.groupIconType),
                contentDescription = "그룹 아이콘",
                modifier = Modifier.size(26.dp)
            )

            // 그룹명
            Text(
                text = group.groupName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            // 그룹 멤버 수
            Text(
                text = "👤 ${group.groupMemberCount}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupListPreview() {
    val navController = rememberNavController()
    GroupList(
        group = GroupDto(
            groupId = 1,
            groupName = "동기모임",
            groupIconType = R.drawable.hospital_room,
            groupMemberCount = 6
        ),
        navController = navController
    )
}

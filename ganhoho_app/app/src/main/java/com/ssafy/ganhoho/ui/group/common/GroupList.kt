package com.ssafy.ganhoho.ui.group.common

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.ui.group.getGroupIconResource

@Composable
fun GroupItem(
    group: GroupDto,
    navController: NavController,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Card(
        modifier = Modifier

            .clickable{onClick()}
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Text(
                text = group.groupName,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${group.groupMemberCount}명의 그룹원",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF828282),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                val iconRes = getGroupIconResource(group.groupIconType)
                if (iconRes != 0) {  // 유효한 리소스 ID인지 확인
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = "그룹 아이콘",
                        modifier = Modifier.size(50.dp)
                    )
                } else {
                    Log.e("GroupIcon", "❌ Invalid icon resource ID for groupIconType: ${group.groupIconType}")
                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupListPreview() {
    val navController = rememberNavController()
    val sampleGroups = listOf(
        GroupDto(1, "동기모임", 1, 6),
        GroupDto(2, "싸피대학교", 2, 5),
        GroupDto(3, "빵빵즈", 3, 3),
        GroupDto(4, "D209", 4, 4),

        )

}

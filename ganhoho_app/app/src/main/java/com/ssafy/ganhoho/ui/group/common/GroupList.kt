package com.ssafy.ganhoho.ui.group.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.R

@Composable
fun GroupList(
    groups: List<GroupDto>,
    navController: NavController
) {
    // 한 줄에 두 개씩 출력하기 위해 LazyVerticalGrid 사용
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(groups) { group ->
            GroupItem(
                group = group, navController = navController, modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}

@Composable
fun GroupItem(
    group: GroupDto,
    navController: NavController,
    modifier: Modifier = Modifier
){
    Card(
        modifier = Modifier
            .clickable{
                navController.navigate("EachGroupScreen/${group.groupId}"){
                    launchSingleTop = true
                }
        }
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
                Image(
                    painter = painterResource(id = group.groupIconType),
                    contentDescription = "그룹 아이콘",
                    modifier = Modifier
                        .size(26.dp)
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupListPreview() {
    val navController = rememberNavController()
    val sampleGroups = listOf(
        GroupDto(1, "동기모임", R.drawable.emoji_hospital, 6),
        GroupDto(2, "싸피대학교", R.drawable.emoji_school, 5),
        GroupDto(3, "빵빵즈", R.drawable.emoji_dragon, 3),
        GroupDto(4, "D209", R.drawable.emoji_nurse, 4),

        )

}

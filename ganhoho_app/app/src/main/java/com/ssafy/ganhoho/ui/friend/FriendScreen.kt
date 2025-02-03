package com.ssafy.ganhoho.ui.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.data.model.dto.FriendDto
import com.ssafy.ganhoho.ui.friend.common.FriendList
import com.ssafy.ganhoho.data.model.response.friend.FriendListResponse

@Composable
fun FriendScreen(navController: NavController) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)  // 세로 스크롤
            .background(Color(0xffe4f8ff))
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        // 상단 "친구요청", "친구 추가" 버튼
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 1. 친구 요청 버튼
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(15.dp))
                    .background(Color.White, shape = RoundedCornerShape(15.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "친구 요청",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFEAEA), shape = CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "+3",
                            fontSize = 12.sp,
                            color = Color(0xFFFF3B3B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }

            // 2. 친구 추가 버튼
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(15.dp))
                    .background(Color.White, shape = RoundedCornerShape(15.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "친구 추가",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = "친구 추가 이동",
                        tint = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // 친구 검색 화면
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xfff4fbff))
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            val searchText = remember {
                mutableStateOf("")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                OutlinedTextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    placeholder = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "searchFriend",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "이름으로 친구를 검색해보세요.",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp), // 둥근 모서리
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xfffdfdfd),
                        focusedContainerColor = Color(0xfffdfdfd),
                        unfocusedIndicatorColor = Color.LightGray,
                        focusedIndicatorColor = Color.LightGray,
                        focusedTextColor = Color.Black
                    )
                )
            }

            // 친구 리스트
            val friends = arrayOf(
                FriendDto(
                    -1L, "@jeonghu1010", "서정후",
                    "싸피병원", "일반병동", true
                ),
                FriendDto(
                    "@jeonghu1010", "한아영",
                    "싸피병원", "응급병동", true
                ),
                FriendDto(
                    "@jeonghu1010", "이승지",
                    "싸피병원", "중환자실", true
                ),
                FriendDto(
                    "@jeonghu1010", "서정후",
                    "싸피병원", "일반병동", false
                ),
                FriendDto(
                    "@hongbeop", "황홍법",
                    "싸피병원", "일반병동", false
                ),
                FriendDto(
                    "@gimhwan00", "김 환",
                    "싸피병원", "응급병동", false
                ),
                FriendDto(
                    "@minseok", "강민석",
                    "싸피병원", "일반병동", false
                ),
            )

            // 친구 리스트 위젯
            for (friend in friends) {
                FriendList(friend = friend)
            }
        }
    }
}

@Preview
@Composable
fun ScreenPreivew() {

    val navController = rememberNavController()
    FriendScreen(navController)

}
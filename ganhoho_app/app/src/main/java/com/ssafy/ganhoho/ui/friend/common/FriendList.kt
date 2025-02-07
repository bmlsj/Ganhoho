package com.ssafy.ganhoho.ui.friend.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.ganhoho.data.model.dto.friend.FriendDto


@Composable
fun FriendList(
    friend: FriendDto
) {

    // 즐겨찾기 상태를 기억하고 변경 시, UI 업데이트
    val isFavorite = remember { mutableStateOf(friend.isFavorite) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(15.dp))
            .background(Color.White, shape = RoundedCornerShape(15.dp))
            .padding(16.dp)
            .clickable {
                // 아이디로 근무 기록 조회 기능

            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 1. 이름과 아이디
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = friend.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = friend.friendLoginId,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 병원과 병동정보
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    friend.hospital?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .background(
                                    Color(0xfff0f0f0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.Black,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    friend.ward?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .background(
                                    Color(0xfff0f0f0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.Black,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 즐겨찾기 아이콘
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .shadow(4.dp, shape = CircleShape)
                    .clickable {
                        // TODO: isFavorite 변화 수정 로직

                        isFavorite.value = !isFavorite.value
                    }
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector =
                    Icons.Default.Star, contentDescription = "Favorite",
                    tint = if (isFavorite.value) Color(0xffffe600) else Color.LightGray,
                    modifier = Modifier
                        .size(24.dp)

                )
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun FreiendPreview() {
    FriendList(
        FriendDto(
            "@jeonghu1010", "서정후",
            "싸피병원", "일반병동", true
        )
    )
}
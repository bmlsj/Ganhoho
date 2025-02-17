package com.ssafy.ganhoho.ui.friend.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.ganhoho.data.model.dto.friend.FriendDto
import com.ssafy.ganhoho.ui.friend.FriendScheduleScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendList(
    friend: FriendDto,
    onFavoriteClick: (Long, Boolean) -> Unit // 클릭 이벤트 콜백 추가
) {

    // 즐겨찾기 상태를 기억하고 변경 시, UI 업데이트
    val isFavorite = remember { mutableStateOf(friend.isFavorite) }
    // 친구 스케쥴 모달 열기
    val isFriendModal = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(15.dp))
            .background(Color.White, shape = RoundedCornerShape(15.dp))
            .padding(16.dp)
            .clickable {

                isFriendModal.value = true
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
                        text = "@${friend.friendLoginId}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 병원과 병동정보
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = friend.hospital ?: "병원없음", // 데이터가 없으면 빈 문자열
                        modifier = Modifier
                            .background(
                                if (friend.hospital.isNullOrBlank()) Color.Transparent else Color(0xfff0f0f0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (friend.hospital.isNullOrBlank()) Color.Transparent else Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = friend.ward ?: "병동없음", // 데이터가 없으면 "병동없음" 표시
                        modifier = Modifier
                            .background(
                                if (friend.ward.isNullOrBlank()) Color.Transparent else Color(0xfff0f0f0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (friend.ward.isNullOrBlank()) Color.Transparent else Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                }
            }

            // 즐겨찾기 아이콘
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .shadow(2.dp, shape = CircleShape, spotColor = Color.LightGray)
                    .clickable {
                        // TODO: isFavorite 변화 수정 로직
                        val newFavoriteState = !isFavorite.value
                        isFavorite.value = newFavoriteState
                        onFavoriteClick(friend.memberId, newFavoriteState) // ViewModel 업데이트 요청
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

    // 친구 일정 화면 모달 열기
    if (isFriendModal.value) {
        ModalBottomSheet(
            onDismissRequest = { isFriendModal.value = false }, // 바깥 클릭하면 닫힘
            modifier = Modifier
                .clip(shape = RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 친구 스케쥴 화면 표시
                FriendScheduleScreen(
                    friendName = friend.name,
                    friendId = friend.memberId,
                    isFavorite = isFavorite.value
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun FreiendPreview() {
    FriendList(
        FriendDto(
            -1, -1, "jeonghu1010", "서정후",
            "싸피병원", "일반병동", true
        ), onFavoriteClick = { friendId, isFavorite ->
            println("🔥 즐겨찾기 상태 변경: ID = $friendId, 새로운 상태 = $isFavorite")
        }
    )
}
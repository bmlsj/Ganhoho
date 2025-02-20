package com.ssafy.ganhoho.ui.friend.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.ganhoho.data.model.dto.friend.FriendApproveRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendInviteDto
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.FriendViewModel

@Composable
fun FriendRequestList(
    friend: FriendInviteDto
) {

    val viewModel: FriendViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val friendViewModel: FriendViewModel = viewModel()

    // 토큰 로드하기
    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {  // token null 일경우, load
            authViewModel.loadTokens(context)
        }
    }

//    var successDialog by remember { mutableStateOf(false) }
    // 수락 성공 시
    val friendInviteResponse = friendViewModel.friendResponse.collectAsState().value
    val updatedResponse by rememberUpdatedState(friendInviteResponse)

    LaunchedEffect(updatedResponse) {
        updatedResponse?.let {
            if (it.isSuccess) {
                Log.d("friend", "friend add success")
//                successDialog = true // ✅ 다이얼로그 열기
            } else {
                Log.d("friend", "friend add failed")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(15.dp))
            .background(Color.White, shape = RoundedCornerShape(15.dp))
            .padding(16.dp)
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

                // 병동정보
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .wrapContentSize() // 텍스트 크기에 맞게 크기 조절
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(10.dp) // 둥근 모서리
                            )
                            .background(Color.White, RoundedCornerShape(10.dp)) // 배경색
                            .padding(horizontal = 16.dp, vertical = 4.dp) // 내부 여백
                            .clickable {
                                // 친구 요청 수락 API 호출
                                if (token != null) {
                                    viewModel.respondToFriendInvite(
                                        token,
                                        friend.friendRequestId,
                                        FriendApproveRequest("ACCEPTED")
                                    )
                                }
                            }
                    ) {
                        Text(
                            text = "수락",
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun FreiendRequestPreview() {
    FriendRequestList(
        FriendInviteDto(
            -1, "jeonghu1010", "서정후",
            "싸피병원", "일반병동", "pending"
        )
    )
}
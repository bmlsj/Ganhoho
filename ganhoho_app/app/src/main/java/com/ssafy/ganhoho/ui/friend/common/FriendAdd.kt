package com.ssafy.ganhoho.ui.friend.common

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.ssafy.ganhoho.data.model.dto.friend.FriendDto
import com.ssafy.ganhoho.data.model.dto.member.MemberDto
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.FriendViewModel

@Composable
fun FriendAdd(
    member: MemberDto,
    friendList: List<FriendDto>,
    onFriendAdd: (String) -> Unit
) {

    // 다이얼로그
    var successDialog by remember { mutableStateOf(false) }
    var errorDialog by remember { mutableStateOf(false) }

    //  val token = BuildConfig.TOKEN
    val authViewModel: AuthViewModel = viewModel()
    val friendViewModel: FriendViewModel = viewModel()

    // 토큰 로드하기
    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        }
    }

    // ✅ 현재 검색된 회원이 이미 친구인지 확인
    val isFriend = friendList.any { it.friendLoginId == member.loginId }
//    val addFriendResult = friendViewModel.addFriendResult.collectAsState().value
//
//    LaunchedEffect(addFriendResult) {
//        if (addFriendResult != null) {
//            Log.d("FriendAdd", "addFriendResult 값 변경 감지: $addFriendResult")
//
//            addFriendResult.onSuccess { response ->
//                if (response.success) {
//                    Log.d("FriendAdd", "add friend success")
//                    successDialog = true  // 친구 추가 시, 확인 다이얼로그 띄우기
//                    friendViewModel.clearAddFriendResult()
//                } else {
//                    Log.d("FriendAdd", "add friend failed")
//                }
//            }.onFailure { exception ->
//                Log.e("FriendAdd", "🚨 error: ${exception.message}")
//                errorDialog = true // ✅ 409 에러 발생 시 다이얼로그 표시
//                friendViewModel.clearAddFriendResult()
//            }
//        }
//    }


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
                        text = member.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "@${member.loginId}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 병원과 병동정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = member.hospital ?: "병원없음", // 데이터가 없으면 빈 문자열
                            modifier = Modifier
                                .background(
                                    if (member.hospital.isNullOrBlank()) Color.Transparent else Color(0xfff0f0f0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (member.hospital.isNullOrBlank()) Color.Transparent else Color.Black,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = member.ward ?: "병동없음", // 데이터가 없으면 "병동없음" 표시
                            modifier = Modifier
                                .background(
                                    if (member.ward.isNullOrBlank()) Color.Transparent else Color(0xfff0f0f0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (member.ward.isNullOrBlank()) Color.Transparent else Color.Black,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // ✅ 친구 목록에 이미 있는 경우 버튼을 회색으로 비활성화
                    val buttonColor = if (isFriend) Color.LightGray else Color(0xff79C7E3)
                    val isClickable = !isFriend // 이미 친구라면 클릭 비활성화

                    Text(
                        text = "추가",
                        modifier = Modifier
                            .background(
                                buttonColor,
                                shape = RoundedCornerShape(15.dp)
                            )
                            .padding(horizontal = 18.dp, vertical = 4.dp)
                            .clickable(enabled = isClickable) {
                                // 친구 리스트에 친구 추가(POST)하면, true/false 반환
                                onFriendAdd(member.loginId)

//                                if (token != null) {
//                                    friendViewModel.addFriendList(token, member.loginId)
//                                }
                            },
                        color = Color.White,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // ✅ 친구 신청 완료 다이얼로그
    if (successDialog) {
        AlertDialog(
            onDismissRequest = { successDialog = false }, // ✅ 바깥 클릭 시 닫힘
            confirmButton = {
                Button(
                    onClick = { successDialog = false } // ✅ 확인 버튼 클릭 시 닫힘
                ) {
                    Text("확인")
                }
            },
            text = { Text("친구 신청이 완료되었습니다.") }
        )
    }

    // ✅ 이미 요청된 친구 다이얼로그
    if (errorDialog) {
        AlertDialog(
            onDismissRequest = { errorDialog = false },
            confirmButton = {
                Button(onClick = { errorDialog = false }) {
                    Text("확인")
                }
            },
            text = { Text("이미 요청된 친구입니다.") }
        )
    }

}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun FriendAddPreview() {
    FriendAdd(
        MemberDto(
            -1, "jeonghu1010", "서정후",
            "싸피병원", "일반병동"
        ),
        friendList = listOf(  // ✅ 이미 친구로 등록된 경우를 테스트
            FriendDto(-1, -1, "jeonghu1010", "서정후", "싸피병원", "일반병동", true)
        ),
        {}
    )
}
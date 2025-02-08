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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.ganhoho.BuildConfig
import com.ssafy.ganhoho.data.model.dto.friend.FriendAddRequest
import com.ssafy.ganhoho.data.model.dto.friend.FriendDto
import com.ssafy.ganhoho.data.model.dto.member.MemberDto
import com.ssafy.ganhoho.viewmodel.FriendViewModel

@Composable
fun FriendAdd(
    member: MemberDto,
    friendList: List<FriendDto>
) {

    val token = ""
    val friendViewModel: FriendViewModel = viewModel()

    // ✅ 현재 검색된 회원이 이미 친구인지 확인
    val isFriend = friendList.any { it.friendLoginId == member.loginId }
    val addFriendResult = friendViewModel.addFriendResult.collectAsState().value

    LaunchedEffect(addFriendResult) {
        Log.d("FriendAdd", "addFriendResult 값 변경 감지: $addFriendResult")

        addFriendResult?.onSuccess { response ->
            if (response.success) {
                Log.d("FriendAdd", "✅ 친구 추가 성공!")
            } else {
                Log.d("FriendAdd", "❌ 친구 추가 실패!")
            }
        }?.onFailure { exception ->
            Log.e("FriendAdd", "🚨 친구 추가 중 오류 발생: ${exception.message}")
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
                        text = member.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "검색 @${member.loginId}",
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
                        member.hospital?.let {
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

                        member.ward?.let {
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
                                // TODO: 추가 버튼 누를 시,
                                // 친구 리스트에 친구 추가(POST)하면, true/false 반환
                                friendViewModel.addFriendList(token, member.loginId)
                            },
                        color = Color.White,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
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
        )
    )
}
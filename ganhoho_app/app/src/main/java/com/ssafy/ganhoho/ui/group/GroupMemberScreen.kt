package com.ssafy.ganhoho.ui.group

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.ganhoho.base.TokenManager
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.response.group.GroupMemberResponse
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.GroupViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun GroupMemberScreen(
    members: List<GroupMemberResponse>,
    isVisible: Boolean,
    onClose: () -> Unit,
    navController: NavController,
    groupId: Int,
    viewModel: GroupViewModel,
    repository: GroupRepository,
    tokenManager: TokenManager,
    group: GroupDto
) {


    val authViewModel: AuthViewModel = viewModel()
    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        } else {
            Log.d("token", token)
        }
    }


    val yearMonth: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    var inviteLink by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(token) {
        if (token != null) {
            viewModel.fetchMemberSchedules(groupId, yearMonth, token)  // 스케줄 불러오기
            viewModel.fetchMemberList(groupId, token) // 그룹원 리스트 불러오기

            viewModel.fetchGroupInviteLink(token, groupId,
                onSuccess = { link ->
                    inviteLink = "ssafyd209://ganhoho/group?groupCode=$link"
                    Log.d("초대링크", "초대링크 가져오기 성공!~ : $inviteLink")
                },
                onFailure = { error ->
                    Log.e("초대링크", "초대 링크 불러오기 실패: $error")
                })
        }

        // 초대 링크 가져오기
//        val token = tokenManager.getAccessToken() ?: return@LaunchedEffect

    }


    var isDialogVisible by rememberSaveable { mutableStateOf(false) } // 다이얼로그 상태 유지
    val groupMembers by viewModel.groupMembers.collectAsState()

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),  // 오른쪽에서 슬라이드 인
        exit = slideOutHorizontally(targetOffsetX = { it }),  // 오른쪽으로 슬라이드 아웃
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f) // 네비게이션 바보다 위에 배치
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClose() } // 배경 클릭 시 닫힘
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.7f)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                    )
                    .padding(25.dp)
                    .align(Alignment.CenterEnd)
            ) {
                Column {
                    // 상단 제목
                    Text(text = "그룹원", fontSize = 27.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    // 멤버 리스트
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (members.isEmpty()) {
                            item {
                                Text(
                                    text = "그룹원이 없습니다.",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            items(groupMembers) { member ->
                                MemberCard(
                                    member = member,
                                    onClick = {
//                                        navController.navigate("GroupMemberSchedule/${member.loginId}")

                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val context = LocalContext.current

                    Button(
                        onClick = {
                            // 초대링크 공유
                            if (inviteLink.isNotEmpty()) {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    val inviteLinkUrl = inviteLink.substringAfter("groupCode=")

                                    putExtra(Intent.EXTRA_TEXT, inviteLinkUrl)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "초대 링크 공유하기"))
                            }else {
                                Log.e("초대링크", "초대 링크가 존재하지 않음")
                            }

                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF79C7E3)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(43.dp),
                        shape = RoundedCornerShape(13.dp)

                    ) {
                        Text("초대 링크 공유하기", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "그룹 탈퇴하기",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Log.d("DEBUG", "isDialogVisible 변경됨: $isDialogVisible -> true")
                                isDialogVisible = true
                            }
                            .padding(bottom = 35.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    if (isDialogVisible) {
        GroupLeaveDialog(
            isVisible = isDialogVisible,
            onConfirm = { isDialogVisible = false }, // 다이얼로그 닫기
            onDismiss = { isDialogVisible = false }, // 다이얼로그 닫기
            navController = navController,
            repository = repository,
            group = group
        )
    }
}


// 그룹원 개별 카드
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MemberCard(
    member: GroupMemberResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(top = 15.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(7.dp),
        elevation = 4.dp,
        backgroundColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 17.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(text = member.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "@${member.loginId}",
                    fontSize = 10.sp,
                    color = Color(0xFFAAAAAA),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 4.dp)
                )
            }
            Text(
                text = member.hospital ?: "소속 병원 없음",
                fontSize = 10.sp,
                color = Color(0xFF000000)
            )
        }
    }
}
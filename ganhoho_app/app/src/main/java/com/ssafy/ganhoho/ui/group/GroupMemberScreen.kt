package com.ssafy.ganhoho.ui.group

import android.net.Uri
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import com.ssafy.ganhoho.data.model.response.group.GroupMemberResponse

@Composable
fun GroupMemberScreen(
    members: List<GroupMemberResponse>,
    isVisible: Boolean, // 화면 표시 여부
    onClose: () -> Unit, // 사이드 메뉴 닫기
    navController: NavController,
    onNavigateToSchedule: () -> Unit

) {
    var isDialogVisible by rememberSaveable { mutableStateOf(false) } // rememberSaveable로 상태 유지
    var selectedMember by remember { mutableStateOf<GroupMemberResponse?>(null) }
    var isPopupVisible by remember { mutableStateOf(false) }

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
                    .fillMaxHeight() // 세로로 화면을 가득 채움
                    .fillMaxWidth(0.7f) // 오른쪽에서 85% 차지
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                    )
                    .padding(25.dp)
                    .align(Alignment.CenterEnd) // 오른쪽 정렬
            ) {
                Column {
                    // 상단 닫기 버튼
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "그룹원", fontSize = 27.sp, fontWeight = FontWeight(600))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 멤버 리스트 및 멤버별 개인 스케줄
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(members) { member ->
                            MemberCard(
                                member = member,
                                onClick = {
                                    selectedMember = member
                                    onNavigateToSchedule() //네비게이션 바 숨기기
                                    navController.navigate("GroupMemberSchedule/${Uri.encode(member.name)}")
                                }
                            )
                        }
                    }



                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { /* TODO: 초대 링크 공유 기능 추가 */ },
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
                            .clickable { isDialogVisible = true } // 다이얼로그 표시 상태 변경

                            .padding(bottom = 35.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // 그룹 탈퇴 다이얼로그 표시
    if (isDialogVisible) {
        GroupLeaveDialog(
            isVisible = isDialogVisible,
            onConfirm = {
                isDialogVisible = false // 확인 버튼 클릭 시 다이얼로그 닫기
            },
            onDismiss = { /* 확인을 눌러야 닫히도록 변경 (onDismiss 비활성화) */ },
            navController = navController
        )
    }
}

// 그룹원 카드를 개별 컴포넌트로 분리
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MemberCard(
    member: GroupMemberResponse,
    onClick:() -> Unit
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
                modifier = Modifier
                    .padding(bottom = 2.dp)

            ){
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
                text = "싸피병원",
                fontSize = 10.sp,
                color = Color(0xFF000000)
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupMemberScreen() {
    var isVisible by remember { mutableStateOf(true) } // 초기에는 보이도록 설정
    var isDialogVisible by remember { mutableStateOf(false) }
    val navController = TestNavHostController(LocalContext.current) // 테스트용 네비게이션 컨트롤러

    Column {
        GroupMemberScreen(
            members = getSampleMembers(), // 샘플 데이터 사용
            isVisible = isVisible,
            onClose = { isVisible = false }, // 닫기 버튼 클릭 시 숨김
            navController = navController, // 네비게이션 컨트롤러 전달
            onNavigateToSchedule = {} // (필요에 따라 동작 추가 가능)
        )

    }
}

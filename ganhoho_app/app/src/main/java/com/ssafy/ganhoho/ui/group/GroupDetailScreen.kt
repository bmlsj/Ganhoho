package com.ssafy.ganhoho.ui.group

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.base.TokenManager
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.dto.group.WorkScheduleDto
import com.ssafy.ganhoho.data.model.response.group.GroupMemberResponse
import com.ssafy.ganhoho.data.model.response.group.GroupViewModelFactory
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.GroupViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun EachGroupScreen(
    navController: NavController,
    group: GroupDto,
    groupMember: List<GroupMemberResponse>,
    repository: GroupRepository,
    groupId: Int?,
    yearMonth: String
) {

    val authViewModel: AuthViewModel = viewModel()
    val viewModel: GroupViewModel = ViewModelProvider(
        LocalContext.current as ViewModelStoreOwner,
        GroupViewModelFactory(repository)
    )[GroupViewModel::class.java]

    val currentDate = LocalDate.now()
    val currentYear = currentDate.year
    val currentMonth = currentDate.monthValue
    val today = LocalDate.now() // 오늘 날짜 가져오기

    val calendarDays = getCalendarDays(currentYear, currentMonth)
    val weeks = calendarDays.chunked(7)

    var isMemberScreenVisible by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) } // 다이얼로그 상태 추가

    val memberSchedules by viewModel.memberSchedules.collectAsState()
    var inviteLink by rememberSaveable { mutableStateOf("") }


    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        } else {
            Log.d("token", token)
        }
    }

    LaunchedEffect(token) {
        if (groupId != null && token != null) {
            viewModel.fetchMemberSchedules(groupId, yearMonth, token)
            viewModel.fetchMemberList(groupId, token)
        }
    }

    LaunchedEffect(groupId) {
        if (groupId != 0) {
            if (groupId != null && token != null) {
                viewModel.fetchMemberSchedules(groupId, yearMonth, token)
                viewModel.fetchMemberList(groupId, token)
            }
            Log.d("DEBUG", "Fetching schedules for groupId: $groupId, yearMonth: $yearMonth")

        } else {
            val inviteCode = navController.currentBackStackEntry?.arguments?.getString("inviteCode")
            if (inviteCode.isNullOrEmpty()) {
                Log.d("group_invite", "초대 코드 감지: $inviteCode")

                if (token != null) {
                    viewModel.fetchGroupInviteLink(token, groupId,
                        onSuccess = { link ->
                            inviteLink = "ssafyd209://ganhoho/group?groupCode=$link"
                        },
                        onFailure = { error ->
                            Log.e("GroupMemberScreen", "초대 링크 불러오기 실패: $error")
                        })
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 39.dp, start = 10.dp, end = 15.dp, bottom = 60.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val groupIcon = getGroupIconResource(group.groupIconType)

                        Image(
                            painter = painterResource(groupIcon),
                            contentDescription = "그룹 아이콘",
                            modifier = Modifier
                                .size(35.dp)
                                .padding(bottom = 5.dp)
                        )
                        Text(  // 그룹 이름
                            text = group.groupName,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(  // 그룹원 목록 열기
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(bottom = 2.dp)
                                .clickable {
                                    if (!isMemberScreenVisible) { // 이미 열려 있으면 다시 변경하지 않음
                                        Log.d("EachGroupScreen", "🔄 그룹원 목록 열기")

                                        isMemberScreenVisible = true
                                    }
                                }

                        ) {
                            Image(
                                painter = painterResource(R.drawable.icon_group_person),
                                contentDescription = "그룹 인원 수",
                                modifier = Modifier.size(17.dp)
                            )

                            Text(
                                text = "${group.groupMemberCount}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 60.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                            Text(
                                text = day,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = if (day == LocalDate.now().dayOfWeek.getDisplayName(
                                        TextStyle.SHORT, Locale.KOREAN
                                    )
                                ) FontWeight.Bold else FontWeight.Normal,
                                color = if (day == "일") Color(0xFFE61818) else Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Divider(
                        color = Color(0xFFE0E0E0),
                        thickness = 0.7.dp,
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .padding(top = 14.dp, bottom = 13.dp)
                    )
                }

                val adjustedSchedules = memberSchedules.map {
                    Log.d(
                        "DEBUG_MEMBER_SCHEDULE",
                        "Member: ${it.name}, Schedule Size: ${it.schedules.size}"
                    )

                    it.copy(
                        schedules = adjustWorkSchedule(
                            it.schedules,
                            currentYear,
                            currentMonth
                        ),
                        ward = it.ward ?: "미정"
                    )
                }


                itemsIndexed(weeks) { weekIndex, weekDays ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 60.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weekDays.forEach { date ->
                            Text(
                                text = date.ifEmpty { " " },
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = if (date == today.dayOfMonth.toString()) FontWeight.Bold else FontWeight.Normal, // 오늘 날짜는 굵게
                                color = if (date == today.dayOfMonth.toString()) Color(
                                    0xFF1A85AB
                                ) else Color.Black, // 오늘 날짜는 굵게

                                modifier = Modifier
                                    .padding(top = 10.dp, bottom = 5.dp)
                                    .weight(1f)

                            )
                        }
                    }

                    adjustedSchedules.forEach { schedule ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                        ) {
                            Text(
                                text = schedule.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(60.dp)
                            )

                            val weekSchedule =
                                schedule.schedules.chunked(7).getOrNull(weekIndex)
                                    ?: emptyList()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                weekDays.forEachIndexed { index, _ ->
                                    val workSchedule = weekSchedule.getOrNull(index)

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .background(
                                                getShiftColor(workSchedule?.workType ?: ""),
                                                shape = RoundedCornerShape(5.dp)
                                            )
                                            .height(24.dp)
                                            .weight(1f)
                                    ) {
                                        Text(
                                            text = workSchedule?.workType ?: "",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black,
                                            modifier = Modifier.padding(end = 10.dp),
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        val backgroundAlpha by animateFloatAsState(
            targetValue = if (isMemberScreenVisible) 0.3f else 0f,
            animationSpec = tween(durationMillis = 300)
        )

        if (isMemberScreenVisible || backgroundAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = backgroundAlpha))
                    .clickable {
                        if (isMemberScreenVisible) {  // 현재 열려 있을 때만 닫음
                            isMemberScreenVisible = false
                        }
                    }
            )

        }


        AnimatedVisibility(
            visible = isMemberScreenVisible,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(800)
            ),
            exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(800)),
            modifier = Modifier
                .zIndex(2f)
        ) {
            // 사이드 메뉴 (그룹원 리스트)
            group.groupId?.let {
                GroupMemberScreen(
                    members = groupMember,
                    isVisible = isMemberScreenVisible,
                    onClose = {
                        isMemberScreenVisible = false
                    },

                    navController = navController,
                    groupId = it,
                    viewModel = viewModel,
                    repository = repository,
                    tokenManager = TokenManager,
                    group = group
                )
            }

            // 다이얼로그 (그룹 탈퇴)
            GroupLeaveDialog(
                isVisible = isDialogVisible,
                onConfirm = {
                    isDialogVisible = false // 다이얼로그 닫기
                },
                onDismiss = {
                    isDialogVisible = false
                },
                navController = navController,
                repository = repository,
                group = group
            )
        }
    }
}

fun adjustWorkSchedule(
    schedule: List<WorkScheduleDto?>,
    year: Int,
    month: Int
): List<WorkScheduleDto?> {
    val firstDayOfWeek = getFirstDayOfWeek(year, month)
    val adjustedSchedule = mutableListOf<WorkScheduleDto?>()

    // 🔹 시작 요일에 맞춰 빈 값 추가
    repeat(firstDayOfWeek) { adjustedSchedule.add(null) }

    // 🔹 기존 스케줄을 추가
    adjustedSchedule.addAll(schedule)

    // 🔹 마지막 주가 7개로 맞춰지도록 빈 값 추가
    while (adjustedSchedule.size % 7 != 0) {
        adjustedSchedule.add(null)
    }

    Log.d("DEBUG_ADJUST_SCHEDULE", "Adjusted Schedule: ${adjustedSchedule.size} items")

    return adjustedSchedule
}

// ✅ 시작 요일을 반영하여 달력 날짜 생성
fun getCalendarDays(year: Int, month: Int): List<String> {
    val firstDayOfWeek = getFirstDayOfWeek(year, month)  // 이번 달 시작 요일
    val daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth()  // 이번 달 총 일수

    val calendarDays = mutableListOf<String>()

    // 🔹 시작 요일 전까지 빈 칸 추가
    repeat(firstDayOfWeek) { calendarDays.add("") }

    // 🔹 1일부터 마지막 날까지 추가
    for (day in 1..daysInMonth) {
        calendarDays.add(day.toString())
    }

    // 🔹 마지막 주도 7개로 맞추기 위해 빈 칸 추가
    while (calendarDays.size % 7 != 0) {
        calendarDays.add("")
    }

    return calendarDays
}

// 이번 달의 시작 요일 구하기
fun getFirstDayOfWeek(year: Int, month: Int): Int {
    val firstDay = LocalDate.of(year, month, 1)
    return firstDay.dayOfWeek.value % 7  // 월요일(1)~일요일(7) → 일요일(0)~토요일(6)로 변환
}

// 근무 유형별 배경색 설정
fun getShiftColor(shift: String): Color {
    return when (shift) {
        "N" -> Color(0xFFDDD4CE) // 밤 근무 (회색)
        "OF" -> Color(0xFFFCD6C8) // 휴무 (연한 빨강)
        "E" -> Color(0xFFE4C7F1) // 저녁 근무 (보라색)
        "D" -> Color(0xFFFFF8BF) // 낮 근무 (노란색)
        else -> Color.Transparent
    }
}

// 샘플 데이터 생성
fun getSampleGroup(groupId: Int): GroupDto {
    return GroupDto(
        groupId = groupId,
        groupName = "그룹 $groupId",
        groupIconType = R.drawable.icon_profile,
        groupMemberCount = 6
    )
}


fun getSampleMembers(): List<GroupMemberResponse> {
    return listOf(
        GroupMemberResponse("john_doe", "서정후", "서울병원", "ssafy", "응급실"),
        GroupMemberResponse("han_ahyoung", "한아영", "서울병원", "ssafy", "응급실"),
        GroupMemberResponse("lee_seungji", "이승지", "서울병원", "ssafy", "응급실"),

        )
}
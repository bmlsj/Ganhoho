package com.ssafy.ganhoho.ui.group

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.dto.group.WorkScheduleDto
import com.ssafy.ganhoho.data.model.response.group.GroupMemberResponse
import com.ssafy.ganhoho.data.model.response.group.MemberMonthlyScheduleResponse
import kotlinx.datetime.LocalDateTime
import java.time.LocalDate

@Composable
fun EachGroupScreen(
    group: GroupDto,
    groupMember: List<GroupMemberResponse>,
    memberSchedule: List<MemberMonthlyScheduleResponse>
) {
    val currentDate = LocalDate.now()
    val currentYear = currentDate.year
    val currentMonth = currentDate.monthValue
    val currentDay = currentDate.lengthOfMonth()

    val calendarDays = getCalendarDays(currentYear, currentMonth)
    val weeks = calendarDays.chunked(7) // ✅ 날짜를 주 단위로 나눔

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Text(text = currentDay.toString())
        // ✅ 그룹 정보
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = group.groupIconType),
                contentDescription = "그룹 아이콘",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = group.groupName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "👤 ${group.groupMemberCount}",
                fontSize = 12.sp,
                color = Color.Gray
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 요일 헤더 (고정)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("", "", "일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(8.dp),
                    fontWeight = FontWeight.W700,
                )
            }
        }

        // ✅ LazyColumn으로 근무 스케줄을 주 단위로 나눠서 표시
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            val adjustedSchedules = memberSchedule.map {
                it.copy(schedules = adjustWorkSchedule(it.schedules, currentYear, currentMonth))
            }

            itemsIndexed(weeks) { weekIndex, weekDays ->
                // 구분선
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )

                // ✅ 날짜 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    weekDays.forEach { date ->
                        Text(
                            text = date.ifEmpty { " " }, // 빈 칸 처리
                            fontSize = 16.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f)
                        )
                    }
                }


                // ✅ 각 직원의 근무 스케줄
                adjustedSchedules.forEach { schedule ->
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 직원 이름
                        Text(
                            text = schedule.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // ✅ 안전한 접근을 위해 인덱스 체크 추가
                        val weekSchedule =
                            schedule.schedules.chunked(7).getOrNull(weekIndex) ?: emptyList()

                        weekDays.forEachIndexed { index, date ->
                            val workSchedule = weekSchedule.getOrNull(index)
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .background(
                                        getShiftColor(workSchedule?.workType ?: ""),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .weight(1f)
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = workSchedule?.workType ?: "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
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

// ✅ 이번 달의 시작 요일 구하기
fun getFirstDayOfWeek(year: Int, month: Int): Int {
    val firstDay = LocalDate.of(year, month, 1)
    return firstDay.dayOfWeek.value % 7  // 월요일(1)~일요일(7) → 일요일(0)~토요일(6)로 변환
}

// 근무 유형별 배경색 설정
fun getShiftColor(shift: String): Color {
    return when (shift) {
        "Nig" -> Color(0xFFD3C7BF) // 밤 근무 (회색)
        "Off" -> Color(0xFFF4B8A0) // 휴무 (연한 빨강)
        "Eve" -> Color(0xFFC5A7F6) // 저녁 근무 (보라색)
        "Day" -> Color(0xFFFFF176) // 낮 근무 (노란색)
        else -> Color.Transparent
    }
}

// 샘플 데이터 생성
fun getSampleGroup(): GroupDto {
    return GroupDto(
        groupId = 1,
        groupName = "동기모임",
        groupIconType = R.drawable.icon_profile,
        groupMemberCount = 6
    )
}

fun getSampleMembers(): List<GroupMemberResponse> {
    return listOf(
        GroupMemberResponse("john_doe", "서정후", "서울병원", "응급실"),
        GroupMemberResponse("han_ahyoung", "한아영", "서울병원", "응급실"),
        GroupMemberResponse("lee_seungji", "이승지", "서울병원", "응급실")
    )
}

fun getSampleSchedules(): List<MemberMonthlyScheduleResponse> {
    val currentDate = LocalDate.now()
    val nextMonthDate = currentDate.lengthOfMonth()

    return listOf(
        MemberMonthlyScheduleResponse(
            memberId = 1,
            name = "서정후",
            loginId = "john_doe",
            hospital = "서울병원",
            ward = "응급실",
            schedules = List(nextMonthDate) { i ->
                val day = "%02d".format(i + 1)
                WorkScheduleDto(
                    LocalDateTime.parse("2025-02-${day}T00:00:00"),
                    listOf("Nig", "Off", "Eve", "Day").random()
                )
            }
        ),
        MemberMonthlyScheduleResponse(
            memberId = 2,
            name = "한아영",
            loginId = "han_ahyoung",
            hospital = "서울병원",
            ward = "응급실",
            schedules = List(nextMonthDate) { i ->
                val day = "%02d".format(i + 1)
                WorkScheduleDto(
                    LocalDateTime.parse("2025-02-${day}T00:00:00"),
                    listOf("Nig", "Off", "Eve", "Day").random()
                )
            }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEachGroupScreen() {
    EachGroupScreen(
        group = getSampleGroup(),
        groupMember = getSampleMembers(),
        memberSchedule = getSampleSchedules()
    )
}

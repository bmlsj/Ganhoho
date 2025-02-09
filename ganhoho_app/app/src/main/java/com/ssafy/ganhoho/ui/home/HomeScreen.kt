package com.ssafy.ganhoho.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.yearMonth
import com.ssafy.ganhoho.data.model.dto.schedule.MySchedule
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.ScheduleViewModel
import kotlinx.datetime.toJavaLocalDate
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(navController: NavController) {

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(5) }
    val endMonth = remember { currentMonth.plusMonths(5) }
    val daysOfWeek = DayOfWeek.entries
    val yearMonth = currentMonth.toString()

    // 📌 현재 보이는 월을 상태로 저장 (초기값: 현재 월)
    val currentMonthState = remember { mutableStateOf(YearMonth.now()) }

    val scheduleViewModel: ScheduleViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // 개인 스케쥴 조회 리스트
    val myScheduleState = scheduleViewModel.mySchedule.collectAsState().value
    val myScheduleList = myScheduleState?.getOrNull() ?: emptyList()

    val token = ""
    
    // 토큰 로드하기
//    val token = authViewModel.accessToken.collectAsState().value
//    val context = LocalContext.current
//
//    LaunchedEffect(token) {
//        if (token.isNullOrEmpty()) {
//            authViewModel.loadTokens(context)
//        }
//    }

    LaunchedEffect(token) {
        scheduleViewModel.getMySchedule(token)
    }

//    val events = remember {
//        mutableStateListOf(
//            MySchedule(
//                startDt = LocalDateTime.parse("2025-02-03T00:00:00"),
//                endDt = LocalDateTime.parse("2025-02-04T23:59:59"),
//                title = "동기 회식 🎉",
//                color = "#D1EEF2",
//                isPublic = true,
//                isTimeSet = false
//            ),
//            MySchedule(
//                startDt = LocalDateTime.parse("2025-02-07T00:00:00"),
//                endDt = LocalDateTime.parse("2025-02-10T23:59:59"),
//                title = "북 스터디",
//                color = "#FFCAE6",
//                isPublic = false,
//                isTimeSet = true
//            ),
//            MySchedule(
//                startDt = LocalDateTime.parse("2025-02-15T00:00:00"),
//                endDt = LocalDateTime.parse("2025-02-15T23:59:59"),
//                title = "월세 🌼",
//                color = "#FFF59D",
//                isPublic = true,
//                isTimeSet = false
//            ),
//            MySchedule(
//                startDt = LocalDateTime.parse("2025-02-17T00:00:00"),
//                endDt = LocalDateTime.parse("2025-02-18T23:59:59"),
//                title = "북 스터디2 📚",
//                color = "#FFCAE6",
//                isPublic = true,
//                isTimeSet = false
//            ),
//            MySchedule(
//                startDt = LocalDateTime.parse("2025-02-17T00:00:00"),
//                endDt = LocalDateTime.parse("2025-02-20T23:59:59"),
//                title = "북 스터디2 📚",
//                color = "#FFCAE6",
//                isPublic = true,
//                isTimeSet = false
//            ),
//            MySchedule(
//                startDt = LocalDateTime.parse("2025-02-28T00:00:00"),
//                endDt = LocalDateTime.parse("2025-02-28T23:59:59"),
//                title = "제주도 여행 🍊",
//                color = "#FFD1DC",
//                isPublic = false,
//                isTimeSet = true
//            ),
//
//            )
//    }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonthState.value,
        firstDayOfWeek = DayOfWeek.SUNDAY,
        outDateStyle = OutDateStyle.EndOfRow
    )

    // 📌 캘린더의 현재 보이는 달이 변경될 때 상태 업데이트
    LaunchedEffect(calendarState.firstVisibleMonth) {
        currentMonthState.value = calendarState.firstVisibleMonth.yearMonth
        scheduleViewModel.getMySchedule(token)  // 월이 바뀔때마다 일정 로드
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
    ) {
        // 앱 바
        Text(
            text = "간호호",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF79C7E3)
        )

        // 앱바와 헤더 사이 공간
        Spacer(modifier = Modifier.height(30.dp))

        // 캘린더 헤더
        Text(
            text = "${currentMonthState.value.year}년 ${
                currentMonthState.value.month.getDisplayName(TextStyle.FULL, Locale.KOREA)
            }",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 캘린더 출력
        HorizontalCalendar(
            state = calendarState,
            dayContent = { day ->
                DayContent(myScheduleList, day, currentMonthState.value, navController)
            },
            monthHeader = {
                MonthHeader(daysOfWeek)
            },
            modifier = Modifier.fillMaxSize(),
            contentHeightMode = ContentHeightMode.Fill,
            contentPadding = PaddingValues(top = 10.dp, bottom = 50.dp)
        )

    }
}

// 요일
@Composable
fun MonthHeader(daysOfWeek: List<DayOfWeek>) {

    // 일요일부터 시작하도록 정렬
    val sortedDaysOfWeek = listOf(DayOfWeek.SUNDAY) + (daysOfWeek.filter { it != DayOfWeek.SUNDAY })
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            sortedDaysOfWeek.forEach { day ->
                val dayName = day.getDisplayName(TextStyle.SHORT, Locale.KOREA)

                Text(
                    text = dayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(6.dp),
                    textAlign = TextAlign.Center,
                    color = if (dayName == "일") Color.Red else Color.Black // 일요일일 경우 빨간색으로 표시
                )
            }
        }

        // 구분선 추가
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp) // 선의 두께
                .background(Color(0xFFDADADA)) // 선의 색상
                .padding(vertical = 4.dp) // 위아래 여백 추가
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}


@SuppressLint("RememberReturnType")
@Composable
fun DayContent(
    events: List<MySchedule>,
    day: CalendarDay,
    currentMonth: YearMonth,
    navController: NavController
) {

    val date = day.date
    val isOutDate = date.yearMonth != currentMonth  // ✅ outDate 여부 확인

    // 해당 날짜의 이벤트 필터링
    // ✅ `LocalDateTime`을 `LocalDate`로 변환 후 비교
    val matchingEvents = events.filter {
        it.startDt.date.toJavaLocalDate() <= date && date <= it.endDt.date.toJavaLocalDate()
    }

    val textHeight = remember { mutableStateOf(15.dp) } // 첫날의 Text 높이를 저장
    val density = LocalDensity.current  // LocalDensity를 미리 가져오기

    val showBottomSheet = remember { mutableStateOf(false) }
    val selectedEvents = remember { mutableStateOf<List<MySchedule>>(emptyList()) }

    // 🎯 장기 일정(이틀 이상)과 단기 일정(당일) 분리
    val longEvents = matchingEvents.filter { it.startDt != it.endDt } // 이틀 이상 지속되는 일정
    val singleEvents = matchingEvents.filter { it.startDt == it.endDt } // 당일 일정

    // 🎯 장기 일정을 기간이 긴 순서대로 정렬하고, 단기 일정은 그대로 배치
    val sortedEvents =
        longEvents.sortedByDescending {
            it.endDt.date.toEpochDays() - it.startDt.date.toEpochDays()
        } + singleEvents


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isOutDate) {
                selectedEvents.value = sortedEvents // ✅ 클릭한 날짜의 일정 저장
                showBottomSheet.value = true // ✅ BottomSheet 열기
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 날짜 표시
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isOutDate) Color.LightGray else Color.Black,  // outDate는 회색
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(5.dp))

        if (!isOutDate) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        selectedEvents.value = sortedEvents // ✅ 클릭한 날짜의 일정 저장
                        showBottomSheet.value = true // ✅ BottomSheet 열기
                    },
                verticalArrangement = Arrangement.Top
            ) {

                // 정렬된 이벤트 표시
                sortedEvents.forEachIndexed { index, event ->

                    val startDate = event.startDt.date.toJavaLocalDate()
                    val endDate = event.endDt.date.toJavaLocalDate()

                    // 🎯 처음과 끝에 RoundedCornerShape 설정
                    val shape = when {
                        startDate == endDate -> RoundedCornerShape(10.dp) // 당일 일정
                        date == startDate -> RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp
                        ) // 시작일
                        date == endDate -> RoundedCornerShape(
                            topEnd = 10.dp,
                            bottomEnd = 10.dp
                        ) // 종료일
                        else -> RoundedCornerShape(0.dp) // 중간일
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(  // TODO: 서버 연동 후 색상 코드 테스트 해보기
                                if (event.color.startsWith("#") && event.color.length == 7) {
                                    Color(android.graphics.Color.parseColor(event.color))
                                } else {
                                    Color.Gray // 기본 색상 적용
                                }, shape = shape
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (date == startDate) {
                            // 첫날일 경우 제목 표시
                            Text(
                                text = event.title,
                                fontSize = 8.sp,
                                color = Color.Black,
                                maxLines = 2,
                                softWrap = true, // ✅ 자동 줄바꿈 활성화
                                // overflow = TextOverflow.Ellipsis, // ✅ 너무 길면 ... 표시 => ???
                                lineHeight = 3.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(start = 3.dp)
                                    .onSizeChanged { size ->
                                        textHeight.value =
                                            with(density) { size.height.toDp() } // 첫날 높이 저장
                                    }
                                    .fillMaxWidth()
                                    .height(textHeight.value) // 첫날 높이 적용
                            )
                        } else {
                            // 이후 날짜의 높이를 첫날과 동일하게 유지
                            Box(
                                modifier = Modifier
                                    .height(textHeight.value) // 첫날과 동일한 높이 설정
                                    .padding(start = 3.dp, top = 2.dp)
                                    .fillMaxWidth()
                            ) {
                                // 이후 날짜는 빈 텍스트로 유지 (배경만 표시)
                                Text(
                                    text = event.title,
                                    fontSize = 8.sp,
                                    maxLines = 2,
                                    lineHeight = 3.sp,
                                    color = Color.Transparent,
                                )
                            }
                        }
                    }

                    // 일정 간 간격 추가 (선택적)
                    if (index < sortedEvents.size - 1) {
                        Spacer(modifier = Modifier.height(3.dp))
                    }

                }

            }
        }
    }

    // ✅ 날짜 클릭 시, 일정 추가 바텀시트 실행
    DayBottomSheet(
        showBottomSheet = showBottomSheet,
        selectedEvents = selectedEvents.value,
        date = date,
        navController = navController
    )
}

// ✅ YearMonth 확장 함수 추가 (YearMonth 비교를 쉽게 하기 위함)
val LocalDateTime.yearMonth: YearMonth
    get() = YearMonth.of(this.year, this.month)

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

    val navController = rememberNavController()
    HomeScreen(navController)
}


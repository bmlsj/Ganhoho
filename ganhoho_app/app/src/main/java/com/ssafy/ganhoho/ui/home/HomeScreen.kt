package com.ssafy.ganhoho.ui.home

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.yearMonth
import com.ssafy.ganhoho.data.model.dto.schedule.MySchedule
import com.ssafy.ganhoho.data.model.dto.schedule.WorkScheduleDto
import com.ssafy.ganhoho.fcm.LocationWorker
import com.ssafy.ganhoho.ui.BasicTopAppBar
import com.ssafy.ganhoho.util.convertWorkScheduleToMySchedule
import com.ssafy.ganhoho.util.parsedColor
import com.ssafy.ganhoho.util.toLocalDate
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.ScheduleViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import java.util.concurrent.TimeUnit


@Composable
fun HomeScreen(navController: NavController) {

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(5) }
    val endMonth = remember { currentMonth.plusMonths(5) }
    val daysOfWeek = DayOfWeek.entries

    // 📌 현재 보이는 월을 상태로 저장 (초기값: 현재 월)
    val currentMonthState = remember { mutableStateOf(YearMonth.now()) }

    val scheduleViewModel: ScheduleViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // 개인 스케쥴 조회 리스트
    val myScheduleState = scheduleViewModel.mySchedule.collectAsState().value
    val myScheduleList = remember(myScheduleState) {
        myScheduleState?.getOrNull()?.data ?: emptyList()
    }

    // 근무 스케쥴 조회
    val myWorkScheduleState = scheduleViewModel.myWorkSchedule.collectAsState().value
    val myWorkSchedule =
        remember(myWorkScheduleState) { myWorkScheduleState?.getOrNull() ?: emptyList() }

    // 토큰 로드하기
    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        }
    }

    LaunchedEffect(token) {
        if (token != null) {
            // 근무 스케쥴 불러오기
            scheduleViewModel.getMyWorkSchedule(token)
            Log.d("schedule", myScheduleList.toString())
            // 개인 스케쥴 불러오기
            scheduleViewModel.getMySchedule(token)
        }
    }


    // ✅ 달력이 바뀌어도 다시 불러오기
    LaunchedEffect(token, currentMonthState.value) {
        if (token != null) {
            scheduleViewModel.getMySchedule(token)
        }
    }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonthState.value,
        firstDayOfWeek = DayOfWeek.SUNDAY,
        outDateStyle = OutDateStyle.EndOfRow
    )

    // 📌 캘린더의 현재 보이는 달이 변경될 때 상태 업데이트
    LaunchedEffect(calendarState.firstVisibleMonth, myScheduleList) {
        currentMonthState.value = calendarState.firstVisibleMonth.yearMonth
        if (token != null) {
            scheduleViewModel.fetchMySchedules(token)
        }  // 월이 바뀔때마다 일정 다시 로드
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
    ) {

        //
        BasicTopAppBar(navController = navController)
        // 앱바와 헤더 사이 공간
        Spacer(modifier = Modifier.height(10.dp))

        // 캘린더 헤더
        Text(
            text = "${currentMonthState.value.year}년 ${
                currentMonthState.value.month.getDisplayName(TextStyle.FULL, Locale.KOREA)
            }",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 캘린더 출력
        HorizontalCalendar(
            state = calendarState,
            dayContent = { day ->
                if (token != null) {
                    DayContent(
                        myScheduleList,
                        myWorkSchedule,
                        day,
                        currentMonthState.value,
                        navController,
                        token
                    )
                }
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
    myScheduleList: List<MySchedule>,
    myWorkScheduleList: List<WorkScheduleDto>,
    day: CalendarDay,
    currentMonth: YearMonth,
    navController: NavController,
    token: String
) {

    val scheduleViewModel : ScheduleViewModel = viewModel()
    val date = day.date
    val isOutDate = date.yearMonth != currentMonth  // ✅ outDate 여부 확인

    // ✅ 근무 일정 변환 -> MySchedule형태로 변환
    val convertedWorkSchedules = convertWorkScheduleToMySchedule(myWorkScheduleList)

    // ✅ 해당 날짜의 *근무 스케줄* 필터링 (근무 일정은 무조건 당일 일정)
    val workScheduleEvents = convertedWorkSchedules.filter {
        it.startDt.toLocalDate() == date
    }

    // 해당 날짜의 이벤트 필터링
    // ✅ `LocalDateTime`을 `LocalDate`로 변환 후 비교
    val matchingEvents = myScheduleList.filter {
        it.startDt.toLocalDate() <= date &&
                date <= it.endDt.toLocalDate()
    }

    val textHeight = remember { mutableStateOf(15.dp) } // 첫날의 Text 높이를 저장
    val density = LocalDensity.current  // LocalDensity를 미리 가져오기

    val showBottomSheet = remember { mutableStateOf(false) }
    val selectedEvents = remember { mutableStateOf<List<MySchedule>>(emptyList()) }

    // 🎯 장기 일정(이틀 이상)과 단기 일정(당일) 분리
    val longEvents = matchingEvents.filter { it.startDt != it.endDt } // 이틀 이상 지속되는 일정
    val singleEvents = matchingEvents.filter { it.startDt == it.endDt } // 당일 일정

    // 🎯 근무 시간 다음, 장기 일정을 기간이 긴 순서대로 정렬하고, 단기 일정은 시간 순서대로 정렬
    val sortedEvents = workScheduleEvents +  // 근무 일정
            longEvents.sortedByDescending {  // 장기 일정
                (it.endDt).toLocalDate().toEpochDay() - (it.startDt).toLocalDate().toEpochDay()
            } + singleEvents.sortedBy {  // 당일 일정(시작 시간 순으로 정렬)
        LocalTime.parse(it.startDt)
    }

    // ✅ BottomSheet가 닫힐 때 일정 다시 불러오기
    LaunchedEffect(showBottomSheet.value) {
        if (!showBottomSheet.value) {
            scheduleViewModel.getMySchedule(token)
        }
    }

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

        if (!isOutDate) {  // 해당 월에 속하는 날짜일 경우,
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

                    val startDate = event.startDt.toLocalDate()
                    val endDate = event.endDt.toLocalDate()

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

                    // 색상 적용 이슈
                    Log.d("ColorCheck", "event.color: ${event.scheduleColor}")

                    val colorString = event.scheduleColor.lowercase() // ✅ 소문자로 변환
                    val parsedColor = parsedColor(colorString)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(  // TODO: 서버 연동 후 색상 코드 테스트 해보기
                                parsedColor, shape = shape
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (date == startDate) {
                            // 첫날일 경우 제목 표시
                            Text(
                                text = event.scheduleTitle,
                                fontSize = 9.sp,
                                color = Color.Black,
                                maxLines = 2,
                                softWrap = true, // ✅ 자동 줄바꿈 활성화
                                overflow = TextOverflow.Ellipsis, // ✅ 너무 길면 ... 표시 => ???
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
                                    text = event.scheduleTitle,
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
        navController = navController
    )
}

// ✅ YearMonth 확장 함수 추가 (YearMonth 비교를 쉽게 하기 위함)
val LocalDateTime.yearMonth: YearMonth
    get() = YearMonth.of(this.year, this.month)

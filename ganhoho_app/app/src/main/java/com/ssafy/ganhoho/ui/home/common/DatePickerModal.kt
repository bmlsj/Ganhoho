package com.ssafy.ganhoho.ui.home.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kizitonwose.calendar.compose.*
import com.kizitonwose.calendar.core.*
import com.ssafy.ganhoho.ui.theme.PrimaryBlue
import java.time.*
import java.time.format.TextStyle
import java.util.*

@Preview(showBackground = true)
@Composable
fun ShowCustomDatePicker() {
    val showDialog = remember { mutableStateOf(true) }
    val startDate = remember {
        mutableStateOf(LocalDate.parse(""))
    }
    val endDate = remember {
        mutableStateOf(LocalDate.parse(""))
    }
    CustomDatePickerDialog(
        showDialog = showDialog,
        startDate,
        endDate
    )
}

@Composable
fun CustomDatePickerDialog(
    showDialog: MutableState<Boolean>,
    startDate: MutableState<LocalDate?>,
    endDate: MutableState<LocalDate?>,
) {
    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Column(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                // 📌 날짜 선택
                val tempStartDate = remember { mutableStateOf<LocalDate?>(null) }
                val tempEndDate = remember { mutableStateOf<LocalDate?>(null) }

                HorizontalCalendarPicker(
                    startDate = tempStartDate,
                    endDate = tempEndDate
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 📌 저장 버튼
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (tempStartDate.value != null && tempEndDate.value != null) {
                            startDate.value = tempStartDate.value!! // ✅ 선택한 값 반영
                            endDate.value = tempEndDate.value!!
                            showDialog.value = false // ✅ 다이얼로그 닫기
                        }
                        showDialog.value = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("저장")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun HorizontalCalendarPicker(
    startDate: MutableState<LocalDate?>,
    endDate: MutableState<LocalDate?>
) {
    val currentMonth = remember { YearMonth.now() }
    val calendarState = rememberCalendarState(
        startMonth = currentMonth.minusMonths(6),
        endMonth = currentMonth.plusMonths(6),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.SUNDAY,
        outDateStyle = OutDateStyle.EndOfRow
    )

    Column {
        HorizontalCalendar(
            state = calendarState,
            monthHeader = { month ->
                Text(
                    text = "${month.yearMonth.year}년 ${
                        month.yearMonth.month.getDisplayName(
                            TextStyle.FULL,
                            Locale.KOREA
                        )
                    }",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            },
            dayContent = { day ->
                val date = day.date
                date == startDate.value || date == endDate.value ||
                        (startDate.value != null && endDate.value != null && date in startDate.value!!..endDate.value!!)
                val backgroundColor = when {
                    date == startDate.value -> PrimaryBlue // 시작 날짜 선택
                    date == endDate.value -> PrimaryBlue  // 종료 날짜 선택
                    startDate.value != null && endDate.value != null && date in startDate.value!!..endDate.value!! -> Color(
                        0xffE9F6FA
                    )

                    else -> Color.Transparent // 선택되지 않은 날짜는 투명
                }

                if (day.position == DayPosition.MonthDate) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(backgroundColor, shape = CircleShape)
                            .clickable {
                                when {
                                    startDate.value == null -> startDate.value = date
                                    endDate.value == null && date >= startDate.value -> endDate.value =
                                        date

                                    else -> {
                                        startDate.value = date
                                        endDate.value = null
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        )
    }
}

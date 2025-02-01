package com.ssafy.ganhoho.ui.home.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ssafy.ganhoho.ui.theme.PrimaryBlue
import java.time.LocalDate
import java.time.YearMonth

@Preview(showBackground = true)
@Composable
fun ShowCustomDatePicker() {

    val showDialog = remember { mutableStateOf(true) }
    CustomDatePickerDialog(
        showDialog = showDialog,
        onDateSelected = { date -> println("Selected date: $date") }
    )
}


@Composable
fun CustomDatePickerDialog(
    showDialog: MutableState<Boolean>,
    onDateSelected: (LocalDate) -> Unit
) {
    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Column(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                CustomDatePicker(
                    onDateSelected = { selectedDate ->
                        onDateSelected(selectedDate)
                        showDialog.value = false
                    }
                )

                Spacer(modifier = Modifier.padding(8.dp))

                // 닫기 버튼
                Button(
                    onClick = { showDialog.value = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Text("닫기")
                }
            }
        }
    }
}

/* 
* 일정 추가에서 날짜 범위 선택할 때 사용할 데이터 피커
*/
// 날짜 범위 선택
@Composable
fun CustomDatePicker(
    onDateSelected: (LocalDate) -> Unit
) {
    var currentMonth = remember { mutableStateOf(YearMonth.now()) } // 현재 연도와 월
    var selectedDate = remember { mutableStateOf<LocalDate?>(null) } // 선택된 날짜

    Column(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        // 상단 Month, Year
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { currentMonth.value = currentMonth.value.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }

            Text(
                text = "${currentMonth.value.month.name} ${currentMonth.value.year}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            IconButton(onClick = { currentMonth.value = currentMonth.value.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }

        // 요일 헤더
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(text = day, style = MaterialTheme.typography.bodySmall)
            }
        }

        // 📌 날짜 표시
        val daysInMonth: Int = currentMonth.value.lengthOfMonth() // 해당 월의 총 일수
        val firstDayOfMonth: Int = currentMonth.value.atDay(1).dayOfWeek.value % 7 // 해당 월의 시작 요일

        // 날짜 셀
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            val totalCells = firstDayOfMonth + daysInMonth
            items(totalCells) { index ->
                if (index < firstDayOfMonth) {
                    // 빈 공간 (이전 달 공백)
                    Box(modifier = Modifier.aspectRatio(1f))
                } else {
                    val day = index - firstDayOfMonth + 1
                    val date = LocalDate.of(currentMonth.value.year, currentMonth.value.month, day)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(
                                if (selectedDate.value == date) Color.Cyan else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedDate.value = date
                                onDateSelected(date)
                            }
                    ) {
                        Text(text = day.toString(), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

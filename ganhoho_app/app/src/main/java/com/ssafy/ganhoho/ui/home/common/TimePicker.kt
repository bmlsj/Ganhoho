package com.ssafy.ganhoho.ui.home.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TimePicker(
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    // 시간, 분, AM/PM 값을 위한 상태를 저장합니다.
    val hours = (1..23).toList() // 1~12시간 (12시간 포맷)
    val minutes = (0..59).toList() // 0~59분

    var selectedHour by remember { mutableStateOf(8) } // 기본값: 8
    var selectedMinute by remember { mutableStateOf(0) } // 기본값: 0

    // Row를 사용하여 시간, 분, AM/PM을 가로로 정렬합니다.
    Row(
        modifier = Modifier
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 시간 선택
        AutoScrollPicker(
            items = hours,
            selectedItem = selectedHour,
            onItemSelected = {
                selectedHour = it; onTimeSelected(
                selectedHour,
                selectedMinute,
                //    selectedAmPm
            )
            }
        )

        // ":" 텍스트
        Text(
            text = ":",
            fontSize = 24.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // 분 선택
        AutoScrollPicker(
            items = minutes,
            selectedItem = selectedMinute,
            onItemSelected = { selectedMinute = it; onTimeSelected(selectedHour, selectedMinute) }
        )

    }
}

@Composable
fun <T> AutoScrollPicker(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit
) {

    // LazyColumn의 스크롤 상태를 추적
    val listState = rememberLazyListState()

    // 스크롤 멈췄을 때 자동 선택
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset

            // 선택 항목 계산 (중앙에 가까운 항목 선택)
            val visibleIndex = if (offset > 50) firstVisibleIndex + 1 else firstVisibleIndex
            onItemSelected(items.getOrNull(visibleIndex) ?: items.first())
        }
    }

    // LazyColumn을 사용해 아이템 표시
    Box(
        modifier = Modifier
            .height(100.dp) // 상하 한칸씩만 보이도록 높이 제한
            .width(50.dp)
            .clipToBounds()  // 높이 밖의 항목 잘라냄
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 35.dp),
            verticalArrangement = Arrangement.Center
        ) {
            items(items) { item ->
                Text(
                    text = item.toString().padStart(2, '0'), // 2자리 숫자 표시 (e.g., 01, 02)
                    color = if (item == selectedItem) Color.Black else Color.Gray,  // 선택된 애들만 검정으로
                    fontWeight = if (item == selectedItem) FontWeight.Bold else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            onItemSelected(item) // 항목 클릭 시 선택된 값 업데이트
                        },
                    textAlign = TextAlign.Center, // 중앙 정렬
                    fontSize = 25.sp
                )

            }
        }

        // 가운데에 투명한 오버레이 추가 (선택 강조 효과)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(40.dp)
                .background(Color.Transparent) // 선택된 항목이 강조되도록 중앙 영역만 표시
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimePickerExample() {
    TimePicker { hour, minute ->
        println("Selected Time: $hour:$minute") // 선택된 시간을 출력
    }
}
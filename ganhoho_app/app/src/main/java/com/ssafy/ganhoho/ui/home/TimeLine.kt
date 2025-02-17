package com.ssafy.ganhoho.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.ganhoho.data.model.dto.schedule.TimelineEvent


@Composable
fun TimelineScreen(events: List<TimelineEvent>, onEventClick: (TimelineEvent) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(events) { event ->
            TimelineItem(event = event, onClick = { onEventClick(event) })
        }
    }
}

@Composable
fun TimelineItem(event: TimelineEvent, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onClick()
            }, // 항목 간 간격
        verticalAlignment = Alignment.Top // 위쪽 정렬
    ) {
        // 1. 시간 표시
        Text(
            text = event.startTime,
            modifier = Modifier.width(60.dp), // 일정한 너비로 고정
            textAlign = TextAlign.End, // 오른쪽 정렬
            fontSize = 14.sp,
            color = Color.Gray
        )

        // 2. 타임라인 점과 선
        Column(
            modifier = Modifier
                .width(40.dp) // 점과 선을 표시할 영역의 너비
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 점
            Box(
                modifier = Modifier
                    .size(24.dp) // 점 크기
                    .clip(CircleShape) // 원형
                    .background(
                        color = event.color
                    )
                    .border(2.dp, Color.White, CircleShape) // 테두리 추가
            )
            // 선 (마지막 아이템은 선 없음)
            if (!event.isLast) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp) // 선의 길이
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    event.color.copy(alpha = 0.5f),
                                    Color(0xffD1EEF2)
                                )
                            )
                        )
                )
            }
        }

        // 3. 이벤트 정보
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            // 이벤트 제목
            Text(
                text = event.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            // 이벤트 세부 정보
            Text(
                text = event.dateRange,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

//@Composable
//fun TimelineExample() {
//    val events = listOf(
//        // ✅ 1. 근무 일정 (항상 "All Day", 우선순위 가장 높음)
//        TimelineEvent(
//            startTime = "All Day",
//            title = "오전 근무",
//            dateRange = "2025.01.01",
//            color = Color(0xffD1EEF2), // 근무 일정 색상
//            isLast = false,
//            mySchedule = null
//        ),
//
//        // ✅ 2. 장기 일정 (우선순위 두 번째)
//        TimelineEvent(
//            startTime = "19:00",
//            title = "동기 회식 🎉",
//            dateRange = "2025.01.01 - 2025.01.04",
//            color = Color(0xffD1EEF2),
//            isLast = false
//        ),
//
//        TimelineEvent(
//            startTime = "20:00",
//            title = "북 스터디 📖",
//            dateRange = "2025.01.01 - 2025.01.10",
//            color = Color(0xffFFCAE6),
//            isLast = false
//        ),
//
//        // ✅ 3. 당일 일정 (우선순위 가장 낮음)
//        TimelineEvent(
//            startTime = "09:00",
//            title = "병원 예약 🏥",
//            dateRange = "2025.01.05",
//            color = Color(0xffFFD700), // 노란색
//            isLast = false
//        ),
//        TimelineEvent(
//            startTime = "14:00",
//            title = "스터디 그룹 회의 📚",
//            dateRange = "2025.01.06",
//            color = Color(0xff98FB98), // 연한 초록색
//            isLast = true, // 마지막 일정
//        )
//    )
//
//
//    TimelineScreen(events = events, onEventClick = {})
//}
//
//@Preview(
//    name = "Timeline Preview", // Preview 이름
//    showBackground = true,     // 배경 표시 여부
//    backgroundColor = 0xFFFFFFFF, // 배경색 (Hex 값)
//    widthDp = 360,             // 미리보기 창 너비 (dp)
//    heightDp = 640             // 미리보기 창 높이 (dp)
//)
//@Composable
//fun PreviewTimelineScreen() {
//    // 샘플 데이터를 만들어서 미리보기로 전달
//    TimelineExample()
//}
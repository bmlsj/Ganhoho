package com.ssafy.ganhoho.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ssafy.ganhoho.data.model.dto.schedule.MySchedule
import com.ssafy.ganhoho.data.model.dto.schedule.TimelineEvent
import com.ssafy.ganhoho.ui.theme.PrimaryBlue
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    selectedEvents: List<MySchedule>,
    navController: NavController
) {

    val showAddDateBottomSheet = remember { mutableStateOf(false) } // ✅ 일정 추가 모달 상태 추가

    if (showBottomSheet.value || showAddDateBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
                showAddDateBottomSheet.value = false
            },
            containerColor = Color.White,
        ) {
            AnimatedVisibility(
                visible = !showAddDateBottomSheet.value, // ✅ 첫 번째 모달 : 일정 조회 모달
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { -it })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showBottomSheet.value = false }
                            .align(Alignment.End)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedEvents.isNotEmpty()) {  // 일정이 있으면, 타임라인 보여주기

                        TimelineScreen(events = selectedEvents.mapIndexed { index, event ->
                            val startDt = event.startDt.toLocalDateTime()
                            val endDt = event.endDt.toLocalDateTime()
                            TimelineEvent(
                                startTime = if (event.isTimeSet) {
                                    "${startDt.hour}:00"
                                } else {
                                    "All Day"
                                },
                                title = event.title,
                                dateRange = if (startDt.toLocalDate() == endDt.toLocalDate()) {
                                    // 당일 일정
                                    "${startDt.year}.${startDt.monthValue}.${startDt.dayOfMonth}"
                                } else {
                                    // 장기 일정
                                    "${startDt.year}.${startDt.monthValue}.${startDt.dayOfMonth} - " +
                                            "${endDt.year}.${endDt.monthValue}.${endDt.dayOfMonth}"
                                },
                                // TODO: 서버 연동 후 컬러 테스트 필요
                                color = if (event.color.startsWith("#") && event.color.length == 7) {
                                    Color(android.graphics.Color.parseColor(event.color))
                                } else {
                                    Color.Gray // 기본 색상 적용
                                },
                                isLast = index == selectedEvents.size - 1
                            )
                        })

                    } else {  // 일정이 없는 경우, 일정 추가 모달 보여주기
                        AddDateBottomSheet(
                            showBottomSheet = showBottomSheet,
                            navController = navController
                        )
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                    Button(
                        onClick = {
                            showAddDateBottomSheet.value = true // ✅ 슬라이드 전환
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "추가하기",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }

            AnimatedVisibility(
                visible = showAddDateBottomSheet.value, // ✅ 두 번째 모달 (일정 추가 모달)
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { -it })
            ) {
                AddDateBottomSheet(
                    showBottomSheet = showAddDateBottomSheet,
                    navController = navController
                )
            }
        }
    }
}

package com.ssafy.ganhoho.ui.home.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ssafy.ganhoho.data.model.dto.MySchedule
import com.ssafy.ganhoho.ui.theme.PrimaryBlue
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    selectedEvents: List<MySchedule>,
    date: LocalDate,
    navController: NavController,
    onScheduleAdded: (MySchedule) -> Unit  // ✅ 콜백 추가
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
                visible = !showAddDateBottomSheet.value, // ✅ 첫 번째 모달 (기본 모달)
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

                    if (selectedEvents.isNotEmpty()) {
                        TimelineScreen(events = selectedEvents.mapIndexed { index, event ->
                            TimelineEvent(
                                startTime = "All Day",
                                title = event.title,
                                dateRange = "${date.year}.${date.monthValue}.${date.dayOfMonth}",
                                // TODO: 서버 연동 후 컬러 테스트 필요
                                color = if (event.color.startsWith("#") && event.color.length == 7) {
                                    Color(android.graphics.Color.parseColor(event.color))
                                } else {
                                    Color.Gray // 기본 색상 적용
                                },
                                isLast = index == selectedEvents.size - 1
                            )
                        })
                    } else {
                        AddDateBottomSheet(
                            showBottomSheet = showBottomSheet,
                            navController = navController,
                            onScheduleAdded = { newSchedule ->
                                onScheduleAdded(newSchedule)  // ✅ HomeScreen의 이벤트 리스트 업데이트
                                showBottomSheet.value = false
                            }
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
                    navController = navController,
                    onScheduleAdded = { newSchedule ->
                        onScheduleAdded(newSchedule)  // ✅ HomeScreen의 이벤트 리스트 업데이트
                        showBottomSheet.value = false
                        showAddDateBottomSheet.value = false
                    }
                )
            }
        }
    }

//    if (showBottomSheet.value) {
//        ModalBottomSheet(
//            onDismissRequest = { showBottomSheet.value = false },
//            containerColor = Color.White,
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight(0.9f)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(10.dp)
//                ) {
//                    // 닫기 버튼
//                    Icon(
//                        imageVector = Icons.Default.Close,
//                        contentDescription = "Close",
//                        tint = Color.Gray,
//                        modifier = Modifier
//                            .size(24.dp)
//                            .clickable { showBottomSheet.value = false }
//                            .align(Alignment.End)
//                    )
//
//                    Spacer(modifier = Modifier.height(10.dp))
//
//                    // 선택된 이벤트 표시
//                    if (selectedEvents.isNotEmpty()) {
//                        val timelineEvents = selectedEvents.mapIndexed { index, event ->
//                            TimelineEvent(
//                                startTime = "All Day", // 시간은 임의로 설정
//                                title = event.title,
//                                dateRange = "${date.year}.${date.monthValue}.${date.dayOfMonth}",
//                                color = event.color,
//                                isLast = index == selectedEvents.size - 1
//                            )
//                        }
//                        TimelineScreen(events = timelineEvents)
//                    } else {
//                        AddDateBottomSheet(
//                            showBottomSheet = showBottomSheet,
//                            navController = navController,
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(50.dp))
//
//                    // 스케줄 추가 버튼
//                    Button(
//                        onClick = {
//                            // TODO: 스케줄 DB에 추가 기능
//
//                            // ✅ 일정 추가 모달로 이동
//                            showBottomSheet.value = false // ✅ 현재 모달 닫기
//                            showAddDateBottomSheet.value = true // ✅ 일정 추가 모달 열기
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 20.dp)
//                            .height(50.dp),
//                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
//                        shape = RoundedCornerShape(20.dp)
//                    ) {
//                        Text(
//                            text = "추가하기",
//                            color = Color.White,
//                            fontSize = 18.sp
//                        )
//                    }
//                }
//            }
//
//            // ✅ `showAddDateBottomSheet`이 true일 때 일정 추가 모달 표시
//            LaunchedEffect(showAddDateBottomSheet.value) {
//                if (showAddDateBottomSheet.value) {
//                    showAddDateBottomSheet.value = false
//                    showBottomSheet.value = false // 기존 모달 닫기
//                    showAddDateBottomSheet.value = true
//                }
//            }
//
//            // ✅ 일정 추가 모달
//            if (showAddDateBottomSheet.value) {
//                AddDateBottomSheet(
//                    showBottomSheet = showAddDateBottomSheet,
//                    navController = navController,
//                )
//            }
//        }
//    }
}

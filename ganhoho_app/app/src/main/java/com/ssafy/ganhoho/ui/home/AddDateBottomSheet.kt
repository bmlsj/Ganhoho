package com.ssafy.ganhoho.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.data.model.dto.schedule.MyScheduleRequest
import com.ssafy.ganhoho.ui.home.common.CustomDatePickerDialog
import com.ssafy.ganhoho.ui.home.common.TimePicker
import com.ssafy.ganhoho.ui.theme.FieldLightGray
import com.ssafy.ganhoho.ui.theme.PrimaryBlue
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.ScheduleViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun ShowPreview() {
    val navController = rememberNavController()
    AddDateBottomSheet(mutableStateOf(true), navController)
}

@Composable
fun AddDateBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    navController: NavController
) {

    val startDate = remember { mutableStateOf<LocalDate?>(null) } // ✅ 날짜 기본값 설정
    val endDate = remember { mutableStateOf<LocalDate?>(null) }
    val title = remember { mutableStateOf("") }  // 일정 제목 입력
    val selectedColor = remember { mutableStateOf(Color.White) }
    var isTimeSet by remember { mutableStateOf(false) } // ✅ 시간 설정 Switch 상태 저장
    val isPublic = remember { mutableStateOf(false) }

    val startTime = remember { mutableStateOf("00:00") }
    val endTime = remember { mutableStateOf("00:00") }

    // viewModel
    val scheduleViewModel: ScheduleViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // 토큰 로드하기
    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current

    // 일정 추가 결과
    val addScheduleResult by scheduleViewModel.addMyScheduleResult.collectAsState()

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        }
    }

    // 일정 추가 성공 시
    LaunchedEffect(addScheduleResult) {
        addScheduleResult?.let { result ->
            if (result.isSuccess) {
                showBottomSheet.value = false
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
                scheduleViewModel.resetScheduleResult()  // 일정 추가 후, 다시 상태 초기화
            } else {
                Log.e("addScheduleResult", "일정 추가 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        // 일정 추가
        TextField(
            value = title.value,
            onValueChange = { title.value = it },
            placeholder = {
                if (title.value.isEmpty()) {
                    Text(
                        "일정을 입력해주세요.",
                        color = Color(0xFFC0C0C0),
                        fontSize = 24.sp
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFEFEFEF),
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Blue,
                focusedContainerColor = Color.Transparent, // 내부 배경 투명
                unfocusedContainerColor = Color.Transparent, // 내부 배경 투명
                disabledContainerColor = Color.Transparent // 비활성화 상태도 투명
            )
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically // ✅ 수직 정렬
        ) {
            // 컬러 드롭다운
            ColorDropdownMenu(selectedColor)
            Spacer(modifier = Modifier.width(6.dp))

            // 공개 비공개 버튼
            ToggleButton(isPublic)
        }

        Spacer(modifier = Modifier.height(10.dp))
        // ✅ 수정된 DateRangePicker 적용
        DateRangePicker(startDate, endDate)

        Spacer(modifier = Modifier.height(20.dp))

        // 시간 설정 부분
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "시간 설정",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )

            Switch(
                checked = isTimeSet,
                onCheckedChange = {
                    isTimeSet = it
                },
                modifier = Modifier.scale(0.8f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xff76F47E)
                ),
            )

        }

        // 시간 설정
        if (isTimeSet) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // 시작 시간
                TimePicker { hour, minute -> startTime.value = "%02d:%02d".format(hour, minute) }
                Log.d("addTimePicker", startTime.value)
                Spacer(modifier = Modifier.width(5.dp))
                // 종료 시간
                TimePicker { hour, minute -> endTime.value = "%02d:%02d".format(hour, minute) }

            }
        }


        Spacer(modifier = Modifier.height(30.dp))

        // 등록 버튼
        Button(
            onClick = {
                // TODO: 스케줄 추가 기능
                try {
                    val startDateTime = if (isTimeSet) {
                        LocalDateTime.parse("${startDate.value}T${startTime.value}:01")
                    } else {
                        startDate.value!!.atStartOfDay()  // 00:00:00
                    }

                    val endDateTime = if (isTimeSet) {
                        LocalDateTime.parse("${endDate.value}T${endTime.value}:01")
                    } else {
                        endDate.value!!.atTime(23, 59, 59)  // 23:59:59
                    }

                    Log.d("addTime", "${startDate.value} ${endDate.value}")

                    val newSchedule =
                        MyScheduleRequest(  // 새 일정 추가
                            startDt = startDateTime.toString(),
                            endDt = endDateTime.toString(),
                            scheduleTitle = title.value,
                            scheduleColor = "#${Integer.toHexString(selectedColor.value.hashCode())}", // 색상을 HEX 코드로 변환
                            isPublic = isPublic.value,
                            isTimeSet = isTimeSet
                        )


                    Log.d("addSchedule", newSchedule.toString())

                    // 개인 스케쥴 추가
                    if (token != null) {
                        scheduleViewModel.addMySchedule(token = token, request = newSchedule)
                    }

                } catch (e: Exception) {
                    println("🚨 날짜 변환 오류: ${e.message}")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "등록",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

// 공개/비공개
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ToggleButton(isPublic: MutableState<Boolean>) {

    val toggleOffset by animateDpAsState(
        targetValue = if (isPublic.value) 0.dp else 60.dp,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .height(35.dp)
            .padding(1.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0xffDADADA)) // 배경색
            .border(BorderStroke(1.dp, color = FieldLightGray), shape = RoundedCornerShape(25.dp))
            .clickable { isPublic.value = !isPublic.value },
        contentAlignment = Alignment.CenterStart
    ) {
        // 원형 이동 버튼
        Box(
            modifier = Modifier
                .offset(x = toggleOffset)
                .padding(4.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF5A5A5A))
        )

        // 텍스트 표시
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "공개",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = if (isPublic.value) Color.White else Color.Black
            )

            Spacer(modifier = Modifier.weight(0.5f))
            Text(
                text = "비공개",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = if (!isPublic.value) Color.White else Color.Black
            )
        }
    }

}


// 드롭다운
@Composable
fun ColorDropdownMenu(
    selectedColor: MutableState<Color>
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = listOf(
        Color(0xFFF08080),
        Color(0xFFFFADAD),
        Color(0xFFFFD6A5),
        Color(0xFFFDFFB6),
        Color(0xFFCAFFBF),
        Color(0xFFBDE0FE),
        Color(0xFFA2D2FF),
        Color(0xFFCDB4DB),
        Color(0xFFFFC6FF),
        Color(0xFFFFC8DD),
    )

    // ✅ 선택되지 않은 경우 기본 색상은 `gray`
    val borderColor =
        if (selectedColor.value == Color.White) Color.LightGray else selectedColor.value

    // 컬러 드롭다운
    Box(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, shape = RoundedCornerShape(10.dp))
            .clickable { expanded = true }
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(selectedColor.value, shape = RoundedCornerShape(10.dp))
                .border(1.dp, borderColor, shape = RoundedCornerShape(10.dp))
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .border(1.dp, borderColor, shape = RoundedCornerShape(10.dp))
                .padding(8.dp)
        ) {
            Column {
                colors.chunked(6).forEach { rowColors ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        rowColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(color, shape = CircleShape)
                                    .clickable {
                                        selectedColor.value = color
                                        expanded = false
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

}

// 날짜 범위 선택
@Composable
fun DateRangePicker(
    startDate: MutableState<LocalDate?>,
    endDate: MutableState<LocalDate?>
) {

    val showDatePicker = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        DateField( // 시작 날짜 선택 부분 표시
            date = startDate.value,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(10.dp))
        Image(
            painter = painterResource(id = R.drawable.line),
            contentDescription = "line",
            modifier = Modifier.size(10.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))
        DateField( // 시작 날짜 선택 부분 표시
            date = endDate.value,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = {
            showDatePicker.value = true
        }) {
            Icon(
                imageVector = Icons.Default.DateRange, contentDescription = "Date Range Picker"
            )
        }
    }

    if (showDatePicker.value) {  // 일정 추가에서 기간 선택할 다이얼로그 띄우기
        CustomDatePickerDialog(
            showDialog = showDatePicker,
            startDate = startDate, // ✅ 전달
            endDate = endDate      // ✅ 전달
        )
    }
}

// 날짜 선택 필드
@Composable
fun DateField(
    date: LocalDate?,
    modifier: Modifier = Modifier
) {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // ✅ 날짜 포맷 설정
    val formattedDate = date?.format(formatter) ?: "yyyy-mm-dd" // ✅ null이면 플레이스홀더 표시

    Box(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth(), // ✅ 가득 차게 설정
        contentAlignment = Alignment.CenterStart
    ) {

        Image(
            painter = painterResource(id = R.drawable.inner_shadow_textfield),
            contentDescription = "TextField Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize() // Box의 크기에 맞춤
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = formattedDate,
                fontSize = 15.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = if (formattedDate == "yyyy-mm-dd") Color.Gray else Color.Black
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateFieldPreview() {
    Box(
        modifier = Modifier.fillMaxWidth() // ✅ 가득 차게 설정
    ) {
        DateField(
            date = "2025-01-22".toLocalDate()
        )
    }
}
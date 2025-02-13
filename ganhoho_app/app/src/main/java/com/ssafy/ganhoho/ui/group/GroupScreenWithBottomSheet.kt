package com.ssafy.ganhoho.ui.group

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.viewmodel.BottomNavViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupBottomSheet(
    isSheetOpen: Boolean,
    onDismiss: () -> Unit,
    viewModel: BottomNavViewModel
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // 바텀시트 상태 업데이트
    LaunchedEffect(isSheetOpen) {
        if (isSheetOpen) {
            coroutineScope.launch { bottomSheetState.show() }
        } else {
            coroutineScope.launch { bottomSheetState.hide() }
        }
    }

    // 배경을 어둡게 (scrimColor 설정)
    ModalBottomSheet(
        onDismissRequest = {
            viewModel.showBottomNav()
            onDismiss()
        },
        sheetState = bottomSheetState,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp), // 상단 모서리 둥글게
        containerColor = Color.White, // 바텀시트 배경을 흰색으로
        scrimColor = Color.Black.copy(alpha = 0.5f) // 바텀시트 외 배경을 반투명 어둡게 설정
    ) {
        GroupBottomSheetContent(viewModel, onDismiss)
    }
}

@Composable
fun GroupBottomSheetContent(viewModel: BottomNavViewModel, onClose: () -> Unit) {
    var groupName by remember { mutableStateOf(TextFieldValue("")) }
    var selectedEmoji by remember { mutableStateOf("💗") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val emojiList = listOf("🏥", "🏡", "⭐", "🍺", "🤍")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .background(color = Color.White, shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .shadow(elevation = 20.dp, spotColor = Color(0x26000000), ambientColor = Color(0x26000000)),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "그룹 정보 입력",
            fontSize = 22.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 10.dp),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 이모지 선택 버튼
        Box(
            modifier = Modifier
                .width(85.dp)
                .wrapContentSize(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .clickable { isDropdownExpanded = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { }) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_dropdown),
                        contentDescription = "이모지 아이콘",
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                emojiList.forEach { emoji ->
                    DropdownMenuItem(
                        text = { Text(emoji, fontSize = 24.sp) },
                        onClick = {
                            selectedEmoji = emoji
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 그룹 이름 입력 필드
        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            placeholder = { Text("그룹 이름을 입력해 주세요.", color = Color(0xFFB5B5B5)) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 그룹 만들기 버튼
        Button(
            onClick = {
                viewModel.showBottomNav()
                onClose()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF79C7E3)),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(13.dp), clip = false)
        ) {
            Text("그룹 만들기", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true, name = "Group Bottom Sheet")
@Composable
fun PreviewGroupBottomSheet() {
    GroupBottomSheet(isSheetOpen = true, onDismiss = {}, viewModel = BottomNavViewModel())
}

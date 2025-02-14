package com.ssafy.ganhoho.ui.group

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
    viewModel: BottomNavViewModel,
    onAddGroup: (String, Int) -> Unit,
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
        GroupBottomSheetContent(viewModel, onDismiss, onAddGroup)
    }
}

fun getGroupIconResource(groupIconType: Int): Int {
    //Log.d("GroupIcon", "🔍 Received groupIconType: $groupIconType")

    val validIcons = mapOf(
        1 to R.drawable.emoji_hospital,
        2 to R.drawable.emoji_dragon,
        3 to R.drawable.emoji_school,
        4 to R.drawable.emoji_heart_white,
        5 to R.drawable.emoji_beer,
        6 to R.drawable.emoji_star
    )

    return validIcons[groupIconType] ?: run {
        Log.w("GroupIcon", "⚠️ Warning: Invalid groupIconType received -> $groupIconType. Using default icon.")
        R.drawable.emoji_nurse // 기본 아이콘
    }
}



@Composable
fun GroupBottomSheetContent(
    viewModel: BottomNavViewModel,
    onClose: () -> Unit,
    onAddGroup: (String, Int) -> Unit,
) {
    var groupName by remember { mutableStateOf(TextFieldValue("")) }
    var selectedIcon by remember { mutableStateOf(1) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val iconList = listOf(1,2,3,4,5,6)


    Column(
        modifier = Modifier
            .height(300.dp)
            .padding(top = 10.dp, start = 20.dp, end = 20.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .shadow(
                elevation = 20.dp,
                spotColor = Color(0x26000000),
                ambientColor = Color(0x26000000)
            ),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "그룹 정보 입력",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 10.dp),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 이모지 선택 버튼
        Row(
            modifier = Modifier
                .height(49.dp)
                .width(85.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .padding(start = 5.dp, end = 2.dp)
                .clickable { isDropdownExpanded = true }, //클릭하면 드롭다운 열림
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = getGroupIconResource(selectedIcon)),
                contentDescription="선택된 아이콘",
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.icon_dropdown),
                contentDescription = "드롭다운 버튼",
                modifier = Modifier.size(24.dp)
            )
        }

        //드롭다운 메뉴
        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false },
            Modifier
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .align(Alignment.CenterHorizontally)
                .width(72.dp)
        ) {
            iconList.forEach{iconId ->
                DropdownMenuItem(
                    onClick = {
                        selectedIcon = iconId
                        isDropdownExpanded = false
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)

                        ){
                            Image(
                                painter = painterResource(id = getGroupIconResource(iconId)),
                                contentDescription = "아이콘 $iconId",
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

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
                if (groupName.text.isNotBlank()) {
                    onAddGroup(groupName.text, selectedIcon)
                    onClose()
                }
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

//@Preview(showBackground = true)
//@Composable
//fun GroupBottomSheetPreview() {
//
//
//    GroupBottomSheet(
//        isSheetOpen = true, // 바텀시트가 항상 열리도록 설정
//        onDismiss = { },
//        viewModel = fakeBottomNavViewModel,
//        onAddGroup = { _, _ -> }
//    )
//}

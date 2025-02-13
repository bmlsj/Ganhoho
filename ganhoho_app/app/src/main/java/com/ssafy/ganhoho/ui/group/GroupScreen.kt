package com.ssafy.ganhoho.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.ui.bottom_navigation.CustomBottomNavigation
import com.ssafy.ganhoho.ui.group.common.GroupItem
import com.ssafy.ganhoho.viewmodel.BottomNavViewModel
import kotlinx.coroutines.launch

@Composable
fun GroupScreen(
    navController: NavController,
    bottomNavViewModel: BottomNavViewModel = androidx.lifecycle.viewmodel.compose.viewModel() // ViewModel 주입
) {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    // 바텀시트 상태 변경 감지하여 네비게이션 숨기기
    LaunchedEffect(bottomSheetState.currentValue) {
        if (bottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
            bottomNavViewModel.showBottomNav()
        } else {
            bottomNavViewModel.hideBottomNav()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF4F5F8),
        bottomBar = {
            if (bottomNavViewModel.isBottomNavVisible.value) {
                CustomBottomNavigation(navController)
            }
        },
        content = { paddingValues ->
            ModalBottomSheetLayout(
                sheetState = bottomSheetState,
                sheetContent = {
                    GroupBottomSheetContent(
                        onClose = {
                            coroutineScope.launch {
                                bottomSheetState.hide() // 바텀시트 닫기
                                bottomNavViewModel.showBottomNav() // 바텀 네비 다시 보이게
                            }
                        },
                        viewModel = bottomNavViewModel // ViewModel 전달
                    )
                }
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xFFF4F5F8))
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .background(color = Color(0xFFF4F5F8))
                    ) {
                        Text(
                            text = "나의 그룹",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        val groupList = listOf(
                            GroupDto(1, "백엔드 마스터즈", R.drawable.emoji_hospital, 3),
                            GroupDto(2, "프론트엔드 위저드", R.drawable.emoji_dragon, 10),
                            GroupDto(3, "디자인 감성단", R.drawable.emoji_school, 8),
                            GroupDto(4, "AI 연구소", R.drawable.emoji_nurse, 5)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(groupList) { group ->
                                GroupItem(
                                    group = group,
                                    navController = navController,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(109.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch { bottomSheetState.show() }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF79C7E3)),
                            shape = RoundedCornerShape(13.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .navigationBarsPadding()
                                .padding(horizontal = 9.dp)
                                .shadow(
                                    elevation = 20.dp,
                                    spotColor = Color(0x1A000000),
                                    ambientColor = Color(0x1A000000)
                                )
                        ) {
                            Text("그룹 추가", color = Color.White)
                        }
                    }
                }
            }
        }
    )
}



@Preview(showBackground = true)
@Composable
fun PreviewGroupScreen() {
    val navController = rememberNavController()
    val bottomNavViewModel = BottomNavViewModel() // ViewModel 인스턴스 생성

    MaterialTheme {
        GroupScreen(navController, bottomNavViewModel)
    }
}

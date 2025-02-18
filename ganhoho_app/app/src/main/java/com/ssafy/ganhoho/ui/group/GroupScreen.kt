package com.ssafy.ganhoho.ui.group

import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.ssafy.ganhoho.data.model.response.group.GroupViewModelFactory
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.ui.group.common.GroupItem
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.BottomNavViewModel
import com.ssafy.ganhoho.viewmodel.GroupViewModel

@Composable
fun GroupScreen(
    navController: NavHostController,
    bottomNavViewModel: BottomNavViewModel = viewModel()
) {

    val authViewModel: AuthViewModel = viewModel()
    val repository = GroupRepository()

    // 토큰 로드하기
    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        }
    }

    //그룹 추가 용도
    val groupViewModel: GroupViewModel = viewModel(
        factory = GroupViewModelFactory(repository)
    )
//    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
//    val coroutineScope = rememberCoroutineScope()

    // viewGroup에서 상태 가져오기
    val groupList by groupViewModel.groupList.collectAsState()
    val errorMessage by groupViewModel.errorMessage.collectAsState()

    // bottom sheet 관련
    var isSheetOpen by remember { mutableStateOf(false) }


    // 화면 진입 시 자동으로 그룹 목록 불러오도록 설정
    LaunchedEffect(token) {
        if (token != null) {
            groupViewModel.fetchGroupList(token)
        } else {
            Log.d("GroupViewModel", "token is null")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF4F5F8))
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

            // 그룹이 없다면!
            if (groupList.isEmpty()) {
                Text(
                    text = "현재 참여하고 있는 그룹이 없습니다. \n그룹을 만들어서 스케줄을 공유해 보세요!",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            // 그룹 목록 로드 에러 발생 시 메시지 표시
            if (errorMessage != null) {
                Text(
                    text = "ERROR: 목록 불러오기 실패!",
                    fontSize = 16.sp
                )
            }

            // 나의 그룹 리스트 목록 출력
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(groupList) { group ->
                    GroupItem(
                        group = group,
                        navController = navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = {
                            // 그룹 객체를 JSON 문자열로 변환 후 네비게이션 인자로 전달
                            val groupJson = Uri.encode(Gson().toJson(group))
                            navController.navigate("EachGroupScreen/$groupJson")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 그룹 추가 버튼 -> 바텀 시트 열기
            Button(
                onClick = { isSheetOpen = true },

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

            Spacer(modifier = Modifier.weight(0.2f))

        }
    }

    // 바텀시트 추가
    if (isSheetOpen) {
        GroupBottomSheet(
            isSheetOpen = isSheetOpen,
            onDismiss = { isSheetOpen = false },
            viewModel = bottomNavViewModel,
            onAddGroup = { name, iconType ->
                if (token != null) {
                    groupViewModel.addGroup(name, iconType, token)
                }
                isSheetOpen = false // 바텀시트 닫기
            }
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun PreviewGroupScreen() {
//    val navController = rememberNavController()
//    val bottomNavViewModel = BottomNavViewModel() // ViewModel 인스턴스 생성
//
//    MaterialTheme {
//        GroupScreen(navController, bottomNavViewModel, 1)
//    }
//}

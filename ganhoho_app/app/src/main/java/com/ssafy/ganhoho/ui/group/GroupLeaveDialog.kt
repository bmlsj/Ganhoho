package com.ssafy.ganhoho.ui.group

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.response.group.GroupViewModelFactory
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.GroupViewModel

@Composable
fun GroupLeaveDialog(
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    navController: NavController,
    repository: GroupRepository,
    group: GroupDto
) {

    val authViewModel: AuthViewModel = viewModel()
    val groupViewModel: GroupViewModel = viewModel(
        factory = GroupViewModelFactory(repository)
    )

    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        } else {
            Log.d("token", token)
        }
    }

    if (isVisible) {
        Dialog( onDismissRequest = { onDismiss()}) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.icon_error),
                        contentDescription = "경고 아이콘",
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "그룹을 탈퇴하시겠습니까?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            group.groupId?.let {
                                if (token != null) {
                                    groupViewModel.leaveGroup(
                                        it,
                                        token,
                                        onSuccess = {
                                            Log.d("GroupLeaveDialog", "✅ 그룹 탈퇴 성공! 홈 화면으로 이동")
                                            navController.navigate("group") // 🔹 탈퇴 성공 시 그룹 목록으로 이동
                                            onConfirm() // 다이얼로그 닫기
                                        },
                                        onFailure = { errorMessage ->
                                            Log.e("GroupLeaveDialog", "❌ 그룹 탈퇴 실패: $errorMessage")
                                            onDismiss() // 실패 시 다이얼로그 닫기
                                        }
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF79C7E3)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(
                            text = "확인",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
        }
    }
}






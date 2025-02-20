package com.ssafy.ganhoho.ui.mypage

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.ssafy.ganhoho.data.model.dto.member.UpdateHospitalWardRequest
import com.ssafy.ganhoho.data.model.response.auth.SearchResultItem
import com.ssafy.ganhoho.data.model.response.member.MyPageResponse
import com.ssafy.ganhoho.ui.nav_host.Route
import com.ssafy.ganhoho.ui.theme.FieldGray
import com.ssafy.ganhoho.ui.theme.PrimaryBlue
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.MemberViewModel

private const val TAG = "UpdateMemberInfo"
@Composable
fun UpdateMemberInfo(navController: NavController) {
    val context = LocalContext.current

    // 1. NavController 상태 안정화
    val backStackEntry by remember {
        derivedStateOf { navController.currentBackStackEntry }
    }
    val savedStateHandle = backStackEntry?.savedStateHandle

    // 2. ViewModel 안전한 초기화
    val memberViewModel: MemberViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // 상태 변수
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var hospital by remember { mutableStateOf<SearchResultItem?>(null) }
    var ward by remember { mutableStateOf("") }
    var hospitalName by remember { mutableStateOf("") }

    // 토큰 처리
    val token = authViewModel.accessToken.collectAsState().value

    // 3. LaunchedEffect 최적화
    LaunchedEffect(Unit) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        } else {
            memberViewModel.getMyPageInfo(token)
        }
    }

    LaunchedEffect(token) {
        if (token != null) {
            memberViewModel.getMyPageInfo(token)
        }
    }

    LaunchedEffect(savedStateHandle) {
        Log.d(TAG, "UpdateMemberInfo: savedStateHandle :${savedStateHandle?.get<SearchResultItem>("selectedHospital")}")
        savedStateHandle?.get<SearchResultItem>("selectedHospital")?.let {
            hospital = it
            hospitalName = it.name
            Log.d(TAG, "UpdateMemberInfo: hospital check in savedStateHandle :${hospital}")
        }
    }

    // 회원 정보 관찰
    val memberInfoState = memberViewModel.mypageInfo.collectAsState().value
    val memberInfo = memberInfoState?.getOrNull() ?: MyPageResponse(-1, "", "", "", "")

    LaunchedEffect(memberInfo) {
        Log.d(TAG, "UpdateMemberInfo: memberInfo changed ${memberInfo}")
        id = memberInfo.loginId
        name = memberInfo.name
        if(savedStateHandle?.get<SearchResultItem>("selectedHospital") == null) hospitalName = memberInfo.hospital ?: ""
        ward = memberInfo.ward ?: ""
    }

    // UI 구성
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffE9F6FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .shadow(6.dp, shape = RoundedCornerShape(40.dp))
                .clip(RoundedCornerShape(40.dp))
                .background(Color(0xffA5D8F3))
                .padding(20.dp)
                .fillMaxWidth(0.85f),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "내 정보 수정",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ID 입력 필드
            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                leadingIcon = {
                    Row {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Icon",
                            tint = Color.LightGray
                        )
                    }
                },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xffE5E5E5),
                    focusedContainerColor = Color(0xffE5E5E5),
                    unfocusedContainerColor = Color(0xffE5E5E5),
                    disabledContainerColor = Color(0xffE5E5E5)
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 이름 입력 필드
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                leadingIcon = {
                    Row {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Name Icon",
                            tint = FieldGray
                        )
                    }
                },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xffE5E5E5),
                    focusedContainerColor = Color(0xffE5E5E5),
                    unfocusedContainerColor = Color(0xffE5E5E5),
                    disabledContainerColor = Color(0xffE5E5E5)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 병원 선택 섹션
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = hospitalName,
                    onValueChange = { hospitalName = it },
                    leadingIcon = {
                        Row {
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.home_health),
                                contentDescription = "Hospital Icon",
                                tint = FieldGray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = FieldGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        savedStateHandle?.set("id", id)
                        savedStateHandle?.set("name", name)
                        savedStateHandle?.set("ward", ward)
                        navController.navigate(Route.HospitalInfo.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(text = "찾기")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 병동 입력 필드
            OutlinedTextField(
                value = ward,
                onValueChange = { ward = it },
                leadingIcon = {
                    Row {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.hospital_room),
                            contentDescription = "Ward Icon",
                            tint = FieldGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = FieldGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 저장 버튼
            Button(
                onClick = {
                    val request = UpdateHospitalWardRequest(
                        hospital?.name,
                        ward,
                        hospital?.y?.toDoubleOrNull(),
                        hospital?.x?.toDoubleOrNull()
                    )
                    token?.let {
                        Log.d(TAG, "UpdateMemberInfo: request ${request}")
                        memberViewModel.updateHospitalAndWardInfo(it, request)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(text = "저장", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUpdateMemberInfo() {
    val navController = rememberNavController()
    UpdateMemberInfo(navController)
}

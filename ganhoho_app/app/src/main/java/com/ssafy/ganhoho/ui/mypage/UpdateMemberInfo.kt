package com.ssafy.ganhoho.ui.mypage

import android.util.Log
import androidx.compose.foundation.background
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
import com.ssafy.ganhoho.ui.theme.FieldGray
import com.ssafy.ganhoho.ui.theme.PrimaryBlue
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.MemberViewModel

@Composable
fun UpdateMemberInfo(navController: NavController) {

    // 변수
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var hospital by remember { mutableStateOf("") }
    var ward by remember { mutableStateOf("") }

    val memberViewModel: MemberViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current


    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        } else {
            // 마이페이지 정보 불러오기
            memberViewModel.getMyPageInfo(token)
        }
    }

    val memberInfoState = memberViewModel.mypageInfo.collectAsState().value
    val memberInfo = memberInfoState?.getOrNull() ?: MyPageResponse(-1, "", "", "", "")

    // 회원 정보가 업데이트되면 UI에 반영
    LaunchedEffect(memberInfo) {
        id = memberInfo.loginId
        name = memberInfo.name
        hospital = memberInfo.hospital ?: ""
        ward = memberInfo.ward ?: ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffE9F6FA)), // 배경 색상
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .shadow(6.dp, shape = RoundedCornerShape(40.dp)) // ✅ 그림자 조정
                .clip(RoundedCornerShape(40.dp))
                .background(Color(0xffA5D8F3))
                .padding(20.dp)
                .fillMaxWidth(0.85f),
        ) {

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "내 정보 수정",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ID 입력
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
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // 포커스 있을 때 테두리 제거
                    unfocusedIndicatorColor = Color.Transparent, // 포커스 없을 때 테두리 제거
                    disabledIndicatorColor = Color.Transparent, // 비활성화 시 테두리 제거
                    cursorColor = Color(0xffE5E5E5),
                    focusedContainerColor = Color(0xffE5E5E5), // 내부 배경 투명
                    unfocusedContainerColor = Color(0xffE5E5E5), // 내부 배경 투명
                    disabledContainerColor = Color(0xffE5E5E5) // 비활성화 상태도 투명
                ),
            )

//            Spacer(modifier = Modifier.height(16.dp))

            // PW 입력
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = {
//                    Text(
//                        "PW", color = Color(0xFFC0C0C0)
//                    )
//                },
//                leadingIcon = {
//                    Row {
//                        Spacer(modifier = Modifier.width(12.dp))
//                        Icon(
//                            imageVector = Icons.Default.Lock,
//                            contentDescription = "Lock Icon",
//                            tint = FieldGray,
//                        )
//                    }
//                },
//                trailingIcon = {
//                    Row {
//                        Icon(painter = if (passwordVisible) painterResource(id = R.drawable.visibility) else painterResource(
//                            id = R.drawable.visibilty_off
//                        ),
//                            contentDescription = "Toggle Password Visibility",
//                            modifier = Modifier
//                                .clickable {
//                                    passwordVisible = !passwordVisible
//                                }
//                                .size(22.dp),
//                            tint = Color(0xFFC0C0C0))
//                        Spacer(modifier = Modifier.width(12.dp))
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(30.dp),
//                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = Color.Transparent, // 포커스 있을 때 테두리 제거
//                    unfocusedIndicatorColor = Color.Transparent, // 포커스 없을 때 테두리 제거
//                    disabledIndicatorColor = Color.Transparent, // 비활성화 시 테두리 제거
//                    cursorColor = FieldGray,
//                    focusedContainerColor = Color.White, // 내부 배경 투명
//                    unfocusedContainerColor = Color.White, // 내부 배경 투명
//                    disabledContainerColor = Color.White // 비활성화 상태도 투명
//                )
//            )

            Spacer(modifier = Modifier.height(16.dp))


            // Name 입력
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // 포커스 있을 때 테두리 제거
                    unfocusedIndicatorColor = Color.Transparent, // 포커스 없을 때 테두리 제거
                    disabledIndicatorColor = Color.Transparent, // 비활성화 시 테두리 제거
                    cursorColor = Color(0xffE5E5E5),
                    focusedContainerColor = Color(0xffE5E5E5), // 내부 배경 투명
                    unfocusedContainerColor = Color(0xffE5E5E5), // 내부 배경 투명
                    disabledContainerColor = Color(0xffE5E5E5) // 비활성화 상태도 투명
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hospital 선택
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                OutlinedTextField(
                    value = hospital,
                    onValueChange = { hospital = it },
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
                        focusedIndicatorColor = Color.Transparent, // 포커스 있을 때 테두리 제거
                        unfocusedIndicatorColor = Color.Transparent, // 포커스 없을 때 테두리 제거
                        disabledIndicatorColor = Color.Transparent, // 비활성화 시 테두리 제거
                        cursorColor = FieldGray,
                        focusedContainerColor = Color.White, // 내부 배경 투명
                        unfocusedContainerColor = Color.White, // 내부 배경 투명
                        disabledContainerColor = Color.White // 비활성화 상태도 투명
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        // 병원 찾기 페이지로 이동W
//                        navController.navigate("hospitalInfo")
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Text(text = "찾기")
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Ward 선택
            OutlinedTextField(
                value = ward,
                onValueChange = { ward = it },
                leadingIcon = {
                    Row {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.hospital_room),
                            contentDescription = "Name Icon",
                            tint = FieldGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // 포커스 있을 때 테두리 제거
                    unfocusedIndicatorColor = Color.Transparent, // 포커스 없을 때 테두리 제거
                    disabledIndicatorColor = Color.Transparent, // 비활성화 시 테두리 제거
                    cursorColor = FieldGray,
                    focusedContainerColor = Color.White, // 내부 배경 투명
                    unfocusedContainerColor = Color.White, // 내부 배경 투명
                    disabledContainerColor = Color.White // 비활성화 상태도 투명
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 저장 버튼
            Button(
                onClick = {
                    // 회원정보 수정 저장
                    Log.d("mypage", "$hospital ${ward}")
                    val request = UpdateHospitalWardRequest(hospital, ward)
                    if (token != null) {
                        memberViewModel.updateHospitalAndWardInfo(token, request)
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

            Spacer(modifier = Modifier.height(16.dp))
        }

    }

}

@Preview(showBackground = true)
@Composable
fun Preview() {
    val navController = rememberNavController()
    UpdateMemberInfo(navController)
}
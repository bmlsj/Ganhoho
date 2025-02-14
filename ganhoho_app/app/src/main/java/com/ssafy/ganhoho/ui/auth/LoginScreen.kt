package com.ssafy.ganhoho.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.data.model.dto.member.LoginRequest
import com.ssafy.ganhoho.ui.theme.BackgroundBlue40
import com.ssafy.ganhoho.ui.theme.FieldGray
import com.ssafy.ganhoho.ui.theme.FieldLightGray
import com.ssafy.ganhoho.ui.theme.PrimaryBlue
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import kotlin.math.log

@Composable
fun LoginScreen(navController: NavController) {

    // 입력 필드 상태
    val id = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    // 로그인 결과 상태 감지
    val loginResult = authViewModel.loginResult.collectAsState().value

    LaunchedEffect(loginResult) {
        loginResult?.onSuccess {
            // ✅ 로그인 성공 시 메인 화면으로 이동
            Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
        }?.onFailure { error ->
            Toast.makeText(context, "로그인 실패: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // fcm 토큰 로그인 요청에 넣기
    val fcmToken = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken.value = task.result
            } else {
                fcmToken.value = null
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue40), // 배경색 설정
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // 로고 영역
        Text(
            text = "간호호",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(100.dp))

        // 로그인 영역
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .background(
                    Color.White,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(30.dp))

                // Login 텍스트
                Text(
                    text = "로그인",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ID 입력
                OutlinedTextField(
                    value = id.value,
                    onValueChange = { id.value = it },
                    label = { Text("ID", color = FieldGray) },
                    leadingIcon = {
                        Row {
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "User Icon",
                                tint = FieldGray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color(0xFFEFEFEF),
                        cursorColor = Color.Blue,
                        focusedContainerColor = Color.Transparent, // 내부 배경 투명
                        unfocusedContainerColor = Color.Transparent, // 내부 배경 투명
                        disabledContainerColor = Color.Transparent // 비활성화 상태도 투명
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // PW 입력
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = {
                        Text(
                            "PW",
                            color = Color(0xFFC0C0C0)
                        )
                    },
                    leadingIcon = {
                        Row {
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Icon",
                                tint = Color(0xFFC0C0C0),
                            )
                        }
                    },
                    trailingIcon = {
                        Row {
                            Icon(
                                painter = if (passwordVisible.value) painterResource(id = R.drawable.visibility) else painterResource(
                                    id = R.drawable.visibilty_off
                                ),
                                contentDescription = "Toggle Password Visibility",
                                modifier = Modifier
                                    .clickable {
                                        passwordVisible.value = !passwordVisible.value
                                    }
                                    .size(22.dp),
                                tint = Color(0xFFC0C0C0)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = FieldLightGray,
                        cursorColor = Color.Blue,
                        focusedContainerColor = Color.Transparent, // 내부 배경 투명
                        unfocusedContainerColor = Color.Transparent, // 내부 배경 투명
                        disabledContainerColor = Color.Transparent // 비활성화 상태도 투명
                    )
                )

                Spacer(modifier = Modifier.height(44.dp))

                // Sign In 버튼
                Button(
                    onClick = {

                        // ✅ 로그인 완료 후 MainScreen으로 이동
                        val fcmTokenCheck = fcmToken.value ?: ""  // 토큰이 빈 값일 때
                        val loginResult = LoginRequest(id.value, password.value, fcmTokenCheck)
                        Log.d("fcmToken", "LoginScreen: $loginResult")
                        authViewModel.login(loginResult, context)

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "로그인",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 회원가입 텍스트
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "계정이 없으신가요?", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "회원가입",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { // 회원 가입 화면으로 이동
                            // Log.d("join", "join button")
                            navController.navigate("join")
                        }
                    )
                }
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val navController = rememberNavController()
    LoginScreen(navController)
}
package com.ssafy.ganhoho.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.wearable.Wearable
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.data.model.dto.auth.LoginRequest
import com.ssafy.ganhoho.ui.AuthActivity
import com.ssafy.ganhoho.ui.nav_host.Route
import com.ssafy.ganhoho.ui.theme.BackgroundBlue40
import com.ssafy.ganhoho.ui.theme.FieldGray
import com.ssafy.ganhoho.ui.theme.FieldLightGray
import com.ssafy.ganhoho.ui.theme.PrimaryBlue
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import kotlin.math.sin

@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(navController: NavController, deepLinkUri: Uri?) {

    // 입력 필드 상태
    val id = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current as AuthActivity
    val focusManager = LocalFocusManager.current

    // 로그인 결과 상태 감지
     val loginResult = authViewModel.loginResult.collectAsState().value
    val loginState = authViewModel.loginState.collectAsState()

    LaunchedEffect(loginResult) {
        loginResult?.let {
            if (it.isSuccess) {
                Log.d("LoginScreen", "로그인 성공 → 메인 이동")
//                navController.navigate(Route.Main.route) {
//                    popUpTo(Route.Login.route) { inclusive = true }
//                }
                context.navigateToMain(deepLinkUri)
            } else {
                Log.e("LoginScreen", "로그인 실패: error")
            }
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
            .background(BackgroundBlue40) // 배경색 설정
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(BackgroundBlue40)

        ){
            // 로고 영역
            Column(
                modifier = Modifier
                    .padding(start = 32.dp)
            ) {
                Text(
                    text = "간호사를 위한",
                    fontSize = 26.sp ,
                    fontWeight = FontWeight.Medium,
                    //fontFamily = normalFont,
                    color = Color(0xFF4D5860)
                )
                Text(
                    text = "올인원 업무•일정 관리 앱",
                    fontSize = 26.sp ,
                    fontWeight = FontWeight.Medium,
                    //fontFamily = normalFont,
                    color = Color(0xFF4D5860),
                )

                Spacer(modifier = Modifier.height(22.dp))

                Image(
                    painter = painterResource(R.drawable.logo_image_large),
                    contentDescription = "간호호 로고",
                    modifier = Modifier
                        .size(160.dp, 59.dp)
                        .align(Alignment.Start)
                )

            }

            Image(
                painter = painterResource(R.drawable.login_character),
                contentDescription = "로그인 캐릭터 이미지",
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.TopEnd)
                    //.padding(end=0.dp, top=100.dp)
                    .offset(y = 100.dp)
                    .offset(x = 38.dp)
            )

        }

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
                    fontSize = 30.sp,
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
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
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
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                Spacer(modifier = Modifier.height(44.dp))

                // Sign In 버튼
                Button(
                    onClick = {

                        // ✅ 로그인 완료 후 MainScreen으로 이동
                        val fcmTokenCheck = fcmToken.value ?: ""  // 토큰이 빈 값
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

                // 로그인 상태 변화 감지해서 토스트 표시
                LaunchedEffect(loginState.value){
                    when(loginState.value){
                        "success" -> {
                            Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                            authViewModel.resetLoginState() // 상태 초기화 - 여러번 틀렸을 시에도 토스트 띄우기 위함
                        }
                        "failure" ->{
                            Toast.makeText(context, "로그인 실패!", Toast.LENGTH_LONG).show()
                            authViewModel.resetLoginState()
                        }
                    }
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
   // LoginScreen(navController)
}

fun findConnectedNodes(context: Context, token: String){
    Wearable.getNodeClient(context).connectedNodes
        .addOnSuccessListener { nodes ->
            if (nodes.isNotEmpty()) {
                val nodeId = nodes[0].id // 첫 번째 노드의 ID 가져오기
                sendTokenToNode(context, nodeId, token) // 메시지 전송 함수 호출
            }
        }
        .addOnFailureListener { exception ->
            // 오류 처리
        }


}

fun sendTokenToNode(context: Context, nodeId: String, token: String) {
    val messagePath = "/jwt_token"
    val messageData = token

    Wearable.getMessageClient(context).sendMessage(nodeId, messagePath, messageData.toByteArray())
        .addOnSuccessListener {
            // 성공적으로 메시지 전송
        }
        .addOnFailureListener {
            // 메시지 전송 실패 처리
        }
}
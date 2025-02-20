package com.ssafy.ganhoho.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.data.model.dto.auth.SignUpRequest
import com.ssafy.ganhoho.data.model.response.auth.SearchResultItem
import com.ssafy.ganhoho.ui.nav_host.Route
import com.ssafy.ganhoho.ui.theme.FieldGray
import com.ssafy.ganhoho.ui.theme.FieldLightGray
import com.ssafy.ganhoho.ui.theme.PrimaryBlue
import com.ssafy.ganhoho.viewmodel.AuthViewModel

@Composable
fun JoinScreen(navController: NavController) {

    // 뷰 모델
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    // ✅ savedStateHandle을 사용하여 데이터 유지
    val backStackEntry = navController.currentBackStackEntry
    val savedStateHandle = backStackEntry?.savedStateHandle

    // 변수
    var id by remember { mutableStateOf(savedStateHandle?.get("id") ?: "") }
    var idCheck by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf(savedStateHandle?.get("password") ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(savedStateHandle?.get("name") ?: "") }
    var hospital = remember { mutableStateOf<SearchResultItem?>(savedStateHandle?.get("selectedHospital")) }
    var ward by remember { mutableStateOf("") }

    // 아이디 중복 확인 결과
    val idCheckResult by authViewModel.isIdUsed.collectAsState()
    // 회원가입 요청 결과 확인
    val signUpResult by authViewModel.signUpResult.collectAsState()

    // 회원가입 버튼 활성화 조건
    val isSignUpEnabled =
        id.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && idCheckResult != null && !idCheck

    // 아이디 중복 체크 결과 감지
    LaunchedEffect(idCheckResult) {
        idCheckResult?.onSuccess { isUsed ->
            idCheck = isUsed
            val message = if (!isUsed) "사용 가능한 ID입니다." else "이미 사용 중인 ID입니다."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }?.onFailure { error ->
            // Toast.makeText(context, "아이디 중복 확인 실패: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ 병원 선택값이 변경되면 업데이트
    LaunchedEffect(savedStateHandle?.get<SearchResultItem>("selectedHospital")) {
        savedStateHandle?.get<SearchResultItem>("selectedHospital")?.let {
            hospital.value = it
        }
    }

    // 회원가입 요청 결과 감지 → 성공하면 main 화면으로 이동
    LaunchedEffect(signUpResult) {
        signUpResult?.onSuccess {
            Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
            navController.navigate(Route.Login.route) // ✅ 회원가입 성공하면 login 이동
        }?.onFailure { error ->
            // Toast.makeText(context, "회원가입 실패: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 병원 이름 받아오기
    LaunchedEffect(navController.currentBackStackEntry) {
        val hospitals = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<SearchResultItem>("selectedHospital")

        if (hospitals != null) {
            hospital.value = hospitals
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9F6FA)), // 배경색
        horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // 로고 영역
        Text(
            text = "간호호",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Join 화면 영역
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 10.dp, shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .background(
                    Color.White, shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Join 텍스트
                Text(
                    text = "회원가입",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ID 입력
                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it; savedStateHandle?.set("id", it) },
                    label = { Text("ID", color = FieldGray) },
                    leadingIcon = {
                        Row {
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Icon",
                                tint = FieldGray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = Color(0xFFEFEFEF),
                        cursorColor = Color.Black,
                        focusedContainerColor = Color.Transparent, // 내부 배경 투명
                        unfocusedContainerColor = Color.Transparent, // 내부 배경 투명
                        disabledContainerColor = Color.Transparent // 비활성화 상태도 투명
                    ),
                    trailingIcon = {
                        Row {
                            Box(
                                modifier = Modifier
                                    .alpha(0.2f)
                                    .border(
                                        width = 1.5.dp, color = when {
                                            idCheckResult == null -> Color.Gray  // 버튼을 누르지 않은 상태
                                            idCheck -> Color.Red  // 중복된 아이디
                                            else -> PrimaryBlue  // 사용 가능 아이디
                                        }, shape = RoundedCornerShape(size = 5.dp)
                                    )
                                    .width(25.dp)
                                    .height(25.dp)
                                    .background(
                                        color = when {
                                            idCheckResult == null -> Color.LightGray  // 버튼을 누르지 않은 상태
                                            idCheck -> Color.Magenta.copy(0.2f)  // 중복된 아이디(좀더 연하게)
                                            else -> PrimaryBlue.copy(0.2f)  // 사용 가능 아이디
                                        }, shape = RoundedCornerShape(size = 5.dp)
                                    )
                            ) {
                                Icon(imageVector = Icons.Default.Check,
                                    contentDescription = "Check ID",
                                    tint = when {
                                        idCheckResult == null -> Color.DarkGray  // 버튼을 누르지 않은 상태
                                        idCheck -> Color.Red  // 중복된 아이디
                                        else -> Color.Blue  // 사용 가능 아이디
                                    },
                                    modifier = Modifier.clickable {
                                        if (id.isNotEmpty()) {
                                            authViewModel.checkIsIdUsed(id)
                                        }
                                    })

                            }

                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PW 입력
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; savedStateHandle?.set("password", it) },
                    label = {
                        Text(
                            "PW", color = Color(0xFFC0C0C0)
                        )
                    },
                    leadingIcon = {
                        Row {
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Icon",
                                tint = FieldGray,
                            )
                        }
                    },
                    trailingIcon = {
                        Row {
                            Icon(painter = if (passwordVisible) painterResource(id = R.drawable.visibility) else painterResource(
                                id = R.drawable.visibilty_off
                            ),
                                contentDescription = "Toggle Password Visibility",
                                modifier = Modifier
                                    .clickable {
                                        passwordVisible = !passwordVisible
                                    }
                                    .size(22.dp),
                                tint = Color(0xFFC0C0C0))
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = FieldLightGray,
                        cursorColor = Color.Black,
                        focusedContainerColor = Color.Transparent, // 내부 배경 투명
                        unfocusedContainerColor = Color.Transparent, // 내부 배경 투명
                        disabledContainerColor = Color.Transparent // 비활성화 상태도 투명
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                // Name 입력
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; savedStateHandle?.set("name", it) },
                    label = { Text("이름", color = FieldGray) },
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
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = FieldLightGray,
                        cursorColor = Color.Black,
                        focusedContainerColor = Color.Transparent, // 내부 배경 투명
                        unfocusedContainerColor = Color.Transparent, // 내부 배경 투명
                        disabledContainerColor = Color.Transparent // 비활성화 상태도 투명
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Hospital 선택
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = hospital.value?.name ?: "",
                        onValueChange = { hospital.value?.name = it },
                        label = { Text("병원명", color = FieldGray) },
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
                        modifier = Modifier
                            .weight(1f)
                            .height(IntrinsicSize.Min),
                        shape = RoundedCornerShape(30.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.LightGray,
                            unfocusedIndicatorColor = FieldLightGray,
                            cursorColor = Color.Black,
                            focusedContainerColor = Color.Transparent, // 내부 배경 투명
                            unfocusedContainerColor = Color.Transparent, // 내부 배경 투명
                            disabledContainerColor = Color.Transparent // 비활성화 상태도 투명
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            // ✅ 현재 입력된 데이터들을 저장 후 병원 찾기 화면으로 이동
                            savedStateHandle?.set("id", id)
                            savedStateHandle?.set("password", password)
                            savedStateHandle?.set("name", name)

                            // 병원 찾기 페이지로 이동
                            navController.navigate(Route.HospitalInfo.route)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                    ) {
                        Text(text = "찾기")
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Ward 선택
                OutlinedTextField(
                    value = ward,
                    onValueChange = { ward = it },
                    label = { Text("병동", color = FieldGray) },
                    placeholder = { Text(text = "병동을 입력해주세요. ex) 일반, 긴급, 특수 등",
                        color = Color.LightGray)},
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
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = Color(0xFFEFEFEF),
                        cursorColor = Color.Black,
                        focusedContainerColor = Color.Transparent, // 내부 배경 투명
                        unfocusedContainerColor = Color.Transparent, // 내부 배경 투명
                        disabledContainerColor = Color.Transparent // 비활성화 상태도 투명
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Up 버튼
                Button(
                    onClick = {
                        val signUpRequest = SignUpRequest(id, password, name, hospital.value?.name, ward)
                        authViewModel.signUp(signUpRequest) // ✅ 회원가입 요청 보내기
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable {
                            val signUpRequest =
                                SignUpRequest(loginId= id, password = password,name= name, hospital = hospital.value?.name, ward = ward, hospitalLat = hospital.value?.y?.toDoubleOrNull(), hospitalLng = hospital.value?.x?.toDoubleOrNull())

                            authViewModel.signUp(signUpRequest = signUpRequest)
                        },
                    colors = ButtonDefaults.buttonColors(containerColor = if (idCheck) Color.Gray else PrimaryBlue),
                    shape = RoundedCornerShape(20.dp),
                    enabled = isSignUpEnabled // 활성화
                ) {
                    Text(text = "회원가입", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 로그인 링크
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "이미 계정이 있으신가요?", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "로그인",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { /* 로그인 화면으로 이동 */
                            navController.navigate(Route.Login.route)
                        })
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun JoinScreenPreview() {
    val navController = rememberNavController()
    JoinScreen(navController)
}

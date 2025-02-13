package com.ssafy.ganhoho.ui.mypage

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
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.data.model.response.member.MyPageResponse
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.MemberViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MyPageScreen() {

    val memberViewModel: MemberViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current


    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        }
    }

    LaunchedEffect(token) {
        if (token != null) {
            // 마이페이지 정보 불러오기
            memberViewModel.getMyPageInfo(token)
        }
    }

    val memberInfoState = memberViewModel.mypageInfo.collectAsState().value
    val memberInfo = memberInfoState?.getOrNull() ?: MyPageResponse(-1, "", "", "", "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        // 로고
        Text(
            text = "GANHOHO",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF79C7E3),
            modifier = Modifier.padding(vertical = 20.dp)
        )

        // 프로필 카드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                .background(Color(0xFF79C7E3), shape = RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column {

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = memberInfo.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "@${memberInfo.loginId}",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "병원",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    memberInfo.hospital?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "병동",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    memberInfo.ward?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))
        // 마이페이지 메뉴
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            MenuItem(
                icon = R.drawable.modify, // 회원정보 수정 아이콘
                text = "회원정보 수정",
                {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuItem(
                icon = R.drawable.logout, // 로그아웃 아이콘
                text = "로그아웃",
                onClick = { // 로그아웃 기능
                    authViewModel.logout(context)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuItem(
                icon = R.drawable.unsubscribe, // 회원탈퇴 아이콘
                text = "회원탈퇴",
                onClick = {
                    // 토큰 날리기
                }
            )

        }

        Divider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            MenuItem(
                icon = R.drawable.center, // 고객센터 아이콘
                text = "고객센터",
                {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuItem(
                icon = R.drawable.appinfo, // 앱 정보 아이콘
                text = "앱 정보",
                {}
            )
        }
    }

}

@Composable
fun MenuItem(icon: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyPageScreenPrevier() {
    MyPageScreen()
}
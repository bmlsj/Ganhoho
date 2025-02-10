package com.ssafy.ganhoho.ui.friend

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.BuildConfig
import com.ssafy.ganhoho.ui.friend.common.FriendAdd
import com.ssafy.ganhoho.ui.friend.common.FriendList
import com.ssafy.ganhoho.ui.friend.common.FriendRequestList
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.FriendViewModel
import com.ssafy.ganhoho.viewmodel.MemberViewModel

@Composable
fun FriendScreen(navController: NavController) {

    val scrollState = rememberScrollState()
    val currentScreen = remember { mutableStateOf("list") }
    val searchText = remember { mutableStateOf("") }

    val friendViewModel: FriendViewModel = viewModel()
    val memberViewModel: MemberViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // 친구 목록 조회
    val friendListState = friendViewModel.friendList.collectAsState().value
    val friendList = friendListState?.getOrNull() ?: emptyList()

    // 친구 요청 조회
    val friendInviteState = friendViewModel.friendInviteList.collectAsState().value
    val friendInvite = friendInviteState?.getOrNull() ?: emptyList()

    // 검색된 친구 목록 필터링
    val filteredFriendList = remember(friendList, searchText.value) {
        friendList.filter { friend ->
            searchText.value.isEmpty() ||
                    friend.friendLoginId.contains(searchText.value, ignoreCase = true) ||
                    friend.name.contains(searchText.value, ignoreCase = true)
        }
    }

    // 검색된 회원 전체 목록
    val memberListState = memberViewModel.memberList.collectAsState().value
    val memberList = memberListState.getOrNull() ?: emptyList()

    // 토큰 로드하기
    val token = authViewModel.accessToken.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            authViewModel.loadTokens(context)
        } else {
            Log.d("token", token)
            // 화면 처음 로드 시 친구 목록 불러오기
            friendViewModel.getFriendList(token)  // 친구 목록 조회
            friendViewModel.getFriendInvite(token)  // 친구 요청 목록 조회
        }
    }

    LaunchedEffect(searchText.value, token) {
        println("검색어: ${searchText.value}")
        println("memberList: $memberList")
        if (!token.isNullOrEmpty()) {
            // 검색한 텍스트로 회원 전체 목록 검색
            memberViewModel.searchFriend(token, searchText.value)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)  // 세로 스크롤
            .background(Color(0xffe4f8ff))
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        // 상단 "친구 목록", "친구 요청", "친구 추가" 버튼
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            MenuItem("list", "친구목록", currentScreen)
            MenuItem("request", "친구요청", currentScreen)
            MenuItem("search", "친구검색", currentScreen)

        }

        Spacer(modifier = Modifier.height(25.dp))

        // 친구 검색 화면
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xfff4fbff))
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            if (currentScreen.value != "request") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // 친구 검색
                    OutlinedTextField(
                        value = searchText.value,
                        onValueChange = { searchText.value = it },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "searchFriend",
                                tint = Color.Gray
                            )
                        },
                        placeholder = {
                            Text(
                                text = "아이디로 친구를 검색해보세요.",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(30.dp), // 둥근 모서리
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xfffdfdfd),
                            focusedContainerColor = Color(0xfffdfdfd),
                            unfocusedIndicatorColor = Color.LightGray,
                            focusedIndicatorColor = Color.LightGray,
                            focusedTextColor = Color.Black
                        )
                    )
                }
            }

            // menu 별로 데이터 보여주기
            when (currentScreen.value) {
                "list" -> {  // 전체 친구 목록
                    if (filteredFriendList.isNotEmpty()) {
                        filteredFriendList.forEach { friend ->
                            FriendList(
                                friend = friend,
                                onFavoriteClick = { friendMemberId, isFavorite ->
                                    if (token != null) {
                                        friendViewModel.updateFriendFavorite(
                                            token,
                                            friendMemberId,
                                            isFavorite
                                        )
                                    }
                                }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(50.dp))
                        Text(
                            text = "등록된 친구가 없습니다.",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }

                "request" -> {  // 친구 요청 리스트
                    if (friendInvite.isNotEmpty()) {
                        friendInvite.forEach { friend ->
                            FriendRequestList(friend = friend)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(50.dp))
                        Text(text = "친구 요청이 없습니다.", fontSize = 16.sp, color = Color.Gray)
                    }
                }

                "search" -> {  // 친구 추가를 위해 전체 회원 목록 검색
                    if (searchText.value.isNotEmpty() && memberList.isNotEmpty()) {
                        memberList.forEach { member ->
                            FriendAdd(member = member, friendList = friendList)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(50.dp))
                        Text(text = "추가할 친구가 없습니다.", fontSize = 16.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// 메뉴 아이템
@Composable
fun MenuItem(screen: String, title: String, currentScreen: MutableState<String>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { currentScreen.value = screen }
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (currentScreen.value == screen) Color.Black else Color.Gray
        )

        Spacer(modifier = Modifier.height(3.dp))
        if (currentScreen.value == screen) {
            Box(
                modifier = Modifier
                    .width(75.dp)
                    .height(3.dp)
                    .background(Color(0xff35A6CC))
            )
        }
    }
}


@Preview
@Composable
fun ScreenPreivew() {

    val navController = rememberNavController()
    FriendScreen(navController)

}
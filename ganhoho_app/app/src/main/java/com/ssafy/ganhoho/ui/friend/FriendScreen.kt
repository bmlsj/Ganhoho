package com.ssafy.ganhoho.ui.friend

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import com.ssafy.ganhoho.data.model.dto.member.MemberDto
import com.ssafy.ganhoho.ui.friend.common.FriendAdd
import com.ssafy.ganhoho.ui.friend.common.FriendList
import com.ssafy.ganhoho.ui.friend.common.FriendRequestList
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.FriendViewModel
import com.ssafy.ganhoho.viewmodel.MemberViewModel

@SuppressLint("UnrememberedMutableState")
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

    // ✅ 검색된 친구 목록을 필터링하고, 즐겨찾기(isFavorite=true)한 친구들을 먼저 정렬
    val filteredFriendList by rememberUpdatedState(
        derivedStateOf {
            friendList
                .filter { friend ->
                    searchText.value.isEmpty() ||
                            friend.friendLoginId.contains(searchText.value, ignoreCase = true) ||
                            friend.name.contains(searchText.value, ignoreCase = true)
                }
                .sortedByDescending { it.isFavorite } // 즐겨찾기 친구가 먼저 나오도록 정렬
        }
    )

    // 검색된 회원 전체 목록
    val memberListState = memberViewModel.memberList.collectAsState().value
    val memberList = memberListState.getOrNull() ?: emptyList()

    // ✅ 친구 검색 리스트 정렬: 추가 가능한 친구를 먼저 정렬
    val filteredMemberList by remember(memberList, friendList, searchText.value) {
        derivedStateOf {
            memberList
                .sortedWith(compareByDescending<MemberDto> { member ->
                    // ✅ 친구 목록에 없는 경우 (추가 가능) → 리스트 앞쪽으로 배치
                    friendList.none { it.friendLoginId == member.loginId }
                }.thenBy { it.name }) // ✅ 추가 가능성이 같으면 이름순 정렬
        }
    }


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

    // ✅ 다이얼로그 상태 추가 (한 번만 표시하기 위해)
    var friendListSuccessDialog by remember { mutableStateOf(false) } // 친구 추가 성공
    var friendListErrorDialog by remember { mutableStateOf(false) }  // 친구 추가 실패
    var friendAcceptDialog by remember { mutableStateOf(false) } // 친구 요청 수락 성공


    // ✅ 친구 추가 요청 결과 감지
    val addFriendResult = friendViewModel.addFriendResult.collectAsState().value

    // ✅ ViewModel에서 addFriendResult 변경 감지 → 다이얼로그 상태 업데이트
    LaunchedEffect(addFriendResult) {
        addFriendResult?.onSuccess { response ->
            if (response.success) {
                friendListSuccessDialog = true
            }
        }?.onFailure {
            friendListErrorDialog = true
        }
        friendViewModel.clearAddFriendResult()
    }

    // ✅ 친구 요청 수락 감지
    val friendAcceptResult = friendViewModel.friendResponse.collectAsState().value
    LaunchedEffect(friendAcceptResult) {
        friendAcceptResult?.let {
            if (it.isSuccess) {
                Log.d("friend", "친구 요청 수락 성공")
                friendAcceptDialog = true // ✅ 다이얼로그 표시
            } else {
                Log.d("friend", "친구 요청 수락 실패")
            }
        }
    }

    // ✅ 친구 목록을 갱신하는 LaunchedEffect 추가
    LaunchedEffect(friendList) {
        if (token != null) {
            friendViewModel.getFriendList(token)
        }
    }

    // ✅ 친구 목록 새로고침 함수 추가
    fun fetchFriendData() {
        if (!token.isNullOrEmpty()) {
            friendViewModel.getFriendList(token)  // 친구 목록 조회
            friendViewModel.getFriendInvite(token)  // 친구 요청 목록 조회
            if (searchText.value.isNotEmpty()) {
                memberViewModel.searchFriend(token, searchText.value)  // 친구 검색
            }
        }
    }

    // menuItem 변경 시 마다 데이터 새로 고침
    LaunchedEffect(currentScreen.value) {
        Log.d("current", "화면 변경 ${currentScreen.value}")
        fetchFriendData()
        searchText.value = ""
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

            MenuItem("list", "친구목록", currentScreen, friendInvite.size, { fetchFriendData() })
            MenuItem("request", "친구요청", currentScreen, friendInvite.size, { fetchFriendData() })
            MenuItem("search", "친구검색", currentScreen, friendInvite.size, { fetchFriendData() })

        }

        Spacer(modifier = Modifier.height(10.dp))

        // 친구 검색 화면
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xfff4fbff))
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            if (currentScreen.value != "request") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(bottom = 80.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // menu 별로 데이터 보여주기
                when (currentScreen.value) {
                    "list" -> {  // 전체 친구 목록
                        if (filteredFriendList.value.isNotEmpty()) {
                            items(filteredFriendList.value) { friend ->
                                FriendList(
                                    friend = friend,
                                    onFavoriteClick = { friendMemberId, isFavorite ->
                                        if (token != null) {
                                            friendViewModel.updateFriendFavorite(
                                                token,
                                                friendMemberId,
                                                isFavorite
                                            )

                                            fetchFriendData()  // 변경 시, 친구 목록 갱신
                                        }
                                    }
                                )
                            }
                        } else {
                            item {
                                Spacer(modifier = Modifier.height(50.dp))
                                Text(
                                    text = "등록된 친구가 없습니다.",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    "request" -> {  // 친구 요청 리스트
                        if (friendInvite.isNotEmpty()) {
                            items(friendInvite) { friend ->
                                FriendRequestList(friend = friend)
                            }
                        } else {
                            item {
                                Spacer(modifier = Modifier.height(50.dp))
                                Text(
                                    text = "친구 요청이 없습니다.",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    "search" -> {  // 친구 추가를 위해 전체 회원 목록 검색
                        if (searchText.value.isNotEmpty() && memberList.isNotEmpty()) {
                            items(filteredMemberList) { member ->
                                FriendAdd(member = member, friendList = friendList,
                                    onFriendAdd = { loginId ->
                                        if (!token.isNullOrEmpty()) {
                                            friendViewModel.addFriendList(token, loginId)
                                        }
                                    }
                                )
                            }

                        } else {
                            item {
                                Spacer(modifier = Modifier.height(50.dp))
                                Text(
                                    text = "추가할 친구가 없습니다.",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }


        }


        // ✅ 친구 추가 성공 다이얼로그
        if (friendListSuccessDialog) {
            AlertDialog(
                onDismissRequest = { friendListSuccessDialog = false },
                confirmButton = {
                    Button(onClick = { friendListSuccessDialog = false }) { Text("확인") }
                },
                text = { Text("친구 신청이 완료되었습니다.") }
            )
        }

        // ✅ 친구 추가 실패 다이얼로그
        if (friendListErrorDialog) {
            AlertDialog(
                onDismissRequest = { friendListErrorDialog = false },
                confirmButton = {
                    Button(onClick = { friendListErrorDialog = false }) { Text("확인") }
                },
                text = { Text("이미 요청된 친구입니다.") }
            )
        }

        // ✅ 친구 요청 수락 성공 다이얼로그
        if (friendAcceptDialog) {
            AlertDialog(
                onDismissRequest = { friendAcceptDialog = false },
                confirmButton = { Button(onClick = { friendAcceptDialog = false }) { Text("확인") } },
                text = { Text("친구 요청을 수락했습니다!") }
            )
        }
    }
}

// 메뉴 아이템
@Composable
fun MenuItem(
    screen: String,
    title: String,
    currentScreen: MutableState<String>,
    friendRequestCount: Int,
    fetchFriendData: () -> Unit
) {
    val isSelected = currentScreen.value == screen

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            currentScreen.value = screen
            fetchFriendData()  // ✅ 메뉴 클릭 시 친구 목록 새로고침
        }
    ) {
        if (title == "친구요청") {
            FriendRequestBadge(friendRequestCount, isSelected)
        } else {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.Black else Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(3.dp))
        if (isSelected) {
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

@Composable
fun FriendRequestBadge(friendRequestCount: Int, isSelected: Boolean) {
    Box { // 외부 Box
        // 친구 요청 아이콘 (예제 아이콘)
        Text(
            "친구요청",
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 2.dp, end = 8.dp),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.Gray
        )

        // 🔴 빨간 알림 배지 (알림 개수가 0보다 클 때만 표시)
        if (friendRequestCount > 0) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.Red, shape = CircleShape)
                    .align(Alignment.TopEnd), // 오른쪽 상단 정렬
                contentAlignment = Alignment.Center
            ) {

            }
        }
    }
}

@Preview
@Composable
fun FriendRequestBadgePreview() {
    FriendRequestBadge(friendRequestCount = 3, false) // 🔥 예제: 친구 요청 3개
}

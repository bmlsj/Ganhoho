package com.ssafy.ganhoho.ui.bottom_navigation//package com.example.calendar.ui.bottomnavigation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.ganhoho.R

@Composable
fun CustomBottomNavigation(selectedItem: MutableState<Int>) {

    // 리소스를 명확히 선언하여 수정
    val items = listOf(
        Pair("근무 일정", R.drawable.nav_work),
        Pair("알약 찾기", R.drawable.nav_pill),
        Pair("홈", R.drawable.nav_home),
        Pair("그룹", R.drawable.nav_group),
        Pair("친구", R.drawable.nav_friend)
    )

    Row(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
                    .clickable { selectedItem.value = index }
            ) {
                Icon(
                    painter = painterResource(id = item.second),
                    contentDescription = item.first,
                    tint = if (selectedItem.value == index) Color.White else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.first,
                    fontSize = 12.sp,
                    color = if (selectedItem.value == index) Color.White else Color.Gray
                )
            }

        }

    }
}


@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun BottomNav() {
    CustomBottomNavigation(mutableStateOf(2))
}

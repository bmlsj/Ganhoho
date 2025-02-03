package com.ssafy.ganhoho.ui.bottom_navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ssafy.ganhoho.ui.friend.FriendScreen
import com.ssafy.ganhoho.ui.group.GroupScreen
import com.ssafy.ganhoho.ui.home.HomeScreen
import com.ssafy.ganhoho.ui.pill.PillScreen
import com.ssafy.ganhoho.ui.work_schedule.WorkScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("work") { WorkScreen(modifier) }
        composable("pill") { PillScreen(modifier) }
        composable("home") { HomeScreen(modifier) }
        composable("group") { GroupScreen(modifier) }
        composable("friend") { FriendScreen(navController) }
    }
}
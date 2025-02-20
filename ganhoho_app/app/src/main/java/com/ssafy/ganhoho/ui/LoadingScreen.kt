package com.ssafy.ganhoho.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.ganhoho.R

@Composable
fun LoadingScreen(isLoading: Boolean) {

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = rememberLottieComposition(
                    spec = LottieCompositionSpec.RawRes(R.raw.loading)
                ).value,
                modifier = Modifier.size(150.dp),
                iterations = LottieConstants.IterateForever,
                speed = 2f
            )
        }
    }
}
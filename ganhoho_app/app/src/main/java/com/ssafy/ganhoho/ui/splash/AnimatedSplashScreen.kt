package com.ssafy.ganhoho.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.ganhoho.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedSplashScreen(navController: NavController) {

    val characterScale1 = remember { Animatable(0f) } // 캐릭터 1 크기 애니메이션
    val characterScale2 = remember { Animatable(0f) } // 캐릭터 2 크기 애니메이션

    LaunchedEffect(Unit) {
        launch {
            characterScale1.animateTo(1f, animationSpec = tween(500))
            delay(500)
            characterScale2.animateTo(1f, animationSpec = tween(500))

        }

        delay(1500) // 스플래시 지속 시간

        // 동작 후 로그인으로 이동
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    val normalFont = FontFamily(Font(R.font.scdream5))
    val lightFont = FontFamily(Font(R.font.scdream3))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9F6FA)),
    ) {
        // 텍스트 스플래시 파트
        Column(
            modifier = Modifier
                .padding(start = 41.dp, end = 40.dp, top = 150.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "간호사를 위한",
                fontSize = 26.sp ,
                fontWeight = FontWeight.Medium,
                fontFamily = normalFont,
                color = Color(0xFF4D5860)
            )
            Text(
                text = "올인원 업무•일정 관리 앱",
                fontSize = 26.sp ,
                fontWeight = FontWeight.Medium,
                fontFamily = normalFont,
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

        Column(
            modifier= Modifier
                .fillMaxWidth()
                .padding(top = 270.dp)

        ) {
            // 캐릭터 이미지
            Image(
                painter = painterResource(R.drawable.character1),
                contentDescription = "간호호 캐릭터",
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.End)
                    .offset(x = 45.dp)
                    .graphicsLayer(
                        scaleX = characterScale1.value,
                        scaleY = characterScale1.value
                    )
            )

            Spacer(modifier = Modifier.height(17.dp))

            // 캐릭터 이미지2
            Image(
                painter = painterResource(R.drawable.character2),
                contentDescription = "간호호 캐릭터",
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.Start)
                    .offset(x = 50.dp)
                    .graphicsLayer(
                        scaleX = characterScale2.value,
                        scaleY = characterScale2.value
                    )
            )

        }

        // 팀 이름
        Column (
            modifier= Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ){
            Text(
                text = "기획폭발단",
                fontFamily = lightFont,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(61.dp))
        }


    }

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF79C7E3)),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = "GANHOHO",
//            fontSize = 32.sp * scale.value, // 확대 애니메이션 적용
//            fontWeight = FontWeight.Bold,
//            color = Color.White
//        )
//    }

}

@Preview(showBackground = true)
@Composable
fun PreviewAnimatedSplashScreen() {
    val navController = rememberNavController()
    AnimatedSplashScreen(navController)
}
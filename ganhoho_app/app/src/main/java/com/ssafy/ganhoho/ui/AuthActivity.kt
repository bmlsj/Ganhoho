package com.ssafy.ganhoho.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kakao.vectormap.KakaoMapSdk
import com.ssafy.ganhoho.BuildConfig.KAKAO_NATIVE_APP_KEY
import com.ssafy.ganhoho.R
import com.ssafy.ganhoho.base.SecureDataStore
import com.ssafy.ganhoho.data.model.response.group.GroupViewModelFactory
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.ui.auth.AuthDataStore
import com.ssafy.ganhoho.ui.nav_host.AuthNavHost
import com.ssafy.ganhoho.viewmodel.AuthViewModel
import com.ssafy.ganhoho.viewmodel.GroupViewModel
import okhttp3.Route

class AuthActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authDataStore = AuthDataStore(applicationContext) //DataStore 생성
        val repository = GroupRepository()

        // 앱 실행 시 딥링크 처리
        val groupViewModel: GroupViewModel = ViewModelProvider(
            this,
            GroupViewModelFactory(repository)
        )[GroupViewModel::class.java]

        val token = SecureDataStore.getAccessToken(applicationContext)
        val deepLinkUri = intent?.data  // 딥링크 데이터

        // 저장된 토큰 불러오기
        authViewModel.loadTokens(this)
        // 카카오 맵
        KakaoMapSdk.init(
            this@AuthActivity,
            KAKAO_NATIVE_APP_KEY
        )

        setContent {
            val navController = rememberNavController()
            AuthNavHost(navController, deepLinkUri)
        }
    }

    // 로그인 완료 후 main으로 이동
    fun navigateToMain(deepLinkUri: Uri?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            deepLinkUri?.let { data = it }  // 딥링크 있으면 전달
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}


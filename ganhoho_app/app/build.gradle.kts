import java.util.Properties

plugins {
    alias(libs.plugins.android.application) // Android 애플리케이션 플러그인
    alias(libs.plugins.jetbrains.kotlin.android) // Kotlin Android 플러그인
    alias(libs.plugins.compose.compiler) // Jetpack Compose 컴파일러 플러그인
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

// 1. 추가
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.ssafy.ganhoho"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ssafy.ganhoho"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // 2. 추가
        buildConfigField("String", "SERVER_URL", "\"${localProperties.getProperty("SERVER_URL", "")}\"")
        buildConfigField("String", "WEBVIEW_PILL_URL", "\"${localProperties.getProperty("WEBVIEW_PILL_URL", "")}\"")
        buildConfigField("String", "WEBVIEW_WORK_URL", "\"${localProperties.getProperty("WEBVIEW_WORK_URL", "")}\"")
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"${localProperties.getProperty("KAKAO_NATIVE_APP_KEY", "")}\"")

        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = localProperties["KAKAO_NATIVE_APP_KEY"] ?: ""

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        // 3. 추가
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.gms.play.services.wearable)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.camera.view)
    implementation(libs.gms.play.services.wearable)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.kizitonwose.calendar:compose:2.6.2")
    implementation("androidx.compose.material:material:1.7.5")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.compose.material:material:1.7.5")     // material2 지원
    implementation ("androidx.navigation:navigation-compose:2.8.5")  // 네비게이션 구현

    implementation("androidx.compose.foundation:foundation:1.7.7") // LazyGrid 지원
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0") // 날짜 지원

    implementation("androidx.compose.foundation:foundation:1.7.7") // LazyGrid 지원
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0") // 날짜 지원


    // retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // Compose ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    // fcm
    implementation(platform("com.google.firebase:firebase-bom:33.8.0")) // 최신 버전 유지
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    // okhttp3
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // datastore
    implementation ("androidx.datastore:datastore-preferences:1.1.2")
    implementation ("androidx.datastore:datastore-core:1.1.2")  // 코어 의존성 (필요 시)

    // 카카오 지도 API
    implementation ("com.kakao.maps.open:android:2.11.9")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation ("com.kakao.sdk:v2-all:2.20.6") // 전체 모듈 설치, 2.11.0 버전부터 지원

    // lottie
    implementation ("com.airbnb.android:lottie-compose:6.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation ("androidx.navigation:navigation-compose:2.4.0-alpha10") //네비게이션

    // okhttp3
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // datastore
    implementation ("androidx.datastore:datastore-preferences:1.1.2")
    implementation ("androidx.datastore:datastore-core:1.1.2")  // 코어 의존성 (필요 시)

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation ("androidx.navigation:navigation-compose:2.4.0-alpha10") //네비게이션
    implementation("androidx.compose.material:material:1.5.4")


    implementation("androidx.camera:camera-camera2:1.4.0-alpha02")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha02")
    implementation ("androidx.camera:camera-view:1.4.0-alpha02")

    // workmanager
    implementation ("androidx.work:work-runtime-ktx:2.7.1")

    // location
    implementation("com.google.android.gms:play-services-location:21.0.1")
}
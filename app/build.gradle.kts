// app: 앱의 시작점. 모든 모듈을 모아 하나의 앱으로 조립합니다.
// 공통 설정(compileSdk, minSdk, Compose 등)은 build-logic의 subday.android.application에 있습니다.
import java.util.Properties

plugins {
    alias(libs.plugins.subday.android.application)
    alias(libs.plugins.subday.hilt)
}

// 업로드 키 정보(키 파일 위치, 비밀번호). 저장소에 올리지 않는 파일이라(.gitignore) 없을 수도 있습니다.
// 없으면(CI 등) 서명 없이 빌드만 하고, 있으면 릴리스 빌드에 서명합니다. 형식은 keystore.properties.example 참고
val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) file.inputStream().use(::load)
}

android {
    namespace = "com.jeongmango.subday"

    defaultConfig {
        applicationId = "com.jeongmango.subday"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (!keystoreProperties.isEmpty) {
            create("release") {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            // R8: 쓰지 않는 코드를 지우고(축소) 이름을 짧게 바꿔(난독화) 앱 크기를 줄입니다.
            isMinifyEnabled = true
            // 쓰지 않는 리소스(이미지 등)도 지웁니다. isMinifyEnabled가 켜져 있어야 동작합니다.
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfigs.findByName("release")?.let { signingConfig = it }
        }
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.notification)
    implementation(projects.feature.home)
    implementation(projects.feature.edit)
    implementation(projects.feature.settings)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    // SubDayApplication에서 WorkManager를 Hilt와 연결할 때 씁니다.
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

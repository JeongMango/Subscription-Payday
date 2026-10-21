// app: 앱의 시작점. 모든 모듈을 모아 하나의 앱으로 조립합니다.
// 공통 설정(compileSdk, minSdk, Compose 등)은 build-logic의 subday.android.application에 있습니다.
plugins {
    alias(libs.plugins.subday.android.application)
    alias(libs.plugins.subday.hilt)
}

android {
    namespace = "com.jeongmango.subday"

    defaultConfig {
        applicationId = "com.jeongmango.subday"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.feature.home)
    implementation(projects.feature.edit)
    implementation(projects.feature.settings)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

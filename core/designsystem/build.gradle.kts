// core:designsystem: 테마(색상, 글꼴)와 여러 화면이 함께 쓰는 Compose 부품
plugins {
    alias(libs.plugins.subday.android.library.compose)
}

android {
    namespace = "com.jeongmango.subday.core.designsystem"
}

dependencies {
    // api: designsystem을 쓰는 화면 모듈도 Material 3와 Compose UI를 바로 쓸 수 있습니다.
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.icons.core)
}

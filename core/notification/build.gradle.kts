// core:notification: 결제일 알림 예약과 표시 (WorkManager). M4에서 채웁니다.
plugins {
    alias(libs.plugins.subday.android.library)
}

android {
    namespace = "com.jeongmango.subday.core.notification"
}

dependencies {
    implementation(projects.core.domain)
}

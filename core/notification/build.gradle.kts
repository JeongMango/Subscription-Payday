// core:notification: 결제일 알림 예약과 표시 (WorkManager)
// "누구에게 언제 알릴지" 계산은 core:domain(ReminderPlanner)이 하고, 여기는 Android에서 실제로 예약하고 띄우는 일만 합니다.
plugins {
    alias(libs.plugins.subday.android.library)
    alias(libs.plugins.subday.hilt)
}

android {
    namespace = "com.jeongmango.subday.core.notification"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}

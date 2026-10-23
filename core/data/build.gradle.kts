// core:data: 데이터를 실제로 저장하고 꺼내는 곳. Room DB, DataStore, Repository 구현
// domain의 Repository 인터페이스를 구현합니다. 화면 모듈은 이 모듈을 모릅니다.
plugins {
    alias(libs.plugins.subday.android.library)
    alias(libs.plugins.subday.hilt)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.jeongmango.subday.core.data"
}

// DB 구조(스키마)를 JSON으로 저장합니다. 나중에 테이블을 바꿀 때 이전 구조와 비교하는 기준이 됩니다.
room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.kotlinx.coroutines.test)
}

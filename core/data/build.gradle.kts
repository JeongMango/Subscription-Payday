// core:data: 데이터를 실제로 저장하고 꺼내는 곳. Room DB, DataStore, Repository 구현
// domain의 Repository 인터페이스를 구현합니다. 화면 모듈은 이 모듈을 모릅니다.
plugins {
    alias(libs.plugins.subday.android.library)
}

android {
    namespace = "com.jeongmango.subday.core.data"
}

dependencies {
    implementation(projects.core.domain)
}

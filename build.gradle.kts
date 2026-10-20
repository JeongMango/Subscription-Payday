// Top-level build file where you can add configuration options common to all sub-projects/modules.
// 플러그인을 여기서 한 번 불러와 두면(apply false) 각 모듈과 build-logic이 같은 버전을 씁니다.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

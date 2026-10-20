plugins {
    `kotlin-dsl`
}

group = "com.jeongmango.subday.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

// 모듈의 build.gradle.kts에서 plugins { alias(libs.plugins.subday.xxx) }로 가져다 씁니다.
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "subday.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "subday.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "subday.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "subday.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "subday.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}

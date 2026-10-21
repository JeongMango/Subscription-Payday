import com.jeongmango.subday.buildlogic.libs
import com.jeongmango.subday.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Hilt(의존성 주입)를 쓰는 Android 모듈용 (app, core:data, feature:*)
 * KSP는 빌드할 때 @Inject, @Module 같은 표시를 읽고 연결 코드를 자동으로 만들어 줍니다.
 */
class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("com.google.dagger.hilt.android")

            dependencies {
                add("implementation", libs.library("hilt-android"))
                add("ksp", libs.library("hilt-compiler"))
            }
        }
    }
}

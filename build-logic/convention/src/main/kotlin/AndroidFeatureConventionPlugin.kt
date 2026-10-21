import com.jeongmango.subday.buildlogic.libs
import com.jeongmango.subday.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 화면 모듈용 (feature:home, feature:edit, feature:settings)
 * 화면을 만들 때 항상 필요한 것(Compose, 디자인 시스템, 도메인, ViewModel, Hilt, Navigation)을 한 번에 연결합니다.
 * 새 화면 모듈은 이 플러그인 한 줄만 적으면 됩니다.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("subday.android.library.compose")
            pluginManager.apply("subday.hilt")
            // 화면 경로(HomeRoute 등)를 @Serializable 클래스로 정의하기 위해 필요합니다.
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:domain"))

                add("implementation", libs.library("androidx-lifecycle-runtime-compose"))
                add("implementation", libs.library("androidx-lifecycle-viewmodel-compose"))
                add("implementation", libs.library("androidx-hilt-lifecycle-viewmodel-compose"))
                add("implementation", libs.library("androidx-navigation-compose"))
                add("implementation", libs.library("kotlinx-serialization-core"))

                add("testImplementation", libs.library("kotlinx-coroutines-test"))
            }
        }
    }
}

import com.jeongmango.subday.buildlogic.configureKotlinJvm
import com.jeongmango.subday.buildlogic.libs
import com.jeongmango.subday.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 순수 Kotlin 모듈용 (core:model, core:domain)
 * Android 플러그인을 쓰지 않으므로 이 모듈에서는 android.* 클래스를 import할 수 없습니다.
 * 덕분에 테스트가 에뮬레이터 없이 JVM에서 바로 돕니다.
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            configureKotlinJvm()

            dependencies {
                add("testImplementation", libs.library("junit"))
            }
        }
    }
}

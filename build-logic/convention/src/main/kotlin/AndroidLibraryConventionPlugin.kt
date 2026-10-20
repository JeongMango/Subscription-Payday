import com.android.build.api.dsl.LibraryExtension
import com.jeongmango.subday.buildlogic.configureKotlinAndroid
import com.jeongmango.subday.buildlogic.libs
import com.jeongmango.subday.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/** 화면이 없는 Android 모듈용 (core:data, core:notification) */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            dependencies {
                add("testImplementation", libs.library("junit"))
            }
        }
    }
}

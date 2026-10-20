import com.android.build.api.dsl.ApplicationExtension
import com.jeongmango.subday.buildlogic.TARGET_SDK
import com.jeongmango.subday.buildlogic.configureAndroidCompose
import com.jeongmango.subday.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** :app 모듈용. Android 앱 + Compose */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
                defaultConfig.targetSdk = TARGET_SDK
            }
        }
    }
}

package com.jeongmango.subday

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jeongmango.subday.core.designsystem.theme.SubDayTheme
import com.jeongmango.subday.navigation.SubDayNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * 앱의 유일한 Activity입니다.
 * XML 방식은 setContentView(R.layout.activity_main)으로 화면을 붙였지만,
 * Compose는 setContent { } 안에 @Composable 함수를 호출해서 화면을 그립니다.
 *
 * @AndroidEntryPoint: 이 Activity 안의 화면에서 hiltViewModel()을 쓸 수 있게 합니다.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 스플래시 화면 연결. super.onCreate()보다 먼저 불러야 합니다.
        val splashScreen = installSplashScreen()

        // 우리 앱은 0.5초 안에 첫 화면이 준비돼서, 그냥 두면 망고 애니메이션(1초)이 중간에 잘립니다.
        // 애니메이션이 거의 끝날 때까지(0.9초)만 스플래시를 유지합니다. 더 오래 붙잡지는 않습니다.
        val startedAt = SystemClock.uptimeMillis()
        splashScreen.setKeepOnScreenCondition { SystemClock.uptimeMillis() - startedAt < SPLASH_MIN_MILLIS }

        // 스플래시가 사라질 때 툭 끊기지 않게 0.2초 동안 투명해지며 사라집니다.
        splashScreen.setOnExitAnimationListener { splashView ->
            splashView.view.animate()
                .alpha(0f)
                .setDuration(200L)
                .withEndAction { splashView.remove() }
                .start()
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SubDayTheme {
                SubDayNavHost()
            }
        }
    }

    private companion object {
        const val SPLASH_MIN_MILLIS = 900L
    }
}

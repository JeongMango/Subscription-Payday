package com.jeongmango.subday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SubDayTheme {
                SubDayNavHost()
            }
        }
    }
}

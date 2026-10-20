package com.jeongmango.subday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jeongmango.subday.core.designsystem.theme.SubDayTheme
import com.jeongmango.subday.feature.home.HomeScreen

/**
 * 앱의 유일한 Activity입니다.
 * XML 방식은 setContentView(R.layout.activity_main)으로 화면을 붙였지만,
 * Compose는 setContent { } 안에 @Composable 함수를 호출해서 화면을 그립니다.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SubDayTheme {
                SubDayApp()
            }
        }
    }
}

// @Composable: "이 함수는 화면 조각을 그린다"는 표시입니다. XML 레이아웃 파일 하나에 해당합니다.
// 지금은 홈 화면만 띄웁니다. ③단계에서 Navigation을 붙여 화면을 이동하게 합니다.
@Composable
fun SubDayApp() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        HomeScreen(modifier = Modifier.padding(innerPadding))
    }
}

// @Preview: 앱을 실행하지 않고 Android Studio 오른쪽 Design 탭에서 화면을 바로 봅니다.
@Preview(showBackground = true)
@Composable
private fun SubDayAppPreview() {
    SubDayTheme {
        SubDayApp()
    }
}

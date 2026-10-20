package com.jeongmango.subday.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = MangoDark,
    secondary = Mango,
    background = Paper,
    surface = Paper,
    onBackground = Ink,
    onSurface = Ink,
)

private val DarkColors = darkColorScheme(
    primary = Mango,
    secondary = MangoDark,
)

/**
 * 앱 전체에 색상과 글꼴을 적용하는 Compose 테마입니다.
 * XML의 styles.xml/themes.xml 역할을 Kotlin 코드로 대신합니다.
 *
 * 화면을 만들 때 가장 바깥을 SubDayTheme { ... }으로 감싸면,
 * 안쪽 컴포넌트는 MaterialTheme.colorScheme.primary처럼 테마 값을 꺼내 씁니다.
 */
@Composable
fun SubDayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}

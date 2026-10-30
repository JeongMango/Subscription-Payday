package com.jeongmango.subday.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Material 3 부품은 colorScheme의 정해진 "역할"에서 색을 꺼내 씁니다.
 * 예: 버튼 = primary, 선택된 칩 = secondaryContainer, + 버튼 = primaryContainer, 카드 = surfaceContainer...
 * 역할을 정하지 않으면 Material 기본값(보라색)이 들어가므로, 앱에서 쓰는 역할은 모두 채웁니다.
 */
private val LightColors = lightColorScheme(
    primary = MangoDeep, // 저장 버튼, 스위치, 선택 표시
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD8A8), // + 버튼
    onPrimaryContainer = Color(0xFF4A2500),
    secondary = Color(0xFF8D5A2B),
    onSecondary = Color.White,
    secondaryContainer = MangoLight, // 선택된 칩
    onSecondaryContainer = Color(0xFF4E2600),
    tertiary = Leaf,
    onTertiary = Color.White,
    tertiaryContainer = LeafLight,
    onTertiaryContainer = Color(0xFF1B3A0B),
    error = Alert, // 결제 임박(D-3 이내), 삭제, 경고
    onError = Color.White,
    errorContainer = AlertLight,
    onErrorContainer = Color(0xFF5F0B0B),
    background = Cream,
    onBackground = Cocoa,
    surface = Cream,
    onSurface = Cocoa,
    onSurfaceVariant = CocoaSoft, // 보조 글자 (날짜, 설명)
    surfaceVariant = Color(0xFFF6E6D3),
    // 카드, 입력창 등 바탕 위에 올라가는 면. 숫자가 클수록 진함
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFFFBF6),
    surfaceContainer = Color(0xFFFFF3E3),
    surfaceContainerHigh = Color(0xFFFFEDD8),
    surfaceContainerHighest = Color(0xFFFCE6CC),
    outline = Color(0xFFD9C2AA), // 입력창, 칩 테두리
    outlineVariant = Color(0xFFF0E0CC), // 구분선
)

/** 어두운 모드: 같은 망고 색을 밝게, 배경은 따뜻한 짙은 갈색 */
private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB86B),
    onPrimary = Color(0xFF4A2500),
    primaryContainer = Color(0xFF6B3A00),
    onPrimaryContainer = Color(0xFFFFDCBE),
    secondary = Color(0xFFE6BE95),
    onSecondary = Color(0xFF432B12),
    secondaryContainer = Color(0xFF5C3D1E),
    onSecondaryContainer = Color(0xFFFFDCBE),
    tertiary = Color(0xFFAED581),
    onTertiary = Color(0xFF1B3A0B),
    tertiaryContainer = Color(0xFF33561C),
    onTertiaryContainer = LeafLight,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = AlertLight,
    background = Color(0xFF1E1914),
    onBackground = Color(0xFFF0E0D0),
    surface = Color(0xFF1E1914),
    onSurface = Color(0xFFF0E0D0),
    onSurfaceVariant = Color(0xFFD8C2AE),
    surfaceVariant = Color(0xFF4F4539),
    surfaceContainerLowest = Color(0xFF18130F),
    surfaceContainerLow = Color(0xFF26201A),
    surfaceContainer = Color(0xFF2B241E),
    surfaceContainerHigh = Color(0xFF362E27),
    surfaceContainerHighest = Color(0xFF413932),
    outline = Color(0xFFA08D7B),
    outlineVariant = Color(0xFF4F4539),
)

/**
 * 앱 전체에 색상, 글꼴, 모서리 모양을 적용하는 Compose 테마입니다.
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
        shapes = Shapes,
        content = content,
    )
}

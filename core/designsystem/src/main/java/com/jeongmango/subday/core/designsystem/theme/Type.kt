package com.jeongmango.subday.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jeongmango.subday.core.designsystem.R

/**
 * Pretendard: 부드러운 고딕 글꼴. 숫자(금액, 날짜)가 잘 읽혀서 가계부·결제 앱에 어울립니다.
 * 무료 글꼴(SIL OFL)이라 앱에 넣어 배포할 수 있습니다. 라이선스: assets/licenses/pretendard_ofl.txt
 *
 * 앱 크기를 줄이려고 자주 쓰는 한글 2,350자 + 영문·숫자·기호만 남긴 파일을 씁니다 (한 굵기 약 380KB).
 * 파일에 없는 글자는 기기 기본 글꼴로 자동 대체됩니다.
 */
val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    // SemiBold 파일은 넣지 않고 Bold로 대신합니다 (용량 절약)
    Font(R.font.pretendard_bold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
)

private val Base = Typography()

/** Material 3 기본 크기는 그대로 두고 글꼴만 Pretendard로, 제목은 조금 더 굵게 바꿉니다. */
val Typography = Typography(
    displayLarge = Base.displayLarge.copy(fontFamily = Pretendard),
    displayMedium = Base.displayMedium.copy(fontFamily = Pretendard),
    displaySmall = Base.displaySmall.copy(fontFamily = Pretendard),
    headlineLarge = Base.headlineLarge.copy(fontFamily = Pretendard, fontWeight = FontWeight.Bold),
    headlineMedium = Base.headlineMedium.copy(fontFamily = Pretendard, fontWeight = FontWeight.Bold),
    headlineSmall = Base.headlineSmall.copy(fontFamily = Pretendard, fontWeight = FontWeight.Bold),
    titleLarge = Base.titleLarge.copy(fontFamily = Pretendard, fontWeight = FontWeight.Bold),
    titleMedium = Base.titleMedium.copy(fontFamily = Pretendard, fontWeight = FontWeight.SemiBold),
    titleSmall = Base.titleSmall.copy(fontFamily = Pretendard, fontWeight = FontWeight.SemiBold),
    bodyLarge = Base.bodyLarge.copy(fontFamily = Pretendard),
    bodyMedium = Base.bodyMedium.copy(fontFamily = Pretendard),
    bodySmall = Base.bodySmall.copy(fontFamily = Pretendard),
    labelLarge = Base.labelLarge.copy(fontFamily = Pretendard, fontWeight = FontWeight.SemiBold),
    labelMedium = Base.labelMedium.copy(fontFamily = Pretendard),
    labelSmall = Base.labelSmall.copy(fontFamily = Pretendard),
)

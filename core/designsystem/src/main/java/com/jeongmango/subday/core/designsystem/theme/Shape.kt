package com.jeongmango.subday.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * 모서리 둥글기. 망고 캐릭터의 말랑한 느낌에 맞춰 Material 기본값보다 둥글게 합니다.
 * 카드 = medium, 다이얼로그 = extraLarge처럼 부품마다 쓰는 크기가 정해져 있습니다.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

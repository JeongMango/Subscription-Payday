package com.jeongmango.subday.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.jeongmango.subday.core.designsystem.R

/**
 * 앱의 마스코트: ₩ 동전에 기댄 망고 캐릭터 (앱 아이콘과 같은 그림).
 * 홈 화면 요약 카드, 빈 화면 등 여러 화면에서 쓰므로 core:designsystem에 둡니다.
 * 크기는 호출하는 쪽에서 Modifier.size()로 정합니다. 그림 비율은 가로 68 : 세로 64입니다.
 */
@Composable
fun MangoMascot(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_mango_mascot),
        // 꾸밈용 그림이라 화면 읽기(TalkBack)에서는 건너뜁니다.
        contentDescription = null,
        modifier = modifier,
    )
}

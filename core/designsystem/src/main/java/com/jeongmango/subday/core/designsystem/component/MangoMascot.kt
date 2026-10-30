package com.jeongmango.subday.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jeongmango.subday.core.designsystem.R
import kotlinx.coroutines.delay

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

/**
 * 움직이는 마스코트 (빈 화면용). 계속 반복합니다.
 *   - 망고가 위아래로 살짝 둥실거림
 *   - 오른쪽 위에서 "D-1" 말풍선이 뿅 나와 띠딩 흔들리고(앞으로 재생), 잠시 뒤 들어감(거꾸로 재생)
 *
 * 그림은 아이콘 캔버스(108) 그대로라 둘레에 여백이 있습니다. 같은 크기로 보이려면 MangoMascot보다 크게 잡습니다.
 */
@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedMangoMascot(modifier: Modifier = Modifier) {
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.avd_bubble_ding)

    // atEnd를 true로 바꾸면 애니메이션이 끝 장면까지 재생되고, false로 바꾸면 거꾸로 처음 장면까지 돌아갑니다.
    var atEnd by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(600)
            atEnd = true // 말풍선 등장 + 띠딩
            delay(2_400)
            atEnd = false // 말풍선 들어감
            delay(900)
        }
    }

    // 둥실거림: 0 → -6dp → 0을 1.4초마다 반복
    val floatY by rememberInfiniteTransition(label = "mascotFloat").animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(1_400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "mascotFloatY",
    )

    Image(
        painter = rememberAnimatedVectorPainter(image, atEnd),
        contentDescription = null,
        modifier = modifier.graphicsLayer { translationY = floatY.dp.toPx() },
    )
}

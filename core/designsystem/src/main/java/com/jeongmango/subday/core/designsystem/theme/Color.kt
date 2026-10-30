package com.jeongmango.subday.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// 브랜드 색상. 앱 아이콘(망고 캐릭터, ₩ 동전, D-1 말풍선, 잎)에서 뽑았습니다.
// 화면 코드는 이 값을 직접 쓰지 않고 MaterialTheme.colorScheme을 통해 씁니다 (Theme.kt).

// 망고: 메인 색
val Mango = Color(0xFFFFA726)
val MangoDark = Color(0xFFF57C00)

/** 흰 글씨를 올리는 버튼 배경. MangoDark보다 진해야 글씨가 잘 읽힙니다. */
val MangoDeep = Color(0xFFE65100)

/** 선택된 칩, + 버튼처럼 옅게 강조하는 배경 */
val MangoLight = Color(0xFFFFE0B2)

// 배경: 아이콘의 살구색 배경과 이어지는 따뜻한 크림색
val Apricot = Color(0xFFFFE7B8)
val Cream = Color(0xFFFFF8EE)

// 글자: 아이콘의 망고 눈과 같은 따뜻한 갈색 계열 (검정보다 부드러움)
val Cocoa = Color(0xFF3E2A1E)
val CocoaSoft = Color(0xFF7A6250)

// D-1 말풍선 빨강: 결제 임박, 삭제
val Alert = Color(0xFFD32F2F)
val AlertLight = Color(0xFFFFDAD6)

// 잎 초록: 보조 강조
val Leaf = Color(0xFF558B2F)
val LeafLight = Color(0xFFDCEDC8)

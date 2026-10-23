package com.jeongmango.subday.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jeongmango.subday.feature.home.HomeScreen
import kotlinx.serialization.Serializable

/** 홈 화면의 경로(주소). 문자열 대신 클래스라서 오타가 컴파일 에러로 잡힙니다. */
@Serializable
data object HomeRoute

/**
 * app의 NavHost에 홈 화면을 등록합니다.
 * 홈은 등록·설정 화면을 모릅니다. "버튼이 눌렸다"고만 알리고, 어디로 갈지는 app이 정합니다.
 */
fun NavGraphBuilder.homeScreen(
    onAddClick: () -> Unit,
    onSubscriptionClick: (subscriptionId: Long) -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onAddClick = onAddClick,
            onSubscriptionClick = onSubscriptionClick,
            onSettingsClick = onSettingsClick,
        )
    }
}

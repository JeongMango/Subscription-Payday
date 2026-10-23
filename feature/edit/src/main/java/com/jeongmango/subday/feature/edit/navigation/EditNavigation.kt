package com.jeongmango.subday.feature.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jeongmango.subday.feature.edit.EditScreen
import kotlinx.serialization.Serializable

/**
 * 등록·수정 화면의 경로. 수정할 구독의 id를 함께 넘깁니다. 0이면 새로 등록.
 * EditViewModel은 SavedStateHandle에서 같은 이름("subscriptionId")으로 이 값을 꺼냅니다.
 */
@Serializable
data class EditRoute(val subscriptionId: Long = 0L)

fun NavController.navigateToEdit(subscriptionId: Long = 0L) = navigate(EditRoute(subscriptionId))

fun NavGraphBuilder.editScreen(onBackClick: () -> Unit) {
    composable<EditRoute> {
        EditScreen(onBackClick = onBackClick)
    }
}

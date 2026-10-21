package com.jeongmango.subday.feature.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jeongmango.subday.feature.edit.EditScreen
import kotlinx.serialization.Serializable

/** 등록 화면의 경로. M3에서 수정할 구독 id를 담도록 바꿉니다. */
@Serializable
data object EditRoute

fun NavController.navigateToEdit() = navigate(EditRoute)

fun NavGraphBuilder.editScreen(onBackClick: () -> Unit) {
    composable<EditRoute> {
        EditScreen(onBackClick = onBackClick)
    }
}

package com.jeongmango.subday.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.jeongmango.subday.feature.edit.navigation.editScreen
import com.jeongmango.subday.feature.edit.navigation.navigateToEdit
import com.jeongmango.subday.feature.home.navigation.HomeRoute
import com.jeongmango.subday.feature.home.navigation.homeScreen
import com.jeongmango.subday.feature.settings.navigation.navigateToSettings
import com.jeongmango.subday.feature.settings.navigation.settingsScreen

/**
 * 앱의 화면 지도입니다. feature 모듈끼리는 서로 모르기 때문에, 어느 버튼이 어느 화면으로 가는지는 여기서만 정합니다.
 * 새 화면을 추가하면 이 파일에 한 줄씩 등록합니다.
 */
@Composable
fun SubDayNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {
        homeScreen(
            onAddClick = { navController.navigateToEdit() },                    // 새로 등록
            onSubscriptionClick = { id -> navController.navigateToEdit(id) },   // 이 구독 수정
            onSettingsClick = navController::navigateToSettings,
        )
        editScreen(onBackClick = navController::popBackStack)
        settingsScreen(onBackClick = navController::popBackStack)
    }
}

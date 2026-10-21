package com.jeongmango.subday.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jeongmango.subday.feature.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings() = navigate(SettingsRoute)

fun NavGraphBuilder.settingsScreen(onBackClick: () -> Unit) {
    composable<SettingsRoute> {
        SettingsScreen(onBackClick = onBackClick)
    }
}

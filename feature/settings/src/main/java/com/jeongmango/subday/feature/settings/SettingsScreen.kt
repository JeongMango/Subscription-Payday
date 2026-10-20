package com.jeongmango.subday.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jeongmango.subday.core.designsystem.theme.SubDayTheme

/**
 * 설정 화면 자리입니다. M4에서 알림 설정과 SettingsViewModel을 붙입니다.
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "설정", style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SubDayTheme {
        SettingsScreen()
    }
}

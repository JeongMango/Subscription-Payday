package com.jeongmango.subday.feature.home

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
 * 홈 화면 자리입니다. ③단계에서 HomeViewModel과 연결해 구독 목록을 보여 줍니다.
 */
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "홈", style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SubDayTheme {
        HomeScreen()
    }
}

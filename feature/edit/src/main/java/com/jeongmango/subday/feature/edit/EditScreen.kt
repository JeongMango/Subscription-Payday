package com.jeongmango.subday.feature.edit

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
 * 구독 등록·수정 화면 자리입니다. M3에서 입력 폼과 EditViewModel을 붙입니다.
 */
@Composable
fun EditScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "구독 등록", style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun EditScreenPreview() {
    SubDayTheme {
        EditScreen()
    }
}

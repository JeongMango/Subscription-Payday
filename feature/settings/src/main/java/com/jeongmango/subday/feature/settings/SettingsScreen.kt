package com.jeongmango.subday.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeongmango.subday.core.designsystem.theme.SubDayTheme
import com.jeongmango.subday.core.model.NotificationSettings
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** 설정 화면의 입구. ViewModel에서 상태를 꺼내 SettingsContent에 넘깁니다. */
@Composable
internal fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onNotificationEnabledChange = viewModel::onNotificationEnabledChange,
        onDaysBeforeChange = viewModel::onDaysBeforeChange,
        onTimeChange = viewModel::onTimeChange,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsContent(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onNotificationEnabledChange: (Boolean) -> Unit,
    onDaysBeforeChange: (Int) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            SettingsUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is SettingsUiState.Success -> NotificationSettingsSection(
                settings = uiState.settings,
                modifier = Modifier.padding(innerPadding),
                onNotificationEnabledChange = onNotificationEnabledChange,
                onDaysBeforeChange = onDaysBeforeChange,
                onTimeChange = onTimeChange,
            )
        }
    }
}

@Composable
private fun NotificationSettingsSection(
    settings: NotificationSettings,
    modifier: Modifier = Modifier,
    onNotificationEnabledChange: (Boolean) -> Unit,
    onDaysBeforeChange: (Int) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
) {
    // 시각 선택 창을 열었는지는 이 화면 안에서만 필요한 값이라 ViewModel이 아닌 remember로 둡니다.
    var showTimePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ListItem(
            headlineContent = { Text("결제 알림") },
            supportingContent = { Text("결제일이 다가오면 알려 드려요") },
            trailingContent = {
                Switch(checked = settings.enabled, onCheckedChange = onNotificationEnabledChange)
            },
        )
        HorizontalDivider()

        // 알림이 꺼져 있으면 아래 항목은 눌러도 의미가 없어서 비활성화합니다.
        ListItem(
            headlineContent = { Text("언제 알려 드릴까요") },
            supportingContent = {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    NotificationSettings.DAYS_BEFORE_OPTIONS.forEach { days ->
                        FilterChip(
                            selected = settings.daysBefore == days,
                            onClick = { onDaysBeforeChange(days) },
                            label = { Text(daysBeforeLabel(days)) },
                            enabled = settings.enabled,
                        )
                    }
                }
            },
        )
        ListItem(
            headlineContent = { Text("알림 시각") },
            supportingContent = { Text(formatTime(settings.time)) },
            modifier = Modifier.clickable(enabled = settings.enabled) { showTimePicker = true },
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = settings.time,
            onDismiss = { showTimePicker = false },
            onConfirm = { time ->
                onTimeChange(time)
                showTimePicker = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) }) { Text("확인") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
        text = { TimePicker(state = state) },
    )
}

private fun daysBeforeLabel(days: Int): String = if (days == 0) "당일" else "${days}일 전"

/** 09:00 → "오전 9:00" */
private fun formatTime(time: LocalTime): String =
    time.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA))

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    SubDayTheme {
        SettingsContent(
            uiState = SettingsUiState.Success(NotificationSettings()),
            onBackClick = {},
            onNotificationEnabledChange = {},
            onDaysBeforeChange = {},
            onTimeChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentDisabledPreview() {
    SubDayTheme {
        SettingsContent(
            uiState = SettingsUiState.Success(NotificationSettings(enabled = false)),
            onBackClick = {},
            onNotificationEnabledChange = {},
            onDaysBeforeChange = {},
            onTimeChange = {},
        )
    }
}

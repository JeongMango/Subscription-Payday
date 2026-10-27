package com.jeongmango.subday.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeongmango.subday.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

/**
 * 설정 화면의 ViewModel.
 *
 * 등록 화면(EditViewModel)과 달리 상태를 직접 들고 있지 않습니다.
 *   스위치를 누름 → Repository에 저장 → DataStore가 새 설정을 흘려보냄 → uiState 갱신 → 화면 갱신
 * 저장된 값이 곧 화면 상태라서, 화면과 저장 내용이 어긋날 일이 없습니다.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = settingsRepository.observeNotificationSettings()
        .map(SettingsUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState.Loading,
        )

    fun onNotificationEnabledChange(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationEnabled(enabled) }
    }

    fun onDaysBeforeChange(days: Int) {
        viewModelScope.launch { settingsRepository.setReminderDaysBefore(days) }
    }

    fun onTimeChange(time: LocalTime) {
        viewModelScope.launch { settingsRepository.setReminderTime(time) }
    }
}

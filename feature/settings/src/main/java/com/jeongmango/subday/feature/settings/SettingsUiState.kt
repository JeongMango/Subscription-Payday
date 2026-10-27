package com.jeongmango.subday.feature.settings

import com.jeongmango.subday.core.model.NotificationSettings

/** 설정 화면이 그릴 수 있는 상태. 홈 화면과 같은 Loading / Success 구조입니다. */
sealed interface SettingsUiState {
    /** 저장된 설정을 아직 읽지 못함 */
    data object Loading : SettingsUiState

    data class Success(val settings: NotificationSettings) : SettingsUiState
}

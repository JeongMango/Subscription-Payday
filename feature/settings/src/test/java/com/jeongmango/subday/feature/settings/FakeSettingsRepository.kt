package com.jeongmango.subday.feature.settings

import com.jeongmango.subday.core.domain.repository.SettingsRepository
import com.jeongmango.subday.core.model.NotificationSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime

/** 테스트용 가짜 설정 저장소. 파일 대신 메모리(MutableStateFlow)에 저장합니다. */
class FakeSettingsRepository : SettingsRepository {

    private val settings = MutableStateFlow(NotificationSettings())

    override fun observeNotificationSettings(): Flow<NotificationSettings> = settings

    override suspend fun setNotificationEnabled(enabled: Boolean) = settings.update { it.copy(enabled = enabled) }

    override suspend fun setReminderDaysBefore(days: Int) = settings.update { it.copy(daysBefore = days) }

    override suspend fun setReminderTime(time: LocalTime) = settings.update { it.copy(time = time) }
}

package com.jeongmango.subday.core.domain.repository

import com.jeongmango.subday.core.model.NotificationSettings
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

/**
 * 앱 설정을 다루는 약속(인터페이스). SubscriptionRepository와 같은 방식입니다.
 * 구현은 core:data(DataStore)에 있고, 테스트에서는 가짜 구현을 넣습니다.
 */
interface SettingsRepository {
    /** 알림 설정. 값을 바꾸면 새 설정이 흘러나옵니다. */
    fun observeNotificationSettings(): Flow<NotificationSettings>

    suspend fun setNotificationEnabled(enabled: Boolean)

    suspend fun setReminderDaysBefore(days: Int)

    suspend fun setReminderTime(time: LocalTime)
}

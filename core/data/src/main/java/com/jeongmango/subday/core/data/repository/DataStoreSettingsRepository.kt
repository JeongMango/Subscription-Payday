package com.jeongmango.subday.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.jeongmango.subday.core.domain.repository.SettingsRepository
import com.jeongmango.subday.core.model.NotificationSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject

/**
 * 설정을 DataStore(키-값 파일)에 저장합니다.
 * 저장한 적 없는 값은 NotificationSettings의 기본값을 씁니다.
 */
internal class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    private val defaults = NotificationSettings()

    override fun observeNotificationSettings(): Flow<NotificationSettings> =
        dataStore.data.map { preferences ->
            NotificationSettings(
                enabled = preferences[Keys.ENABLED] ?: defaults.enabled,
                daysBefore = preferences[Keys.DAYS_BEFORE] ?: defaults.daysBefore,
                time = preferences[Keys.TIME_MINUTES]
                    ?.let { LocalTime.of(it / 60, it % 60) }
                    ?: defaults.time,
            )
        }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.ENABLED] = enabled }
    }

    override suspend fun setReminderDaysBefore(days: Int) {
        dataStore.edit { it[Keys.DAYS_BEFORE] = days }
    }

    /** LocalTime은 DataStore가 바로 저장하지 못해서 "자정부터 몇 분"인 숫자로 바꿔 저장합니다. 09:30 → 570 */
    override suspend fun setReminderTime(time: LocalTime) {
        dataStore.edit { it[Keys.TIME_MINUTES] = time.hour * 60 + time.minute }
    }

    /** 저장 파일 안에서 쓰는 이름. 출시 후에 바꾸면 기존 사용자의 설정을 못 읽으니 바꾸지 않습니다. */
    private object Keys {
        val ENABLED = booleanPreferencesKey("notification_enabled")
        val DAYS_BEFORE = intPreferencesKey("reminder_days_before")
        val TIME_MINUTES = intPreferencesKey("reminder_time_minutes")
    }
}

package com.jeongmango.subday.core.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.jeongmango.subday.core.model.NotificationSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.time.LocalTime

/**
 * 설정 저장소 테스트. DataStore는 Android 없이도 동작해서 Robolectric 없이 임시 폴더의 파일로 확인합니다.
 */
class DataStoreSettingsRepositoryTest {

    /** 테스트마다 새 임시 폴더를 만들고 끝나면 지웁니다. 테스트끼리 설정이 섞이지 않습니다. */
    @get:Rule
    val tmpFolder = TemporaryFolder()

    @Test
    fun `저장한 적이 없으면 기본값을 준다`() = runTest {
        val repository = newRepository()

        assertEquals(NotificationSettings(), repository.observeNotificationSettings().first())
    }

    @Test
    fun `알림 켜기·끄기와 며칠 전을 저장한다`() = runTest {
        val repository = newRepository()

        repository.setNotificationEnabled(false)
        repository.setReminderDaysBefore(3)

        val settings = repository.observeNotificationSettings().first()
        assertEquals(false, settings.enabled)
        assertEquals(3, settings.daysBefore)
    }

    @Test
    fun `알림 시각은 분까지 그대로 돌려준다`() = runTest {
        val repository = newRepository()

        repository.setReminderTime(LocalTime.of(21, 45))

        assertEquals(LocalTime.of(21, 45), repository.observeNotificationSettings().first().time)
    }

    /** backgroundScope: 테스트가 끝나면 DataStore가 쓰던 작업도 함께 정리됩니다. */
    private fun TestScope.newRepository() = DataStoreSettingsRepository(
        PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { File(tmpFolder.root, "settings.preferences_pb") },
        ),
    )
}

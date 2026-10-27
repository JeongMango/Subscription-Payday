package com.jeongmango.subday.feature.settings

import com.jeongmango.subday.core.model.NotificationSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

/**
 * 설정 화면의 규칙. 화면에서 무언가 바꾸면(onXxxChange) 저장소를 거쳐 uiState에 반영되는지 확인합니다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSettingsRepository()
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        viewModel = SettingsViewModel(repository)
    }

    @Test
    fun `처음에는 기본 설정을 보여준다`() = runTest {
        startCollecting()

        assertEquals(NotificationSettings(), settings())
    }

    @Test
    fun `알림을 끄면 꺼진 상태가 된다`() = runTest {
        startCollecting()

        viewModel.onNotificationEnabledChange(false)

        assertEquals(false, settings().enabled)
    }

    @Test
    fun `며칠 전 알림을 바꾼다`() = runTest {
        startCollecting()

        viewModel.onDaysBeforeChange(7)

        assertEquals(7, settings().daysBefore)
    }

    @Test
    fun `알림 시각을 바꾼다`() = runTest {
        startCollecting()

        viewModel.onTimeChange(LocalTime.of(20, 30))

        assertEquals(LocalTime.of(20, 30), settings().time)
    }

    /** uiState는 누군가 지켜볼 때만 값이 흐릅니다(WhileSubscribed). 화면 대신 지켜보는 역할 */
    private fun TestScope.startCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    }

    private fun settings() = (viewModel.uiState.value as SettingsUiState.Success).settings
}

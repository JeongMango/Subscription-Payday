package com.jeongmango.subday.core.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jeongmango.subday.core.domain.reminder.ReminderPlanner
import com.jeongmango.subday.core.domain.repository.SettingsRepository
import com.jeongmango.subday.core.model.NotificationSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ReminderWorker를 "다음 알림 시각"에 실행되도록 WorkManager에 예약합니다.
 * 예약은 항상 하나만 있도록 이름(WORK_NAME)을 붙여 관리합니다.
 */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val clock: Clock,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * 앱이 시작할 때 한 번 호출합니다(SubDayApplication).
     * 이후 설정 화면에서 값을 바꿀 때마다 예약을 새 시각으로 바꾸거나, 알림을 끄면 취소합니다.
     */
    fun startSyncing() {
        scope.launch {
            settingsRepository.observeNotificationSettings()
                .distinctUntilChanged()
                .collect { schedule(it, ExistingWorkPolicy.REPLACE) }
        }
    }

    /**
     * 워커가 오늘 일을 마친 뒤 내일 것을 예약합니다.
     * REPLACE를 쓰면 지금 실행 중인 자기 자신이 취소되므로, 지금 작업이 끝난 뒤에 이어 붙입니다(APPEND_OR_REPLACE).
     */
    internal fun scheduleNext(settings: NotificationSettings) = schedule(settings, ExistingWorkPolicy.APPEND_OR_REPLACE)

    private fun schedule(settings: NotificationSettings, policy: ExistingWorkPolicy) {
        val workManager = WorkManager.getInstance(context)
        if (!settings.enabled) {
            workManager.cancelUniqueWork(WORK_NAME)
            return
        }

        val now = LocalDateTime.now(clock)
        val delay = Duration.between(now, ReminderPlanner.nextRunAt(now, settings.time))
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay)
            .build()
        workManager.enqueueUniqueWork(WORK_NAME, policy, request)
    }

    private companion object {
        const val WORK_NAME = "subscription_reminder"
    }
}

package com.jeongmango.subday.core.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jeongmango.subday.core.domain.reminder.ReminderPlanner
import com.jeongmango.subday.core.domain.repository.SettingsRepository
import com.jeongmango.subday.core.domain.repository.SubscriptionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.LocalDate

/**
 * 하루에 한 번, 알림 시각에 WorkManager가 실행하는 작업입니다. 앱이 꺼져 있어도 실행됩니다.
 *
 *   1. 설정과 구독 목록을 읽음
 *   2. ReminderPlanner로 오늘 알릴 구독을 고름
 *   3. 알림을 띄움
 *   4. 내일 같은 시각으로 다시 예약
 *
 * context, params는 WorkManager가 넣고(@Assisted), 나머지는 Hilt가 넣습니다.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val subscriptionRepository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository,
    private val notifier: ReminderNotifier,
    private val scheduler: ReminderScheduler,
    private val clock: Clock,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settings = settingsRepository.observeNotificationSettings().first()
        if (!settings.enabled) return Result.success()

        val subscriptions = subscriptionRepository.observeSubscriptions().first()
        ReminderPlanner.remindersFor(subscriptions, LocalDate.now(clock), settings.daysBefore)
            .forEach(notifier::show)

        scheduler.scheduleNext(settings)
        return Result.success()
    }
}

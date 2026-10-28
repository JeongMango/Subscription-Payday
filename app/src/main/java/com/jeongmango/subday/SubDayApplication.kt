package com.jeongmango.subday

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.jeongmango.subday.core.notification.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Hilt의 시작점입니다. 앱이 켜질 때 Hilt가 객체를 만들고 연결할 준비를 합니다.
 * AndroidManifest.xml의 android:name에 등록되어 있습니다.
 *
 * Configuration.Provider: WorkManager가 Worker를 만들 때 Hilt를 거치게 합니다.
 * 이게 없으면 ReminderWorker에 Repository를 넣어 줄 수 없습니다.
 */
@HiltAndroidApp
class SubDayApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // 알림 설정을 지켜보다가 바뀌면 알림 예약을 다시 잡습니다.
        reminderScheduler.startSyncing()
    }
}

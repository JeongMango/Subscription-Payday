package com.jeongmango.subday.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.jeongmango.subday.core.domain.reminder.Reminder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

/** 알림을 실제로 화면(상태 표시줄)에 띄웁니다. */
class ReminderNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun show(reminder: Reminder) {
        // Android 13부터는 사용자가 알림 권한을 허용해야 합니다. 허용하지 않았으면 조용히 넘어갑니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        createChannel()

        val subscription = reminder.subscription
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title(reminder))
            .setContentText(
                "${reminder.billingDate.monthValue}월 ${reminder.billingDate.dayOfMonth}일 " +
                    "${formatWon(subscription.amount)}이 결제돼요",
            )
            .setContentIntent(openAppIntent())
            .setAutoCancel(true) // 누르면 알림이 사라짐
            .build()

        // 구독 id를 알림 id로 써서, 같은 구독 알림은 하나만 남게 합니다.
        NotificationManagerCompat.from(context).notify(subscription.id.toInt(), notification)
    }

    /**
     * 알림 채널: 사용자가 기기 설정에서 알림 종류별로 켜고 끌 수 있는 단위입니다 (Android 8부터 필수).
     * 이미 있으면 아무 일도 하지 않으므로 매번 호출해도 됩니다.
     */
    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "결제 알림", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "구독 결제일이 다가오면 알려 드려요"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    /**
     * 알림을 누르면 앱을 엽니다.
     * core:notification은 app 모듈(MainActivity)을 모르기 때문에, "이 앱의 시작 화면"을 시스템에 물어봅니다.
     */
    private fun openAppIntent(): PendingIntent? {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return null
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun title(reminder: Reminder): String {
        val name = reminder.subscription.name
        return when (reminder.daysUntilBilling) {
            0L -> "오늘 $name 결제일이에요"
            1L -> "내일 $name 결제일이에요"
            else -> "$name 결제 ${reminder.daysUntilBilling}일 전이에요"
        }
    }

    private fun formatWon(amount: Long): String =
        NumberFormat.getNumberInstance(Locale.KOREA).format(amount) + "원"

    private companion object {
        const val CHANNEL_ID = "billing_reminder"
    }
}

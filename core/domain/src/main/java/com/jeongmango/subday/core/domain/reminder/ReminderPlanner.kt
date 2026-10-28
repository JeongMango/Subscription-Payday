package com.jeongmango.subday.core.domain.reminder

import com.jeongmango.subday.core.domain.billing.BillingCalculator
import com.jeongmango.subday.core.model.Subscription
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/** 알림 한 건에 필요한 값 */
data class Reminder(
    val subscription: Subscription,
    val billingDate: LocalDate,
    /** 0이면 오늘 결제 */
    val daysUntilBilling: Long,
)

/**
 * 알림 규칙. 규칙은 ReminderPlannerTest가 정의합니다.
 * 알림을 실제로 띄우는 일(Android)은 core:notification이 하고, 여기는 "누구에게, 언제"만 계산합니다.
 */
object ReminderPlanner {

    /** 오늘이 알림일인 구독. 매일 한 번 알림 시각에 호출합니다. */
    fun remindersFor(subscriptions: List<Subscription>, today: LocalDate, daysBefore: Int): List<Reminder> =
        subscriptions
            .filter { BillingCalculator.nextReminderDate(it, today, daysBefore) == today }
            .map { subscription ->
                val billingDate = BillingCalculator.nextBillingDate(subscription, today.plusDays(daysBefore.toLong()))
                Reminder(
                    subscription = subscription,
                    billingDate = billingDate,
                    daysUntilBilling = ChronoUnit.DAYS.between(today, billingDate),
                )
            }

    /** 다음에 알림을 확인할 시각. 오늘 알림 시각이 아직 안 됐으면 오늘, 지났으면 내일 */
    fun nextRunAt(now: LocalDateTime, time: LocalTime): LocalDateTime {
        val todayAtTime = now.toLocalDate().atTime(time)
        return if (now.isBefore(todayAtTime)) todayAtTime else todayAtTime.plusDays(1)
    }
}

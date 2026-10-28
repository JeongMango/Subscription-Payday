package com.jeongmango.subday.core.domain.reminder

import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 알림 규칙. "오늘 누구에게 알릴지"와 "다음에 언제 확인할지"를 정의합니다.
 */
class ReminderPlannerTest {

    private val today = LocalDate.of(2026, 10, 28)

    // ── 오늘 알릴 구독 고르기 ─────────────────────────────────

    @Test
    fun `3일 전 설정이면 결제일이 3일 남은 구독만 고른다`() {
        val subscriptions = listOf(
            subscription(id = 1, first = LocalDate.of(2026, 1, 31)), // 다음 결제 10/31, 3일 남음
            subscription(id = 2, first = LocalDate.of(2026, 1, 30)), // 다음 결제 10/30, 2일 남음
            subscription(id = 3, first = LocalDate.of(2026, 1, 28)), // 오늘 결제
        )

        val reminders = ReminderPlanner.remindersFor(subscriptions, today, daysBefore = 3)

        assertEquals(listOf(1L), reminders.map { it.subscription.id })
        assertEquals(LocalDate.of(2026, 10, 31), reminders.single().billingDate)
        assertEquals(3L, reminders.single().daysUntilBilling)
    }

    @Test
    fun `당일 설정이면 오늘 결제하는 구독을 고른다`() {
        val subscriptions = listOf(
            subscription(id = 1, first = LocalDate.of(2026, 1, 28)),
            subscription(id = 2, first = LocalDate.of(2026, 1, 29)),
        )

        val reminders = ReminderPlanner.remindersFor(subscriptions, today, daysBefore = 0)

        assertEquals(listOf(1L), reminders.map { it.subscription.id })
        assertEquals(0L, reminders.single().daysUntilBilling)
    }

    @Test
    fun `무료 체험 중이면 체험 종료일을 결제일로 본다`() {
        val trial = subscription(id = 1, first = LocalDate.of(2026, 10, 1))
            .copy(freeTrialEndDate = LocalDate.of(2026, 10, 29))

        val reminders = ReminderPlanner.remindersFor(listOf(trial), today, daysBefore = 1)

        assertEquals(LocalDate.of(2026, 10, 29), reminders.single().billingDate)
    }

    @Test
    fun `말일 결제는 짧은 달의 말일 기준으로 알린다`() {
        // 1/31 결제 → 2026년 2월은 2/28 결제 → 1일 전 알림은 2/27
        val subscriptions = listOf(subscription(id = 1, first = LocalDate.of(2026, 1, 31)))

        val reminders = ReminderPlanner.remindersFor(subscriptions, LocalDate.of(2026, 2, 27), daysBefore = 1)

        assertEquals(LocalDate.of(2026, 2, 28), reminders.single().billingDate)
    }

    @Test
    fun `알릴 구독이 없으면 빈 목록`() {
        val subscriptions = listOf(subscription(id = 1, first = LocalDate.of(2026, 1, 15)))

        assertEquals(emptyList<Reminder>(), ReminderPlanner.remindersFor(subscriptions, today, daysBefore = 1))
    }

    // ── 다음 확인 시각 ─────────────────────────────────────────

    @Test
    fun `알림 시각 전이면 오늘 그 시각에 확인한다`() {
        val now = LocalDateTime.of(2026, 10, 28, 8, 30)

        assertEquals(LocalDateTime.of(2026, 10, 28, 9, 0), ReminderPlanner.nextRunAt(now, LocalTime.of(9, 0)))
    }

    @Test
    fun `알림 시각이 지났거나 같으면 내일 그 시각에 확인한다`() {
        val time = LocalTime.of(9, 0)

        assertEquals(
            LocalDateTime.of(2026, 10, 29, 9, 0),
            ReminderPlanner.nextRunAt(LocalDateTime.of(2026, 10, 28, 9, 0), time),
        )
        assertEquals(
            LocalDateTime.of(2026, 10, 29, 9, 0),
            ReminderPlanner.nextRunAt(LocalDateTime.of(2026, 10, 28, 21, 15), time),
        )
    }

    private fun subscription(id: Long, first: LocalDate) = Subscription(
        id = id,
        name = "구독$id",
        amount = 10_000,
        billingCycle = BillingCycle.MONTHLY,
        firstBillingDate = first,
    )
}

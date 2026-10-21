package com.jeongmango.subday.core.domain.billing

import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

/**
 * 결제일 계산 규칙. 기획서 5장 "결제일 계산 규칙" 표의 번호와 같습니다.
 * 이 테스트가 곧 규칙 문서입니다. 규칙을 바꾸려면 테스트부터 바꿉니다.
 */
class BillingCalculatorTest {

    // ── 다음 결제일 ──────────────────────────────────────────

    @Test
    fun `1 월 결제 - 오늘 이후 가장 가까운 결제일`() {
        val sub = subscription(BillingCycle.MONTHLY, first = date(2026, 1, 15))

        assertEquals(date(2026, 3, 15), BillingCalculator.nextBillingDate(sub, today = date(2026, 3, 10)))
    }

    @Test
    fun `2 오늘이 결제일이면 오늘`() {
        val sub = subscription(BillingCycle.MONTHLY, first = date(2026, 1, 15))

        assertEquals(date(2026, 3, 15), BillingCalculator.nextBillingDate(sub, today = date(2026, 3, 15)))
    }

    @Test
    fun `3 31일 결제는 평년 2월에 28일`() {
        val sub = subscription(BillingCycle.MONTHLY, first = date(2026, 1, 31))

        assertEquals(date(2026, 2, 28), BillingCalculator.nextBillingDate(sub, today = date(2026, 2, 1)))
    }

    @Test
    fun `4 31일 결제는 윤년 2월에 29일`() {
        val sub = subscription(BillingCycle.MONTHLY, first = date(2028, 1, 31))

        assertEquals(date(2028, 2, 29), BillingCalculator.nextBillingDate(sub, today = date(2028, 2, 1)))
    }

    @Test
    fun `5 2월에 28일로 당겨져도 3월에는 다시 31일`() {
        val sub = subscription(BillingCycle.MONTHLY, first = date(2026, 1, 31))

        assertEquals(date(2026, 3, 31), BillingCalculator.nextBillingDate(sub, today = date(2026, 3, 1)))
    }

    @Test
    fun `6 윤년 2월 29일 연 결제는 다음 해 2월 28일`() {
        val sub = subscription(BillingCycle.YEARLY, first = date(2028, 2, 29))

        assertEquals(date(2029, 2, 28), BillingCalculator.nextBillingDate(sub, today = date(2028, 3, 1)))
    }

    @Test
    fun `7 주 결제는 다음 주 같은 요일`() {
        val sub = subscription(BillingCycle.WEEKLY, first = date(2026, 10, 5)) // 월요일

        assertEquals(date(2026, 10, 12), BillingCalculator.nextBillingDate(sub, today = date(2026, 10, 6)))
    }

    @Test
    fun `8 첫 결제일이 미래면 첫 결제일`() {
        val sub = subscription(BillingCycle.MONTHLY, first = date(2026, 12, 1))

        assertEquals(date(2026, 12, 1), BillingCalculator.nextBillingDate(sub, today = date(2026, 10, 21)))
    }

    // ── 알림일 ──────────────────────────────────────────────

    @Test
    fun `9 알림일이 이미 지났으면 다음 회차 알림으로`() {
        // 다음 결제 10/23까지 2일 남음. 3일 전 알림(10/20)은 이미 지났으므로 다음 회차(11/23)의 3일 전.
        val sub = subscription(BillingCycle.MONTHLY, first = date(2026, 1, 23))

        assertEquals(
            date(2026, 11, 20),
            BillingCalculator.nextReminderDate(sub, today = date(2026, 10, 21), daysBefore = 3),
        )
    }

    @Test
    fun `9-1 알림일이 오늘이면 오늘`() {
        val sub = subscription(BillingCycle.MONTHLY, first = date(2026, 1, 24))

        assertEquals(
            date(2026, 10, 21),
            BillingCalculator.nextReminderDate(sub, today = date(2026, 10, 21), daysBefore = 3),
        )
    }

    // ── 무료 체험 ────────────────────────────────────────────

    @Test
    fun `10 무료 체험 종료일이 있으면 그날이 첫 결제일`() {
        val sub = subscription(
            BillingCycle.MONTHLY,
            first = date(2026, 10, 1),
            freeTrialEnd = date(2026, 10, 31),
        )

        assertEquals(date(2026, 10, 31), BillingCalculator.nextBillingDate(sub, today = date(2026, 10, 21)))
        assertEquals(date(2026, 11, 30), BillingCalculator.nextBillingDate(sub, today = date(2026, 11, 1)))
    }

    // ── 금액 ────────────────────────────────────────────────

    @Test
    fun `11 연 결제의 월 환산은 12로 나눈 값`() {
        val sub = subscription(BillingCycle.YEARLY, amount = 120_000)

        assertEquals(10_000L, BillingCalculator.monthlyEquivalent(sub))
    }

    @Test
    fun `12 주 결제의 월 환산은 52주를 12달로 나누고 원 단위 반올림`() {
        // 10,000 × 52 / 12 = 43,333.33… → 43,333
        val sub = subscription(BillingCycle.WEEKLY, amount = 10_000)

        assertEquals(43_333L, BillingCalculator.monthlyEquivalent(sub))
    }

    @Test
    fun `12-1 반올림은 0점5부터 올림`() {
        // 연 6원 / 12 = 0.5 → 1
        val sub = subscription(BillingCycle.YEARLY, amount = 6)

        assertEquals(1L, BillingCalculator.monthlyEquivalent(sub))
    }

    @Test
    fun `13 이번 달 지출은 이번 달에 실제로 결제일이 있는 것만 합산`() {
        val subscriptions = listOf(
            subscription(BillingCycle.MONTHLY, amount = 17_000, first = date(2026, 1, 15)), // 10/15 ✅
            subscription(BillingCycle.YEARLY, amount = 39_000, first = date(2025, 11, 30)), // 11/30 ❌
            subscription(BillingCycle.YEARLY, amount = 99_000, first = date(2025, 10, 3)),  // 10/3 ✅
            subscription(BillingCycle.WEEKLY, amount = 1_000, first = date(2026, 10, 5)),   // 10/5,12,19,26 → 4번
            subscription(BillingCycle.MONTHLY, amount = 5_000, first = date(2026, 11, 1)),  // 아직 시작 전 ❌
        )

        assertEquals(
            17_000L + 99_000L + 4 * 1_000L,
            BillingCalculator.spendingInMonth(subscriptions, YearMonth.of(2026, 10)),
        )
    }

    // ── 도우미 ──────────────────────────────────────────────

    private fun date(year: Int, month: Int, day: Int) = LocalDate.of(year, month, day)

    private fun subscription(
        cycle: BillingCycle,
        amount: Long = 10_000,
        first: LocalDate = date(2026, 1, 1),
        freeTrialEnd: LocalDate? = null,
    ) = Subscription(
        id = 1,
        name = "테스트 구독",
        amount = amount,
        billingCycle = cycle,
        firstBillingDate = first,
        freeTrialEndDate = freeTrialEnd,
    )
}

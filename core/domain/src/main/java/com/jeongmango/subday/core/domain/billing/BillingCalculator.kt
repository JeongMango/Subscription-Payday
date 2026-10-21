package com.jeongmango.subday.core.domain.billing

import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

/**
 * 결제일과 금액 계산. 규칙은 BillingCalculatorTest가 정의합니다.
 * "오늘"은 항상 파라미터로 받습니다. LocalDate.now()를 직접 부르면 테스트에서 날짜를 고정할 수 없습니다.
 */
object BillingCalculator {

    /** 오늘을 포함해 오늘 이후 가장 가까운 결제일 */
    fun nextBillingDate(subscription: Subscription, today: LocalDate): LocalDate {
        val anchor = subscription.anchorDate()
        if (!anchor.isBefore(today)) return anchor

        // 대략 몇 회차 지났는지 계산한 뒤, 오늘 이전이면 한 회차씩 넘깁니다.
        var n = subscription.billingCycle.unit.between(anchor, today)
        while (subscription.occurrence(n).isBefore(today)) n++
        return subscription.occurrence(n)
    }

    /**
     * 다음 알림일. 결제일 [daysBefore]일 전이며, 그날이 이미 지났으면 다음 회차의 알림일입니다.
     * 알림일이 오늘이면 오늘을 돌려줍니다.
     */
    fun nextReminderDate(subscription: Subscription, today: LocalDate, daysBefore: Int): LocalDate {
        var billingDate = nextBillingDate(subscription, today)
        while (billingDate.minusDays(daysBefore.toLong()).isBefore(today)) {
            billingDate = nextBillingDate(subscription, billingDate.plusDays(1))
        }
        return billingDate.minusDays(daysBefore.toLong())
    }

    /** 한 달 기준으로 환산한 금액. 원 단위에서 반올림(0.5 이상 올림)합니다. */
    fun monthlyEquivalent(subscription: Subscription): Long {
        val amount = BigDecimal.valueOf(subscription.amount)
        val monthly = when (subscription.billingCycle) {
            BillingCycle.WEEKLY -> amount.multiply(BigDecimal.valueOf(52)).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP)
            BillingCycle.MONTHLY -> amount
            BillingCycle.YEARLY -> amount.divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP)
        }
        return monthly.toLong()
    }

    /** [month]에 실제로 결제일이 있는 금액의 합. 주 결제는 그달의 결제 횟수만큼 더합니다. */
    fun spendingInMonth(subscriptions: List<Subscription>, month: YearMonth): Long =
        subscriptions.sumOf { subscription ->
            subscription.amount * billingCountInMonth(subscription, month)
        }

    private fun billingCountInMonth(subscription: Subscription, month: YearMonth): Int {
        var count = 0
        var date = nextBillingDate(subscription, month.atDay(1))
        while (!date.isAfter(month.atEndOfMonth())) {
            count++
            date = nextBillingDate(subscription, date.plusDays(1))
        }
        return count
    }

    /** 회차 계산의 기준일. 무료 체험이 있으면 체험 종료일이 첫 결제일입니다. */
    private fun Subscription.anchorDate(): LocalDate = freeTrialEndDate ?: firstBillingDate

    /**
     * n번째 결제일. 항상 기준일에서 n회차를 더해 계산합니다.
     * 직전 결제일에서 한 달씩 더하면 1/31 → 2/28 → 3/28처럼 날짜가 줄어든 채로 굳기 때문입니다.
     */
    private fun Subscription.occurrence(n: Long): LocalDate = when (billingCycle) {
        BillingCycle.WEEKLY -> anchorDate().plusWeeks(n)
        BillingCycle.MONTHLY -> anchorDate().plusMonths(n)
        BillingCycle.YEARLY -> anchorDate().plusYears(n)
    }

    private val BillingCycle.unit: ChronoUnit
        get() = when (this) {
            BillingCycle.WEEKLY -> ChronoUnit.WEEKS
            BillingCycle.MONTHLY -> ChronoUnit.MONTHS
            BillingCycle.YEARLY -> ChronoUnit.YEARS
        }
}

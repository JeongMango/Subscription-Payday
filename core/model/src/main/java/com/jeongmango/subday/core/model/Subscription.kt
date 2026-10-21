package com.jeongmango.subday.core.model

import java.time.LocalDate

/**
 * 구독 하나. 앱 전체(화면, 계산, DB)가 이 모양으로 데이터를 주고받습니다.
 * 카테고리, 메모는 M3에서 추가합니다.
 */
data class Subscription(
    val id: Long,
    val name: String,
    /** 원 단위. 소수점 오차를 피하려고 Double 대신 Long을 씁니다. */
    val amount: Long,
    val billingCycle: BillingCycle,
    val firstBillingDate: LocalDate,
    /** 무료 체험 종료일. 있으면 이 날이 첫 결제일이 됩니다. */
    val freeTrialEndDate: LocalDate? = null,
)

enum class BillingCycle {
    WEEKLY,
    MONTHLY,
    YEARLY,
}

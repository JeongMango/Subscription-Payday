package com.jeongmango.subday.feature.home

import com.jeongmango.subday.core.model.Subscription
import java.time.LocalDate
import java.time.YearMonth

/**
 * 홈 화면이 그릴 수 있는 모든 상태입니다.
 * 화면은 이 값만 보고 그리므로, "지금 무엇을 보여 줘야 하나"를 한곳에서 알 수 있습니다.
 */
sealed interface HomeUiState {
    /** 데이터를 아직 받지 못함 */
    data object Loading : HomeUiState

    data class Success(
        /** 다음 결제일이 가까운 순 */
        val items: List<HomeSubscriptionItem>,
        val month: YearMonth,
        /** [month]에 실제로 결제일이 있는 금액의 합 */
        val monthlySpending: Long,
    ) : HomeUiState
}

/** 목록 한 줄에 필요한 값. 계산은 ViewModel에서 끝내고 화면은 그리기만 합니다. */
data class HomeSubscriptionItem(
    val subscription: Subscription,
    val nextBillingDate: LocalDate,
    /** 오늘이 결제일이면 0 */
    val daysUntilBilling: Long,
)

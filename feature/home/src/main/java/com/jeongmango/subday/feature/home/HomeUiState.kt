package com.jeongmango.subday.feature.home

import com.jeongmango.subday.core.model.Subscription

/**
 * 홈 화면이 그릴 수 있는 모든 상태입니다.
 * 화면은 이 값만 보고 그리므로, "지금 무엇을 보여 줘야 하나"를 한곳에서 알 수 있습니다.
 */
sealed interface HomeUiState {
    /** 데이터를 아직 받지 못함 */
    data object Loading : HomeUiState

    data class Success(
        val subscriptions: List<Subscription>,
        /** 등록된 구독 금액의 단순 합계. M1에서 월 환산 기준의 이번 달 예상 지출로 바꿉니다. */
        val totalAmount: Long,
    ) : HomeUiState
}

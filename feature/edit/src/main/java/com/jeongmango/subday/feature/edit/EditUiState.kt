package com.jeongmango.subday.feature.edit

import com.jeongmango.subday.core.model.BillingCycle
import java.time.LocalDate

/**
 * 등록·수정 화면의 상태입니다. 입력 중인 값, 에러 메시지, 완료 여부를 한곳에 담습니다.
 *
 * 금액은 입력창 그대로의 글자(amountText)로 들고 있습니다.
 * 사용자가 지우는 중에는 빈칸일 수 있어서, 숫자로 바꾸는 건 저장할 때 합니다.
 */
data class EditUiState(
    /** 수정 화면에서 기존 구독을 불러오는 중 */
    val isLoading: Boolean = false,
    /** false면 새로 등록, true면 기존 구독 수정 */
    val isEditMode: Boolean = false,

    val name: String = "",
    val amountText: String = "",
    val billingCycle: BillingCycle = BillingCycle.MONTHLY,
    val firstBillingDate: LocalDate,
    /** null이면 무료 체험 없음 */
    val freeTrialEndDate: LocalDate? = null,

    val nameError: String? = null,
    val amountError: String? = null,

    /** 저장 또는 삭제가 끝남. 화면은 이걸 보고 이전 화면으로 돌아갑니다. */
    val isDone: Boolean = false,
)

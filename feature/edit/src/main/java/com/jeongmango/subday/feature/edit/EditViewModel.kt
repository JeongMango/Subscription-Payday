package com.jeongmango.subday.feature.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeongmango.subday.core.domain.repository.SubscriptionRepository
import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

/**
 * 등록·수정 화면의 ViewModel. "이벤트는 위로" 흐름의 중간 지점입니다.
 *
 *   화면: 사용자가 입력하거나 버튼을 누름 → onXxx() 호출
 *   ViewModel: 입력값을 상태에 반영, 저장할 때 검증 → Repository에 저장 요청
 *   Repository: DB에 저장 → 홈 화면은 Flow로 자동 갱신
 */
@HiltViewModel
class EditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SubscriptionRepository,
    clock: Clock,
) : ViewModel() {

    /** 화면 이동 때 넘어온 값(EditRoute.subscriptionId). 0이면 새로 등록 */
    private val subscriptionId: Long = savedStateHandle["subscriptionId"] ?: 0L

    private val _uiState = MutableStateFlow(
        EditUiState(
            isLoading = subscriptionId != 0L,
            isEditMode = subscriptionId != 0L,
            firstBillingDate = LocalDate.now(clock),
        ),
    )

    /** 화면에는 읽기 전용으로 내보냅니다. 상태를 바꾸는 건 이 ViewModel만 할 수 있습니다. */
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    init {
        if (subscriptionId != 0L) loadSubscription()
    }

    private fun loadSubscription() = viewModelScope.launch {
        val subscription = repository.getSubscription(subscriptionId) ?: run {
            // 그사이 삭제되었다면 화면을 닫습니다.
            _uiState.update { it.copy(isLoading = false, isDone = true) }
            return@launch
        }
        _uiState.update {
            it.copy(
                isLoading = false,
                name = subscription.name,
                amountText = subscription.amount.toString(),
                billingCycle = subscription.billingCycle,
                firstBillingDate = subscription.firstBillingDate,
                freeTrialEndDate = subscription.freeTrialEndDate,
            )
        }
    }

    // ── 입력 이벤트: 화면에서 값이 바뀔 때마다 호출 ─────────────────────

    fun onNameChange(name: String) = _uiState.update { it.copy(name = name, nameError = null) }

    /** 숫자만 남깁니다. "17,000"을 붙여 넣어도 "17000"이 됩니다. */
    fun onAmountChange(text: String) =
        _uiState.update { it.copy(amountText = text.filter(Char::isDigit), amountError = null) }

    fun onBillingCycleChange(cycle: BillingCycle) = _uiState.update { it.copy(billingCycle = cycle) }

    fun onFirstBillingDateChange(date: LocalDate) = _uiState.update { it.copy(firstBillingDate = date) }

    /** null을 넘기면 무료 체험 없음 */
    fun onFreeTrialEndDateChange(date: LocalDate?) = _uiState.update { it.copy(freeTrialEndDate = date) }

    // ── 버튼 이벤트 ─────────────────────────────────────────────────

    fun onSaveClick() {
        val state = _uiState.value
        val amount = state.amountText.toLongOrNull()

        val nameError = if (state.name.isBlank()) "이름을 입력하세요" else null
        val amountError = if (amount == null || amount <= 0) "금액을 입력하세요" else null
        if (nameError != null || amountError != null || amount == null) {
            _uiState.update { it.copy(nameError = nameError, amountError = amountError) }
            return
        }

        viewModelScope.launch {
            repository.saveSubscription(
                Subscription(
                    id = subscriptionId,
                    name = state.name.trim(),
                    amount = amount,
                    billingCycle = state.billingCycle,
                    firstBillingDate = state.firstBillingDate,
                    freeTrialEndDate = state.freeTrialEndDate,
                ),
            )
            _uiState.update { it.copy(isDone = true) }
        }
    }

    fun onDeleteClick() {
        if (subscriptionId == 0L) return
        viewModelScope.launch {
            repository.deleteSubscription(subscriptionId)
            _uiState.update { it.copy(isDone = true) }
        }
    }
}

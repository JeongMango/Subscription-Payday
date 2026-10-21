package com.jeongmango.subday.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeongmango.subday.core.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Repository에서 받은 구독 목록을 홈 화면용 상태(HomeUiState)로 바꿉니다.
 *
 * - Repository "인터페이스"만 알고, 실제 구현이 무엇인지는 모릅니다. Hilt가 넣어 줍니다.
 * - Android 화면 코드(Compose)를 모르므로 화면 없이 단위 테스트할 수 있습니다.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = subscriptionRepository.observeSubscriptions()
        .map { subscriptions ->
            HomeUiState.Success(
                subscriptions = subscriptions,
                totalAmount = subscriptions.sumOf { it.amount },
            )
        }
        .stateIn(
            scope = viewModelScope,
            // 화면이 보이지 않게 된 뒤 5초 동안은 구독을 유지합니다 (화면 회전 시 다시 불러오지 않도록).
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )
}

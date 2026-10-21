package com.jeongmango.subday.feature.home

import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSubscriptionRepository()
    private lateinit var viewModel: HomeViewModel

    // ViewModel은 MainDispatcherRule이 메인 스레드를 바꿔 끼운 "뒤에" 만들어야 합니다.
    // 필드에서 바로 만들면 규칙이 적용되기 전에 생성되어 viewModelScope가 동작하지 않습니다.
    @Before
    fun setUp() {
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun `데이터를 받기 전에는 Loading 상태다`() {
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `구독 목록을 받으면 Success 상태가 되고 금액을 합산한다`() = runTest {
        // 화면이 상태를 지켜보고 있는 상황을 흉내 냅니다 (WhileSubscribed는 지켜보는 쪽이 있어야 동작).
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        repository.emit(
            listOf(
                subscription(id = 1, amount = 17_000),
                subscription(id = 2, amount = 14_900),
            ),
        )

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        state as HomeUiState.Success
        assertEquals(2, state.subscriptions.size)
        assertEquals(31_900L, state.totalAmount)
    }

    @Test
    fun `구독이 없으면 빈 목록과 합계 0원을 보여준다`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        repository.emit(emptyList())

        assertEquals(HomeUiState.Success(subscriptions = emptyList(), totalAmount = 0), viewModel.uiState.value)
    }

    private fun subscription(id: Long, amount: Long) = Subscription(
        id = id,
        name = "구독 $id",
        amount = amount,
        billingCycle = BillingCycle.MONTHLY,
        firstBillingDate = LocalDate.of(2026, 1, 1),
    )
}

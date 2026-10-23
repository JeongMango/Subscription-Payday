package com.jeongmango.subday.feature.home

import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSubscriptionRepository()

    // 테스트에서 "오늘"은 항상 2026년 10월 21일입니다. 실행하는 날과 상관없이 결과가 같습니다.
    private val zone = ZoneId.of("Asia/Seoul")
    private val today = LocalDate.of(2026, 10, 21)
    private val clock = Clock.fixed(today.atStartOfDay(zone).toInstant(), zone)

    private lateinit var viewModel: HomeViewModel

    // ViewModel은 MainDispatcherRule이 메인 스레드를 바꿔 끼운 "뒤에" 만들어야 합니다.
    // 필드에서 바로 만들면 규칙이 적용되기 전에 생성되어 viewModelScope가 동작하지 않습니다.
    @Before
    fun setUp() {
        viewModel = HomeViewModel(repository, clock)
    }

    @Test
    fun `데이터를 받기 전에는 Loading 상태다`() {
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `다음 결제일이 가까운 순으로 정렬한다`() = runTest {
        startCollecting()

        repository.emit(
            listOf(
                subscription(id = 1, first = LocalDate.of(2026, 1, 30)), // 다음 결제 10/30
                subscription(id = 2, first = LocalDate.of(2026, 1, 22)), // 다음 결제 10/22
                subscription(id = 3, first = LocalDate.of(2026, 1, 25)), // 다음 결제 10/25
            ),
        )

        assertEquals(listOf(2L, 3L, 1L), successState().items.map { it.subscription.id })
    }

    @Test
    fun `다음 결제일까지 남은 날을 계산한다`() = runTest {
        startCollecting()

        repository.emit(
            listOf(
                subscription(id = 1, first = LocalDate.of(2026, 1, 21)), // 오늘 결제
                subscription(id = 2, first = LocalDate.of(2026, 1, 25)), // 4일 남음
            ),
        )

        val items = successState().items
        assertEquals(LocalDate.of(2026, 10, 21), items[0].nextBillingDate)
        assertEquals(0L, items[0].daysUntilBilling)
        assertEquals(4L, items[1].daysUntilBilling)
    }

    @Test
    fun `이번 달 지출은 이번 달에 결제일이 있는 것만 합산한다`() = runTest {
        startCollecting()

        repository.emit(
            listOf(
                subscription(id = 1, amount = 17_000, first = LocalDate.of(2026, 1, 15)),                       // 10/15 ✅
                subscription(id = 2, amount = 39_000, cycle = BillingCycle.YEARLY, first = LocalDate.of(2025, 11, 30)), // 11/30 ❌
            ),
        )

        val state = successState()
        assertEquals(YearMonth.of(2026, 10), state.month)
        assertEquals(17_000L, state.monthlySpending)
    }

    @Test
    fun `등록된 구독 개수를 센다`() = runTest {
        startCollecting()

        repository.emit(
            listOf(
                subscription(id = 1),
                subscription(id = 2),
                subscription(id = 3),
            ),
        )

        assertEquals(3, successState().subscriptionCount)
    }

    @Test
    fun `구독이 없으면 빈 목록과 지출 0원을 보여준다`() = runTest {
        startCollecting()

        repository.emit(emptyList())

        assertEquals(
            HomeUiState.Success(
                items = emptyList(),
                month = YearMonth.of(2026, 10),
                monthlySpending = 0,
                subscriptionCount = 0,
            ),
            viewModel.uiState.value,
        )
    }

    // 화면이 상태를 지켜보고 있는 상황을 흉내 냅니다 (WhileSubscribed는 지켜보는 쪽이 있어야 동작).
    private fun TestScope.startCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    }

    private fun successState() = viewModel.uiState.value as HomeUiState.Success

    private fun subscription(
        id: Long,
        amount: Long = 10_000,
        cycle: BillingCycle = BillingCycle.MONTHLY,
        first: LocalDate = LocalDate.of(2026, 1, 1),
    ) = Subscription(
        id = id,
        name = "구독 $id",
        amount = amount,
        billingCycle = cycle,
        firstBillingDate = first,
    )
}

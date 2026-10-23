package com.jeongmango.subday.feature.edit

import androidx.lifecycle.SavedStateHandle
import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

/**
 * 등록·수정 화면의 규칙. 화면 없이 ViewModel 함수만 호출해서 확인합니다.
 * 사용자가 입력하는 것 = onXxxChange() 호출, 저장 버튼 = onSaveClick() 호출.
 */
class EditViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val zone = ZoneId.of("Asia/Seoul")
    private val today = LocalDate.of(2026, 10, 23)
    private val clock = Clock.fixed(today.atStartOfDay(zone).toInstant(), zone)

    private val netflix = Subscription(
        id = 7,
        name = "넷플릭스",
        amount = 17_000,
        billingCycle = BillingCycle.MONTHLY,
        firstBillingDate = LocalDate.of(2026, 1, 15),
    )

    // ── 새로 등록 ───────────────────────────────────────────

    @Test
    fun `새로 등록할 때 첫 결제일 기본값은 오늘`() {
        val viewModel = newViewModel()

        val state = viewModel.uiState.value
        assertFalse(state.isEditMode)
        assertEquals(today, state.firstBillingDate)
    }

    @Test
    fun `이름이 비어 있으면 저장하지 않고 에러를 보여준다`() {
        val repository = FakeSubscriptionRepository()
        val viewModel = newViewModel(repository = repository)

        viewModel.onAmountChange("17000")
        viewModel.onSaveClick()

        assertEquals("이름을 입력하세요", viewModel.uiState.value.nameError)
        assertTrue(repository.saved.isEmpty())
        assertFalse(viewModel.uiState.value.isDone)
    }

    @Test
    fun `금액이 비었거나 0원이면 저장하지 않고 에러를 보여준다`() {
        val repository = FakeSubscriptionRepository()
        val viewModel = newViewModel(repository = repository)

        viewModel.onNameChange("넷플릭스")
        viewModel.onAmountChange("0")
        viewModel.onSaveClick()

        assertEquals("금액을 입력하세요", viewModel.uiState.value.amountError)
        assertTrue(repository.saved.isEmpty())
    }

    @Test
    fun `금액 입력은 숫자만 남긴다`() {
        val viewModel = newViewModel()

        viewModel.onAmountChange("17,000원")

        assertEquals("17000", viewModel.uiState.value.amountText)
    }

    @Test
    fun `다시 입력하면 에러가 사라진다`() {
        val viewModel = newViewModel()
        viewModel.onSaveClick()

        viewModel.onNameChange("넷")

        assertNull(viewModel.uiState.value.nameError)
    }

    @Test
    fun `올바르게 입력하고 저장하면 저장되고 화면을 닫는다`() {
        val repository = FakeSubscriptionRepository()
        val viewModel = newViewModel(repository = repository)

        viewModel.onNameChange("  유튜브 프리미엄 ")
        viewModel.onAmountChange("14900")
        viewModel.onBillingCycleChange(BillingCycle.YEARLY)
        viewModel.onFirstBillingDateChange(LocalDate.of(2026, 3, 2))
        viewModel.onSaveClick()

        val saved = repository.saved.values.single()
        assertEquals("유튜브 프리미엄", saved.name) // 앞뒤 공백 제거
        assertEquals(14_900L, saved.amount)
        assertEquals(BillingCycle.YEARLY, saved.billingCycle)
        assertEquals(LocalDate.of(2026, 3, 2), saved.firstBillingDate)
        assertTrue(viewModel.uiState.value.isDone)
    }

    // ── 수정 ───────────────────────────────────────────────

    @Test
    fun `수정할 때는 기존 값을 불러온다`() {
        val viewModel = newViewModel(subscriptionId = 7, repository = FakeSubscriptionRepository(listOf(netflix)))

        val state = viewModel.uiState.value
        assertTrue(state.isEditMode)
        assertFalse(state.isLoading)
        assertEquals("넷플릭스", state.name)
        assertEquals("17000", state.amountText)
        assertEquals(LocalDate.of(2026, 1, 15), state.firstBillingDate)
    }

    @Test
    fun `수정 후 저장하면 같은 id로 덮어쓴다`() {
        val repository = FakeSubscriptionRepository(listOf(netflix))
        val viewModel = newViewModel(subscriptionId = 7, repository = repository)

        viewModel.onAmountChange("13500")
        viewModel.onSaveClick()

        assertEquals(1, repository.saved.size)
        assertEquals(13_500L, repository.saved.getValue(7).amount)
    }

    @Test
    fun `삭제하면 저장소에서 빠지고 화면을 닫는다`() {
        val repository = FakeSubscriptionRepository(listOf(netflix))
        val viewModel = newViewModel(subscriptionId = 7, repository = repository)

        viewModel.onDeleteClick()

        assertTrue(repository.saved.isEmpty())
        assertTrue(viewModel.uiState.value.isDone)
    }

    // ── 도우미 ─────────────────────────────────────────────

    /**
     * 화면 이동 때 넘어오는 값(EditRoute.subscriptionId)을 SavedStateHandle로 흉내 냅니다.
     * ViewModel은 MainDispatcherRule이 적용된 뒤(테스트 함수 안)에서 만들어야 합니다.
     */
    private fun newViewModel(
        subscriptionId: Long = 0L,
        repository: FakeSubscriptionRepository = FakeSubscriptionRepository(),
    ) = EditViewModel(
        savedStateHandle = SavedStateHandle(mapOf("subscriptionId" to subscriptionId)),
        repository = repository,
        clock = clock,
    )
}

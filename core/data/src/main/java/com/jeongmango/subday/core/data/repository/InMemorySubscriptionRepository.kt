package com.jeongmango.subday.core.data.repository

import com.jeongmango.subday.core.domain.repository.SubscriptionRepository
import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import javax.inject.Inject

/**
 * 메모리에 샘플 구독을 들고 있는 임시 구현입니다. 앱을 끄면 사라집니다.
 * M2에서 Room을 쓰는 구현으로 교체합니다. 교체해도 ViewModel과 화면은 바꿀 필요가 없습니다.
 */
class InMemorySubscriptionRepository @Inject constructor() : SubscriptionRepository {

    private val subscriptions = MutableStateFlow(
        listOf(
            Subscription(1, "넷플릭스", 17_000, BillingCycle.MONTHLY, LocalDate.of(2026, 1, 15)),
            Subscription(2, "유튜브 프리미엄", 14_900, BillingCycle.MONTHLY, LocalDate.of(2026, 3, 2)),
            Subscription(3, "iCloud+", 39_000, BillingCycle.YEARLY, LocalDate.of(2025, 11, 30)),
        ),
    )

    override fun observeSubscriptions(): Flow<List<Subscription>> = subscriptions
}

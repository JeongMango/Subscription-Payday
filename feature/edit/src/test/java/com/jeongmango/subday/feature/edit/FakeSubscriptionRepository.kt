package com.jeongmango.subday.feature.edit

import com.jeongmango.subday.core.domain.repository.SubscriptionRepository
import com.jeongmango.subday.core.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * 테스트용 가짜 Repository. 메모리의 Map에 저장해서, 테스트가 무엇이 저장됐는지 바로 확인할 수 있습니다.
 */
class FakeSubscriptionRepository(initial: List<Subscription> = emptyList()) : SubscriptionRepository {

    private val store = MutableStateFlow(initial.associateBy { it.id })
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1

    /** 테스트에서 저장 결과를 확인할 때 씁니다. */
    val saved: Map<Long, Subscription> get() = store.value

    override fun observeSubscriptions(): Flow<List<Subscription>> = error("등록 화면 테스트에서는 쓰지 않음")

    override suspend fun getSubscription(id: Long): Subscription? = store.value[id]

    override suspend fun saveSubscription(subscription: Subscription): Long {
        val id = if (subscription.id == 0L) nextId++ else subscription.id
        store.update { it + (id to subscription.copy(id = id)) }
        return id
    }

    override suspend fun deleteSubscription(id: Long) = store.update { it - id }
}

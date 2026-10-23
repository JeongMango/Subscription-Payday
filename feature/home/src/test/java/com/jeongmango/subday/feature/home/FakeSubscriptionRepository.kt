package com.jeongmango.subday.feature.home

import com.jeongmango.subday.core.domain.repository.SubscriptionRepository
import com.jeongmango.subday.core.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 테스트용 가짜 Repository. 테스트 코드가 원하는 시점에 원하는 목록을 흘려보낼 수 있습니다.
 * ViewModel이 인터페이스에만 의존하기 때문에 이렇게 바꿔 끼울 수 있습니다.
 * 홈 화면은 목록만 보므로 저장·삭제는 쓰지 않습니다.
 */
class FakeSubscriptionRepository : SubscriptionRepository {

    private val subscriptions = MutableSharedFlow<List<Subscription>>(replay = 1)

    override fun observeSubscriptions(): Flow<List<Subscription>> = subscriptions

    suspend fun emit(value: List<Subscription>) = subscriptions.emit(value)

    override suspend fun getSubscription(id: Long): Subscription? = error("홈 테스트에서는 쓰지 않음")

    override suspend fun saveSubscription(subscription: Subscription): Long = error("홈 테스트에서는 쓰지 않음")

    override suspend fun deleteSubscription(id: Long) = error("홈 테스트에서는 쓰지 않음")
}

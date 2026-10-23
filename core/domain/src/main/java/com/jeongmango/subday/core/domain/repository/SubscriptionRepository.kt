package com.jeongmango.subday.core.domain.repository

import com.jeongmango.subday.core.model.Subscription
import kotlinx.coroutines.flow.Flow

/**
 * 구독 데이터를 다루는 "약속(인터페이스)"입니다.
 * 화면(ViewModel)은 이 약속만 알고, 실제로 데이터가 메모리에 있는지 DB에 있는지는 모릅니다.
 * 구현은 core:data에 있고, 테스트에서는 가짜 구현으로 바꿔 끼웁니다.
 */
interface SubscriptionRepository {
    /** 구독 목록. 데이터가 바뀔 때마다 새 목록이 흘러나옵니다. */
    fun observeSubscriptions(): Flow<List<Subscription>>

    /** 구독 하나. 없으면 null */
    suspend fun getSubscription(id: Long): Subscription?

    /** id가 0이면 새로 추가, 아니면 수정합니다. 저장된 구독의 id를 돌려줍니다. */
    suspend fun saveSubscription(subscription: Subscription): Long

    suspend fun deleteSubscription(id: Long)
}

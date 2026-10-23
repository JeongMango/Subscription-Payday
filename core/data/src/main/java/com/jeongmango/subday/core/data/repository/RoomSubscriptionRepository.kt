package com.jeongmango.subday.core.data.repository

import com.jeongmango.subday.core.data.local.SubscriptionDao
import com.jeongmango.subday.core.data.local.toModel
import com.jeongmango.subday.core.domain.repository.SubscriptionRepository
import com.jeongmango.subday.core.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Room DB에 저장하는 구현입니다. 앱을 꺼도 데이터가 남습니다.
 * DB용 모양(SubscriptionEntity)을 앱 모양(Subscription)으로 바꿔서 내보내므로, 위쪽은 DB를 모릅니다.
 */
internal class RoomSubscriptionRepository @Inject constructor(
    private val dao: SubscriptionDao,
) : SubscriptionRepository {

    override fun observeSubscriptions(): Flow<List<Subscription>> =
        dao.observeAll().map { entities -> entities.map { it.toModel() } }
}

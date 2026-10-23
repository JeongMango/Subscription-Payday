package com.jeongmango.subday.core.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * DB에 보낼 질문(SQL)을 모아 둔 곳입니다. 구현 코드는 빌드할 때 Room이 자동으로 만듭니다.
 */
@Dao
interface SubscriptionDao {

    /** 테이블이 바뀔 때마다 새 목록이 흘러나옵니다. 그래서 저장하면 홈 화면이 자동으로 갱신됩니다. */
    @Query("SELECT * FROM subscriptions")
    fun observeAll(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: Long): SubscriptionEntity?

    /** id가 0이면 새로 추가, 이미 있는 id면 수정합니다. 추가된 행의 id를 돌려줍니다. */
    @Upsert
    suspend fun upsert(subscription: SubscriptionEntity): Long

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteById(id: Long)
}

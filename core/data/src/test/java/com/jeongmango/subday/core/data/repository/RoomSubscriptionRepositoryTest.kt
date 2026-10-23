package com.jeongmango.subday.core.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.jeongmango.subday.core.data.local.SubDayDatabase
import com.jeongmango.subday.core.data.local.SubscriptionDao
import com.jeongmango.subday.core.data.local.SubscriptionEntity
import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

/**
 * 실제 Room DB에 저장하고 다시 읽어서 확인합니다.
 * - Robolectric: 에뮬레이터 없이 PC에서 Android 환경을 흉내 냅니다.
 * - inMemoryDatabaseBuilder: 파일이 아니라 메모리에 DB를 만들어서 테스트가 끝나면 사라집니다.
 */
@RunWith(RobolectricTestRunner::class)
class RoomSubscriptionRepositoryTest {

    private lateinit var database: SubDayDatabase
    private lateinit var dao: SubscriptionDao
    private lateinit var repository: RoomSubscriptionRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SubDayDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.subscriptionDao()
        repository = RoomSubscriptionRepository(dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `처음에는 비어 있다`() = runTest {
        assertTrue(repository.observeSubscriptions().first().isEmpty())
    }

    @Test
    fun `저장한 구독이 그대로 조회된다`() = runTest {
        val id = dao.upsert(entity(name = "넷플릭스", freeTrialEnd = LocalDate.of(2026, 11, 1)))

        val saved = repository.observeSubscriptions().first().single()

        assertEquals(
            Subscription(
                id = id,
                name = "넷플릭스",
                amount = 17_000,
                billingCycle = BillingCycle.MONTHLY,
                firstBillingDate = LocalDate.of(2026, 1, 31),
                freeTrialEndDate = LocalDate.of(2026, 11, 1),
            ),
            saved,
        )
    }

    @Test
    fun `같은 id로 저장하면 수정된다`() = runTest {
        val id = dao.upsert(entity(name = "넷플릭스"))

        dao.upsert(entity(id = id, name = "넷플릭스 프리미엄"))

        val all = repository.observeSubscriptions().first()
        assertEquals(1, all.size)
        assertEquals("넷플릭스 프리미엄", all.single().name)
    }

    @Test
    fun `삭제하면 목록에서 빠진다`() = runTest {
        val keepId = dao.upsert(entity(name = "유튜브"))
        val deleteId = dao.upsert(entity(name = "넷플릭스"))

        dao.deleteById(deleteId)

        assertEquals(listOf(keepId), repository.observeSubscriptions().first().map { it.id })
    }

    private fun entity(id: Long = 0, name: String, freeTrialEnd: LocalDate? = null) = SubscriptionEntity(
        id = id,
        name = name,
        amount = 17_000,
        billingCycle = BillingCycle.MONTHLY,
        firstBillingDate = LocalDate.of(2026, 1, 31),
        freeTrialEndDate = freeTrialEnd,
    )
}

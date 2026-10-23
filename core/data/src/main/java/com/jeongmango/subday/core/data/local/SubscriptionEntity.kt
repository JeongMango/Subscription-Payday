package com.jeongmango.subday.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jeongmango.subday.core.model.BillingCycle
import com.jeongmango.subday.core.model.Subscription
import java.time.LocalDate

/**
 * DB 테이블의 한 줄(행)입니다.
 *
 * 앱 전체가 쓰는 Subscription(core:model)과 일부러 나눠 둡니다.
 * DB 구조가 바뀌어도 Subscription은 그대로 두고 아래 변환 함수만 고치면 됩니다.
 * 이 클래스는 core:data 밖으로 나가지 않습니다.
 */
@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Long,
    val billingCycle: BillingCycle,
    val firstBillingDate: LocalDate,
    val freeTrialEndDate: LocalDate?,
)

fun SubscriptionEntity.toModel() = Subscription(
    id = id,
    name = name,
    amount = amount,
    billingCycle = billingCycle,
    firstBillingDate = firstBillingDate,
    freeTrialEndDate = freeTrialEndDate,
)

fun Subscription.toEntity() = SubscriptionEntity(
    id = id,
    name = name,
    amount = amount,
    billingCycle = billingCycle,
    firstBillingDate = firstBillingDate,
    freeTrialEndDate = freeTrialEndDate,
)

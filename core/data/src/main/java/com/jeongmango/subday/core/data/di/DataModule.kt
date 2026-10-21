package com.jeongmango.subday.core.data.di

import com.jeongmango.subday.core.data.repository.InMemorySubscriptionRepository
import com.jeongmango.subday.core.domain.repository.SubscriptionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt에게 "SubscriptionRepository가 필요하면 InMemorySubscriptionRepository를 넣어 줘"라고 알려 줍니다.
 * M2에서 Room 구현으로 바꿀 때 이 한 줄만 고치면 앱 전체가 새 구현을 씁니다.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(impl: InMemorySubscriptionRepository): SubscriptionRepository
}

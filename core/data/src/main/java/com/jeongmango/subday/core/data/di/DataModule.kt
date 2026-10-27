package com.jeongmango.subday.core.data.di

import com.jeongmango.subday.core.data.repository.DataStoreSettingsRepository
import com.jeongmango.subday.core.data.repository.RoomSubscriptionRepository
import com.jeongmango.subday.core.domain.repository.SettingsRepository
import com.jeongmango.subday.core.domain.repository.SubscriptionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt에게 "SubscriptionRepository가 필요하면 RoomSubscriptionRepository를 넣어 줘"라고 알려 줍니다.
 * M2에서 InMemorySubscriptionRepository → RoomSubscriptionRepository로 이 한 줄만 바꿨고,
 * ViewModel과 화면은 고치지 않았습니다.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(impl: RoomSubscriptionRepository): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: DataStoreSettingsRepository): SettingsRepository
}

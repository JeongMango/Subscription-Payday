package com.jeongmango.subday.core.data.di

import android.content.Context
import androidx.room.Room
import com.jeongmango.subday.core.data.local.SubDayDatabase
import com.jeongmango.subday.core.data.local.SubscriptionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DB는 @Inject constructor로 만들 수 없어서(Room.databaseBuilder로 만들어야 함) 만드는 방법을 직접 알려 줍니다.
 * @Binds: "이 인터페이스엔 이 구현" / @Provides: "이건 이렇게 만들어"
 */
@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SubDayDatabase =
        Room.databaseBuilder(context, SubDayDatabase::class.java, "subday.db").build()

    @Provides
    fun provideSubscriptionDao(database: SubDayDatabase): SubscriptionDao = database.subscriptionDao()
}

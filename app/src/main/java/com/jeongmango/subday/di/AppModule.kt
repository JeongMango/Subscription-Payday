package com.jeongmango.subday.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

/**
 * 앱 전체가 함께 쓰는 객체를 Hilt에 알려 줍니다.
 * Clock: "오늘 날짜"의 출처. 앱에서는 기기 시계를, 테스트에서는 날짜가 고정된 시계를 씁니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()
}

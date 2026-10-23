package com.jeongmango.subday.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * 앱의 DB 전체. 테이블 목록과 버전을 정합니다.
 * 테이블 구조를 바꾸면 version을 올리고 마이그레이션(이전 데이터를 새 구조로 옮기는 방법)을 추가해야 합니다.
 */
@Database(
    entities = [SubscriptionEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class SubDayDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
}

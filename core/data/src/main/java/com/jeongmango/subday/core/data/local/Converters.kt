package com.jeongmango.subday.core.data.local

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * DB(SQLite)는 LocalDate를 모르므로 숫자로 바꿔 저장합니다.
 * epochDay: 1970년 1월 1일부터 지난 날 수. 날짜 순 정렬도 숫자 정렬로 그대로 됩니다.
 */
internal class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? = epochDay?.let(LocalDate::ofEpochDay)
}

package com.jeongmango.subday.core.model

import java.time.LocalTime

/**
 * 결제 알림 설정. 설정 화면에서 바꾸고, 알림을 보낼 때(core:notification) 읽습니다.
 * 기본값은 처음 앱을 설치했을 때 값입니다.
 */
data class NotificationSettings(
    val enabled: Boolean = true,
    /** 결제일 며칠 전에 알릴지. 0이면 결제일 당일 */
    val daysBefore: Int = 1,
    /** 알림을 보낼 시각 */
    val time: LocalTime = LocalTime.of(9, 0),
) {
    companion object {
        /** 설정 화면에서 고를 수 있는 값 */
        val DAYS_BEFORE_OPTIONS = listOf(0, 1, 3, 7)
    }
}

package com.jeongmango.subday

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Hilt의 시작점입니다. 앱이 켜질 때 Hilt가 객체를 만들고 연결할 준비를 합니다.
 * AndroidManifest.xml의 android:name에 등록되어 있습니다.
 */
@HiltAndroidApp
class SubDayApplication : Application()

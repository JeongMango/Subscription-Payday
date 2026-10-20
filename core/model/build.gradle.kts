// core:model: 앱 전체가 함께 쓰는 데이터의 모양 (Subscription, BillingCycle 등)
// 순수 Kotlin 모듈이라 Android 클래스를 쓸 수 없고, 어떤 모듈에도 의존하지 않습니다.
plugins {
    alias(libs.plugins.subday.jvm.library)
}

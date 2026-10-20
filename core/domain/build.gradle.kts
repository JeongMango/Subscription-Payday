// core:domain: 앱의 규칙. 결제일 계산(BillingCalculator), Repository 인터페이스
// 순수 Kotlin 모듈이라 테스트가 빠르고, 화면이나 DB가 바뀌어도 영향을 받지 않습니다.
plugins {
    alias(libs.plugins.subday.jvm.library)
}

dependencies {
    // api: domain을 쓰는 모듈은 model도 자동으로 쓸 수 있습니다.
    api(projects.core.model)
}

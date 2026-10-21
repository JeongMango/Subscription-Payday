# 설계 결정 기록

> 고민한 선택을 "선택 / 이유 / 버린 대안"으로 남깁니다. 면접 답변의 재료입니다.
> 새 결정은 아래에 번호를 이어서 추가합니다.

---

## 1. UI는 Jetpack Compose + Material 3 (2026-10-20)

- **선택:** XML 템플릿을 Compose로 전환
- **이유:** 상태가 바뀌면 화면이 따라 그려지는 방식이라 ViewModel의 상태(UiState)와 잘 맞음. Preview로 상태별 화면을 바로 확인할 수 있음
- **버린 대안:** XML + ViewBinding. 실무 경험은 있지만 상태와 화면을 맞추는 코드를 직접 써야 함

## 2. MVVM + Repository, UseCase 계층은 생략 (2026-10-20)

- **선택:** Screen → ViewModel → Repository(인터페이스) 구조. UseCase는 만들지 않음
- **이유:** 화면과 로직을 나눠야 로직을 테스트할 수 있음. 현재 규모에서는 Repository와 BillingCalculator로 충분하고, UseCase를 넣으면 한 줄짜리 전달 클래스만 늘어남
- **버린 대안:** Clean Architecture의 UseCase 계층. 여러 Repository를 조합하는 로직이 생기면 그때 추가
- **관련 문서:** [architecture.md](architecture.md)

## 3. 처음부터 멀티 모듈 (2026-10-20)

- **선택:** app, core 5개, feature 3개 모듈로 시작
- **이유:** 계층 경계를 컴파일러가 강제하는 구조를 경험하기 위해. feature끼리, feature와 data가 서로 직접 참조하면 컴파일 에러가 남
- **버린 대안:** 단일 모듈 + 패키지로 계층 분리 (기획서의 원래 계획, 이전 회사에서 쓰던 방식). 작은 앱과 소규모 팀에서는 이쪽이 더 효율적일 수 있음
- **비용:** 초기 Gradle 설정이 복잡하고, 화면 추가 시 모듈 설정이 필요함
- **관련 문서:** [build-structure.md](build-structure.md)

## 4. 공통 Gradle 설정은 build-logic 컨벤션 플러그인으로 (2026-10-20)

- **선택:** `build-logic/convention`에 플러그인 6종(application, library, library.compose, feature, jvm.library, hilt)
- **이유:** 모듈 9개가 compileSdk, Compose 설정을 반복하지 않도록. 공통 값을 한곳에서만 바꿈. ③단계에서 feature 양식 한 파일에 Hilt와 Navigation을 추가하자 화면 모듈 3개에 모두 적용됨
- **버린 대안:** 모듈마다 설정 복사 (빠뜨리기 쉬움), 루트의 `subprojects { }` 블록 (Gradle이 권장하지 않음, 모듈마다 다른 설정을 주기 어려움)

## 5. AGP 8.13 / compileSdk 36 유지 (2026-10-20)

- **선택:** AGP 9.x로 올리지 않고 8.13 유지
- **이유:** 개발자 판단으로 나중에 올리기로 함
- **비용:** 각 라이브러리의 최신 버전 상당수가 AGP 9.1, compileSdk 37 이상을 요구해서 몇 달 전 버전을 씀

| 라이브러리 | 최신 | 사용 | 막힌 이유 |
|---|---|---|---|
| core-ktx | 1.19.1 | 1.18.0 | 1.19부터 AGP 9.1 |
| Compose BOM | 2026.09.00 | 2026.06.01 | Compose UI 1.12부터 AGP 9.1 |
| lifecycle | 2.11.0 | 2.10.0 | 2.11 Compose 쪽이 AGP 9.1 |
| navigation-compose | 2.10.2 | 2.9.8 | 2.10부터 AGP 9.1 |
| Hilt | 2.60.1 | 2.58 | 2.59부터 Hilt Gradle 플러그인이 AGP 9 |

- **확인 방법:** 라이브러리 AAR 안의 `META-INF/com/android/build/gradle/aar-metadata.properties`에서 `minCompileSdk`, `minAndroidGradlePluginVersion` 확인
- **AGP를 올릴 때:** 위 표의 라이브러리를 함께 최신으로 올림. CI가 깨지는지 확인

## 6. Kotlin 2.0.21 → 2.2.21 (2026-10-21)

- **선택:** Kotlin을 2.2.21로, KSP를 2.2.21-2.0.5로 올림 (AGP는 그대로)
- **이유:** Hilt 2.58이 Kotlin 2.2 표준 라이브러리를 요구해서 `Module was compiled with an incompatible version of Kotlin` 에러 발생. 앞으로 넣을 Room, WorkManager에서도 같은 문제가 반복될 것으로 판단
- **버린 대안:** Hilt를 더 낮은 버전으로 내리기. 당장은 되지만 라이브러리를 추가할 때마다 같은 문제를 겪음

## 7. 의존성 주입은 Hilt (2026-10-21)

- **선택:** Hilt + KSP
- **이유:** ViewModel이 Repository 인터페이스만 받고, 앱에서는 실제 구현을, 테스트에서는 가짜를 넣기 위해. Android 공식 권장이고 ViewModel 연동(`hiltViewModel()`)이 간단함
- **버린 대안:** 수동 DI (모듈이 늘면 생성 코드가 길어짐), Koin (런타임에 연결해서 연결 실수를 앱 실행 후에 발견)

## 8. 화면 이동은 Type-safe Navigation (2026-10-21)

- **선택:** `@Serializable data object HomeRoute` 같은 클래스 경로
- **이유:** 문자열 경로(`"home"`)는 오타가 실행 중 크래시로 나타나지만, 클래스는 컴파일 에러로 잡힘
- **구조:** 각 feature가 자기 경로와 `NavGraphBuilder.xxxScreen()`을 공개하고, `app`의 `SubDayNavHost`가 화면 사이를 연결. feature끼리는 서로 모름

## 9. Screen과 Content 분리 (2026-10-21)

- **선택:** `HomeScreen`(ViewModel 연결) + `HomeContent`(상태만 받아 그리기)
- **이유:** Content는 ViewModel 없이 상태만 받으므로 Preview와 UI 테스트에서 가짜 상태를 넣기 쉬움

## 10. Room 전에 메모리 Repository로 먼저 연결 (2026-10-21)

- **선택:** `InMemorySubscriptionRepository`에 샘플 3개를 두고 화면까지 연결
- **이유:** MVVM 데이터 흐름을 DB 없이 먼저 확인. M2에서 `DataModule`의 바인딩 한 줄만 바꿔 Room으로 교체하면서 "ViewModel과 화면은 안 바뀐다"를 확인할 수 있음

## 11. CI는 `./gradlew test` (2026-10-20)

- **선택:** `testDebugUnitTest` 대신 `test`
- **이유:** `testDebugUnitTest`는 Android 모듈만 실행해서, 가장 중요한 `core:domain`(순수 Kotlin) 테스트가 빠짐
- **비용:** Android 모듈은 debug와 release 테스트를 둘 다 돌려서 조금 느림

## 12. 금액은 Long (원 단위) (2026-10-21)

- **선택:** `amount: Long`
- **이유:** Double은 소수점 오차가 생김 (0.1 + 0.2 ≠ 0.3). 원화는 소수점이 없음
- **나중에:** 해외 결제(2차 기능)에서 소수점 통화가 필요하면 BigDecimal 검토

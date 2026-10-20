# SubDay 프로젝트 규칙

구독 결제일을 알려 주는 Android 앱. 서버 없이 기기 안에서만 동작합니다 (로그인 없음, 개인정보 수집 없음).
이직용 포트폴리오가 목적이라, 빨리 만드는 것보다 **설계 이유를 설명할 수 있는 것**이 우선입니다.

## 진행 방식

- 개발자는 MVVM, Compose 멀티 모듈, 테스트, CI/CD를 배우는 중입니다. 새 개념은 짧게 설명하고 진행합니다.
- 작업 전에 할 일과 이유를 먼저 말하고, 큰 작업은 단계로 나눕니다.
- **commit과 push는 개발자가 직접 합니다.** Claude는 파일을 만들고 빌드·테스트까지 확인한 뒤, 커밋 메시지를 제안합니다.
- 고민한 설계 선택은 `docs/decisions.md`에 "선택 / 이유 / 버린 대안"으로 기록합니다.

## 모듈 구조

```
app                 조립 담당. MainActivity, Navigation, Hilt 시작점
core:model          데이터 모양 (Subscription 등). 순수 Kotlin
core:domain         앱 규칙, 계산(BillingCalculator), Repository 인터페이스. 순수 Kotlin
core:data           Room, DataStore, Repository 구현
core:designsystem   테마, 여러 화면이 함께 쓰는 Compose 부품
core:notification   알림 예약 (WorkManager)
feature:*           화면 단위 기능 (home, edit, settings)
build-logic         모듈 공통 Gradle 설정 (컨벤션 플러그인)
```

## 코드를 어디에 두나

**한 화면에서만 쓰면 feature, 여러 곳에서 함께 쓰면 core.**

| 만들 것 | 위치 |
|---|---|
| Screen, ViewModel, UiState | 해당 `feature:*` 모듈 (한 세트로) |
| 데이터 모양 | `core:model` |
| 계산, 규칙, Repository 인터페이스 | `core:domain` |
| Repository 구현, DB, 통신 | `core:data` |
| 공통 UI 부품, 테마 | `core:designsystem` |

- 관련된 화면은 같은 feature 모듈에 둡니다 (예: 등록 중 카테고리 선택 → `feature:edit`). 독립된 새 기능만 새 feature 모듈로 만듭니다.
- 새 feature 모듈: `build.gradle.kts`에 `alias(libs.plugins.subday.android.feature)` + `namespace`, `settings.gradle.kts`에 `include`, `app`에 의존성 추가.

## 의존 규칙

- `feature:*`끼리는 서로 참조하지 않습니다. 화면 이동은 `app`의 Navigation이 연결합니다.
- `feature:*`는 `core:data`를 직접 참조하지 않습니다. `core:domain`의 Repository 인터페이스만 씁니다.
- `core:model`, `core:domain`은 Android 클래스를 import하지 않습니다.
- ViewModel은 Repository 인터페이스에만 의존합니다. 테스트에서는 FakeRepository를 넣습니다.
- 현재 날짜는 `Clock`을 주입받아 씁니다. `LocalDate.now()`를 직접 부르지 않습니다.
- 금액은 Double 대신 Long(원 단위) 또는 BigDecimal을 씁니다.

## Gradle과 버전

- 공통 설정(compileSdk, minSdk, Compose 등)은 `build-logic/convention`에서만 바꿉니다. 모듈 파일에 반복해서 적지 않습니다.
- 라이브러리 버전은 `gradle/libs.versions.toml`에서만 관리합니다.
- **AGP 8.13 / compileSdk 36 유지 중.** 라이브러리를 추가하거나 올릴 때 AAR의 `minCompileSdk`, `minAndroidGradlePluginVersion`을 확인합니다. 최신 버전 상당수가 AGP 9.1과 compileSdk 37을 요구합니다.

## 테스트와 확인

- 모든 단위 테스트: `./gradlew test` (CI도 같은 명령)
- 디버그 빌드: `./gradlew assembleDebug`
- 작업을 마치기 전에 두 명령이 모두 통과하는지 확인합니다.
- 결제일, 금액 계산은 반드시 테스트를 먼저 또는 함께 작성합니다.

## 커밋 메시지

`[태그] 제목` + 빈 줄 + 본문. 태그: `[추가]` `[수정]` `[개선]` `[리팩터]` `[삭제]` `[테스트]` `[문서]` `[설정]` `[완료]`
자세한 규칙은 `docs/commit-convention.md`.

## 참고 문서

- `docs/commit-convention.md`: 커밋 규칙
- `docs/architecture.md`: MVVM, Compose, 모듈 구조 설명 (작성 예정)
- `docs/decisions.md`: 설계 결정 기록 (작성 예정)
- `../doc/구독관리앱-사이드프로젝트.md`: 전체 기획서 (저장소 밖, 마일스톤과 결제일 계산 규칙 표)

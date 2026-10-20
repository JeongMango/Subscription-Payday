# 빌드 구조 정리: 단일 모듈 개발자를 위한 안내

> 단일 모듈(app 하나)로만 개발해 본 사람이 SubDay의 멀티 모듈 구조를 이해하기 위한 문서입니다.
> "단일 모듈에서는 이랬는데, 여기서는 어디로 갔나"를 기준으로 설명합니다.

---

## 1. 한 장 요약

```
[단일 모듈]                              [SubDay 멀티 모듈]

app/                                    app/                 조립만 담당
 ├ build.gradle.kts  ← 설정 전부          core/
 └ src/.../                               ├ model/           데이터 모양
    ├ ui/home/                            ├ domain/          규칙, 계산
    ├ ui/edit/                            ├ data/            DB, 저장
    ├ domain/                             ├ designsystem/    테마
    └ data/                               └ notification/    알림
                                        feature/
                                          ├ home/            홈 화면
                                          ├ edit/            등록 화면
                                          └ settings/        설정 화면
                                        build-logic/         공통 설정 양식
```

- 코드의 **역할 구분(UI / Domain / Data)은 똑같습니다.** 담는 그릇만 패키지에서 모듈로 바뀌었습니다.
- 모듈이 여러 개가 되면서 생긴 문제(설정 반복)를 해결하려고 **build-logic**이 추가됐습니다.

---

## 2. 단일 모듈에서 알던 것은 어디로 갔나

| 단일 모듈에서 | SubDay에서 | 바뀐 이유 |
|---|---|---|
| `app/build.gradle.kts`의 `compileSdk`, `minSdk` | `build-logic/.../KotlinAndroid.kt` | 9개 모듈이 같은 값을 써야 해서 한곳으로 모음 |
| `app/build.gradle.kts`의 `buildFeatures { compose = true }` | `build-logic/.../AndroidCompose.kt` | Compose를 쓰는 모듈이 여러 개라서 |
| `app/build.gradle.kts`의 `dependencies` | 각 모듈의 `build.gradle.kts` + build-logic | 모듈마다 필요한 것만 가짐 |
| `app/src/.../ui/home/` 패키지 | `feature/home/` 모듈 | 화면끼리 서로 참조하지 못하게 벽을 세움 |
| `app/src/.../domain/` 패키지 | `core/domain/` 모듈 | Android 없이 테스트하도록 순수 Kotlin으로 분리 |
| `app/src/.../data/` 패키지 | `core/data/` 모듈 | 화면이 DB를 직접 못 부르게 분리 |
| `app/src/.../ui/theme/` 패키지 | `core/designsystem/` 모듈 | 모든 화면 모듈이 함께 쓰므로 |
| `settings.gradle.kts`의 `include(":app")` 한 줄 | `include` 9줄 + `includeBuild("build-logic")` | 모듈이 늘어서 |
| `libs.versions.toml` | 그대로 (항목만 늘어남) | 버전은 여전히 여기서만 관리 |

---

## 3. 빌드 관련 파일 6종

| 파일 | 한 줄 설명 | 단일 모듈에도 있었나 | 언제 고치나 |
|---|---|---|---|
| `settings.gradle.kts` | **어떤 모듈이 있는지** 목록 | 있었음 | 모듈을 추가할 때 |
| `build.gradle.kts` (루트) | 쓸 플러그인을 미리 불러둠 | 있었음 | 거의 안 고침 |
| `gradle/libs.versions.toml` | 라이브러리 **이름표와 버전** 목록 | 있었음 | 라이브러리 추가, 버전 변경 |
| `<모듈>/build.gradle.kts` | **이 모듈이** 무엇을 쓰는지 | app 것만 있었음 | 모듈에 라이브러리 추가할 때 |
| `build-logic/` | 모듈 공통 **설정 양식** | **없었음 (새로 생김)** | 모든 모듈에 공통으로 바꿀 때만 |
| `.github/workflows/ci.yml` | CI 지시서 | 빌드와 무관 | CI 동작을 바꿀 때 |

**새로 배워야 하는 건 사실상 build-logic 하나입니다.** 나머지는 단일 모듈에도 있던 파일이 늘어났을 뿐입니다.

---

## 4. build-logic 세 줄 요약

1. **문제:** 모듈 9개가 `compileSdk`, `minSdk`, Compose 설정을 각자 반복해서 적어야 함
2. **해결:** 반복되는 설정을 **이름 붙은 양식(컨벤션 플러그인)**으로 만들어 둠
3. **사용:** 각 모듈은 양식 이름 한 줄만 적음

```kotlin
// feature/home/build.gradle.kts 전체
plugins {
    alias(libs.plugins.subday.android.feature)   // 양식 불러오기
}
android {
    namespace = "com.jeongmango.subday.feature.home"   // 이 모듈만의 정보
}
```

### 양식 5종

양식은 레고처럼 아래 것을 포함하며 쌓입니다.

```
subday.android.feature          화면 모듈용      = 아래 전부 + 테마 + domain + ViewModel
  └ subday.android.library.compose   Compose 모듈용  = 아래 + Compose
      └ subday.android.library       Android 모듈용  = compileSdk, minSdk, Java 버전, JUnit

subday.android.application      app 전용        = Android 앱 + Compose
subday.jvm.library              순수 Kotlin용   = Kotlin만 (Android 사용 불가)
```

| 모듈 | 쓰는 양식 |
|---|---|
| app | `subday.android.application` |
| feature:home, edit, settings | `subday.android.feature` |
| core:designsystem | `subday.android.library.compose` |
| core:data, core:notification | `subday.android.library` |
| core:model, core:domain | `subday.jvm.library` |

### build-logic 폴더 안

```
build-logic/convention/src/main/kotlin/
├── AndroidApplicationConventionPlugin.kt       양식: app
├── AndroidFeatureConventionPlugin.kt           양식: 화면 모듈
├── AndroidLibraryComposeConventionPlugin.kt    양식: Compose 모듈
├── AndroidLibraryConventionPlugin.kt           양식: Android 모듈
├── JvmLibraryConventionPlugin.kt               양식: 순수 Kotlin
└── com/jeongmango/subday/buildlogic/
    ├── KotlinAndroid.kt        숫자 설정 (COMPILE_SDK, MIN_SDK, TARGET_SDK, Java 버전)
    ├── AndroidCompose.kt       Compose 공통 설정
    └── ProjectExtensions.kt    libs.versions.toml을 읽는 도우미
```

**한 번 만들어 두면 거의 손댈 일이 없습니다.** Android Studio에는 build-logic 생성 메뉴가 없어서, Google 공식 샘플(Now in Android)을 참고해 직접 작성하는 것이 일반적입니다.

---

## 5. 빌드가 실행되는 순서

```
① settings.gradle.kts        "모듈 9개가 있고, 양식은 build-logic에서 가져온다"
        ↓
② build-logic 컴파일          양식 5종을 사용할 수 있게 준비
        ↓
③ 각 모듈의 build.gradle.kts  "나는 feature 양식을 쓴다" → 양식이 펼쳐져 설정 완성
        ↓
④ 작업 실행                   의존 순서대로 컴파일 (model → domain → designsystem → feature → app)
                             바뀌지 않은 모듈은 이전 결과를 재사용 (UP-TO-DATE)
```

---

## 6. 자주 하는 작업

| 하고 싶은 것 | 고칠 파일 | build-logic 수정? |
|---|---|---|
| 기존 화면에 기능 추가 | 해당 `feature/*/src/` 코드만 | ❌ |
| 관련 화면 추가 (등록 중 카테고리 선택) | 기존 feature 모듈에 파일 추가 | ❌ |
| 독립된 새 기능 (통계) | 새 `feature/statistics/` + `settings.gradle.kts` + `app/build.gradle.kts` | ❌ |
| 한 모듈에만 라이브러리 추가 | `libs.versions.toml` + 그 모듈의 `build.gradle.kts` | ❌ |
| **모든** 화면 모듈에 라이브러리 추가 | `libs.versions.toml` + `AndroidFeatureConventionPlugin.kt` | ⭕ |
| 라이브러리 버전 변경 | `libs.versions.toml`만 | ❌ |
| minSdk, compileSdk 변경 | `KotlinAndroid.kt`의 숫자 | ⭕ |

### 새 feature 모듈 만드는 순서

1. `feature/home` 폴더를 복사해서 `feature/statistics`로 이름 변경
2. `build.gradle.kts`의 `namespace`와 Kotlin 파일의 패키지명 수정
3. `settings.gradle.kts`에 `include(":feature:statistics")` 추가
4. `app/build.gradle.kts`에 `implementation(projects.feature.statistics)` 추가

Android Studio의 New Module 메뉴를 쓰면 설정 30줄이 전부 적힌 파일이 생기므로, 내용을 지우고 양식 한 줄로 바꿔야 합니다.

---

## 7. 단일 모듈과 비교

| | 단일 모듈 | 멀티 모듈 |
|---|---|---|
| 처음 세팅 | 쉬움 | 어려움 (build-logic 필요) |
| 화면 추가 | 패키지 + 파일 | 모듈 + Gradle 설정 + 파일 |
| 계층 규칙 | 사람이 지킴 | 컴파일러가 강제 |
| 빌드 속도 | 작은 앱에선 비슷하거나 더 빠름 | 큰 앱에서 유리 (바뀐 모듈만 컴파일, 병렬 빌드) |
| 잘 맞는 곳 | 소규모 팀, 납품형 프로젝트 | 대규모 팀, 장기 운영 서비스 |

**어느 쪽이든 UI / Domain / Data 계층 분리가 핵심입니다.** 단일 모듈에서 계층을 잘 나눠 두면, 나중에 패키지를 모듈로 옮기기만 하면 됩니다.

---

## 8. 면접에서 설명하기

**"멀티 모듈로 왜 했나요?"**
> 작은 앱이라 단일 모듈로도 충분했지만, 계층 경계를 컴파일러가 강제하는 구조를 경험해 보려고 멀티 모듈로 구성했습니다. 화면 모듈끼리, 그리고 화면과 DB 모듈이 서로 직접 참조하지 못하게 해서 의존 방향을 지켰습니다.

**"build-logic은 왜 썼나요?"**
> 모듈이 9개라 compileSdk, Compose 설정 같은 공통 설정이 반복됐습니다. 이를 컨벤션 플러그인으로 묶어서 각 모듈은 한 줄로 설정하고, 공통 값은 한곳에서만 바꾸도록 했습니다.

**"단점은 없었나요?"**
> 초기 설정 비용이 컸고, 화면 하나를 추가할 때도 Gradle 설정이 필요했습니다. 그래서 소규모 팀이나 납품형 프로젝트에서는 단일 모듈에 패키지로 계층을 나누는 편이 더 효율적일 수 있다고 생각합니다. 실제로 이전 회사에서는 그 방식을 썼습니다.

---

## 9. 용어

| 용어 | 뜻 |
|---|---|
| 모듈 | `build.gradle.kts`를 가진 폴더. Gradle이 따로 빌드하는 단위 |
| 계층 | 코드의 역할 구분 (UI / Domain / Data) |
| 의존성 | "이 모듈은 저 모듈을 쓴다"는 관계. `implementation(...)` |
| `implementation` vs `api` | `api`는 나를 쓰는 모듈에게도 그 의존성을 보여 줌. `implementation`은 나만 씀 |
| 컨벤션 플러그인 | build-logic에 만든 설정 양식 |
| 버전 카탈로그 | `libs.versions.toml`. 라이브러리 이름표와 버전 목록 |
| UP-TO-DATE | 입력이 바뀌지 않아 이전 빌드 결과를 재사용했다는 Gradle 표시 |

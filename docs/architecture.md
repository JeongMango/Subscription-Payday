# SubDay 아키텍처: MVVM과 Compose

> MVVM을 처음 접하는 사람을 위한 문서입니다.
> 완성된 코드를 바로 설명하지 않고, **모든 걸 한곳에 짠 코드에서 시작해 문제가 생길 때마다 하나씩 떼어내는** 순서로 설명합니다.
> 빌드 구조(모듈, build-logic)는 [build-structure.md](build-structure.md)를 보세요.

---

## 1. 먼저 알아 둘 것

### 1-1. 지금 꼭 이해할 것 (5가지)

| 개념 | 한 줄 요약 | 이 문서 |
|---|---|---|
| MVVM 데이터 흐름 | Repository → ViewModel → UiState → Screen | 2장 |
| UiState | 화면이 가질 수 있는 상태를 하나로 묶은 것 | 2장 3단계 |
| Screen과 Content 분리 | ViewModel을 아는 쪽과 상태만 받는 쪽 | 2장 4단계 |
| Repository 인터페이스 | ViewModel은 약속만 알고, 구현은 바꿔 끼울 수 있음 | 5장 |
| 코드를 어디에 두나 | 한 화면만 쓰면 feature, 여럿이 쓰면 core | 6장 |

### 1-2. 지금은 "도구구나" 하고 넘어가도 되는 것

| 코드에서 보이는 것 | 정체 | 자세한 설명 |
|---|---|---|
| `@Inject`, `@HiltViewModel`, `hiltViewModel()` | Hilt: 객체를 대신 만들어 넣어 주는 도구 | 부록 A |
| `Flow`, `StateFlow`, `stateIn`, `collectAsStateWithLifecycle` | 값이 바뀌면 자동으로 전달해 주는 도구 | 부록 B |
| `MainDispatcherRule` | ViewModel 테스트에 붙이는 공식 | 부록 C |

이 도구들은 **MVVM 자체가 아니라 MVVM을 편하게 쓰게 해 주는 것**입니다.

---

## 2. MVVM: 한곳에 짠 코드에서 시작하기

### 0단계: 전부 한곳에

화면 코드 안에 데이터, 계산, 그리기가 모두 있습니다. 작은 앱이라면 이걸로도 충분합니다.

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val list = listOf(Subscription(1, "넷플릭스", 17_000, ...))   // 데이터
        val total = list.sumOf { it.amount }                         // 계산
        setContent {                                                 // 그리기
            Text("합계 ${total}원")
            list.forEach { Text(it.name) }
        }
    }
}
```

앱이 커지면 아래 문제가 생깁니다.

### 1단계: "데이터를 DB나 서버에서 가져오게 되면?" → Repository

데이터 출처가 바뀔 때마다 화면 코드를 고쳐야 합니다. **데이터 가져오는 일을 따로 뺍니다.**

```kotlin
class SubscriptionRepository {
    fun getSubscriptions(): List<Subscription> = ...   // 메모리든 DB든 서버든 여기서만 앎
}
```

이제 출처가 바뀌어도 Repository만 고치면 됩니다.

📁 [InMemorySubscriptionRepository.kt](../core/data/src/main/java/com/jeongmango/subday/core/data/repository/InMemorySubscriptionRepository.kt)

### 2단계: "화면을 돌리면 다 날아가고, 테스트도 못 한다" → ViewModel

- Android는 화면을 회전하면 Activity를 새로 만듭니다. 데이터를 다시 불러오고, 입력하던 값도 사라집니다.
- 계산이 Activity 안에 있으면 앱을 실행하지 않고는 "합계가 맞나?"를 확인할 수 없습니다.

**계산과 데이터 보관을 화면 밖으로 뺍니다.**

```kotlin
class HomeViewModel(repository: SubscriptionRepository) : ViewModel() {
    val list = repository.getSubscriptions()
    val total = list.sumOf { it.amount }
}
```

- ViewModel은 화면 회전에도 살아남도록 Android가 관리합니다.
- ViewModel은 Compose를 모르는 일반 Kotlin 클래스라서 앱 없이 테스트할 수 있습니다.

📁 [HomeViewModel.kt](../feature/home/src/main/java/com/jeongmango/subday/feature/home/HomeViewModel.kt), [HomeViewModelTest.kt](../feature/home/src/test/java/com/jeongmango/subday/feature/home/HomeViewModelTest.kt)

### 3단계: "화면에 넘길 값이 계속 늘어난다" → UiState

목록, 합계, 로딩 여부, 에러 메시지가 변수로 흩어지면 "로딩 중인데 목록도 있다" 같은 말이 안 되는 조합이 생깁니다. **화면 상태를 하나로 묶습니다.**

```kotlin
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val subscriptions: List<Subscription>, val totalAmount: Long) : HomeUiState
}
```

화면은 "지금 어떤 상태야?" 하나만 보면 됩니다.

📁 [HomeUiState.kt](../feature/home/src/main/java/com/jeongmango/subday/feature/home/HomeUiState.kt)

### 4단계: 화면은 그리기만 → Screen과 Content

화면에 남은 일은 상태를 받아 그리는 것뿐입니다. SubDay는 이걸 두 함수로 나눕니다.

```kotlin
// Screen: ViewModel에서 상태를 꺼내 Content에 넘기기만 함
@Composable
internal fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), ...) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(uiState = uiState, ...)
}

// Content: 상태만 받아 그림. ViewModel을 모름
@Composable
internal fun HomeContent(uiState: HomeUiState, ...) {
    when (uiState) {
        HomeUiState.Loading -> CircularProgressIndicator()
        is HomeUiState.Success -> SubscriptionList(uiState)
    }
}
```

Content는 상태만 받으니까 **Preview에 가짜 상태를 넣어** 앱 실행 없이 화면을 볼 수 있습니다.

📁 [HomeScreen.kt](../feature/home/src/main/java/com/jeongmango/subday/feature/home/HomeScreen.kt) (맨 아래 Preview 2개)

### 정리: 이게 MVVM

```
0단계                         MVVM

┌───────────────────┐        ┌────────────┐
│ MainActivity      │        │ Repository │  데이터 가져오기   ← Model
│  데이터 가져오기     │        └─────┬──────┘
│  계산              │   →    ┌─────▼──────┐
│  상태 관리          │        │ ViewModel  │  계산, 상태 보관   ← ViewModel
│  그리기            │        └─────┬──────┘
└───────────────────┘        ┌─────▼──────┐
                             │ Screen     │  그리기만         ← View
                             └────────────┘
```

| 역할 | 하는 일 | 나눈 이유 |
|---|---|---|
| **M**odel (Repository) | 데이터 가져오기, 저장하기 | 출처가 바뀌어도 위쪽은 그대로 |
| **V**iew (Screen) | 그리기, 사용자 행동 알리기 | Preview로 바로 확인 |
| **V**iew**M**odel | 계산, 검증, 상태 보관 | 화면 회전에 살아남음, 테스트 가능 |

---

## 3. 데이터는 아래로, 이벤트는 위로

### 조회: 데이터가 아래로

```
Repository ──▶ ViewModel ──▶ Screen
 (가져오기)       (가공)        (그리기)
```

### 저장·전송: 이벤트가 위로, 결과가 다시 아래로

```
Screen ──▶ ViewModel ──▶ Repository ──▶ DB 또는 서버
(버튼 클릭)  (검증, 변환)     (저장, 전송)
                                           │ 결과
Screen ◀── ViewModel ◀── Repository ◀──────┘
(결과 표시)  (상태 변경)
```

```kotlin
// Screen: 누른 것만 알림
Button(onClick = viewModel::onSaveClick) { Text("저장") }

// ViewModel: 검증 → 저장 요청 → 결과로 상태 변경
fun onSaveClick() {
    if (input.name.isBlank()) { /* 에러 상태 */ return }
    viewModelScope.launch {
        _uiState.update { it.copy(isSaving = true) }
        val result = repository.addSubscription(input.toSubscription())
        _uiState.update { if (result.isSuccess) it.copy(isSaved = true) else it.copy(error = "저장 실패") }
    }
}
```

이 방식을 **단방향 데이터 흐름(UDF, Unidirectional Data Flow)**이라고 합니다.

**지킬 것**
- Screen은 Repository를 직접 부르지 않습니다. 항상 ViewModel을 거칩니다.
- 서버 응답 모양(DTO)은 Repository 밖으로 내보내지 않습니다. Repository에서 앱 모델(`Subscription`)로 바꿔서 넘깁니다.

### 다른 화면에 자동으로 반영되는 이유

M3에서 등록 화면이 구독을 저장하면, 홈 화면은 아무 코드 없이 자동으로 갱신됩니다.

```
[등록 화면] 저장 → Repository의 목록이 3개 → 4개로 바뀜
                        │ StateFlow가 "바뀌었어" 알림
[홈 화면]   HomeViewModel이 새 목록을 받아 다시 계산 → 화면 다시 그림
```

두 화면은 서로를 모릅니다. **같은 Repository(앱 전체에 1개)를 바라보고 있을 뿐**입니다. XML 시절의 "결과를 돌려받아서 목록 새로고침" 코드가 필요 없습니다.

---

## 4. 앱이 켜질 때 실행 순서

| 순서 | 파일 | 하는 일 |
|---|---|---|
| (빌드 때) | `build/generated/ksp/` | Hilt가 객체 생성 코드를 자동으로 만들어 둠 |
| 1 | [AndroidManifest.xml](../app/src/main/AndroidManifest.xml) | 시작할 Application과 Activity 지정 |
| 2 | [SubDayApplication.kt](../app/src/main/java/com/jeongmango/subday/SubDayApplication.kt) | Hilt 준비 (`@HiltAndroidApp`) |
| 3 | [MainActivity.kt](../app/src/main/java/com/jeongmango/subday/MainActivity.kt) | `setContent`로 Compose 시작, 테마 적용 |
| 4 | [SubDayNavHost.kt](../app/src/main/java/com/jeongmango/subday/navigation/SubDayNavHost.kt) | 첫 화면 = 홈 (`startDestination = HomeRoute`) |
| 5 | [HomeNavigation.kt](../feature/home/src/main/java/com/jeongmango/subday/feature/home/navigation/HomeNavigation.kt) | HomeRoute 주소에 HomeScreen 연결 |
| 6 | [HomeScreen.kt](../feature/home/src/main/java/com/jeongmango/subday/feature/home/HomeScreen.kt) | `hiltViewModel()` → Hilt가 ViewModel과 Repository를 차례로 만듦 |
| 7 | [HomeViewModel.kt](../feature/home/src/main/java/com/jeongmango/subday/feature/home/HomeViewModel.kt) | 처음엔 `Loading` → 데이터가 오면 `Success` |
| 8 | `HomeContent` | 상태가 바뀌면 다시 그림 (Recomposition) |

---

## 5. 인터페이스로 나눈 이유

```kotlin
// core:domain - 약속
interface SubscriptionRepository {
    fun observeSubscriptions(): Flow<List<Subscription>>
}

// core:data - 실제 구현 (지금은 메모리, M2에서 Room)
class InMemorySubscriptionRepository : SubscriptionRepository { ... }

// feature:home 테스트 - 가짜 구현
class FakeSubscriptionRepository : SubscriptionRepository { ... }
```

ViewModel은 `SubscriptionRepository`(약속)만 압니다. 그래서

- **앱에서는** Hilt가 `InMemorySubscriptionRepository`를 넣고 ([DataModule.kt](../core/data/src/main/java/com/jeongmango/subday/core/data/di/DataModule.kt))
- **테스트에서는** `FakeSubscriptionRepository`를 직접 넣고
- **M2에서는** DataModule 한 줄만 바꿔 Room 구현을 넣습니다. ViewModel과 화면은 그대로입니다.

---

## 6. 코드를 어디에 두나

**한 화면에서만 쓰면 feature, 여러 곳에서 함께 쓰면 core.**

| 만들 것 | 위치 | 예시 |
|---|---|---|
| Screen, ViewModel, UiState | `feature:*` (한 세트로) | HomeScreen, HomeViewModel, HomeUiState |
| 화면 경로와 등록 함수 | `feature:*/navigation/` | HomeRoute, homeScreen() |
| 데이터 모양 | `core:model` | Subscription |
| 계산, 규칙, Repository 인터페이스 | `core:domain` | BillingCalculator (M1), SubscriptionRepository |
| Repository 구현, DB, 통신 | `core:data` | InMemorySubscriptionRepository |
| 공통 UI 부품, 테마 | `core:designsystem` | SubDayTheme |
| 화면 이동 연결 | `app` | SubDayNavHost |

### 새 화면 추가 체크리스트

1. (독립된 기능이면) 새 `feature:*` 모듈 만들기 → [build-structure.md 6장](build-structure.md)
2. `XxxUiState`: 화면이 가질 수 있는 상태 정의
3. `XxxViewModel`: `@HiltViewModel`, Repository 인터페이스 주입, `uiState` 노출
4. `XxxScreen` (ViewModel 연결) + `XxxContent` (그리기) + Preview
5. `navigation/XxxNavigation.kt`: `@Serializable` 경로, `NavGraphBuilder.xxxScreen()`, `NavController.navigateToXxx()`
6. `app`의 `SubDayNavHost`에 등록
7. ViewModel 테스트 (가짜 Repository 사용)

---

## 7. Compose 기본

| XML 방식 | Compose |
|---|---|
| `activity_main.xml` 레이아웃 파일 | `@Composable` 함수 |
| `setContentView(R.layout.x)` | `setContent { ... }` |
| `findViewById` 후 `textView.text = "..."` | 상태를 바꾸면 화면이 **자동으로 다시 그려짐** |
| `RecyclerView` + Adapter | `LazyColumn { items(list) { ... } }` |
| `themes.xml`, `colors.xml` | `SubDayTheme`, `MaterialTheme.colorScheme` |
| 레이아웃 미리보기 | `@Preview` |

핵심은 **"화면을 직접 고치지 않고, 상태를 바꾸면 화면이 따라온다"**입니다. 그래서 MVVM과 잘 맞습니다. ViewModel이 상태를 바꾸면 Compose가 알아서 다시 그립니다.

---

## 부록 A. Hilt (의존성 주입)

**문제:** ViewModel이 Repository를 직접 만들면(`InMemorySubscriptionRepository()`) 테스트 때 가짜로 바꿔 끼울 수 없습니다.

**해결:** "필요한 걸 밖에서 넣어 받는다." 넣어 주는 일은 Hilt가 합니다.

| 표시 | 의미 |
|---|---|
| `@HiltAndroidApp` | 앱 전체에서 Hilt 시작 |
| `@AndroidEntryPoint` | 이 Activity에서 Hilt 사용 |
| `@HiltViewModel` + `@Inject constructor(...)` | 이 ViewModel을 만들 때 괄호 안 재료를 Hilt가 넣어 줌 |
| `@Inject constructor()` | 이 클래스는 Hilt가 만들 수 있음 |
| `@Module` + `@Binds` | "인터페이스를 달라고 하면 이 구현을 줘" 연결표 |
| `@Singleton` | 앱 전체에서 하나만 만듦 |
| `hiltViewModel()` | Compose에서 ViewModel 요청 |

빌드할 때 Hilt가 `HomeViewModel_Factory` 같은 생성 코드를 자동으로 만듭니다 (`build/generated/ksp/`).

## 부록 B. Flow와 StateFlow

| 용어 | 비유 |
|---|---|
| `Flow` | 값이 시간에 따라 계속 흘러나오는 수도관 |
| `StateFlow` | 현재 값을 담고 있고, 바뀌면 지켜보는 쪽에 알려 주는 상자 |
| `.map { }` | 흘러오는 값을 바꾸는 필터 |
| `.stateIn(...)` | Flow를 StateFlow로 바꿈. `initialValue`가 첫 값 (Loading) |
| `WhileSubscribed(5_000)` | 화면이 보고 있을 때만 동작. 화면이 사라져도 5초는 유지 (회전 대비) |
| `collectAsStateWithLifecycle()` | StateFlow를 Compose 상태로 바꿈. 앱이 백그라운드면 멈춤 |

ViewModel마다 같은 패턴을 쓰므로, 처음에는 HomeViewModel을 복사해서 바꿔 쓰면 됩니다.

## 부록 C. ViewModel 테스트 공식

```kotlin
class XxxViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()   // 1. 필수
    private val repository = FakeXxxRepository()
    private lateinit var viewModel: XxxViewModel

    @Before fun setUp() { viewModel = XxxViewModel(repository) }   // 2. 필드에서 만들지 말 것

    @Test fun `무엇을 하면 어떻게 된다`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }  // 3. 화면이 보는 척
        repository.emit(...)                                       // 4. 데이터 넣기
        assertEquals(기대값, viewModel.uiState.value)               // 5. 상태 확인
    }
}
```

**주의:** ViewModel을 필드에서 바로 만들면(`private val viewModel = XxxViewModel(...)`) `MainDispatcherRule`이 적용되기 전에 생성되어 상태가 `Loading`에서 멈춥니다. ③단계에서 실제로 겪은 문제입니다.

# Kotlin Multiplatform Learning Guide for React/React Native Developers

## Table of Contents

1. [Quick Start](#quick-start)
2. [React/RN → KMP Concept Mapping](#reactrn--kmp-concept-mapping)
3. [Project Structure](#project-structure)
4. [Key Concepts](#key-concepts)
5. [Development Workflow](#development-workflow)
6. [Common Patterns](#common-patterns)
7. [Troubleshooting](#troubleshooting)

---

## Quick Start

### Prerequisites

- **Android Studio** (latest version) or **IntelliJ IDEA**
- **Xcode** (for iOS development, macOS only)
- **JDK 11+**
- **Kotlin** (comes with Android Studio)

### First Steps

1. Open the project in Android Studio
2. Sync Gradle files (Android Studio will prompt you)
3. For Android: Run `composeApp` configuration
4. For iOS:
   - Open `iosApp/iosApp.xcodeproj` in Xcode
   - Run the iOS app

---

## React/RN → KMP Concept Mapping

### 1. Components → Composable Functions

**React:**
```jsx
function MyComponent({ name, onPress }) {
  return <Button onClick={onPress}>{name}</Button>;
}
```

**KMP (Compose):**
```kotlin
@Composable
fun MyComponent(
    name: String,
    onPress: () -> Unit
) {
    Button(onClick = onPress) {
        Text(name)
    }
}
```

**Key Differences:**
- `@Composable` annotation instead of `function`
- Type-safe parameters (no PropTypes needed)
- `Unit` = void/undefined
- `() -> Unit` = function type

**Example from this project:**
See `composeApp/src/commonMain/kotlin/com/example/pohonch/App.kt`

---

### 2. State Management

**React:**
```jsx
const [count, setCount] = useState(0);
const [loading, setLoading] = useState(false);

useEffect(() => {
  fetchData().then(setData);
}, []);
```

**KMP:**
```kotlin
var count by remember { mutableStateOf(0) }
var loading by remember { mutableStateOf(false) }

LaunchedEffect(Unit) {
    val data = fetchData()
    // update state
}
```

**Key Differences:**
- `remember` = React's `useState` + memoization
- `mutableStateOf` = state container
- `LaunchedEffect` = `useEffect` for async operations
- `by` = property delegate (syntactic sugar)

**Example from this project:**
```kotlin
// In App.kt
var showContent by remember { mutableStateOf(false) }
```

---

### 3. Props/Props Drilling → Dependency Injection

**React:**
```jsx
// Props drilling
<App>
  <Screen user={user} />
</App>;

function Screen({ user }) {
  return <Component user={user} />;
}
```

**KMP (Future - with Koin):**
```kotlin
// Dependency Injection with Koin (when you add it)
@Composable
fun App() {
    val userRepository: UserRepository = koinInject()
    Screen()
}

@Composable
fun Screen() {
    val userRepository: UserRepository = koinInject()
    Component()
}
```

**Key Differences:**
- No props drilling needed (when using DI)
- Dependencies injected via `koinInject()` (when you add Koin)
- Similar to React Context but more explicit

**Current project:** Uses direct function parameters (like React props)

---

### 4. Hooks → Composable Functions

**React:**
```jsx
function useApi(url) {
  const [data, setData] = useState(null);
  useEffect(() => {
    fetch(url).then(setData);
  }, [url]);
  return data;
}
```

**KMP:**
```kotlin
@Composable
fun rememberApi(url: String): ApiData? {
    var data by remember { mutableStateOf<ApiData?>(null) }

    LaunchedEffect(url) {
        data = fetch(url)
    }

    return data
}
```

**Key Differences:**
- Custom hooks → `remember*` functions
- `LaunchedEffect` replaces `useEffect` for async
- Type-safe return values

---

### 5. Async Operations

**React:**
```jsx
async function sendRemittance(data) {
  setLoading(true);
  try {
    const result = await api.send(data);
    setResult(result);
  } catch (error) {
    setError(error.message);
  } finally {
    setLoading(false);
  }
}
```

**KMP:**
```kotlin
suspend fun sendRemittance(data: RemittanceRequest): Result<RemittanceResponse> {
    return try {
        val result = api.send(data)
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// In ViewModel (when you add it)
fun sendRemittance(request: RemittanceRequest) {
    coroutineScope.launch {
        sendRemittanceUseCase(request)
            .onSuccess { response ->
                uiState = uiState.copy(result = response)
            }
            .onFailure { error ->
                uiState = uiState.copy(error = error.message)
            }
    }
}
```

**Key Differences:**
- `suspend` = async function
- `Result<T>` = explicit success/failure (no try/catch needed)
- Coroutines = Kotlin's concurrency (like Promises but better)
- `launch` = fire-and-forget async
- `async/await` = `async { }` / `.await()`

---

### 6. Navigation

**React Navigation:**
```jsx
<NavigationContainer>
  <Stack.Navigator>
    <Stack.Screen name="Home" component={HomeScreen} />
    <Stack.Screen name="Details" component={DetailsScreen} />
  </Stack.Navigator>
</NavigationContainer>
```

**KMP (Compose Navigation - when you add it):**
```kotlin
NavHost(
    navController = navController,
    startDestination = "home"
) {
    composable("home") { HomeScreen() }
    composable("details") { DetailsScreen() }
}
```

**Key Differences:**
- Type-safe route definitions
- Similar structure to React Navigation
- `NavController` = navigation object

**Current project:** Single screen (no navigation yet)

---

### 7. Styling

**React Native:**
```jsx
<View style={{ padding: 16, backgroundColor: "blue" }}>
  <Text style={{ fontSize: 18, color: "white" }}>Hello</Text>
</View>
```

**KMP Compose:**
```kotlin
Column(
    modifier = Modifier
        .padding(16.dp)
        .background(Color.Blue)
) {
    Text(
        text = "Hello",
        fontSize = 18.sp,
        color = Color.White
    )
}
```

**Key Differences:**
- `Modifier` = style object (chainable)
- `.dp` = density-independent pixels
- `.sp` = scalable pixels (for text)
- More type-safe (no string-based styles)

**Example from this project:**
```kotlin
// In App.kt
Column(
    modifier = Modifier
        .background(MaterialTheme.colorScheme.primaryContainer)
        .safeContentPadding()
        .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    // ...
}
```

---

### 8. Lists

**React Native:**
```jsx
<FlatList
  data={items}
  renderItem={({ item }) => <ItemComponent item={item} />}
  keyExtractor={(item) => item.id}
/>
```

**KMP:**
```kotlin
LazyColumn {
    items(items, key = { it.id }) { item ->
        ItemComponent(item = item)
    }
}
```

**Key Differences:**
- `LazyColumn` = vertical FlatList
- `LazyRow` = horizontal FlatList
- `items()` = automatic key handling
- More concise syntax

---

## Project Structure

```
PohonchUAE/
├── composeApp/                          # Shared KMP module (like shared RN code)
│   └── src/
│       ├── commonMain/                  # Shared code for all platforms
│       │   └── kotlin/com/example/pohonch/
│       │       ├── App.kt               # Main UI composable
│       │       ├── Greeting.kt          # Business logic example
│       │       └── Platform.kt          # Platform interface
│       │
│       ├── androidMain/                 # Android-specific code
│       │   └── kotlin/com/example/pohonch/
│       │       ├── MainActivity.kt      # Android entry point
│       │       └── Platform.android.kt  # Android platform impl
│       │
│       └── iosMain/                     # iOS-specific Kotlin code
│           └── kotlin/com/example/pohonch/
│               ├── MainViewController.kt # iOS Compose bridge
│               └── Platform.ios.kt       # iOS platform impl
│
└── iosApp/                              # iOS-Specific Swift Code
    └── iosApp/
        ├── iOSApp.swift                 # iOS app entry point
        └── ContentView.swift            # SwiftUI bridge
```

**Future Structure (when building features):**
```
composeApp/src/commonMain/kotlin/com/example/pohonch/
├── data/                # Data layer (API, models)
│   ├── model/          # Data models (like TypeScript interfaces)
│   ├── remote/         # API clients (like axios/fetch)
│   └── repository/      # Data repositories (abstraction layer)
├── domain/             # Business logic (like Redux actions/reducers)
│   └── usecase/        # Use cases (single responsibility functions)
├── presentation/       # UI layer (like React components)
│   ├── screen/         # Screen composables
│   └── viewmodel/      # ViewModels (like React state + logic)
└── di/                 # Dependency Injection (like Context providers)
```

---

## Key Concepts

### 1. **Shared Code Philosophy**

- **React Native:** Write once, runs on both platforms
- **KMP:** Write business logic once, write UI separately per platform (or use Compose Multiplatform for shared UI)

### 2. **Architecture Pattern: Clean Architecture (Future)**

```
Presentation (UI) → Domain (Business Logic) → Data (API/Database)
```

- **Presentation:** Composable functions, ViewModels
- **Domain:** Use cases, business rules
- **Data:** API clients, repositories, models

**Current project:** Simple structure (App.kt → Greeting.kt → Platform.kt)

### 3. **Platform-Specific Code (expect/actual)**

**Current example in this project:**

```kotlin
// Platform.kt (commonMain)
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// Platform.android.kt (androidMain)
actual fun getPlatform(): Platform = AndroidPlatform()

// Platform.ios.kt (iosMain)
actual fun getPlatform(): Platform = IOSPlatform()
```

This is how KMP handles platform-specific implementations!

### 4. **Coroutines (Async/Await) - Future**

```kotlin
// Suspend function (like async function)
suspend fun fetchData(): Data {
    delay(1000) // non-blocking delay
    return Data()
}

// Call from coroutine scope
coroutineScope.launch {
    val data = fetchData()
    // use data
}
```

### 5. **Result Type (Error Handling) - Future**

```kotlin
// Instead of try/catch everywhere
fun getData(): Result<Data> {
    return try {
        Result.success(fetchData())
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// Use it
getData()
    .onSuccess { data -> /* handle success */ }
    .onFailure { error -> /* handle error */ }
```

---

## Development Workflow

### Daily Development Cycle

1. **Write Business Logic (Shared Module)**

   ```kotlin
   // composeApp/src/commonMain/kotlin/.../domain/usecase/MyUseCase.kt
   class MyUseCase(private val repository: MyRepository) {
       suspend operator fun invoke(): Result<Data> {
           return repository.getData()
       }
   }
   ```

2. **Create/Update ViewModel (Future)**

   ```kotlin
   // composeApp/src/commonMain/kotlin/.../presentation/viewmodel/MyViewModel.kt
   class MyViewModel(private val useCase: MyUseCase) {
       var uiState by mutableStateOf(MyUiState())

       fun loadData() {
           coroutineScope.launch {
               useCase()
                   .onSuccess { uiState = uiState.copy(data = it) }
           }
       }
   }
   ```

3. **Build UI (Compose)**

   ```kotlin
   // composeApp/src/commonMain/kotlin/.../presentation/screen/MyScreen.kt
   @Composable
   fun MyScreen(viewModel: MyViewModel) {
       val uiState = viewModel.uiState
       // Build UI
   }
   ```

4. **Test on Android**
   - Run `composeApp` configuration in Android Studio
   - Hot reload works (similar to React Native Fast Refresh)

5. **Test on iOS**
   - Open Xcode project
   - Run iOS app

### Code Organization Rules (Future)

1. **Models** (`data/model/`): Data structures (like TypeScript interfaces)
2. **API** (`data/remote/`): Network calls (like API service files)
3. **Repository** (`data/repository/`): Data abstraction (like data access layer)
4. **Use Cases** (`domain/usecase/`): Business logic (one use case = one action)
5. **ViewModels** (`presentation/viewmodel/`): UI state + logic (like React hooks + state)
6. **Screens** (`presentation/screen/`): UI components (like React components)

---

## Common Patterns

### Pattern 1: Loading State (Future)

```kotlin
data class UiState(
    val isLoading: Boolean = false,
    val data: Data? = null,
    val error: String? = null
)

fun loadData() {
    uiState = uiState.copy(isLoading = true, error = null)
    coroutineScope.launch {
        repository.getData()
            .onSuccess {
                uiState = uiState.copy(isLoading = false, data = it)
            }
            .onFailure {
                uiState = uiState.copy(isLoading = false, error = it.message)
            }
    }
}
```

### Pattern 2: Form Handling (Future)

```kotlin
data class FormState(
    val amount: String = "",
    val currency: String = "USD",
    val isValid: Boolean = false
)

fun updateAmount(value: String) {
    uiState = uiState.copy(
        amount = value,
        isValid = value.toDoubleOrNull() != null && value.toDouble() > 0
    )
}
```

### Pattern 3: Navigation (Future)

```kotlin
val navController = rememberNavController()

NavHost(navController, "home") {
    composable("home") { HomeScreen(navController) }
    composable("details/{id}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id")
        DetailsScreen(id)
    }
}

// Navigate
navController.navigate("details/$id")
```

---

## Troubleshooting

### Issue: "Unresolved reference"

- **Solution:** Sync Gradle files (File → Sync Project with Gradle Files)

### Issue: iOS build fails

- **Solution:** 
  - Make sure you're opening `.xcodeproj` (not `.xcworkspace`)
  - Build Kotlin framework first: `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`
  - Then build in Xcode

### Issue: Compose preview not working

- **Solution:** Make sure you're using the correct Compose version and have `@Preview` annotation

### Issue: Coroutine not executing

- **Solution:** Make sure you're calling from a coroutine scope (`launch` or `async`)

### Issue: State not updating

- **Solution:** Make sure you're using `mutableStateOf` and updating via `copy()` or direct assignment

### Issue: Platform-specific code not compiling

- **Solution:** Make sure you have matching `expect` and `actual` declarations in the right source sets

---

## Next Steps

1. **Start with the shared module** - This is where 80% of your code lives
2. **Learn Kotlin basics** - Variables, functions, classes, null safety
3. **Understand Coroutines** - This is crucial for async operations (when you add them)
4. **Practice Compose** - Start with simple screens, build up complexity
5. **Use the existing code as reference** - The App.kt structure is a good starting point

---

## Resources

- **Kotlin Docs:** https://kotlinlang.org/docs/home.html
- **Compose Multiplatform:** https://www.jetbrains.com/lp/compose-multiplatform/
- **Ktor (Networking):** https://ktor.io/docs/client.html
- **Koin (DI):** https://insert-koin.io/
- **Coroutines Guide:** https://kotlinlang.org/docs/coroutines-guide.html

---

## Quick Reference Card

| React/RN      | KMP Equivalent                  |
| ------------- | ------------------------------- |
| `useState`    | `remember { mutableStateOf() }` |
| `useEffect`   | `LaunchedEffect`                |
| `async/await` | `suspend` + `launch`            |
| `props`       | Function parameters             |
| `Context`     | `koinInject()` (when added)     |
| `FlatList`    | `LazyColumn`                    |
| `StyleSheet`  | `Modifier`                      |
| `fetch`       | `HttpClient` (Ktor, when added)  |
| `try/catch`   | `Result<T>` (when added)         |

---

**Remember:** You're learning as you go. Start small, build incrementally, and refer back to this guide often!

# Step-by-Step Development Guide

This document provides a detailed, step-by-step guide for developing features in your Kotlin Multiplatform app.

## 📋 Table of Contents
1. [Setting Up Architecture](#setting-up-architecture)
2. [Adding a New Feature](#adding-a-new-feature)
3. [Adding a New Screen](#adding-a-new-screen)
4. [Integrating a Real API](#integrating-a-real-api)
5. [Adding Form Validation](#adding-form-validation)
6. [Adding Navigation](#adding-navigation)

---

## Setting Up Architecture

### Step 1: Create Directory Structure (10 min)

Create the following directories in `composeApp/src/commonMain/kotlin/com/example/pohonch/`:

```
com/example/pohonch/
├── data/
│   ├── model/
│   ├── remote/
│   └── repository/
├── domain/
│   └── usecase/
├── presentation/
│   ├── screen/
│   └── viewmodel/
└── di/
```

**How to do it:**
- Right-click on `com/example/pohonch/` in Android Studio
- New → Package
- Create each package: `data.model`, `data.remote`, `data.repository`, etc.

---

### Step 2: Add Dependencies (15 min)

**File:** `composeApp/build.gradle.kts`

Add to `commonMain.dependencies`:
```kotlin
// Networking
implementation("io.ktor:ktor-client-core:2.3.5")
implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")

// Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

// Dependency Injection
implementation("io.insert-koin:koin-core:3.5.0")
implementation("io.insert-koin:koin-compose:1.1.0")

// Coroutines (if not already included)
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

// Navigation (when needed)
implementation("androidx.navigation:navigation-compose:2.7.5")
```

Add platform-specific networking:
```kotlin
androidMain.dependencies {
    implementation("io.ktor:ktor-client-android:2.3.5")
}

iosMain.dependencies {
    implementation("io.ktor:ktor-client-darwin:2.3.5")
}
```

---

### Step 3: Set Up Dependency Injection (20 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/di/AppModule.kt`

```kotlin
package com.example.pohonch.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun appModule() = module {
    // Add your dependencies here as you create them
    // Example:
    // singleOf(::RemittanceRepository)
    // singleOf(::SendRemittanceUseCase)
}
```

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/di/InitKoin.kt`

```kotlin
package com.example.pohonch.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule())
}

fun initKoin() = initKoin {}
```

**File:** Update `composeApp/src/androidMain/kotlin/com/example/pohonch/MainActivity.kt`

```kotlin
import com.example.pohonch.di.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        initKoin() // Add this
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
```

**File:** Update `composeApp/src/iosMain/kotlin/com/example/pohonch/MainViewController.kt`

```kotlin
import com.example.pohonch.di.initKoin

fun MainViewController() = ComposeUIViewController { 
    initKoin() // Add this
    App() 
}
```

---

## Adding a New Feature

### Example: Add "Get Balance" Feature

#### Step 1: Define the Data Model (5 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/data/model/RemittanceModels.kt`

```kotlin
package com.example.pohonch.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Balance(
    val accountId: String,
    val amount: Double,
    val currency: String,
    val lastUpdated: Long
)
```

**What you're doing:** Defining the data structure (like TypeScript interface)

---

#### Step 2: Add API Method (10 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/data/remote/RemittanceApi.kt`

```kotlin
package com.example.pohonch.data.remote

import com.example.pohonch.data.model.Balance
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class RemittanceApi(private val client: HttpClient) {
    suspend fun getBalance(accountId: String): Balance {
        // TODO: Replace with actual API call
        // For now, return mock data
        return Balance(
            accountId = accountId,
            amount = 1000.0,
            currency = "USD",
            lastUpdated = System.currentTimeMillis()
        )
        
        // Real implementation would be:
        // return client.get("$baseUrl/accounts/$accountId/balance").body()
    }
}
```

**What you're doing:** Adding API endpoint (like axios call)

---

#### Step 3: Add Repository Method (5 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/data/repository/RemittanceRepository.kt`

```kotlin
package com.example.pohonch.data.repository

import com.example.pohonch.data.model.Balance
import com.example.pohonch.data.remote.RemittanceApi

class RemittanceRepository(
    private val api: RemittanceApi
) {
    suspend fun getBalance(accountId: String): Result<Balance> {
        return try {
            val balance = api.getBalance(accountId)
            Result.success(balance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**What you're doing:** Wrapping API call with error handling

---

#### Step 4: Create Use Case (5 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/domain/usecase/GetBalanceUseCase.kt`

```kotlin
package com.example.pohonch.domain.usecase

import com.example.pohonch.data.model.Balance
import com.example.pohonch.data.repository.RemittanceRepository

class GetBalanceUseCase(
    private val repository: RemittanceRepository
) {
    suspend operator fun invoke(accountId: String): Result<Balance> {
        if (accountId.isBlank()) {
            return Result.failure(Exception("Account ID cannot be empty"))
        }
        return repository.getBalance(accountId)
    }
}
```

**What you're doing:** Adding business logic and validation

---

#### Step 5: Register in DI (2 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/di/AppModule.kt`

```kotlin
fun appModule() = module {
    // HTTP Client
    single {
        io.ktor.client.HttpClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }
    }
    
    // API
    single { com.example.pohonch.data.remote.RemittanceApi(get()) }
    
    // Repository
    singleOf(::RemittanceRepository)
    
    // Use Cases
    singleOf(::GetBalanceUseCase)
}
```

**What you're doing:** Making dependencies available for injection

---

#### Step 6: Add to ViewModel (10 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/presentation/viewmodel/RemittanceViewModel.kt`

```kotlin
package com.example.pohonch.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.pohonch.data.model.Balance
import com.example.pohonch.domain.usecase.GetBalanceUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RemittanceViewModel(
    private val getBalanceUseCase: GetBalanceUseCase
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    data class RemittanceUiState(
        val balance: Balance? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
    
    var uiState by mutableStateOf(RemittanceUiState())
        private set
    
    fun loadBalance(accountId: String) {
        uiState = uiState.copy(isLoading = true, error = null)
        coroutineScope.launch {
            getBalanceUseCase(accountId)
                .onSuccess { balance ->
                    uiState = uiState.copy(
                        isLoading = false,
                        balance = balance
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }
}
```

**What you're doing:** Adding state management (like React useState + useEffect)

---

#### Step 7: Update UI (15 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/App.kt`

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pohonch.di.koinInject
import com.example.pohonch.presentation.viewmodel.RemittanceViewModel

@Composable
fun App() {
    MaterialTheme {
        val viewModel: RemittanceViewModel = koinInject()
        val uiState = viewModel.uiState
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Balance display
            uiState.balance?.let { balance ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Balance: ${balance.amount} ${balance.currency}")
                    }
                }
            }
            
            // Loading indicator
            if (uiState.isLoading) {
                CircularProgressIndicator()
            }
            
            // Error message
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            // Load balance button
            Button(
                onClick = { viewModel.loadBalance("account123") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Load Balance")
            }
        }
    }
}
```

**What you're doing:** Building UI (like React component)

---

#### Step 8: Test (5 min)

1. Run Android app
2. Click "Load Balance" button
3. Verify balance displays
4. Run iOS app
5. Repeat test

**Total Time:** ~60 minutes

---

## Adding a New Screen

### Example: Add "Send Remittance" Screen

#### Step 1: Create Screen File (20 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/presentation/screen/SendRemittanceScreen.kt`

```kotlin
package com.example.pohonch.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SendRemittanceScreen(
    onNavigateBack: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back button
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Form fields
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = recipientName,
            onValueChange = { recipientName = it },
            label = { Text("Recipient Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Submit button
        Button(
            onClick = {
                // TODO: Implement send logic
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send")
        }
    }
}
```

---

#### Step 2: Add Navigation (10 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/presentation/navigation/AppNavigation.kt`

```kotlin
package com.example.pohonch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pohonch.presentation.screen.SendRemittanceScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            // Your home screen
        }
        
        composable("send") {
            SendRemittanceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
```

---

#### Step 3: Update App.kt (2 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/App.kt`

```kotlin
@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavigation()
        }
    }
}
```

**Total Time:** ~40 minutes

---

## Integrating a Real API

### Step 1: Update Base URL (2 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/data/remote/RemittanceApi.kt`

```kotlin
class RemittanceApi(private val client: HttpClient) {
    private val baseUrl = "https://api.yourremittance.com/v1" // Replace with real URL
    
    suspend fun getBalance(accountId: String): Balance {
        return client.get("$baseUrl/accounts/$accountId/balance").body()
    }
}
```

---

### Step 2: Add Authentication (20 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/data/remote/AuthManager.kt`

```kotlin
package com.example.pohonch.data.remote

object AuthManager {
    var token: String? = null
        private set
    
    fun setToken(newToken: String) {
        token = newToken
    }
    
    fun clearToken() {
        token = null
    }
}
```

**Update RemittanceApi:**
```kotlin
suspend fun getBalance(accountId: String): Balance {
    val token = AuthManager.token ?: throw Exception("Not authenticated")
    
    return client.get("$baseUrl/accounts/$accountId/balance") {
        header("Authorization", "Bearer $token")
    }.body()
}
```

**Total Time:** ~25 minutes

---

## Adding Form Validation

### Step 1: Create Validation Use Case (10 min)

**File:** `composeApp/src/commonMain/kotlin/com/example/pohonch/domain/usecase/ValidateRemittanceUseCase.kt`

```kotlin
package com.example.pohonch.domain.usecase

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

class ValidateRemittanceUseCase {
    operator fun invoke(amount: Double, recipientName: String): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (amount <= 0) {
            errors.add("Amount must be greater than 0")
        }
        
        if (amount > 10000) {
            errors.add("Amount cannot exceed $10,000")
        }
        
        if (recipientName.isBlank()) {
            errors.add("Recipient name is required")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors.joinToString(", "))
        }
    }
}
```

---

### Step 2: Use in ViewModel (5 min)

```kotlin
fun sendRemittance(request: RemittanceRequest) {
    val validation = validateRemittanceUseCase(request.amount, request.recipientName)
    
    if (validation is ValidationResult.Error) {
        uiState = uiState.copy(error = validation.message)
        return
    }
    
    // Proceed with sending
    // ...
}
```

---

### Step 3: Show Validation in UI (10 min)

```kotlin
@Composable
fun SendRemittanceScreen(viewModel: RemittanceViewModel) {
    val uiState = viewModel.uiState
    
    // Show validation errors
    uiState.error?.let { error ->
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error
        )
    }
    
    // Disable submit if invalid
    Button(
        onClick = { /* ... */ },
        enabled = uiState.isValid
    ) {
        Text("Send")
    }
}
```

**Total Time:** ~25 minutes

---

## Checklist for Every Feature

- [ ] Data model defined
- [ ] API method added (or mocked)
- [ ] Repository method added
- [ ] Use case created
- [ ] Registered in DI
- [ ] ViewModel updated
- [ ] UI implemented
- [ ] Tested on Android
- [ ] Tested on iOS
- [ ] Error handling added
- [ ] Loading states added

---

## Time Estimates

| Task | Time |
|------|------|
| Architecture setup | 1-2 hours |
| Simple feature (CRUD) | 1-2 hours |
| Complex feature (with validation) | 2-4 hours |
| New screen | 30-60 minutes |
| API integration | 1-2 hours |
| Form validation | 30-60 minutes |
| Navigation | 15-30 minutes |

---

## Tips

1. **Start with architecture** - Set up the structure first
2. **Start with models** - Define data structures first
3. **Mock first** - Get UI working with mock data, then integrate API
4. **Test incrementally** - Test after each step
5. **Use existing code** - Copy patterns from existing features
6. **Ask for help** - Don't struggle alone

---

**Remember:** This is a learning process. Take your time and refer to the guides often!

Start with the architecture setup, then build features incrementally.

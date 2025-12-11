# Team Responsibilities & Development Workflow

## Team Structure

### Role 1: Backend/API Developer

**Responsibilities:**

- Design and implement REST API endpoints
- Define API contracts (request/response models)
- Handle authentication/authorization
- Database design and management
- API documentation

**KMP Tasks:**

- Create `data/remote/` directory and API client files
- Define data models in `data/model/`
- Implement error handling for API responses
- Add authentication headers/tokens

**Files to Work On (Future):**

- `composeApp/src/commonMain/kotlin/com/example/pohonch/data/remote/RemittanceApi.kt`
- `composeApp/src/commonMain/kotlin/com/example/pohonch/data/model/RemittanceModels.kt`

**Current Project:** These directories don't exist yet - you'll create them when adding features.

---

### Role 2: Business Logic Developer

**Responsibilities:**

- Implement use cases (business rules)
- Validation logic
- Data transformation
- Error handling strategies

**KMP Tasks:**

- Create `domain/usecase/` directory and implement use cases
- Add validation in use cases
- Handle business rule violations
- Coordinate between repositories

**Files to Work On (Future):**

- `composeApp/src/commonMain/kotlin/com/example/pohonch/domain/usecase/*.kt`
- Create new use cases as needed

**Example (Future):**

```kotlin
class ValidateRemittanceUseCase {
    operator fun invoke(amount: Double, currency: String): ValidationResult {
        // Business rules:
        // - Minimum amount: $10
        // - Maximum amount: $10,000
        // - Supported currencies: USD, EUR, GBP, etc.
        return when {
            amount < 10.0 -> ValidationResult.Error("Minimum amount is $10")
            amount > 10000.0 -> ValidationResult.Error("Maximum amount is $10,000")
            !isSupportedCurrency(currency) -> ValidationResult.Error("Unsupported currency")
            else -> ValidationResult.Success
        }
    }
}
```

---

### Role 3: UI/UX Developer (Compose)

**Responsibilities:**

- Design and implement UI screens
- User interactions and animations
- Form validation feedback
- Loading states and error displays
- Navigation flow

**KMP Tasks:**

- Create/update screens in `presentation/screen/`
- Implement ViewModels for UI state
- Handle user input and form validation
- Design navigation flow
- Platform-specific UI tweaks (if needed)

**Files to Work On:**

- `composeApp/src/commonMain/kotlin/com/example/pohonch/presentation/screen/*.kt` (create when needed)
- `composeApp/src/commonMain/kotlin/com/example/pohonch/presentation/viewmodel/*.kt` (create when needed)
- Current: `composeApp/src/commonMain/kotlin/com/example/pohonch/App.kt` (main UI)

**Example Workflow:**

1. Design screen layout
2. Create ViewModel for screen state
3. Connect ViewModel to use cases
4. Build UI with Compose
5. Handle user interactions
6. Test on both platforms

---

### Role 4: Platform-Specific Developer (Android/iOS)

**Responsibilities:**

- Platform-specific configurations
- Native integrations (camera, location, etc.)
- App signing and deployment
- Platform-specific UI adjustments
- Performance optimization

**Android Developer Tasks:**

- Update `composeApp/src/androidMain/AndroidManifest.xml` for permissions
- Configure app signing
- Handle Android-specific features
- Optimize for different screen sizes

**iOS Developer Tasks:**

- Update `iosApp/iosApp/Info.plist` for permissions
- Configure app signing and provisioning
- Handle iOS-specific features
- Optimize for different device sizes

**Files to Work On:**

- `composeApp/src/androidMain/AndroidManifest.xml`
- `composeApp/src/androidMain/kotlin/com/example/pohonch/MainActivity.kt`
- `iosApp/iosApp/Info.plist`
- `iosApp/iosApp/ContentView.swift`
- `iosApp/iosApp/iOSApp.swift`

---

### Role 5: QA/Testing Developer

**Responsibilities:**

- Write unit tests for business logic
- Integration tests for API calls
- UI tests for critical flows
- Manual testing on both platforms
- Bug reporting and verification

**KMP Tasks:**

- Write tests in `composeApp/src/commonTest/`
- Test use cases, repositories, ViewModels (when created)
- Test API integration (when added)
- Manual testing on Android and iOS

**Files to Work On:**

- `composeApp/src/commonTest/kotlin/com/example/pohonch/domain/usecase/*Test.kt` (create when needed)
- `composeApp/src/commonTest/kotlin/com/example/pohonch/data/repository/*Test.kt` (create when needed)
- Current: `composeApp/src/commonTest/kotlin/com/example/pohonch/ComposeAppCommonTest.kt`

---

## Development Workflow

### Sprint Planning

1. **Break down features** into tasks
2. **Assign responsibilities** based on roles
3. **Define API contracts** first (backend + frontend agree)
4. **Create tickets** with clear acceptance criteria

### Daily Development Flow

#### Step 1: Project Setup (Week 1)

**Who:** All Team Members
**Tasks:**

- Set up development environment
- Understand project structure
- Review existing code
- Set up architecture layers (data/domain/presentation)

**Deliverable:**

- Working development environment
- Understanding of project structure
- Architecture foundation ready

---

#### Step 2: API Contract Definition (When Adding Features)

**Who:** Backend Developer + Business Logic Developer
**Tasks:**

- Define request/response models
- Agree on endpoints and data structure
- Document API contract

**Deliverable:**

- Updated data models in `data/model/`
- API documentation (Swagger/Postman)

---

#### Step 3: Implement Business Logic (When Adding Features)

**Who:** Business Logic Developer
**Tasks:**

- Implement use cases
- Add validation logic
- Handle error cases

**Deliverable:**

- Working use cases with tests

**Example (Future):**

```kotlin
// Basic implementation
class SendRemittanceUseCase {
    suspend operator fun invoke(request: RemittanceRequest): Result<RemittanceResponse> {
        return repository.sendRemittance(request)
    }
}

// With validation
class SendRemittanceUseCase {
    suspend operator fun invoke(request: RemittanceRequest): Result<RemittanceResponse> {
        // Validate
        validateRequest(request)?.let { return Result.failure(it) }

        // Check balance
        val balance = repository.getBalance()
        if (balance < request.amount) {
            return Result.failure(Exception("Insufficient balance"))
        }

        // Send
        return repository.sendRemittance(request)
    }
}
```

---

#### Step 4: Implement API Layer (When Adding Features)

**Who:** Backend Developer
**Tasks:**

- Implement actual API calls
- Handle authentication
- Error handling and retries

**Deliverable:**

- Working API integration
- Error handling

**Example (Future):**

```kotlin
// Replace mock with real API
suspend fun sendRemittance(request: RemittanceRequest): RemittanceResponse {
    return client.post("$baseUrl/remittances") {
        contentType(ContentType.Application.Json)
        setBody(request)
        header("Authorization", "Bearer $token")
    }.body()
}
```

---

#### Step 5: Build UI (When Adding Features)

**Who:** UI/UX Developer
**Tasks:**

- Design screen layout
- Implement ViewModel
- Build Compose UI
- Handle user interactions

**Deliverable:**

- Working UI screens
- User interactions

**Example Workflow (Future):**

```kotlin
// Create ViewModel
class SendRemittanceViewModel(
    private val sendRemittanceUseCase: SendRemittanceUseCase
) {
    var uiState by mutableStateOf(SendRemittanceUiState())

    fun sendRemittance(request: RemittanceRequest) {
        // Implementation
    }
}

// Build UI
@Composable
fun SendRemittanceScreen(viewModel: SendRemittanceViewModel) {
    // UI implementation
}
```

**Current Project:** Start by modifying `App.kt` to add new UI features.

---

#### Step 6: Platform-Specific Work (When Needed)

**Who:** Platform Developers
**Tasks:**

- Configure app settings
- Add permissions
- Test on devices
- Optimize performance

---

#### Step 7: Testing & QA (Ongoing)

**Who:** QA Developer + All Team
**Tasks:**

- Write automated tests
- Manual testing
- Bug fixes
- Performance testing

---

## Communication Protocol

### Daily Standup (15 min)

1. What did you complete yesterday?
2. What are you working on today?
3. Any blockers?

### API Contract Changes

- **Rule:** Any API contract change must be communicated immediately
- **Process:** Update data models → Notify team → Update API docs

### Code Review Process

1. Create feature branch: `feature/send-remittance`
2. Implement feature
3. Create pull request
4. At least 1 reviewer required
5. Merge after approval

### Bug Reporting

- Use GitHub Issues (or your issue tracker)
- Include: Platform (Android/iOS), Steps to reproduce, Expected vs Actual
- Assign to appropriate developer based on bug type

---

## File Ownership Guide

### Shared Module (composeApp/src/commonMain/)

- **Models** (`data/model/`): Backend + Business Logic (create when needed)
- **API** (`data/remote/`): Backend Developer (create when needed)
- **Repository** (`data/repository/`): Business Logic Developer (create when needed)
- **Use Cases** (`domain/usecase/`): Business Logic Developer (create when needed)
- **ViewModels** (`presentation/viewmodel/`): UI Developer (create when needed)
- **Screens** (`presentation/screen/`): UI Developer (create when needed)
- **DI** (`di/`): Backend/Business Logic Developer (create when needed)
- **Current Files:**
  - `App.kt`: UI Developer
  - `Greeting.kt`: Business Logic Developer
  - `Platform.kt`: Platform Developers

### Platform-Specific

- **Android:** `composeApp/src/androidMain/` - Android Developer
- **iOS:** `composeApp/src/iosMain/` and `iosApp/` - iOS Developer

---

## MVP Feature Checklist

### Phase 1: Foundation (Week 1)

- [x] Project setup and configuration
- [ ] Architecture layers setup (data/domain/presentation)
- [ ] Dependency injection setup (Koin)
- [ ] Navigation setup
- [ ] API integration setup
- [ ] Authentication flow (if needed)

### Phase 2: Core Features (Week 2-3)

- [ ] Send remittance screen
- [ ] Exchange rate display
- [ ] Transaction history
- [ ] Form validation

### Phase 3: Polish (Week 4)

- [ ] Error handling
- [ ] Loading states
- [ ] UI/UX improvements
- [ ] Testing

---

## Quick Decision Guide

### "Where should this code go?"

**Business Logic?** → `domain/usecase/` (create when needed)
**API Call?** → `data/remote/` (create when needed)
**Data Model?** → `data/model/` (create when needed)
**UI Component?** → `presentation/screen/` (create when needed) or modify `App.kt` for now
**UI State Management?** → `presentation/viewmodel/` (create when needed)
**Platform-Specific?** → `composeApp/src/androidMain/` or `iosMain/`

### "Who should work on this?"

**API/Backend related?** → Backend Developer
**Business rules/validation?** → Business Logic Developer
**UI/Screens?** → UI Developer
**Platform config?** → Platform Developer
**Tests?** → QA Developer

---

## Current Project State

**What Exists:**

- Basic KMP project structure
- `App.kt` - Main UI composable
- `Greeting.kt` - Example business logic
- `Platform.kt` - Platform detection example
- Android and iOS entry points

**What Needs to Be Created:**

- Architecture layers (data/domain/presentation)
- Dependency injection setup
- Networking setup
- Feature-specific code

---

## Emergency Contacts

- **Build Issues:** Check `LEARNING_GUIDE.md` Troubleshooting section
- **API Issues:** Backend Developer
- **Architecture Questions:** Tech Lead / Senior Developer
- **Platform-Specific Issues:** Platform Developer

---

**Remember:** This is a learning-as-you-go project. Don't hesitate to ask questions, and help each other learn!

The current project is a starter template. Use this guide as you build out features and create the architecture layers.

# Quick Start Guide

## 🚀 Get Up and Running in 10 Minutes

### Step 1: Prerequisites Check (2 min)

**Required:**

- [ ] Android Studio installed (latest version) or IntelliJ IDEA
- [ ] JDK 11+ installed
- [ ] Xcode installed (macOS only, for iOS)

**Check installations:**

```bash
# Check Java
java -version  # Should show 11 or higher

# Check CocoaPods (iOS, if needed)
pod --version  # Should show version number
```

---

### Step 2: Open Project (1 min)

1. Open Android Studio
2. File → Open → Select `PohonchUAE` folder
3. Wait for Gradle sync (may take 2-5 minutes first time)

**If Gradle sync fails:**

- Check internet connection
- File → Invalidate Caches / Restart
- Try again

---

### Step 3: Run Android App (2 min)

1. In Android Studio, select `composeApp` from the run configuration dropdown (top toolbar)
2. Click the green Run button (or press Shift+F10 / Cmd+R)
3. Select an emulator or connected device
4. Wait for app to build and launch

**Expected Result:** App launches showing a button "Click me!" and when clicked, displays "Compose: Hello, Android [version]!"

**Alternative (Terminal):**

```bash
./gradlew :composeApp:assembleDebug
```

---

### Step 4: Run iOS App (5 min, macOS only)

1. Open Terminal
2. Navigate to project:
   ```bash
   cd /path/to/PohonchUAE
   ```
3. Open the iOS project in Xcode:
   ```bash
   open iosApp/iosApp.xcodeproj
   ```
4. In Xcode:
   - Select a simulator (e.g., iPhone 15)
   - Click Run button (or press Cmd+R)
   - Wait for build and launch

**Expected Result:** App launches showing a button "Click me!" and when clicked, displays "Compose: Hello, iOS [version]!"

**Note:** This project uses Compose Multiplatform, so you don't need to run `pod install` unless you add native iOS dependencies.

---

## 🎯 Your First Code Change

Let's make a simple change to verify everything works:

### Change the Button Text

1. Open: `composeApp/src/commonMain/kotlin/com/example/pohonch/App.kt`
2. Find line 35 with `Text("Click me!")`
3. Change to: `Text("Hello from KMP!")`
4. Save (Ctrl+S / Cmd+S)
5. Run app again - you should see the new button text!

### Change the Greeting Message

1. Open: `composeApp/src/commonMain/kotlin/com/example/pohonch/Greeting.kt`
2. Find line 7 with the return statement
3. Change to: `return "Welcome to ${platform.name}!"`
4. Save and run - you should see the new greeting!

---

## 📚 Next Steps

1. **Read [LEARNING_GUIDE.md](./LEARNING_GUIDE.md)** - Understand React/RN → KMP concepts
2. **Read [RESPONSIBILITIES.md](./RESPONSIBILITIES.md)** - Understand your role and workflow
3. **Explore the codebase:**
   - Start with `App.kt` (UI)
   - Check `Greeting.kt` (Business logic example)
   - Look at `Platform.kt` (Platform detection)
   - Review `MainActivity.kt` (Android entry point)
   - Review `MainViewController.kt` (iOS entry point)

---

## 🐛 Common Issues

### "Gradle sync failed"

- **Solution:** Check internet, invalidate caches (File → Invalidate Caches / Restart), try again

### "Unresolved reference" errors

- **Solution:** Sync Gradle files (File → Sync Project with Gradle Files)

### iOS: Build fails with framework errors

- **Solution:**
  - Make sure you're opening `iosApp.xcodeproj` (not `.xcworkspace`)
  - Clean build folder in Xcode (Product → Clean Build Folder)
  - Try building again

### iOS: "No such module 'ComposeApp'"

- **Solution:**
  - Build the Kotlin framework first from Android Studio
  - Or run: `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`
  - Then build in Xcode

### Android: App crashes on launch

- **Solution:**
  - Check AndroidManifest.xml permissions
  - Make sure minSdk is compatible with your device/emulator
  - Check logcat for specific errors

---

## ✅ Verification Checklist

- [ ] Android app runs successfully
- [ ] iOS app runs successfully (macOS)
- [ ] Can make code changes and see them reflected
- [ ] No build errors in Android Studio
- [ ] Understand where to add new features

---

## 🎓 Learning Path

**Day 1:**

- [ ] Run both apps
- [ ] Read LEARNING_GUIDE.md sections 1-3
- [ ] Make a small UI change

**Day 2:**

- [ ] Read LEARNING_GUIDE.md sections 4-6
- [ ] Understand the project structure
- [ ] Review existing code (App.kt, Greeting.kt, Platform.kt)

**Day 3:**

- [ ] Read RESPONSIBILITIES.md
- [ ] Understand your role
- [ ] Start planning your first feature

---

## 💡 Tips

1. **Start Small:** Don't try to understand everything at once
2. **Use the Guides:** Refer to LEARNING_GUIDE.md frequently
3. **Ask Questions:** Better to ask than struggle
4. **Test Often:** Run the app after each change
5. **Read Error Messages:** They're usually helpful
6. **Use Android Studio's Preview:** Right-click on `@Preview` functions to see UI previews

---

## 🔍 Understanding the Current Code

### App.kt

- Main UI composable
- Uses Material3 theme
- Has a button that toggles content visibility
- Shows an example of state management with `remember { mutableStateOf() }`

### Greeting.kt

- Example business logic class
- Uses platform detection to customize greeting
- Demonstrates how shared code can use platform-specific implementations

### Platform.kt

- Defines platform interface using `expect` keyword
- Platform-specific implementations in `Platform.android.kt` and `Platform.ios.kt`
- Shows KMP's platform-specific code pattern

### MainActivity.kt (Android)

- Android entry point
- Sets up Compose content
- Uses `enableEdgeToEdge()` for modern Android UI

### MainViewController.kt (iOS)

- iOS entry point for Compose
- Creates a UIViewController that hosts Compose UI
- Bridged to SwiftUI via ContentView.swift

---

**You're ready to start coding! 🚀**

For detailed information, see:

- [README.md](./README.md) - KMP template overview
- [LEARNING_GUIDE.md](./LEARNING_GUIDE.md) - React/RN → KMP guide
- [RESPONSIBILITIES.md](./RESPONSIBILITIES.md) - Team workflow
- [PROJECT_OVERVIEW.md](./PROJECT_OVERVIEW.md) - Project structure details

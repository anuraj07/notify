# Notify - Modern Android Notes App

**Notify** is a clean, minimal, offline-first note-taking Android application built with modern Jetpack Compose, structured under standard clean architecture principles. This project showcases premium Material 3 design alignment and robust local storage features.

---

## 🎨 Visual Identity & Styling
Notify is locked to a single premium Light Theme based on the **Organic Professional** design system.

- **Primary Tone**: `#345037` (Forest Green) & `#4B684D` (Primary Container)
- **Secondary Tone**: `#4B654E` (Sage Green) & `#CAE7CB` (Secondary Container)
- **Background**: `#FBF9F8` (Soft Off-white)
- **Typographic Details**: Styled with clean **Inter** headlines and monospaced metadata info.
- **Card Styling**: Rounded 16.dp cards with a subtle outline border (`outlineVariant`) for a tactile appearance.
- **Category Badges**: Categorized tags (`Dev`, `Work`, `Personal`, `Side-Gig`) display on notes feed.

---

## 🚀 Key Features
- **Offline Storage**: Backed by a SQLite Room database for fully offline operations.
- **Dynamic Search Dock**: Top header search bar filtering notes in real-time.
- **Layout Reflow**: Smoothly toggle between List and Staggered Grid formats.
- **Rich Editor Toolbar**: Markdown helper formatting bar supporting Bold, Italic, Bulleted Lists, Image links, and Code syntax.
- **Backup & Restore**: Native JSON serialization/deserialization backing up notes directly to local files via SAF.

---

## 🏗️ Architecture & DI
The application uses the **Model-View-ViewModel (MVVM)** pattern combined with a Repository interface.

- **UI Layer**: Jetpack Compose screens (`NotesListScreen`, `AddEditNoteScreen`, `BackupRestoreScreen`).
- **ViewModel**: `NoteViewModel` manages UI states and data conversions.
- **DI Container**: Manual Dependency Injection via `AppContainer` to minimize build overheads and keep compile times extremely fast.
- **Preferences**: Theme/layout configurations persisted using `SharedPreferences`.

---

## 🛠️ Build & Installation
1. **Requirements**: Android Studio Jellyfish+, JDK 17/20.
2. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
3. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

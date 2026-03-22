# Auto Expense Tracker 💸

[![Build](https://github.com/github/docs/actions/workflows/main.yml/badge.svg)](https://github.com/praslnx8/Auto-SMS-Tracker/actions)
[![GitHub issues](https://img.shields.io/github/issues/praslnx8/Auto-SMS-Tracker)](https://github.com/praslnx8/Auto-SMS-Tracker/issues)
[![GitHub license](https://img.shields.io/github/license/praslnx8/Auto-SMS-Tracker)](https://github.com/praslnx8/Auto-SMS-Tracker/blob/master/LICENSE)

An intelligent, open-source Android application that automates expense tracking by analyzing transactional SMS messages. Built with modern Android development practices, it provides a seamless and secure way to manage your finances.

---

## 🌟 Features

- **🚀 Automated Tracking**: Automatically detects and extracts expense details from transactional SMS messages.
- **🧠 Smart Suggestions**: Uses intelligent pattern matching (and TFLite) to categorize and suggest expenses.
- **📊 Expense Management**: View, add, edit, and delete expenses with a clean, intuitive interface.
- **🛡️ Secure & Private**: Data is stored locally using Room. Includes secure Logout and Account Deletion with confirmation safety.
- **🎨 Modern UI**: Fully built with **Jetpack Compose** following Material 3 guidelines.
- **🌓 Dark Mode Support**: Beautifully crafted themes for both day and night use.

---

## 🏗️ Architecture & Tech Stack

The project follows **Clean Architecture** principles and the **MVVM** (Model-View-ViewModel) pattern, separated into logical modules:

- **`:app`**: Main entry point, Compose UI, and DI implementation.
- **`:presentation`**: State management and ViewModels.
- **`:domain`**: Pure business logic, Use Cases, and Repository interfaces.
- **`:api`**: Data sources (Room DB, DataStore, Network).

### Technologies Used:
- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Asynchronous Flow**: [Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)
- **Navigation**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- **Background Tasks**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Local Storage**: [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- **ML**: [TensorFlow Lite](https://www.tensorflow.org/lite) (for SMS classification)

---

## 📸 Screenshots

| Home | Suggestions | Add Expense | Profile |
| :---: | :---: | :---: | :---: |
| ![Expense Page](https://user-images.githubusercontent.com/8796235/183958958-09251ee3-8fed-4b8c-bea3-b32a05484d5e.png) | ![Suggestion Page](https://user-images.githubusercontent.com/8796235/183959090-ca6b8cc2-95d4-404a-8005-cb52e3065605.png) | ![Add Expense Page](https://user-images.githubusercontent.com/8796235/183959109-682f6731-3835-41c2-8c0c-a1f0eefce075.png) | (Profile Image Coming Soon) |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Iguana (2023.2.1) or newer.
- Android SDK 34.

### Running the App
1. Clone the repository.
2. Sync Project with Gradle Files.
3. Run the following command to build the APK:
   ```bash
   ./gradlew assembleDebug
   ```

### Quality Control
- **Format Code**:
  ```bash
  ./gradlew ktlintFormat
  ```
- **Check Lint**:
  ```bash
  ./gradlew ktlintCheck
  ```
- **Run Tests**:
  ```bash
  ./gradlew testDebugUnitTest
  ```

---

## 🗺️ Design Diagrams

### Module Dependencies
![Modules Dependencies](https://user-images.githubusercontent.com/8796235/183961785-2e097a86-c7d1-491b-8282-8c6cb88bd7de.png)

### Logic Workflow
![App Works](https://user-images.githubusercontent.com/8796235/183963920-aead0fa5-7deb-4137-bb5b-88f5fb4b94b6.png)

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

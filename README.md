# Auto Expense Tracker

An open-source Android application designed to automatically detect and manage your expenses by intelligently reading and categorizing bank transaction SMS messages. 

## 🚀 Features
* **Automated Expense Tracking:** Parses incoming bank SMS transactions to automatically log expenses without manual entry.
* **Smart Suggestions:** Provides categorization suggestions based on your transaction history.
* **Modern UI:** Built entirely with Jetpack Compose for a smooth, reactive, and native user interface.
* **Clean Architecture:** Strictly follows the MVVM (Model-View-ViewModel) design pattern for high maintainability and scalability.

## 🛠️ Tech Stack
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **Architecture:** Clean Architecture + MVVM
* **Backend/Scripts:** Python (includes `server.py` for specific API functionalities)

## 🚦 Getting Started

### Prerequisites
* [Android Studio](https://developer.android.com/studio) (Latest version recommended)
* JDK 11 or higher
* An Android device or Emulator for testing (SMS permissions required for full functionality)

### Installation & Running Locally

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/jeevansahukar8-web/Auto-Expense-Tracker.git](https://github.com/jeevansahukar8-web/Auto-Expense-Tracker.git)
Navigate to the directory:

Bash
cd Auto-Expense-Tracker
Build the Debug APK via Command Line (Optional):

Bash
./gradlew assembleDebug
Open in Android Studio:
Open Android Studio, click File > Open, and select the project directory. Let the Gradle sync finish, then press the green Run button to install the app on your device or emulator.

🧪 Testing & Linting
To maintain code quality, this project uses standard Kotlin linting and includes unit test configurations.

Run Unit Tests:

Bash
./gradlew testDebugUnitTest
Check Code Formatting (Ktlint):

Bash
./gradlew --continue ktlintCheck
Auto-Format Code:

Bash
./gradlew --continue ktlintFormat
🏗️ Architecture Design
The application is separated into distinct modules to enforce separation of concerns and keep the codebase decoupled:

app/: The main application module connecting dependencies.

presentation/: Contains all UI components, screens (Jetpack Compose), and ViewModels.

domain/: Contains the core business logic, use cases, and repository interfaces.

api/: Handles external data sources, networking, and API communications.
created by Jeevan Sahukar❤️

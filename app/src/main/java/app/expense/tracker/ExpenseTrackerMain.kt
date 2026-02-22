package app.expense.tracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.expense.domain.utils.UserPreferences
import app.expense.tracker.ui.nav.HomeNavigation
import app.expense.tracker.ui.theme.AutoExpenseTrackerTheme
import app.expense.tracker.ui.views.auth.LoadingScreen
import app.expense.tracker.ui.views.auth.LoginScreen

@Composable
fun ExpenseTrackerMain() {
    AutoExpenseTrackerTheme {
        val context = LocalContext.current
        val userPreferences = remember { UserPreferences(context) }
        
        // Collect the login state. null means it is still checking.
        val isLoggedIn by userPreferences.isLoggedInFlow.collectAsState(initial = null)

        when (isLoggedIn) {
            null -> {
                // Still reading from DataStore, show loading
                LoadingScreen()
            }
            false -> {
                // Not logged in, show login screen
                LoginScreen(
                    onLoginSuccess = {
                        // The flow will automatically update to true
                    }
                )
            }
            true -> {
                // Logged in, show the main app
                HomeNavigation()
            }
        }
    }
}

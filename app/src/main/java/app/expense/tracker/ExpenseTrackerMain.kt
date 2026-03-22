package app.expense.tracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import app.expense.presentation.viewModels.AuthViewModel
import app.expense.tracker.ui.nav.HomeNavigation
import app.expense.tracker.ui.theme.AutoExpenseTrackerTheme
import app.expense.tracker.ui.views.auth.LoadingScreen
import app.expense.tracker.ui.views.auth.LoginScreen

@Composable
fun ExpenseTrackerMain(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    AutoExpenseTrackerTheme {
        // Collect the login state from the centralized AuthViewModel
        val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

        when (isLoggedIn) {
            null -> {
                // Still reading from DataStore, show loading
                LoadingScreen()
            }
            false -> {
                // Not logged in, show login screen
                LoginScreen(
                    onLoginSuccess = {
                        // The flow will automatically update to true because 
                        // AuthAPI updates the DataStore on success
                    },
                    viewModel = authViewModel
                )
            }
            true -> {
                // Logged in, show the main app
                HomeNavigation()
            }
        }
    }
}

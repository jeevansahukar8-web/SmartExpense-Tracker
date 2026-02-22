package app.expense.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.expense.domain.utils.UserDetails
import app.expense.domain.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val userDetailsFlow: Flow<UserDetails> = userPreferences.userDetailsFlow

    fun updateUserDetails(firstName: String, lastName: String, email: String, phone: String) {
        viewModelScope.launch {
            userPreferences.saveUser(firstName, lastName, email, phone)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.logout()
        }
    }
}

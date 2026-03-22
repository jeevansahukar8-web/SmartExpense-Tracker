package app.expense.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.expense.domain.auth.LoginUseCase
import app.expense.domain.utils.UserDetails
import app.expense.domain.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _userDetails = MutableStateFlow(UserDetails("", "", "", ""))
    val userDetails: StateFlow<UserDetails> = _userDetails

    init {
        loadUserDetails()
    }

    private fun loadUserDetails() {
        viewModelScope.launch {
            loginUseCase.getLoggedInUser().onSuccess { user ->
                if (user != null) {
                    val names = user.name.split(" ")
                    _userDetails.value = UserDetails(
                        firstName = names.getOrNull(0) ?: "",
                        lastName = names.drop(1).joinToString(" "),
                        email = user.email,
                        phone = "" 
                    )
                }
            }
        }
    }

    fun updateUserDetails(firstName: String, lastName: String, email: String, phone: String) {
        viewModelScope.launch {
            val fullName = "$firstName $lastName".trim()
            loginUseCase.updateUserName(fullName).onSuccess {
                loadUserDetails()
            }
            userPreferences.saveUser(firstName, lastName, email, phone)
        }
    }

    fun logout() {
        viewModelScope.launch {
            loginUseCase.logout()
            userPreferences.logout()
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            loginUseCase.deleteAccount().onSuccess {
                userPreferences.logout()
                onSuccess()
            }
        }
    }
}

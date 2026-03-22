package app.expense.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.expense.domain.auth.LoginUseCase
import app.expense.domain.auth.RegisterUseCase
import app.expense.domain.utils.UserPreferences
import app.expense.presentation.viewStates.AuthViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthViewState>(AuthViewState.Idle)
    val authState: StateFlow<AuthViewState> = _authState

    val isLoggedIn: StateFlow<Boolean?> = loginUseCase.isUserLoggedIn()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthViewState.Error("Email and Password cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthViewState.Loading
            loginUseCase.login(email, pass)
                .onSuccess { 
                    // Fetch user details and save to legacy preferences for Home Screen
                    loginUseCase.getLoggedInUser().onSuccess { user ->
                        if (user != null) {
                            val names = user.name.split(" ")
                            userPreferences.saveUser(
                                names.getOrNull(0) ?: "",
                                names.drop(1).joinToString(" "),
                                user.email,
                                ""
                            )
                        }
                    }
                    _authState.value = AuthViewState.LoginSuccess 
                }
                .onFailure { _authState.value = AuthViewState.Error(it.message ?: "Login Failed") }
        }
    }

    fun register(email: String, pass: String, name: String) {
        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            _authState.value = AuthViewState.Error("All fields are required")
            return
        }
        if (!email.contains("@")) {
            _authState.value = AuthViewState.Error("Invalid email format")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthViewState.Loading
            registerUseCase.register(email, pass, name)
                .onSuccess { _authState.value = AuthViewState.RegisterSuccess }
                .onFailure { _authState.value = AuthViewState.Error(it.message ?: "Registration Failed") }
        }
    }

    fun resetState() {
        _authState.value = AuthViewState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            loginUseCase.logout()
        }
    }
}

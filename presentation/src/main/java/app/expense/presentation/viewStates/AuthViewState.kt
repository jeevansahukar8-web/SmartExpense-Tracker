package app.expense.presentation.viewStates

sealed class AuthViewState {
    object Idle : AuthViewState()
    object Loading : AuthViewState()
    object LoginSuccess : AuthViewState()
    object RegisterSuccess : AuthViewState()
    data class Error(val message: String) : AuthViewState()
}

package app.expense.domain.auth

import app.expense.api.AuthAPI

class RegisterUseCase(private val authAPI: AuthAPI) {
    suspend fun register(email: String, pass: String, name: String): Result<Unit> {
        return try {
            authAPI.register(email, pass, name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package app.expense.domain.auth

import app.expense.api.AuthAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class LoginUseCase(private val authAPI: AuthAPI) {
    suspend fun login(email: String, pass: String): Result<Boolean> {
        return try {
            val user = authAPI.login(email, pass)
            if (user != null) {
                Result.success(true)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Flow<Boolean> = authAPI.isUserLoggedIn()
    
    suspend fun logout() = authAPI.logout()

    fun getLoggedInUserEmail(): Flow<String?> = authAPI.getLoggedInUserEmail()

    suspend fun getLoggedInUser(): Result<User?> {
        return try {
            val email = authAPI.getLoggedInUserEmail().firstOrNull()
            if (email != null) {
                val userDTO = authAPI.getUser(email)
                if (userDTO != null) {
                    Result.success(User(userDTO.email, userDTO.name))
                } else {
                    Result.failure(Exception("User not found"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserName(newName: String): Result<Unit> {
        return try {
            val email = authAPI.getLoggedInUserEmail().firstOrNull()
            if (email != null) {
                authAPI.updateUserName(email, newName)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val email = authAPI.getLoggedInUserEmail().firstOrNull()
            if (email != null) {
                authAPI.deleteAccount(email)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class User(val email: String, val name: String)

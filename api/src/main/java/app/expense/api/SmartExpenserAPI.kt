package app.expense.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Models
data class LoginRequest(val username: String, val password: String = "password")
data class LoginResponse(val token: String)

data class EncryptedPayload(val iv: String, val ciphertext: String)

data class ClassifyResponse(
    val transaction_state: String, // "Failed", "Credit", or "Debit"
    val category: String,
    val confidence: Double,
    val merchant: String,
    val storage_status: String? = null
)

// Retrofit Interface
interface SmartExpenserAPI {
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/classify")
    suspend fun classifyExpense(
        @Header("Authorization") token: String,
        @Body payload: EncryptedPayload
    ): ClassifyResponse
}

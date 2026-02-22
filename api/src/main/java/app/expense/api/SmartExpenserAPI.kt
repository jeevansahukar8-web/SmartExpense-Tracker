package app.expense.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

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

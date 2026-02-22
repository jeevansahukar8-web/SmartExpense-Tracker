package app.expense.api

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

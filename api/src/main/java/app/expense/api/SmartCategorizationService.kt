package app.expense.api

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartCategorizationService @Inject constructor(
    private val smartExpenserAPI: SmartExpenserAPI
) {
    suspend fun getCategoryForExpense(
        merchant: String,
        amount: Double,
        fullText: String,
        timestamp: Long
    ): String? {
        return try {
            // In a real app, you'd handle login and token storage
            val loginResponse = smartExpenserAPI.login(LoginRequest("admin", "password"))
            val token = "Bearer ${loginResponse.token}"

            // For now, we're not encrypting in this placeholder, 
            // but the server expects EncryptedPayload.
            // You should implement AESGCM encryption here to match the server.
            
            // This is a simplified call assuming the server can handle plain text for this demo
            // or that you will implement the encryption logic.
            val response = smartExpenserAPI.classifyExpense(
                token = token,
                payload = EncryptedPayload(
                    iv = "", // Base64 IV
                    ciphertext = "" // Base64 Encrypted JSON
                )
            )
            response.category
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

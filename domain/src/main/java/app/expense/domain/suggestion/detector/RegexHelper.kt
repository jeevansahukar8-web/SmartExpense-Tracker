package app.expense.domain.suggestion.detector

class RegexHelper {

    companion object {
        // Keywords that indicate the message is NOT an expense or credit (Promotional, OTPs, etc.)
        private val IGNORE_KEYWORDS = listOf(
            "offer", "loan", "cashback", "apply", "win", "reward", "discount", 
            "otp", "limit", "promotional", "free"
        )
        
        // Explicit transaction keywords
        const val DEBIT_PATTERN = "debited|deducted|spent|payment of|paid to"
        const val CREDIT_PATTERN = "credited|received|added|deposited|paid to your account|refunded" 
    }

    /**
     * Checks whether the message is a valid transaction expense.
     * @param message from SMS
     */
    fun isExpense(message: String): Boolean {
        val msg = message.lowercase()

        if (IGNORE_KEYWORDS.any { msg.contains(it) }) return false
        
        // If it's a credit, it shouldn't be an expense
        if (isCredit(message)) return false

        val hasDebitKeyword = DEBIT_PATTERN.toRegex().containsMatchIn(msg) || 
                             Regex("sent to|transfer to").containsMatchIn(msg)
        
        val hasAmount = "(?i)(rs\\.?|inr)\\s?\\d+".toRegex().containsMatchIn(msg)

        return hasDebitKeyword && hasAmount
    }

    /**
     * Check whether the message is of an income/credit type.
     */
    fun isCredit(message: String): Boolean {
        val msg = message.lowercase()
        
        if (IGNORE_KEYWORDS.any { msg.contains(it) }) return false
        
        val hasCreditKeyword = CREDIT_PATTERN.toRegex().containsMatchIn(msg) || 
                              Regex("transfer from|received from").containsMatchIn(msg)
        
        val hasAmount = "(?i)(rs\\.?|inr)\\s?\\d+".toRegex().containsMatchIn(msg)
        
        return hasCreditKeyword && hasAmount
    }

    /**
     * Get PaidTo/Merchant name (or Sender for Credits) from Transaction message.
     */
    fun getPaidToName(message: String): String? {
        val patterns = listOf(
            "(?i)(?:paid to|sent to|transfer to)\\s+([A-Za-z0-9\\s@.-]+?)(?:\\s+(?:via|upi|ref|on|inr|rs|for))",
            "(?i)(?:received from|transfer from|credited by|from)\\s+([A-Za-z0-9\\s@.-]+?)(?:\\s+(?:via|upi|ref|on|inr|rs|for))",
            "(?i)(?:to\\s+vpa\\s+)([A-Za-z0-9@.-]+)",
            "(?i)(?:at\\s+)([A-Za-z0-9\\s]+?)(?:\\s+(?:on|via|ref|inr|rs))",
            "(?i)(?:info[:\\-]\\s?)([A-Za-z0-9\\s@.-]+?)(?:\\s+(?:via|upi|ref|on|inr|rs))"
        )

        for (pattern in patterns) {
            val matchGroup = Regex(pattern).find(message)
            if (matchGroup != null) {
                return matchGroup.groups[1]?.value?.trim()
            }
        }
        return null
    }

    fun getAmountSpent(message: String): Double? {
        val regex = "(?i)(?:RS|INR)\\.?\\s?(\\d+(?:,\\d+)*(?:\\.\\d{1,2})?)"
        val matchGroup = Regex(regex).find(message)?.groups?.get(1)
        return matchGroup?.value?.replace(",", "")?.toDoubleOrNull()
    }

    fun getCardName(message: String): String? {
        val regex = "[0-9]*[Xx*]*[0-9]*[Xx*]+[0-9]{3,}"
        return Regex(regex).find(message)?.value?.trim()
    }
}

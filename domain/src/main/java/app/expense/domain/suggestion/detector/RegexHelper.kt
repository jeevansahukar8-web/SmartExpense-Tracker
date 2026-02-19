package app.expense.domain.suggestion.detector

class RegexHelper {

    companion object {
        // Combined ignore patterns for failures, warnings, and promotional content
        const val IGNORE_PATTERN = "fail|failed|declined|rejected|unsuccessful|bounced|reversed|cooling period|never share|limit is applicable|offer|loan|cashback|apply|win|reward|discount|otp|limit|promotional|free"

        // Added 'sent', 'payment', and 'paid' as standalone action keywords with word boundaries
        // Using \b ensures we match exact words (e.g. 'sent' and not 'absent')
        const val DEBIT_PATTERN = "\\b(debited|deducted|spent|payment|paid|sent)\\b"
        const val CREDIT_PATTERN = "\\b(credited|received|added|deposited|refunded)\\b"
    }

    /**
     * Checks if the message indicates a failed transaction, informational warning, or promotional content.
     */
    fun isFailedOrIgnored(message: String): Boolean {
        return IGNORE_PATTERN.toRegex().containsMatchIn(message.lowercase())
    }

    /**
     * Check whether the message is of an income/credit type.
     */
    fun isCredit(message: String): Boolean {
        if (isFailedOrIgnored(message)) return false
        
        val lowerMsg = message.lowercase()
        
        // FIX FOR TRAP 1: If the message says your account was debited AND credited to a merchant, it's an expense.
        if (lowerMsg.contains("debited") && lowerMsg.contains("credited to")) {
            return false
        }
        
        val hasCreditKeyword = CREDIT_PATTERN.toRegex().containsMatchIn(lowerMsg)
        
        // Ensure there is an amount present to avoid false positives
        val hasAmount = "(?i)(rs\\.?|inr)\\s?\\d+".toRegex().containsMatchIn(lowerMsg)
        
        return hasCreditKeyword && hasAmount
    }

    /**
     * Check whether the message is of transaction type (Expense).
     */
    fun isExpense(message: String): Boolean {
        if (isFailedOrIgnored(message)) return false
        
        if (isCredit(message)) return false

        val lowerMsg = message.lowercase()
        
        // Now checks if the word 'sent' or 'debited' exists anywhere as a whole word
        val hasDebitKeyword = DEBIT_PATTERN.toRegex().containsMatchIn(lowerMsg)
        
        // Ensure there is an amount present
        val hasAmount = "(?i)(rs\\.?|inr)\\s?\\d+".toRegex().containsMatchIn(lowerMsg)
        
        // Fallback generic regex for standard transaction structures
        val genericRegex = "(?i)(?=.*(account|a/c|acct|card))(?=.*debit)(?=.*(inr|rs))"
        val matchesGeneric = Regex(genericRegex).containsMatchIn(lowerMsg)

        return (hasDebitKeyword && hasAmount) || matchesGeneric
    }

    /**
     * Get PaidTo/Merchant name (or Sender for Credits) from Transaction message.
     */
    fun getPaidToName(message: String): String? {
        val patterns = listOf(
            // 1. Catches standalone VPAs perfectly (e.g., "to BHARATPEC...@yesbandltd")
            "(?i)(?:to\\s+)([A-Za-z0-9.-]+@[a-zA-Z0-9.-]+)",
            
            // 2. Standard adjacent patterns ("paid to XYZ on")
            "(?i)(?:paid to|sent to|transfer to)\\s+([A-Za-z0-9\\s@.-]+?)(?:\\s+(?:via|upi|ref|on|inr|rs|for))",
            
            // 3. Standard credit patterns ("received from XYZ")
            "(?i)(?:received from|transfer from|credited by|from)\\s+([A-Za-z0-9\\s@.-]+?)(?:\\s+(?:via|upi|ref|on|inr|rs|for))",
            
            // 4. "to vpa XYZ"
            "(?i)(?:to\\s+vpa\\s+)([A-Za-z0-9@.-]+)",
            
            // 5. Card POS transactions ("at STARBUCKS on")
            "(?i)(?:at\\s+)([A-Za-z0-9\\s]+?)(?:\\s+(?:on|via|ref|inr|rs))",
            
            // 6. Generic info drops
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

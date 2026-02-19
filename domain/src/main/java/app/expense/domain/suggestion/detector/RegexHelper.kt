package app.expense.domain.suggestion.detector

import java.util.regex.Pattern

class RegexHelper {

    companion object {
        const val DEBIT_PATTERN = "debited|debit|deducted|spent|paid"
        const val CREDIT_PATTERN = "credited|received|added|deposited|refunded"
        
        // Added security warning keywords from your dataset
        const val IGNORE_PATTERN = "fail|failed|declined|rejected|unsuccessful|bounced|reversed|cooling period|never share|limit is applicable"
    }

    /**
     * Checks if the message indicates a failed transaction or an informational warning.
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
        
        return CREDIT_PATTERN.toRegex().containsMatchIn(lowerMsg)
    }

    /**
     * Check whether the message is of transaction type (Expense).
     */
    fun isExpense(message: String): Boolean {
        if (isFailedOrIgnored(message)) return false
        
        if (isCredit(message)) return false

        val lowerMsg = message.lowercase()
        val hasDebitKeyword = DEBIT_PATTERN.toRegex().containsMatchIn(lowerMsg)
        
        val genericRegex = "(?=.*[Aa]ccount.*|.*[Aa]/[Cc].*|.*[Aa][Cc][Cc][Tt].*|.*[Cc][Aa][Rr][Dd].*)(?=.*[Dd]ebit.*)(?=.*[Ii][Nn][Rr].*|.*[Rr][Ss].*)"
        val matchesGeneric = Pattern.compile(genericRegex).matcher(message).find()

        return hasDebitKeyword || matchesGeneric
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

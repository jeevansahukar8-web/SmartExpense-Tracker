package app.expense.domain.suggestion.detector

import app.expense.domain.suggestion.models.SMSMessage
import app.expense.domain.suggestion.models.Suggestion
import java.util.Locale

/**
 * Suggestion Detector with the help of Regexp Parsing.
 */
class SuggestionDetectorImpl(private val regexHelper: RegexHelper) :
    SuggestionDetector() {

    /**
     * Check for smsMessage is of Transactional SMS and parse the Expense suggestion.
     */
    override fun detectSuggestions(smsMessage: SMSMessage): Suggestion? {
        // Guard clause: If the regex helper detects failure keywords, ignore it entirely
        if (regexHelper.isFailedOrIgnored(smsMessage.body)) {
            return null
        }

        val isExpenseMessage = regexHelper.isExpense(smsMessage.body)
        val isCreditMessage = regexHelper.isCredit(smsMessage.body)
        
        val spent = regexHelper.getAmountSpent(smsMessage.body)
        var paidToName = regexHelper.getPaidToName(smsMessage.body) ?: "Unknown"

        // Example logic to beautify merchant VPA strings
        if (paidToName.contains(".razorpay@") || paidToName.contains("paytm@")) {
            paidToName = paidToName.substringBefore("@").replace(".", " ").replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }

        // Accept if it's explicitly one or the other, and we found an amount
        if ((isExpenseMessage || isCreditMessage) && spent != null) {
            return Suggestion(
                id = generateUniqueId(smsMessage),
                amount = spent,
                paidTo = paidToName,
                time = smsMessage.time,
                referenceMessage = smsMessage.body,
                referenceMessageSender = smsMessage.address,
                isExpense = !isCreditMessage // False if it's a credit, True if it's a debit
            )
        }

        return null
    }

    private fun generateUniqueId(smsMessage: SMSMessage) =
        smsMessage.body.hashCode().toLong().plus(smsMessage.time)
}

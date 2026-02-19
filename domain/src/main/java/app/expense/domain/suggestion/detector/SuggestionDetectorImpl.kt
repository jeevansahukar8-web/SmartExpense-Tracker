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
        val isCredit = regexHelper.isCredit(smsMessage.body)
        val isExpense = if (isCredit) false else regexHelper.isExpense(smsMessage.body)
        
        val spent = regexHelper.getAmountSpent(smsMessage.body)
        var paidToName = regexHelper.getPaidToName(smsMessage.body) ?: "Unknown"

        // Example logic to beautify merchant VPA strings
        if (paidToName.contains(".razorpay@") || paidToName.contains("paytm@")) {
            paidToName = paidToName.substringBefore("@").replace(".", " ").replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }

        // Accept if it's either an expense or a credit
        if ((isExpense || isCredit) && spent != null) {
            return Suggestion(
                id = generateUniqueId(smsMessage),
                amount = spent,
                paidTo = paidToName,
                time = smsMessage.time,
                referenceMessage = smsMessage.body,
                referenceMessageSender = smsMessage.address,
                isExpense = isExpense // Correctly assign classification
            )
        }

        return null
    }

    private fun generateUniqueId(smsMessage: SMSMessage) =
        smsMessage.body.hashCode().toLong().plus(smsMessage.time)
}

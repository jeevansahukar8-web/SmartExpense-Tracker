package app.expense.domain.suggestion.usecases

import android.Manifest
import androidx.annotation.RequiresPermission
import app.expense.api.SMSReadAPI
import app.expense.api.SuggestionSyncAPI
import app.expense.api.SuggestionsAPI
import app.expense.db.model.SuggestionDTO
import app.expense.domain.suggestion.detector.SuggestionDetector
import app.expense.domain.suggestion.mappers.SMSMessageDataMapper
import app.expense.domain.suggestion.models.Suggestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Sync the New SMS and deducts suggestions.
 */
class SyncSuggestionUseCase(
    private val suggestionSyncAPI: SuggestionSyncAPI,
    private val suggestionsAPI: SuggestionsAPI,
    private val smsReadAPI: SMSReadAPI,
    private val calendar: Calendar,
    private val suggestionDetector: SuggestionDetector,
    private val dataMapper: SMSMessageDataMapper
) {


    /**
     * Sync the New SMS and deducts suggestions.
     * Needs SMS Permission.
     */
    @RequiresPermission(Manifest.permission.READ_SMS)
    fun sync(): Flow<List<Suggestion>> = flow {
        val lastSyncedTime = suggestionSyncAPI.getLastSyncedTime().first()
        val startTime = calendar.timeInMillis
        
        // Calculate 90 days (~3 months) prior to the current moment
        val fallbackTime = startTime - TimeUnit.DAYS.toMillis(90)

        val suggestions: List<Suggestion> =
            smsReadAPI.getAllSms(lastSyncedTime ?: fallbackTime)
                .mapNotNull { smsMessageDTO ->
                    suggestionDetector.detectSuggestions(
                        dataMapper.mapToSMSMessage(
                            smsMessageDTO
                        )
                    )
                }

        suggestionsAPI.storeSuggestions(
            suggestions.map { suggestion ->
                SuggestionDTO(
                    id = suggestion.id,
                    amount = suggestion.amount,
                    paidTo = suggestion.paidTo,
                    time = suggestion.time,
                    referenceMessage = suggestion.referenceMessage,
                    referenceMessageSender = suggestion.referenceMessageSender,
                    isExpense = suggestion.isExpense // Pass the classification flag to DB
                )
            }
        ).collect()

        suggestionSyncAPI.setLastSyncedTime(startTime)

        emit(suggestions)
    }.flowOn(Dispatchers.IO)
}

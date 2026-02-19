package app.expense.presentation.viewModels

import android.icu.text.NumberFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import app.expense.domain.suggestion.models.Suggestion
import app.expense.domain.suggestion.usecases.DeleteSuggestionUseCase
import app.expense.domain.suggestion.usecases.FetchSuggestionUseCase
import app.expense.domain.utils.cleanMerchantName
import app.expense.presentation.viewStates.SuggestionListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Locale.getDefault
import javax.inject.Inject

enum class SuggestionFilter { ALL, DEBIT, CREDIT }

@HiltViewModel
class SuggestionListViewModel @Inject constructor(
    private val fetchSuggestionUseCase: FetchSuggestionUseCase,
    private val deleteSuggestionUseCase: DeleteSuggestionUseCase
) : ViewModel() {

    private val _currentFilter = MutableStateFlow(SuggestionFilter.ALL)
    val currentFilter: StateFlow<SuggestionFilter> = _currentFilter.asStateFlow()

    fun setFilter(filter: SuggestionFilter) {
        _currentFilter.value = filter
    }

    fun getSuggestionListState(): Flow<SuggestionListState> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        
        // Combine the DB flow with the Filter flow
        return combine(
            fetchSuggestionUseCase.getSuggestions(from = calendar.timeInMillis),
            _currentFilter
        ) { suggestions, filter ->
            val filteredSuggestions = when (filter) {
                SuggestionFilter.ALL -> suggestions
                SuggestionFilter.DEBIT -> suggestions.filter { it.isExpense }
                SuggestionFilter.CREDIT -> suggestions.filter { !it.isExpense }
            }
            SuggestionListState(dateSuggestionsMap = getSuggestionsByDate(filteredSuggestions))
        }
    }

    suspend fun deleteSuggestion(suggestionId: Long) {
        deleteSuggestionUseCase.deleteSuggestion(suggestionId)
    }

    /**
     * Fetches all suggestions and formats them as a CSV string.
     */
    suspend fun generateCsvData(): String = withContext(Dispatchers.IO) {
        // Fetch ALL suggestions from the beginning of time
        val allSuggestions = fetchSuggestionUseCase.getSuggestions(from = 0L).first()
        
        val csvBuilder = StringBuilder()
        // CSV Header
        csvBuilder.append("ID,Amount,PaidTo,Sender,Timestamp,IsExpense,Message\n")
        
        allSuggestions.forEach { suggestion ->
            // SMS messages often contain commas or quotes. We must escape them for CSV.
            val escapedMessage = suggestion.referenceMessage.replace("\"", "\"\"")
            
            csvBuilder.append(suggestion.id).append(",")
            csvBuilder.append(suggestion.amount).append(",")
            csvBuilder.append(suggestion.paidTo ?: "Unknown").append(",")
            csvBuilder.append(suggestion.referenceMessageSender).append(",")
            csvBuilder.append(suggestion.time).append(",")
            csvBuilder.append(if (suggestion.isExpense) "Debit" else "Credit").append(",")
            csvBuilder.append("\"").append(escapedMessage).append("\"\n") // Wrap message in quotes
        }
        
        return@withContext csvBuilder.toString()
    }

    private fun getSuggestionsByDate(expenses: List<Suggestion>): Map<String, List<SuggestionListState.Item>> =
        expenses
            .groupBy { expense ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = expense.time
                calendar.get(Calendar.DAY_OF_YEAR)
            }.toSortedMap { day1, day2 ->
                performDescendingCompare(day1, day2)
            }
            .mapValues { mapEntry ->
                mapEntry.value.map { suggestion ->
                    SuggestionListState.Item(
                        id = suggestion.id,
                        amount = NumberFormat.getCurrencyInstance().format(suggestion.amount),
                        // Call the cleaner here!
                        message = suggestion.paidTo?.cleanMerchantName() ?: "Unknown",
                        isExpense = suggestion.isExpense
                    )
                }
            }.mapKeys { mapEntry ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_YEAR, mapEntry.key)
                SimpleDateFormat("dd MMMM yyyy", getDefault()).format(calendar.timeInMillis)
            }

    private fun performDescendingCompare(day1: Int, day2: Int) = when {
        day1 < day2 -> 1
        day2 < day1 -> -1
        else -> 0
    }
}

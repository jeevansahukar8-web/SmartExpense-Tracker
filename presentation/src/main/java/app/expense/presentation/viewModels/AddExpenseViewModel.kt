package app.expense.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.expense.api.SmartCategorizationService
import app.expense.domain.expense.models.Expense
import app.expense.domain.expense.usecases.AddExpenseUseCase
import app.expense.domain.expense.usecases.DeleteExpenseUseCase
import app.expense.domain.expense.usecases.FetchExpenseUseCase
import app.expense.domain.suggestion.detector.LocalCategorizer
import app.expense.domain.suggestion.usecases.DeleteSuggestionUseCase
import app.expense.domain.suggestion.usecases.FetchSuggestionUseCase
import app.expense.domain.utils.cleanMerchantName
import app.expense.presentation.viewStates.AddExpenseViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val fetchExpenseUseCase: FetchExpenseUseCase,
    private val fetchSuggestionUseCase: FetchSuggestionUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val deleteSuggestionUseCase: DeleteSuggestionUseCase,
    private val smartCategorizationService: SmartCategorizationService
) : ViewModel() {

    private val _addExpenseViewStateFlow = MutableStateFlow(AddExpenseViewState())
    val addExpenseViewState: StateFlow<AddExpenseViewState>
        get() = _addExpenseViewStateFlow

    suspend fun getAddExpenseViewState(
        expenseId: Long? = null,
        suggestionId: Long? = null
    ) {
        if (expenseId != null) {
            fetchExpenseUseCase.getExpense(expenseId).first().also { expense ->
                if (expense != null) {
                    _addExpenseViewStateFlow.value = AddExpenseViewState(
                        amount = expense.amount,
                        paidTo = expense.paidTo ?: "",
                        categories = expense.categories.toMutableList(),
                        time = expense.time
                    )
                }
            }
        } else if (suggestionId != null) {
            fetchSuggestionUseCase.getSuggestion(suggestionId).first().also { suggestion ->
                if (suggestion != null) {
                    val cleanedMerchant = suggestion.paidTo?.cleanMerchantName() ?: ""

                    // 1. Check the local categorizer instantly!
                    val localPrediction = LocalCategorizer.predictCategory(cleanedMerchant, suggestion.referenceMessage)
                    val initialCategories = if (localPrediction != "Unknown") listOf(localPrediction) else emptyList()

                    // 2. Open dialog immediately with local prediction
                    _addExpenseViewStateFlow.value = AddExpenseViewState(
                        amount = suggestion.amount,
                        paidTo = cleanedMerchant,
                        time = suggestion.time,
                        suggestionMessage = suggestion.referenceMessage,
                        categories = initialCategories // It pops in instantly!
                    )

                    // 3. Only call AI service if the local categorizer failed
                    if (localPrediction == "Unknown") {
                        viewModelScope.launch {
                            try {
                                val aiCategory = smartCategorizationService.getCategoryForExpense(
                                    merchant = cleanedMerchant,
                                    amount = suggestion.amount,
                                    fullText = suggestion.referenceMessage,
                                    timestamp = suggestion.time
                                )

                                if (!aiCategory.isNullOrBlank() && aiCategory != "Unknown") {
                                    _addExpenseViewStateFlow.value = _addExpenseViewStateFlow.value.copy(
                                        categories = listOf(aiCategory)
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun addExpense(
        expenseId: Long?,
        suggestionId: Long?,
        amount: Double,
        paidTo: String?,
        categories: List<String>,
        time: Long
    ) {
        addExpenseUseCase.addExpense(
            expense = Expense(
                id = expenseId,
                amount = amount,
                paidTo = paidTo,
                categories = categories,
                time = time
            ),
            fromSuggestionId = suggestionId
        )
    }

    suspend fun deleteSuggestion(id: Long) {
        deleteSuggestionUseCase.deleteSuggestion(id)
    }

    suspend fun deleteExpense(id: Long) {
        deleteExpenseUseCase.deleteExpense(id)
    }
}

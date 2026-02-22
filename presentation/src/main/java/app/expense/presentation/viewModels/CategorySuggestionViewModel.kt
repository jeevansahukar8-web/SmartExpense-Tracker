package app.expense.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.expense.domain.categories.FetchCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CategorySuggestionViewModel @Inject constructor(
    private val fetchCategoriesUseCase: FetchCategoriesUseCase
) : ViewModel() {

    // Your exact predefined categories
    private val predefinedCategories = listOf(
        "Food", "Recharge", "Travel", "Health", "Shopping", "Bills", "Education", "Entertainment", "Investment"
    )

    private val _query = MutableStateFlow("")

    /**
     * Merges default categories with user's custom history and filters by query
     */
    val categoriesState: StateFlow<List<String>> = combine(
        _query,
        fetchCategoriesUseCase.getCategories()
    ) { query, customCategories ->
        val allCategories = (predefinedCategories + customCategories).distinct()
        
        if (query.isBlank()) {
            allCategories
        } else {
            allCategories.filter { it.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = predefinedCategories
    )

    fun getCategories(query: String) {
        _query.value = query
    }
}

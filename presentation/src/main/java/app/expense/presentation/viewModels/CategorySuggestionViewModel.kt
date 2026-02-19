package app.expense.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.expense.domain.categories.FetchCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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

    private val searchQuery = MutableStateFlow("")

    /**
     * Merges default categories with user's custom history
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val categoriesState: StateFlow<List<String>> = searchQuery
        .flatMapLatest { query ->
            fetchCategoriesUseCase.getCategories().map { customCategories ->
                // Combine both lists and remove any duplicates
                val allCategories = (predefinedCategories + customCategories).distinct()

                if (query.isBlank()) {
                    allCategories
                } else {
                    allCategories.filter { it.contains(query, ignoreCase = true) }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = predefinedCategories
        )

    fun getCategories(query: String) {
        searchQuery.value = query
    }
}

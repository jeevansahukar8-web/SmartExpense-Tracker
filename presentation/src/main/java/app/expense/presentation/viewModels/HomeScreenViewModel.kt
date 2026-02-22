package app.expense.presentation.viewModels

import android.Manifest
import android.icu.util.Calendar
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.expense.domain.suggestion.usecases.FetchSuggestionUseCase
import app.expense.domain.suggestion.usecases.SyncSuggestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val fetchSuggestionUseCase: FetchSuggestionUseCase,
    private val syncSuggestionUseCase: SyncSuggestionUseCase
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    @RequiresPermission(Manifest.permission.READ_SMS)
    fun syncSuggestions() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                syncSuggestionUseCase.sync().collect()
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun getSuggestionsCount(): Flow<Int> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        return fetchSuggestionUseCase.getSuggestions(from = calendar.timeInMillis).map {
            it.count()
        }
    }
}

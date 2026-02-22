package app.expense.tracker.ui.views.reports

import androidx.compose.runtime.Composable
import app.expense.tracker.ui.views.suggestions.SuggestionsScreen

@Composable
fun ReportsScreen(onAddSuggestion: (suggestionId: Long) -> Unit) {
    SuggestionsScreen(onAddSuggestion = onAddSuggestion)
}

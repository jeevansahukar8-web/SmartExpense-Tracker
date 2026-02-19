package app.expense.domain.suggestion.mappers

import app.expense.db.model.SuggestionDTO
import app.expense.domain.suggestion.models.Suggestion

class SuggestionDataMapper {
    fun mapToDomain(suggestionDTO: SuggestionDTO): Suggestion {
        return Suggestion(
            id = suggestionDTO.id,
            amount = suggestionDTO.amount,
            paidTo = suggestionDTO.paidTo,
            time = suggestionDTO.time,
            referenceMessage = suggestionDTO.referenceMessage,
            referenceMessageSender = suggestionDTO.referenceMessageSender,
            isExpense = suggestionDTO.isExpense
        )
    }

    fun mapToDTO(suggestion: Suggestion): SuggestionDTO {
        return SuggestionDTO(
            id = suggestion.id,
            amount = suggestion.amount,
            paidTo = suggestion.paidTo,
            time = suggestion.time,
            referenceMessage = suggestion.referenceMessage,
            referenceMessageSender = suggestion.referenceMessageSender,
            isExpense = suggestion.isExpense
        )
    }
}

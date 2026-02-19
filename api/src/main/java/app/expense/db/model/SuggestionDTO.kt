package app.expense.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suggestion")
data class SuggestionDTO(
    @PrimaryKey
    val id: Long,
    val amount: Double,
    val paidTo: String?,
    val time: Long,
    val referenceMessage: String,
    val referenceMessageSender: String,
    val isExpense: Boolean
)

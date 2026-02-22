package app.expense.presentation.viewStates

data class BudgetViewState(
    val totalLimit: Double = 5000.0,
    val totalSpent: Double = 0.0,
    val smsCountToday: Int = 0,
    val categories: List<CategoryBudget> = emptyList(),
    val recommendation: String? = null
)

data class CategoryBudget(
    val name: String,
    val spent: Double,
    val limit: Double,
    val colorIndex: Int
)

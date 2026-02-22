package app.expense.presentation.viewStates

data class ExpenseStats(
    val userName: String = "User",
    val totalSpent: String = "$0.00",
    val weeklySpent: List<Double> = emptyList(),
    val categorySpent: Map<String, Double> = emptyMap(),
    val topCategory: String = "None",
    val budgetProgress: Float = 0f
)

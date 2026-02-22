package app.expense.presentation.viewModels

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import app.expense.domain.expense.models.Expense
import app.expense.domain.expense.usecases.FetchExpenseUseCase
import app.expense.domain.utils.UserPreferences
import app.expense.presentation.viewStates.ExpenseStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class ExpenseStatsViewModel @Inject constructor(
    private val fetchExpenseUseCase: FetchExpenseUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun getExpenseStats(): Flow<ExpenseStats> {
        val calendar = Calendar.getInstance()
        // Start of current month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val monthStart = calendar.timeInMillis
        
        // Start of current week (for activity)
        val weekCalendar = Calendar.getInstance()
        weekCalendar.set(Calendar.DAY_OF_WEEK, weekCalendar.firstDayOfWeek)
        weekCalendar.set(Calendar.HOUR_OF_DAY, 0)
        weekCalendar.set(Calendar.MINUTE, 0)
        weekCalendar.set(Calendar.SECOND, 0)
        val weekStart = weekCalendar.timeInMillis

        return combine(
            fetchExpenseUseCase.getExpenses(from = monthStart),
            userPreferences.userDetailsFlow,
            userPreferences.monthlyBudgetFlow,
            userPreferences.categoryBudgetsFlow
        ) { currentMonthExpenses, userDetails, monthlyBudget, categoryBudgets ->
            val totalSpentAmount = currentMonthExpenses.sumOf { it.amount }

            // Monthly budget is now the fixed total target as set in BudgetViewModel
            val totalBudgetLimit = monthlyBudget
            
            // Calculate weekly activity (7 days)
            val currentWeekExpenses = currentMonthExpenses.filter { it.time >= weekStart }
            val weeklyData = DoubleArray(7) { 0.0 }
            currentWeekExpenses.forEach { expense ->
                val cal = Calendar.getInstance()
                cal.timeInMillis = expense.time
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed
                if (dayOfWeek in 0..6) {
                    weeklyData[dayOfWeek] += expense.amount
                }
            }

            val categorySpent = getCategorySpent(currentMonthExpenses)
            val topCategory = categorySpent.maxByOrNull { it.value }?.key ?: "None"
            
            val progress = if (totalBudgetLimit > 0) {
                (totalSpentAmount / totalBudgetLimit).toFloat().coerceIn(0f, 1f)
            } else 0f

            ExpenseStats(
                userName = userDetails.firstName.ifBlank { "User" },
                totalSpent = NumberFormat.getCurrencyInstance().format(totalSpentAmount),
                weeklySpent = weeklyData.toList(),
                categorySpent = categorySpent,
                topCategory = topCategory,
                budgetProgress = progress,
                totalBudget = totalBudgetLimit
            )
        }
    }

    private fun getCategorySpent(expenses: List<Expense>): Map<String, Double> {
        val categoryMap = mutableMapOf<String, Double>()
        expenses.forEach { expense ->
            val category = expense.categories.firstOrNull() ?: "Others"
            categoryMap[category] = (categoryMap[category] ?: 0.0) + expense.amount
        }
        return categoryMap
    }
}

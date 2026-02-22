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
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.util.Locale.getDefault
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
        val monthStart = calendar.timeInMillis
        
        // Start of current week (for activity)
        val weekCalendar = Calendar.getInstance()
        weekCalendar.set(Calendar.DAY_OF_WEEK, weekCalendar.firstDayOfWeek)
        val weekStart = weekCalendar.timeInMillis

        return combine(
            fetchExpenseUseCase.getExpenses(from = 0L),
            userPreferences.userDetailsFlow,
            userPreferences.monthlyBudgetFlow
        ) { allExpenses, userDetails, monthlyBudget ->
            val currentMonthExpenses = allExpenses.filter { it.time >= monthStart }
            val currentWeekExpenses = allExpenses.filter { it.time >= weekStart }
            
            val totalSpentAmount = currentMonthExpenses.sumOf { it.amount }
            
            // Calculate weekly activity (7 days)
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
            
            val progress = (totalSpentAmount / monthlyBudget).toFloat().coerceIn(0f, 1f)

            ExpenseStats(
                userName = userDetails.firstName.ifBlank { "User" },
                totalSpent = NumberFormat.getCurrencyInstance().format(totalSpentAmount),
                weeklySpent = weeklyData.toList(),
                categorySpent = categorySpent,
                topCategory = topCategory,
                budgetProgress = progress
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

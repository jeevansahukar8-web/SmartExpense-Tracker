package app.expense.presentation.viewModels

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.expense.domain.expense.usecases.FetchExpenseUseCase
import app.expense.domain.suggestion.usecases.FetchSuggestionUseCase
import app.expense.domain.utils.UserPreferences
import app.expense.presentation.viewStates.BudgetViewState
import app.expense.presentation.viewStates.CategoryBudget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val fetchExpenseUseCase: FetchExpenseUseCase,
    private val fetchSuggestionUseCase: FetchSuggestionUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun getBudgetViewState(): Flow<BudgetViewState> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val monthStart = calendar.timeInMillis

        val todayCalendar = Calendar.getInstance()
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
        todayCalendar.set(Calendar.MINUTE, 0)
        todayCalendar.set(Calendar.SECOND, 0)
        val todayStart = todayCalendar.timeInMillis

        return combine(
            fetchExpenseUseCase.getExpenses(from = monthStart),
            fetchSuggestionUseCase.getSuggestions(from = todayStart),
            userPreferences.monthlyBudgetFlow,
            userPreferences.categoryBudgetsFlow
        ) { expenses, suggestions, monthlyBudget, categoryBudgets ->
            val totalSpent = expenses.sumOf { it.amount }
            
            val allCategoryNames = (expenses.map { it.categories.firstOrNull() ?: "Others" } + categoryBudgets.keys).distinct()

            val categoryBudgetList = allCategoryNames.map { category ->
                val spent = expenses.filter { (it.categories.firstOrNull() ?: "Others") == category }.sumOf { it.amount }
                
                val limit = categoryBudgets[category] ?: when (category) {
                    "Food & Dining" -> monthlyBudget * 0.2
                    "Entertainment" -> monthlyBudget * 0.1
                    "Transport" -> monthlyBudget * 0.1
                    "Shopping" -> monthlyBudget * 0.3
                    else -> monthlyBudget * 0.3
                }
                
                CategoryBudget(
                    name = category,
                    spent = spent,
                    limit = limit,
                    colorIndex = category.hashCode()
                )
            }

            // Total limit is the sum of all category limits
            val calculatedTotalLimit = categoryBudgetList.sumOf { it.limit }

            BudgetViewState(
                totalLimit = calculatedTotalLimit,
                totalSpent = totalSpent,
                smsCountToday = suggestions.size,
                categories = categoryBudgetList,
                recommendation = generateRecommendation(totalSpent, calculatedTotalLimit, categoryBudgetList)
            )
        }
    }

    /**
     * Updates the overall monthly budget by scaling all category targets proportionally.
     */
    fun updateMonthlyBudget(newTotalBudget: Double) {
        viewModelScope.launch {
            val viewState = getBudgetViewState().first()
            val currentTotal = viewState.totalLimit
            
            if (currentTotal <= 0) {
                userPreferences.updateMonthlyBudget(newTotalBudget)
                return@launch
            }

            val ratio = newTotalBudget / currentTotal
            val currentMonthlyBase = userPreferences.monthlyBudgetFlow.first()
            
            // 1. Scale the base monthly budget (affects categories using default logic)
            userPreferences.updateMonthlyBudget(currentMonthlyBase * ratio)
            
            // 2. Scale all manually set category budgets
            val manualBudgets = userPreferences.categoryBudgetsFlow.first()
            manualBudgets.forEach { (name, limit) ->
                userPreferences.updateCategoryBudget(name, limit * ratio)
            }
        }
    }

    /**
     * Updates a category budget and handles the overall target adjustment.
     * @param category The category name
     * @param newLimit The new target limit for this category
     * @param updateOverall If true, the total budget increases/decreases by the difference.
     *                      If false, other categories are adjusted to keep the total budget constant.
     */
    fun updateCategoryBudgetWithChoice(category: String, newLimit: Double, updateOverall: Boolean) {
        viewModelScope.launch {
            val currentCategories = userPreferences.categoryBudgetsFlow.first()
            val currentMonthlyBase = userPreferences.monthlyBudgetFlow.first()
            
            // Determine old limit
            val oldLimit = currentCategories[category] ?: when (category) {
                "Food & Dining" -> currentMonthlyBase * 0.2
                "Entertainment" -> currentMonthlyBase * 0.1
                "Transport" -> currentMonthlyBase * 0.1
                "Shopping" -> currentMonthlyBase * 0.3
                else -> currentMonthlyBase * 0.3
            }

            if (updateOverall) {
                // Total target naturally changes by the difference because Total = Sum(Categories)
                userPreferences.updateCategoryBudget(category, newLimit)
            } else {
                // Adjust others to keep Total constant
                val diff = newLimit - oldLimit
                
                // We need to know all categories currently in view to redistribute correctly
                val viewState = getBudgetViewState().first()
                val otherCategories = viewState.categories.filter { it.name != category }
                val totalOtherLimit = otherCategories.sumOf { it.limit }
                
                if (totalOtherLimit > 0) {
                    otherCategories.forEach { cat ->
                        // Reduce/Increase other categories proportionally
                        val adjustment = (cat.limit / totalOtherLimit) * diff
                        userPreferences.updateCategoryBudget(cat.name, (cat.limit - adjustment).coerceAtLeast(0.0))
                    }
                }
                userPreferences.updateCategoryBudget(category, newLimit)
            }
        }
    }

    private fun generateRecommendation(totalSpent: Double, totalLimit: Double, categories: List<CategoryBudget>): String {
        val remaining = totalLimit - totalSpent
        val formatter = NumberFormat.getCurrencyInstance()
        
        if (remaining < 0) {
            return "Budget Alert: You've exceeded your total limit by ${formatter.format(-remaining)}. We recommend adjusting your targets or cutting non-essential costs immediately."
        }
        
        val overBudgetCategory = categories.find { it.spent > it.limit }
        if (overBudgetCategory != null) {
            return "Note: '${overBudgetCategory.name}' is over its target. To stay within your ${formatter.format(totalLimit)} total, try saving in other areas this week."
        }
        
        if (totalSpent < totalLimit * 0.4) {
            return "On Track! You've only used ${(totalSpent/totalLimit*100).toInt()}% of your budget. You're set to save significantly for next month!"
        }
        
        return "Smart Tip: You have ${formatter.format(remaining)} left. Keeping your daily spend below ${formatter.format(remaining/15)} will help you meet your savings target."
    }
}

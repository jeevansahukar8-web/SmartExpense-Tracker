package app.expense.domain.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserDetails(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String
)

class UserPreferences(private val context: Context) {
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_FIRST_NAME = stringPreferencesKey("first_name")
        val USER_LAST_NAME = stringPreferencesKey("last_name")
        val USER_EMAIL = stringPreferencesKey("email")
        val USER_PHONE = stringPreferencesKey("phone")
        val MONTHLY_BUDGET = doublePreferencesKey("monthly_budget")
        val CATEGORY_BUDGETS = stringPreferencesKey("category_budgets") // Format: "Cat1:Limit1,Cat2:Limit2"
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val userDetailsFlow: Flow<UserDetails> = context.dataStore.data.map { preferences ->
        UserDetails(
            firstName = preferences[USER_FIRST_NAME] ?: "",
            lastName = preferences[USER_LAST_NAME] ?: "",
            email = preferences[USER_EMAIL] ?: "",
            phone = preferences[USER_PHONE] ?: ""
        )
    }

    val monthlyBudgetFlow: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[MONTHLY_BUDGET] ?: 5000.0
    }

    val categoryBudgetsFlow: Flow<Map<String, Double>> = context.dataStore.data.map { preferences ->
        val serialized = preferences[CATEGORY_BUDGETS] ?: ""
        if (serialized.isBlank()) emptyMap()
        else {
            serialized.split(",").associate {
                val parts = it.split(":")
                parts[0] to (parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0)
            }
        }
    }

    suspend fun saveUser(firstName: String, lastName: String, email: String, phone: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_FIRST_NAME] = firstName
            preferences[USER_LAST_NAME] = lastName
            preferences[USER_EMAIL] = email
            preferences[USER_PHONE] = phone
        }
    }

    suspend fun updateMonthlyBudget(budget: Double) {
        context.dataStore.edit { preferences ->
            preferences[MONTHLY_BUDGET] = budget
        }
    }

    suspend fun updateCategoryBudget(category: String, limit: Double) {
        context.dataStore.edit { preferences ->
            val serialized = preferences[CATEGORY_BUDGETS] ?: ""
            val map = if (serialized.isBlank()) mutableMapOf()
            else {
                serialized.split(",").associate {
                    val parts = it.split(":")
                    parts[0] to (parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0)
                }.toMutableMap()
            }
            map[category] = limit
            preferences[CATEGORY_BUDGETS] = map.entries.joinToString(",") { "${it.key}:${it.value}" }
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

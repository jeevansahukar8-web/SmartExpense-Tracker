package app.expense.tracker.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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

    suspend fun saveUser(firstName: String, lastName: String, email: String, phone: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_FIRST_NAME] = firstName
            preferences[USER_LAST_NAME] = lastName
            preferences[USER_EMAIL] = email
            preferences[USER_PHONE] = phone
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

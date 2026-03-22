package app.expense.api

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.expense.db.daos.UserDAO
import app.expense.db.model.UserDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthAPI(
    private val userDAO: UserDAO,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val LOGGED_IN_USER_EMAIL = stringPreferencesKey("logged_in_user_email")
    }

    suspend fun login(email: String, pass: String): UserDTO? {
        val user = userDAO.login(email, pass)
        if (user != null) {
            dataStore.edit { 
                it[IS_LOGGED_IN] = true 
                it[LOGGED_IN_USER_EMAIL] = email
            }
        }
        return user
    }

    suspend fun register(email: String, pass: String, name: String) {
        userDAO.register(UserDTO(email, pass, name))
    }

    suspend fun logout() {
        dataStore.edit { 
            it[IS_LOGGED_IN] = false 
            it[LOGGED_IN_USER_EMAIL] = ""
        }
    }

    fun isUserLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    }

    fun getLoggedInUserEmail(): Flow<String?> {
        return dataStore.data.map { it[LOGGED_IN_USER_EMAIL] }
    }

    suspend fun getUser(email: String): UserDTO? {
        return userDAO.getUser(email)
    }

    suspend fun updateUserName(email: String, newName: String) {
        userDAO.updateName(email, newName)
    }

    suspend fun deleteAccount(email: String) {
        userDAO.deleteAccount(email)
        logout()
    }
}

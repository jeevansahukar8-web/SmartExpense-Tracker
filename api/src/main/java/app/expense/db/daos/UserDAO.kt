package app.expense.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.expense.db.model.UserDTO

@Dao
interface UserDAO {
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserDTO?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun register(user: UserDTO)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUser(email: String): UserDTO?

    @Query("UPDATE users SET name = :newName WHERE email = :email")
    suspend fun updateName(email: String, newName: String)

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteAccount(email: String)
}

package app.expense.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserDTO(
    @PrimaryKey val email: String,
    val password: String,
    val name: String
)

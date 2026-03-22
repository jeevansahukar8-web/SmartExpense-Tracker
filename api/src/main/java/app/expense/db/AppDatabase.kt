package app.expense.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.expense.db.daos.CategoryDAO
import app.expense.db.daos.ExpenseDAO
import app.expense.db.daos.PaidToDAO
import app.expense.db.daos.SuggestionDAO
import app.expense.db.daos.UserDAO
import app.expense.db.model.CategoryDTO
import app.expense.db.model.ExpenseDTO
import app.expense.db.model.PaidToDTO
import app.expense.db.model.SuggestionDTO
import app.expense.db.model.UserDTO

@Database(
    entities = [SuggestionDTO::class, ExpenseDTO::class, CategoryDTO::class, PaidToDTO::class, UserDTO::class],
    version = 3
)
@TypeConverters(ModelConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun suggestionDAO(): SuggestionDAO
    abstract fun expenseDAO(): ExpenseDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun paidToDAO(): PaidToDAO
    abstract fun userDAO(): UserDAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE suggestion ADD COLUMN is_expense INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`email` TEXT NOT NULL, `password` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`email`))")
            }
        }
    }
}

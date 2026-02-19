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
import app.expense.db.model.CategoryDTO
import app.expense.db.model.ExpenseDTO
import app.expense.db.model.PaidToDTO
import app.expense.db.model.SuggestionDTO

@Database(
    entities = [SuggestionDTO::class, ExpenseDTO::class, CategoryDTO::class, PaidToDTO::class],
    version = 2
)
@TypeConverters(ModelConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun suggestionDAO(): SuggestionDAO
    abstract fun expenseDAO(): ExpenseDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun paidToDAO(): PaidToDAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE suggestion ADD COLUMN is_expense INTEGER NOT NULL DEFAULT 1")
            }
        }
    }
}

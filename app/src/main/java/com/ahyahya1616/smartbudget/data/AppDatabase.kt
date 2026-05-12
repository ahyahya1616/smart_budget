package com.ahyahya1616.smartbudget.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ahyahya1616.smartbudget.data.dao.CategoryDao
import com.ahyahya1616.smartbudget.data.dao.ExpenseDao
import com.ahyahya1616.smartbudget.data.model.Category
import com.ahyahya1616.smartbudget.data.model.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [Category::class, Expense::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_budget_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.categoryDao(), database.expenseDao())
                }
            }
        }

        private suspend fun populateDatabase(categoryDao: CategoryDao, expenseDao: ExpenseDao) {
            // 1. Initial Categories
            val categories = listOf(
                Category(name = "Alimentation", icon = "restaurant", color = "#FF5722"),
                Category(name = "Transport", icon = "directions_bus", color = "#2196F3"),
                Category(name = "Logement", icon = "home", color = "#4CAF50"),
                Category(name = "Santé", icon = "local_hospital", color = "#E91E63"),
                Category(name = "Loisirs", icon = "movie", color = "#9C27B0"),
                Category(name = "Études", icon = "school", color = "#FF9800"),
                Category(name = "Autre", icon = "category", color = "#795548")
            )
            
            val catIds = categories.map { categoryDao.insertCategory(it) }

            // 2. Dummy Expenses (spread over current and previous month)
            val expenses = mutableListOf<Expense>()
            val calendar = Calendar.getInstance()
            
            // Generate some expenses for current month
            for (i in 1..15) {
                calendar.set(Calendar.DAY_OF_MONTH, (1..28).random())
                expenses.add(
                    Expense(
                        amount = (10..500).random().toDouble() + 0.5,
                        date = calendar.timeInMillis,
                        categoryId = catIds.random(),
                        note = "Sample expense $i (Current Month)",
                        paymentMethod = listOf("Card", "Cash").random()
                    )
                )
            }

            // Generate some expenses for previous month
            calendar.add(Calendar.MONTH, -1)
            for (i in 16..30) {
                calendar.set(Calendar.DAY_OF_MONTH, (1..28).random())
                expenses.add(
                    Expense(
                        amount = (10..500).random().toDouble() + 0.5,
                        date = calendar.timeInMillis,
                        categoryId = catIds.random(),
                        note = "Sample expense $i (Previous Month)",
                        paymentMethod = listOf("Card", "Cash").random()
                    )
                )
            }

            expenseDao.insertExpenses(expenses)
        }
    }
}

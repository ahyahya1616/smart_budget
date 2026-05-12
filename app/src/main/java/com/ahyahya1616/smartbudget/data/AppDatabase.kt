package com.ahyahya1616.smartbudget.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ahyahya1616.smartbudget.data.dao.CategoryDao
import com.ahyahya1616.smartbudget.data.dao.ExpenseDao
import com.ahyahya1616.smartbudget.data.dao.MonthlyBudgetDao
import com.ahyahya1616.smartbudget.data.model.Category
import com.ahyahya1616.smartbudget.data.model.Expense
import com.ahyahya1616.smartbudget.data.model.MonthlyBudget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [Category::class, Expense::class, MonthlyBudget::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun monthlyBudgetDao(): MonthlyBudgetDao

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
                .fallbackToDestructiveMigration()
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
                    populateDatabase(
                        database.categoryDao(),
                        database.expenseDao(),
                        database.monthlyBudgetDao()
                    )
                }
            }
        }

        private suspend fun populateDatabase(
            categoryDao: CategoryDao,
            expenseDao: ExpenseDao,
            budgetDao: MonthlyBudgetDao
        ) {
            // ─── 1. Categories (8 active) ────────────────────────────────
            val categories = listOf(
                Category(name = "Alimentation", icon = "🍽️", color = "#FF5722"),
                Category(name = "Transport", icon = "🚌", color = "#42A5F5"),
                Category(name = "Logement", icon = "🏠", color = "#66BB6A"),
                Category(name = "Santé", icon = "🏥", color = "#EC407A"),
                Category(name = "Loisirs", icon = "🎬", color = "#AB47BC"),
                Category(name = "Études", icon = "📚", color = "#FFA726"),
                Category(name = "Vêtements", icon = "👕", color = "#26C6DA"),
                Category(name = "Autre", icon = "📦", color = "#8D6E63")
            )

            val catIds = categories.map { categoryDao.insertCategory(it) }

            // ─── 2. Expenses — 35 entries across 2 months ────────────────
            val now = Calendar.getInstance()
            val currentYear = now.get(Calendar.YEAR)
            val currentMonth = now.get(Calendar.MONTH)

            // Helper to build a date timestamp
            fun dateOf(year: Int, month: Int, day: Int): Long {
                return Calendar.getInstance().apply {
                    set(year, month, day, 10, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }

            // Current month expenses (20)
            val currentExpenses = listOf(
                Expense(amount = 45.50, date = dateOf(currentYear, currentMonth, 2), categoryId = catIds[0], note = "Courses au marché", paymentMethod = "Cash"),
                Expense(amount = 120.00, date = dateOf(currentYear, currentMonth, 3), categoryId = catIds[1], note = "Abonnement tramway", paymentMethod = "Card"),
                Expense(amount = 35.00, date = dateOf(currentYear, currentMonth, 4), categoryId = catIds[4], note = "Cinéma", paymentMethod = "Cash"),
                Expense(amount = 200.00, date = dateOf(currentYear, currentMonth, 5), categoryId = catIds[5], note = "Livres universitaires", paymentMethod = "Card"),
                Expense(amount = 80.00, date = dateOf(currentYear, currentMonth, 6), categoryId = catIds[3], note = "Pharmacie", paymentMethod = "Cash"),
                Expense(amount = 55.00, date = dateOf(currentYear, currentMonth, 7), categoryId = catIds[0], note = "Restaurant midi", paymentMethod = "Card"),
                Expense(amount = 1500.00, date = dateOf(currentYear, currentMonth, 1), categoryId = catIds[2], note = "Loyer mensuel", paymentMethod = "Virement"),
                Expense(amount = 25.00, date = dateOf(currentYear, currentMonth, 8), categoryId = catIds[1], note = "Taxi", paymentMethod = "Cash"),
                Expense(amount = 150.00, date = dateOf(currentYear, currentMonth, 9), categoryId = catIds[6], note = "T-shirt et pantalon", paymentMethod = "Card"),
                Expense(amount = 30.00, date = dateOf(currentYear, currentMonth, 10), categoryId = catIds[0], note = "Fruits et légumes", paymentMethod = "Cash"),
                Expense(amount = 90.00, date = dateOf(currentYear, currentMonth, 11), categoryId = catIds[4], note = "Concert", paymentMethod = "Card"),
                Expense(amount = 15.00, date = dateOf(currentYear, currentMonth, 12), categoryId = catIds[7], note = "Recharge téléphone", paymentMethod = "Cash"),
                Expense(amount = 60.00, date = dateOf(currentYear, currentMonth, 13), categoryId = catIds[0], note = "Supermarché", paymentMethod = "Card"),
                Expense(amount = 40.00, date = dateOf(currentYear, currentMonth, 14), categoryId = catIds[1], note = "Essence", paymentMethod = "Card"),
                Expense(amount = 250.00, date = dateOf(currentYear, currentMonth, 15), categoryId = catIds[3], note = "Consultation médecin", paymentMethod = "Cash"),
                Expense(amount = 20.00, date = dateOf(currentYear, currentMonth, 16), categoryId = catIds[4], note = "Jeu vidéo", paymentMethod = "Card"),
                Expense(amount = 75.00, date = dateOf(currentYear, currentMonth, 17), categoryId = catIds[5], note = "Fournitures scolaires", paymentMethod = "Cash"),
                Expense(amount = 110.00, date = dateOf(currentYear, currentMonth, 18), categoryId = catIds[6], note = "Chaussures", paymentMethod = "Card"),
                Expense(amount = 42.00, date = dateOf(currentYear, currentMonth, 19), categoryId = catIds[0], note = "Boulangerie", paymentMethod = "Cash"),
                Expense(amount = 300.00, date = dateOf(currentYear, currentMonth, 20), categoryId = catIds[2], note = "Facture électricité", paymentMethod = "Virement")
            )

            // Previous month expenses (15)
            val prevMonth = if (currentMonth == 0) 11 else currentMonth - 1
            val prevYear = if (currentMonth == 0) currentYear - 1 else currentYear

            val prevExpenses = listOf(
                Expense(amount = 50.00, date = dateOf(prevYear, prevMonth, 1), categoryId = catIds[0], note = "Épicerie", paymentMethod = "Cash"),
                Expense(amount = 1500.00, date = dateOf(prevYear, prevMonth, 1), categoryId = catIds[2], note = "Loyer mensuel", paymentMethod = "Virement"),
                Expense(amount = 120.00, date = dateOf(prevYear, prevMonth, 3), categoryId = catIds[1], note = "Abonnement tramway", paymentMethod = "Card"),
                Expense(amount = 180.00, date = dateOf(prevYear, prevMonth, 5), categoryId = catIds[3], note = "Dentiste", paymentMethod = "Cash"),
                Expense(amount = 65.00, date = dateOf(prevYear, prevMonth, 7), categoryId = catIds[0], note = "Restaurant", paymentMethod = "Card"),
                Expense(amount = 45.00, date = dateOf(prevYear, prevMonth, 9), categoryId = catIds[4], note = "Bowling", paymentMethod = "Cash"),
                Expense(amount = 300.00, date = dateOf(prevYear, prevMonth, 10), categoryId = catIds[5], note = "Inscription formation", paymentMethod = "Card"),
                Expense(amount = 95.00, date = dateOf(prevYear, prevMonth, 12), categoryId = catIds[6], note = "Veste", paymentMethod = "Card"),
                Expense(amount = 30.00, date = dateOf(prevYear, prevMonth, 14), categoryId = catIds[1], note = "Taxi", paymentMethod = "Cash"),
                Expense(amount = 280.00, date = dateOf(prevYear, prevMonth, 15), categoryId = catIds[2], note = "Facture eau + internet", paymentMethod = "Virement"),
                Expense(amount = 40.00, date = dateOf(prevYear, prevMonth, 18), categoryId = catIds[0], note = "Courses", paymentMethod = "Cash"),
                Expense(amount = 25.00, date = dateOf(prevYear, prevMonth, 20), categoryId = catIds[7], note = "Cadeau", paymentMethod = "Cash"),
                Expense(amount = 60.00, date = dateOf(prevYear, prevMonth, 22), categoryId = catIds[4], note = "Piscine", paymentMethod = "Card"),
                Expense(amount = 35.00, date = dateOf(prevYear, prevMonth, 25), categoryId = catIds[0], note = "Pain et pâtisseries", paymentMethod = "Cash"),
                Expense(amount = 200.00, date = dateOf(prevYear, prevMonth, 28), categoryId = catIds[6], note = "Chaussures sport", paymentMethod = "Card")
            )

            expenseDao.insertExpenses(currentExpenses + prevExpenses)

            // ─── 3. Monthly Budgets (bonus) ──────────────────────────────
            val currentMonthStr = String.format("%04d-%02d", currentYear, currentMonth + 1)
            val prevMonthStr = String.format("%04d-%02d", prevYear, prevMonth + 1)

            val budgets = listOf(
                MonthlyBudget(month = currentMonthStr, categoryId = catIds[0], limitAmount = 500.0),
                MonthlyBudget(month = currentMonthStr, categoryId = catIds[1], limitAmount = 300.0),
                MonthlyBudget(month = currentMonthStr, categoryId = catIds[2], limitAmount = 2000.0),
                MonthlyBudget(month = currentMonthStr, categoryId = catIds[3], limitAmount = 400.0),
                MonthlyBudget(month = currentMonthStr, categoryId = catIds[4], limitAmount = 200.0),
                MonthlyBudget(month = currentMonthStr, categoryId = catIds[5], limitAmount = 350.0),
                MonthlyBudget(month = prevMonthStr, categoryId = catIds[0], limitAmount = 500.0),
                MonthlyBudget(month = prevMonthStr, categoryId = catIds[1], limitAmount = 300.0),
                MonthlyBudget(month = prevMonthStr, categoryId = catIds[2], limitAmount = 2000.0)
            )
            budgets.forEach { budgetDao.insertBudget(it) }
        }
    }
}

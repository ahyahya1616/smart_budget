package com.ahyahya1616.smartbudget.repository

import com.ahyahya1616.smartbudget.data.model.Category
import com.ahyahya1616.smartbudget.data.model.Expense
import com.ahyahya1616.smartbudget.data.model.ExpenseWithCategory
import com.ahyahya1616.smartbudget.data.model.MonthlyBudget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    // ─── Categories ──────────────────────────────────────────────────
    fun getAllCategories(): Flow<List<Category>>
    fun getActiveCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: Long): Category?
    suspend fun getCategoryByName(name: String): Category?
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)

    // ─── Expenses ────────────────────────────────────────────────────
    fun getAllExpenses(): Flow<List<ExpenseWithCategory>>
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>>
    suspend fun getExpenseById(id: Long): Expense?
    suspend fun getExpenseWithCategoryById(id: Long): ExpenseWithCategory?
    suspend fun insertExpense(expense: Expense): Long
    suspend fun insertExpenses(expenses: List<Expense>)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)

    // ─── Monthly Budgets ─────────────────────────────────────────────
    fun getBudgetsForMonth(month: String): Flow<List<MonthlyBudget>>
    suspend fun getBudget(month: String, categoryId: Long): MonthlyBudget?
    suspend fun insertBudget(budget: MonthlyBudget): Long
    suspend fun updateBudget(budget: MonthlyBudget)
    suspend fun deleteBudget(budget: MonthlyBudget)
}

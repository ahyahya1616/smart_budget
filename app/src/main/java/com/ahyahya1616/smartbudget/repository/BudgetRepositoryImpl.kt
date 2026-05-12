package com.ahyahya1616.smartbudget.repository

import com.ahyahya1616.smartbudget.data.dao.CategoryDao
import com.ahyahya1616.smartbudget.data.dao.ExpenseDao
import com.ahyahya1616.smartbudget.data.dao.MonthlyBudgetDao
import com.ahyahya1616.smartbudget.data.model.Category
import com.ahyahya1616.smartbudget.data.model.Expense
import com.ahyahya1616.smartbudget.data.model.ExpenseWithCategory
import com.ahyahya1616.smartbudget.data.model.MonthlyBudget
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
    private val monthlyBudgetDao: MonthlyBudgetDao
) : BudgetRepository {

    // ─── Categories ──────────────────────────────────────────────────
    override fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    override fun getActiveCategories(): Flow<List<Category>> = categoryDao.getActiveCategories()
    override suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
    override suspend fun getCategoryByName(name: String): Category? = categoryDao.getCategoryByName(name)
    override suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    override suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    override suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    // ─── Expenses ────────────────────────────────────────────────────
    override fun getAllExpenses(): Flow<List<ExpenseWithCategory>> = expenseDao.getAllExpenses()
    override fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate)
    }
    override suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)
    override suspend fun getExpenseWithCategoryById(id: Long): ExpenseWithCategory? = expenseDao.getExpenseWithCategoryById(id)
    override suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    override suspend fun insertExpenses(expenses: List<Expense>) = expenseDao.insertExpenses(expenses)
    override suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    override suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    // ─── Monthly Budgets ─────────────────────────────────────────────
    override fun getBudgetsForMonth(month: String): Flow<List<MonthlyBudget>> = monthlyBudgetDao.getBudgetsForMonth(month)
    override suspend fun getBudget(month: String, categoryId: Long): MonthlyBudget? = monthlyBudgetDao.getBudget(month, categoryId)
    override suspend fun insertBudget(budget: MonthlyBudget): Long = monthlyBudgetDao.insertBudget(budget)
    override suspend fun updateBudget(budget: MonthlyBudget) = monthlyBudgetDao.updateBudget(budget)
    override suspend fun deleteBudget(budget: MonthlyBudget) = monthlyBudgetDao.deleteBudget(budget)
}

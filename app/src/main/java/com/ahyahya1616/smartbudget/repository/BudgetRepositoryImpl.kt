package com.ahyahya1616.smartbudget.repository

import com.ahyahya1616.smartbudget.data.dao.CategoryDao
import com.ahyahya1616.smartbudget.data.dao.ExpenseDao
import com.ahyahya1616.smartbudget.data.model.Category
import com.ahyahya1616.smartbudget.data.model.Expense
import com.ahyahya1616.smartbudget.data.model.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao
) : BudgetRepository {

    override fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    override fun getActiveCategories(): Flow<List<Category>> = categoryDao.getActiveCategories()
    override suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
    override suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    override suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    override suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    override fun getAllExpenses(): Flow<List<ExpenseWithCategory>> = expenseDao.getAllExpenses()
    override fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate)
    }
    override suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)
    override suspend fun getExpenseWithCategoryById(id: Long): ExpenseWithCategory? = expenseDao.getExpenseWithCategoryById(id)
    override suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    override suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    override suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
}

package com.ahyahya1616.smartbudget.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.ahyahya1616.smartbudget.data.model.MonthlyBudget
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyBudgetDao {

    @Query("SELECT * FROM monthly_budgets WHERE month = :month ORDER BY categoryId ASC")
    fun getBudgetsForMonth(month: String): Flow<List<MonthlyBudget>>

    @Query("SELECT * FROM monthly_budgets WHERE month = :month AND categoryId = :categoryId LIMIT 1")
    suspend fun getBudget(month: String, categoryId: Long): MonthlyBudget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: MonthlyBudget): Long

    @Update
    suspend fun updateBudget(budget: MonthlyBudget)

    @Delete
    suspend fun deleteBudget(budget: MonthlyBudget)

    @Query("DELETE FROM monthly_budgets WHERE month = :month AND categoryId = :categoryId")
    suspend fun deleteBudgetForCategory(month: String, categoryId: Long)
}

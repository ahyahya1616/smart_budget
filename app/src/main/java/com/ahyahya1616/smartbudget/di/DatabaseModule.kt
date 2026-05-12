package com.ahyahya1616.smartbudget.di

import android.content.Context
import com.ahyahya1616.smartbudget.data.AppDatabase
import com.ahyahya1616.smartbudget.data.dao.CategoryDao
import com.ahyahya1616.smartbudget.data.dao.ExpenseDao
import com.ahyahya1616.smartbudget.data.dao.MonthlyBudgetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    fun provideMonthlyBudgetDao(database: AppDatabase): MonthlyBudgetDao {
        return database.monthlyBudgetDao()
    }
}

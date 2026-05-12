package com.ahyahya1616.smartbudget.di

import com.ahyahya1616.smartbudget.repository.BudgetRepository
import com.ahyahya1616.smartbudget.repository.BudgetRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindBudgetRepository(
        budgetRepositoryImpl: BudgetRepositoryImpl
    ): BudgetRepository
}

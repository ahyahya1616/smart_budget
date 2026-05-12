package com.ahyahya1616.smartbudget.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahyahya1616.smartbudget.data.model.Category
import com.ahyahya1616.smartbudget.data.model.Expense
import com.ahyahya1616.smartbudget.data.model.ExpenseWithCategory
import com.ahyahya1616.smartbudget.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class BudgetUiState(
    val selectedYear: Int,
    val selectedMonth: Int, // 0-based for Calendar.JANUARY
    val expenses: List<ExpenseWithCategory> = emptyList(),
    val totalAmount: Double = 0.0,
    val topCategories: List<Pair<Category, Double>> = emptyList()
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: BudgetRepository
) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val _selectedDate = MutableStateFlow(Pair(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)))

    val categories: StateFlow<List<Category>> = repository.getActiveCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BudgetUiState> = _selectedDate.flatMapLatest { (year, month) ->
        val startCal = Calendar.getInstance().apply {
            set(year, month, 1, 0, 0, 0)
        }
        val endCal = Calendar.getInstance().apply {
            set(year, month, startCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        }
        
        repository.getExpensesBetweenDates(startCal.timeInMillis, endCal.timeInMillis)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()).let { expensesFlow ->
        // Convert Flow<List<Expense...>> to Flow<BudgetUiState>
        val stateFlow = MutableStateFlow(BudgetUiState(_selectedDate.value.first, _selectedDate.value.second))
        viewModelScope.launch {
            expensesFlow.collect { expenses ->
                val total = expenses.sumOf { it.expense.amount }
                val groups = expenses.groupBy { it.category }
                val topCats = groups.map { entry ->
                    entry.key to entry.value.sumOf { it.expense.amount }
                }.sortedByDescending { it.second }

                stateFlow.value = stateFlow.value.copy(
                    expenses = expenses,
                    totalAmount = total,
                    topCategories = topCats
                )
            }
        }
        viewModelScope.launch {
            _selectedDate.collect { (year, month) ->
                stateFlow.value = stateFlow.value.copy(selectedYear = year, selectedMonth = month)
            }
        }
        stateFlow
    }

    fun nextMonth() {
        val current = _selectedDate.value
        val nextMonth = if (current.second == 11) 0 else current.second + 1
        val nextYear = if (current.second == 11) current.first + 1 else current.first
        _selectedDate.value = Pair(nextYear, nextMonth)
    }

    fun prevMonth() {
        val current = _selectedDate.value
        val prevMonth = if (current.second == 0) 11 else current.second - 1
        val prevYear = if (current.second == 0) current.first - 1 else current.first
        _selectedDate.value = Pair(prevYear, prevMonth)
    }

    fun addExpense(amount: Double, dateMillis: Long, categoryId: Long, note: String) {
        viewModelScope.launch {
            repository.insertExpense(
                Expense(
                    amount = amount,
                    date = dateMillis,
                    categoryId = categoryId,
                    note = note
                )
            )
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}

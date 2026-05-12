package com.ahyahya1616.smartbudget.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahyahya1616.smartbudget.data.model.Category
import com.ahyahya1616.smartbudget.data.model.Expense
import com.ahyahya1616.smartbudget.data.model.ExpenseWithCategory
import com.ahyahya1616.smartbudget.data.model.MonthlyBudget
import com.ahyahya1616.smartbudget.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

enum class SortBy { DATE, AMOUNT }

data class CategoryStat(
    val category: Category,
    val total: Double,
    val percentage: Double,
    val budgetLimit: Double? = null
)

data class BudgetUiState(
    val selectedYear: Int,
    val selectedMonth: Int, // 0-indexed
    val expenses: List<ExpenseWithCategory> = emptyList(),
    val filteredExpenses: List<ExpenseWithCategory> = emptyList(),
    val totalAmount: Double = 0.0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val topCategories: List<Pair<Category, Double>> = emptyList(),
    val filterCategoryId: Long? = null,
    val sortBy: SortBy = SortBy.DATE,
    val budgets: List<MonthlyBudget> = emptyList(),
    // Previous month data for comparison
    val previousMonthTotal: Double = 0.0,
    val previousMonthCategoryTotals: Map<Long, Double> = emptyMap()
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: BudgetRepository
) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val _selectedDate = MutableStateFlow(
        Pair(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
    )
    private val _filterCategoryId = MutableStateFlow<Long?>(null)
    private val _sortBy = MutableStateFlow(SortBy.DATE)

    val activeCategories: StateFlow<List<Category>> = repository.getActiveCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Helper to get date range for a given year/month
    private fun getDateRange(year: Int, month: Int): Pair<Long, Long> {
        val startCal = Calendar.getInstance().apply {
            set(year, month, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            set(year, month, startCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return startCal.timeInMillis to endCal.timeInMillis
    }

    private fun getMonthString(year: Int, month: Int): String {
        return String.format("%04d-%02d", year, month + 1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentMonthExpenses = _selectedDate.flatMapLatest { (year, month) ->
        val (start, end) = getDateRange(year, month)
        repository.getExpensesBetweenDates(start, end)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val previousMonthExpenses = _selectedDate.flatMapLatest { (year, month) ->
        val prevMonth = if (month == 0) 11 else month - 1
        val prevYear = if (month == 0) year - 1 else year
        val (start, end) = getDateRange(prevYear, prevMonth)
        repository.getExpensesBetweenDates(start, end)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentBudgets = _selectedDate.flatMapLatest { (year, month) ->
        repository.getBudgetsForMonth(getMonthString(year, month))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BudgetUiState> = combine(
        currentMonthExpenses,
        previousMonthExpenses,
        currentBudgets,
        _selectedDate,
        _filterCategoryId,
        _sortBy
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val expenses = values[0] as List<ExpenseWithCategory>
        @Suppress("UNCHECKED_CAST")
        val prevExpenses = values[1] as List<ExpenseWithCategory>
        @Suppress("UNCHECKED_CAST")
        val budgets = values[2] as List<MonthlyBudget>
        @Suppress("UNCHECKED_CAST")
        val date = values[3] as Pair<Int, Int>
        val filterId = values[4] as Long?
        val sort = values[5] as SortBy

        val total = expenses.sumOf { it.expense.amount }
        val prevTotal = prevExpenses.sumOf { it.expense.amount }

        val prevCategoryTotals = prevExpenses
            .groupBy { it.category.id }
            .mapValues { entry -> entry.value.sumOf { it.expense.amount } }

        val filtered = expenses
            .filter { filterId == null || it.category.id == filterId }
            .let { list ->
                when (sort) {
                    SortBy.DATE -> list.sortedByDescending { it.expense.date }
                    SortBy.AMOUNT -> list.sortedByDescending { it.expense.amount }
                }
            }

        val groups = expenses.groupBy { it.category }
        val topCats = groups.map { entry ->
            entry.key to entry.value.sumOf { it.expense.amount }
        }.sortedByDescending { it.second }

        val categoryStats = topCats.map { (category, catTotal) ->
            val percentage = if (total > 0) (catTotal / total) * 100 else 0.0
            val budgetLimit = budgets.find { it.categoryId == category.id }?.limitAmount
            CategoryStat(
                category = category,
                total = catTotal,
                percentage = percentage,
                budgetLimit = budgetLimit
            )
        }

        BudgetUiState(
            selectedYear = date.first,
            selectedMonth = date.second,
            expenses = expenses,
            filteredExpenses = filtered,
            totalAmount = total,
            categoryStats = categoryStats,
            topCategories = topCats,
            filterCategoryId = filterId,
            sortBy = sort,
            budgets = budgets,
            previousMonthTotal = prevTotal,
            previousMonthCategoryTotals = prevCategoryTotals
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BudgetUiState(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
    )

    // ─── Navigation ──────────────────────────────────────────────────

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

    // ─── Filtering & Sorting ─────────────────────────────────────────

    fun setCategoryFilter(categoryId: Long?) {
        _filterCategoryId.value = categoryId
    }

    fun setSortBy(sortBy: SortBy) {
        _sortBy.value = sortBy
    }

    // ─── Expense CRUD ────────────────────────────────────────────────

    fun addExpense(amount: Double, dateMillis: Long, categoryId: Long, note: String, paymentMethod: String? = null) {
        viewModelScope.launch {
            repository.insertExpense(
                Expense(
                    amount = amount,
                    date = dateMillis,
                    categoryId = categoryId,
                    note = note,
                    paymentMethod = paymentMethod
                )
            )
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    suspend fun getExpenseById(id: Long): Expense? {
        return repository.getExpenseById(id)
    }

    // ─── Category Management ─────────────────────────────────────────

    fun addCategory(name: String, color: String = "#9E9E9E", icon: String = "📦") {
        viewModelScope.launch {
            repository.insertCategory(Category(name = name, color = color, icon = icon))
        }
    }

    fun toggleCategoryActive(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category.copy(isActive = !category.isActive))
        }
    }

    // ─── Monthly Budget Management ───────────────────────────────────

    fun setBudget(categoryId: Long, limitAmount: Double) {
        viewModelScope.launch {
            val date = _selectedDate.value
            val monthStr = getMonthString(date.first, date.second)
            val existing = repository.getBudget(monthStr, categoryId)
            if (existing != null) {
                repository.updateBudget(existing.copy(limitAmount = limitAmount))
            } else {
                repository.insertBudget(
                    MonthlyBudget(month = monthStr, categoryId = categoryId, limitAmount = limitAmount)
                )
            }
        }
    }

    fun removeBudget(budget: MonthlyBudget) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    // ─── CSV Export ──────────────────────────────────────────────────

    fun exportToCSV(): String {
        val state = uiState.value
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val monthNames = listOf(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        )
        val monthHeader = "${monthNames[state.selectedMonth]} ${state.selectedYear}"

        val header = "Catégorie,Montant (MAD),Date,Note,Méthode de paiement\n"
        val rows = state.expenses.joinToString("\n") { expenseWithCat ->
            val expense = expenseWithCat.expense
            val category = expenseWithCat.category
            val escapedNote = (expense.note ?: "").replace("\"", "\"\"")
            "\"${category.name}\",${String.format(Locale.FRANCE, "%.2f", expense.amount)},${dateFormat.format(Date(expense.date))},\"$escapedNote\",${expense.paymentMethod ?: ""}"
        }

        return if (state.expenses.isEmpty()) {
            "Aucune dépense pour $monthHeader\n"
        } else {
            "$monthHeader\n$header$rows"
        }
    }

    // ─── CSV Import ──────────────────────────────────────────────────

    fun importFromCSV(csvContent: String): ImportResult {
        var imported = 0
        var errors = 0
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

        viewModelScope.launch {
            val lines = csvContent.lines().filter { it.isNotBlank() }
            // Skip header lines (month title + column headers)
            val dataLines = lines.drop(2)

            for (line in dataLines) {
                try {
                    val parts = parseCsvLine(line)
                    if (parts.size >= 3) {
                        val categoryName = parts[0].trim().removeSurrounding("\"")
                        val amountStr = parts[1].trim().replace(",", ".").replace(" ", "")
                        val dateStr = parts[2].trim()
                        val note = if (parts.size > 3) parts[3].trim().removeSurrounding("\"") else ""
                        val paymentMethod = if (parts.size > 4) parts[4].trim() else null

                        val amount = amountStr.toDoubleOrNull()
                        val date = try { dateFormat.parse(dateStr)?.time } catch (_: Exception) { null }

                        if (amount != null && amount > 0 && date != null) {
                            // Find or create category
                            var category = repository.getCategoryByName(categoryName)
                            if (category == null) {
                                val id = repository.insertCategory(
                                    Category(name = categoryName, icon = "📦", color = "#9E9E9E")
                                )
                                category = repository.getCategoryById(id)
                            }

                            if (category != null) {
                                repository.insertExpense(
                                    Expense(
                                        amount = amount,
                                        date = date,
                                        categoryId = category.id,
                                        note = note.ifBlank { null },
                                        paymentMethod = paymentMethod?.ifBlank { null }
                                    )
                                )
                                imported++
                            } else {
                                errors++
                            }
                        } else {
                            errors++
                        }
                    } else {
                        errors++
                    }
                } catch (_: Exception) {
                    errors++
                }
            }
        }

        return ImportResult(imported, errors)
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false

        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
        }
        result.add(current.toString())
        return result
    }
}

data class ImportResult(val imported: Int, val errors: Int)

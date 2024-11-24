package com.example.splitpro.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

data class ExpenseDataPoint(
    val date: LocalDate,
    val amount: Float,
    val description: String = "",
    val category: String = ""
)

data class ExpenseSummary(
    val totalAmount: Float,
    val numberOfTransactions: Int,
    val percentageChange: Float
)

data class BiggestExpense(
    val amount: Float,
    val description: String,
    val category: String,
    val date: LocalDate
)

data class HomeScreenState(
    val monthlyExpenses: List<ExpenseDataPoint> = emptyList(),
    val weeklyExpenses: List<ExpenseDataPoint> = emptyList(),
    val weekSummary: ExpenseSummary = ExpenseSummary(0f, 0, 0f),
    val monthSummary: ExpenseSummary = ExpenseSummary(0f, 0, 0f),
    val topExpenses: List<BiggestExpense> = emptyList()
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    init {
        // Sample data for monthly expenses
        val monthlyExpenses = listOf(
            ExpenseDataPoint(
                date = LocalDate.now().minusDays(30),
                amount = 1500f,
                description = "Groceries",
                category = "Food"
            ),
            ExpenseDataPoint(
                date = LocalDate.now().minusDays(25),
                amount = 2000f,
                description = "Rent",
                category = "Housing"
            ),
            ExpenseDataPoint(
                date = LocalDate.now().minusDays(20),
                amount = 800f,
                description = "Utilities",
                category = "Bills"
            ),
            ExpenseDataPoint(
                date = LocalDate.now().minusDays(15),
                amount = 1200f,
                description = "Shopping",
                category = "Personal"
            ),
            ExpenseDataPoint(
                date = LocalDate.now().minusDays(10),
                amount = 1800f,
                description = "Dining Out",
                category = "Food"
            ),
            ExpenseDataPoint(
                date = LocalDate.now().minusDays(5),
                amount = 500f,
                description = "Transportation",
                category = "Travel"
            ),
            ExpenseDataPoint(
                date = LocalDate.now(),
                amount = 1000f,
                description = "Entertainment",
                category = "Personal"
            )
        )

        // Sample data for top 3 expenses
        val topExpenses = listOf(
            BiggestExpense(
                amount = 2000f,
                description = "Rent",
                category = "Housing",
                date = LocalDate.now().minusDays(25)
            ),
            BiggestExpense(
                amount = 1800f,
                description = "Dining Out",
                category = "Food",
                date = LocalDate.now().minusDays(10)
            ),
            BiggestExpense(
                amount = 1500f,
                description = "Groceries",
                category = "Food",
                date = LocalDate.now().minusDays(30)
            )
        )

        // Sample data for summary cards
        val weekSummary = ExpenseSummary(
            totalAmount = 3300f,
            numberOfTransactions = 10,
            percentageChange = 15f
        )

        val monthSummary = ExpenseSummary(
            totalAmount = 8800f,
            numberOfTransactions = 20,
            percentageChange = 22f
        )

        _uiState.value = HomeScreenState(
            monthlyExpenses = monthlyExpenses,
            weeklyExpenses = monthlyExpenses.takeLast(3),
            weekSummary = weekSummary,
            monthSummary = monthSummary,
            topExpenses = topExpenses
        )
    }
}

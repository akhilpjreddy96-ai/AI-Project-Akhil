package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class FinanceRepository(private val financeDao: FinanceDao) {

    val allExpenses: Flow<List<ExpenseEntity>> = financeDao.getAllExpenses()
    val allGoals: Flow<List<GoalEntity>> = financeDao.getAllGoals()

    suspend fun insertExpense(expense: ExpenseEntity) = financeDao.insertExpense(expense)

    suspend fun deleteExpenseById(id: Int) = financeDao.deleteExpenseById(id)

    suspend fun clearAllExpenses() = financeDao.clearAllExpenses()

    suspend fun insertGoal(goal: GoalEntity) = financeDao.insertGoal(goal)

    suspend fun deleteGoalById(id: Int) = financeDao.deleteGoalById(id)

    /**
     * Seeds the database with high-quality Indian context data if empty.
     */
    suspend fun seedMockDataIfEmpty() {
        val expenses = financeDao.getAllExpenses().firstOrNull()
        if (expenses.isNullOrEmpty()) {
            val defaultExpenses = listOf(
                ExpenseEntity(amount = 18000.0, category = "Rent", note = "3BHK Flat Bangalore Rent", dateString = "28 May 2026", type = "EXPENSE"),
                ExpenseEntity(amount = 6200.0, category = "EMI", note = "ICICI Car Loan Payment", dateString = "27 May 2026", type = "EXPENSE"),
                ExpenseEntity(amount = 3200.0, category = "Food", note = "Swiggy & dining out", dateString = "26 May 2026", type = "EXPENSE"),
                ExpenseEntity(amount = 5000.0, category = "Investments", note = "Parag Parikh Flexi Cap SIP", dateString = "25 May 2026", type = "INVEST"), // investment
                ExpenseEntity(amount = 1800.0, category = "Utilities", note = "Electricity & WiFi bill", dateString = "24 May 2026", type = "EXPENSE"),
                ExpenseEntity(amount = 2100.0, category = "Travel", note = "Ola / Uber commute to tech park", dateString = "23 May 2026", type = "EXPENSE"),
                ExpenseEntity(amount = 65000.0, category = "Salary", note = "Tech Corp Monthly Salary Credited", dateString = "01 May 2026", type = "INCOME")
            )
            for (expense in defaultExpenses) {
                financeDao.insertExpense(expense)
            }
        }

        val goals = financeDao.getAllGoals().firstOrNull()
        if (goals.isNullOrEmpty()) {
            val defaultGoals = listOf(
                GoalEntity(title = "Emergency Capital (6m Buffer)", targetAmount = 150000.0, savedAmount = 95000.0, targetMonthsTimeline = 6, category = "Retirement"),
                GoalEntity(title = "Agri-Tech Startup Business", targetAmount = 300000.0, savedAmount = 35000.0, targetMonthsTimeline = 18, category = "Startup"),
                GoalEntity(title = "Ather EV Scooter", targetAmount = 145000.0, savedAmount = 55000.0, targetMonthsTimeline = 12, category = "Car")
            )
            for (goal in defaultGoals) {
                financeDao.insertGoal(goal)
            }
        }
    }
}

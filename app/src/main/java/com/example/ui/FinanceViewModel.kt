package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class FinanceTab {
    DASHBOARD, SAVINGS, INVESTMENT, GOVT_SCHEMES, MENTOR_CHAT
}

data class ChatMessage(
    val sender: String, // "user" or "ai"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val financeDao = AppDatabase.getDatabase(application).financeDao()
    private val repository = FinanceRepository(financeDao)

    // Reactive streams from local database
    val expensesState: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goalsState: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Tab state
    private val _activeTab = MutableStateFlow(FinanceTab.DASHBOARD)
    val activeTabInUi: StateFlow<FinanceTab> = _activeTab.asStateFlow()

    // Interactive states for Dynamic calculations / inputs
    val currentTabsIndex = MutableStateFlow(0)
    val selectedRiskProfile = MutableStateFlow("Moderate") // Conservative, Moderate, Aggressive
    val sipInvestmentAmount = MutableStateFlow(5000f) // Monthly SIP amount
    val sipDurationYears = MutableStateFlow(15f) // Number of years to calculate

    // Startup advice states
    val startupCapital = MutableStateFlow("150000")
    val startupSkills = MutableStateFlow("Management, Tech, Retail")
    val startupLocation = MutableStateFlow("Urban Bengaluru")
    val activeGeneratedStartupPlan = MutableStateFlow<String?>(null)
    val isStartupAdvisorLoading = MutableStateFlow(false)

    // Interactive Advisor Chat state
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessagesStream: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    val isChatLoading = MutableStateFlow(false)

    // Dynamic AI Spending Advice state
    private val _aiAppraisalAdvice = MutableStateFlow<String?>(null)
    val aiAppraisalAdviceText: StateFlow<String?> = _aiAppraisalAdvice.asStateFlow()
    val isAnalyzingSpendingInRealtime = MutableStateFlow(false)

    init {
        // Hydrate database on startup with real context data
        viewModelScope.launch {
            repository.seedMockDataIfEmpty()
            // Pull the initial AI advice
            triggerRealtimeSpendingAppraisal()
            // Setup welcome first assistant chat
            setupInitialAdvisorMessages()
        }
    }

    fun selectTab(tab: FinanceTab) {
        _activeTab.value = tab
    }

    // --- Database Operations ---
    fun addExpense(amount: Double, category: String, note: String, isIncome: Boolean, isFamilyShared: Boolean) {
        viewModelScope.launch {
            val dateStr = "30 May 2026" // Fixed for current ledger timeline simulation
            val typeVal = if (isIncome) "INCOME" else "EXPENSE"
            repository.insertExpense(
                ExpenseEntity(
                    amount = amount,
                    category = category,
                    note = note,
                    dateString = dateStr,
                    type = typeVal,
                    isFamilyShared = isFamilyShared
                )
            )
            triggerRealtimeSpendingAppraisal() // refresh advice when ledger changes
        }
    }

    fun deleteExpense(id: Int) {
        viewModelScope.launch {
            repository.deleteExpenseById(id)
            triggerRealtimeSpendingAppraisal()
        }
    }

    fun addGoal(title: String, targetAmount: Double, timelineMonths: Int, category: String) {
        viewModelScope.launch {
            repository.insertGoal(
                GoalEntity(
                    title = title,
                    targetAmount = targetAmount,
                    savedAmount = 0.0,
                    targetMonthsTimeline = timelineMonths,
                    category = category
                )
            )
        }
    }

    fun updateGoalSavedProgress(id: Int, title: String, targetAmount: Double, additionalSaved: Double, timeline: Int, category: String) {
        viewModelScope.launch {
            repository.insertGoal(
                GoalEntity(
                    id = id,
                    title = title,
                    targetAmount = targetAmount,
                    savedAmount = additionalSaved,
                    targetMonthsTimeline = timeline,
                    category = category
                )
            )
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteGoalById(id)
        }
    }

    // --- Intelligent Calculations & Scores ---
    /**
     * Calculates Financial Health Score out of 100 based on realistic Indian household indicators:
     * - Savings Rate (Savings vs Income)
     * - Rent ratio to total salary
     * - EMI commitments
     * - Active investment behavior (portion of money going to goals or dedicated investments)
     */
    fun calculateFinancialHealthScore(expenses: List<ExpenseEntity>, goals: List<GoalEntity>): Int {
        if (expenses.isEmpty()) return 50 // Standard average

        val totalIncome = expenses.filter { it.type == "INCOME" }.sumOf { it.amount }
        val totalOutflow = expenses.filter { it.type == "EXPENSE" }.sumOf { it.amount }

        if (totalIncome == 0.0) return 40 // Missing income penalty

        var score = 60 // Baseline

        // 1. Savings Rate Metric (Perfect: >30%)
        val savingsRate = ((totalIncome - totalOutflow) / totalIncome) * 100
        if (savingsRate >= 30) score += 15
        else if (savingsRate >= 15) score += 8
        else if (savingsRate < 0) score -= 15 // Overdrawing

        // 2. Commuted Rent Burden (Limit under 35%)
        val rentExpense = expenses.filter { it.category.lowercase() == "rent" }.sumOf { it.amount }
        val rentRatio = (rentExpense / totalIncome) * 100
        if (rentRatio in 1.0..25.0) score += 10
        else if (rentRatio > 35.0) score -= 10

        // 3. Debt Burden (EMI commits)
        val emiExpense = expenses.filter { it.category.lowercase() == "emi" }.sumOf { it.amount }
        val emiRatio = (emiExpense / totalIncome) * 100
        if (emiRatio in 1.0..20.0) score += 5
        else if (emiRatio > 40.0) score -= 15

        // 4. Investment Habit (Active mutual funds / equity check)
        val isInvesting = expenses.any { it.category.lowercase().contains("invest") }
        if (isInvesting) score += 10

        // 5. Goal buffer
        if (goals.isNotEmpty()) {
            val progressAvg = goals.map { it.savedAmount / it.targetAmount }.average()
            if (progressAvg > 0.4) score += 10
        }

        return score.coerceIn(5, 100)
    }

    // --- Dynamic AI Generative Features ---
    /**
     * Triggers dynamic spending analysis recommendation via Gemini API
     */
    fun triggerRealtimeSpendingAppraisal() {
        viewModelScope.launch {
            isAnalyzingSpendingInRealtime.value = true
            val currentExpenses = expensesState.value
            val totalIncome = currentExpenses.filter { it.type == "INCOME" }.sumOf { it.amount }
            val categoriesJson = currentExpenses.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val prompt = """
                Analyze the following monthly financial ledger of an Indian professional and provide 3 concrete, personalized tips to optimize spending, reduce waste, and grow SIP wealth. Identify lifestyle inflation opportunities like food delivery, subscription waste, or transit packages.
                
                Ledger Data:
                - Income: ₹$totalIncome
                - Category breakdowns: $categoriesJson
                
                Provide your reply inside a short, bulleted actionable format with clean bullet lists. Match your details precisely to the currency (INR).
            """.trimIndent()

            val response = GeminiManager.generateFinancialAdvice(
                prompt = prompt,
                systemInstruction = "You are WealthWise AI, India's preeminent financial advisory mentor and SIP coach. Keep guidance brief, actionable, numerical, and in Indian Rupee denomination."
            )
            _aiAppraisalAdvice.value = response
            isAnalyzingSpendingInRealtime.value = false
        }
    }

    /**
     * Dynamic business startup planning generator
     */
    fun generateInteractiveStartupPlan() {
        viewModelScope.launch {
            isStartupAdvisorLoading.value = true
            val cap = startupCapital.value.ifBlank { "100000" }
            val skill = startupSkills.value.ifBlank { "Management" }
            val loc = startupLocation.value.ifBlank { "Tier 2 India" }

            val prompt = """
                Generate a professional Micro-Business / Startup recommendation for an aspiring entrepreneur in India based on:
                - Available Capital: ₹$cap
                - Skills: $skill
                - Target Location: $loc
                
                Present a structured roadmap including:
                1. Recommended Business Concept
                2. Investment Breakdown
                3. Risk Level & Break-even analysis
                4. Marketing/Growth Strategy
            """.trimIndent()

            val response = GeminiManager.generateFinancialAdvice(
                prompt = prompt,
                systemInstruction = "You are WealthWise AI Startup Mentor. Provide custom, realistic local business blueprints tailored to the Indian demographic, Tier-1/Tier-2/rural parameters, and capital bounds."
            )
            activeGeneratedStartupPlan.value = response
            isStartupAdvisorLoading.value = false
        }
    }

    /**
     * Dynamic Conversation Chat with Advisor Mentor
     */
    fun sendChatMessage(input: String) {
        if (input.isBlank()) return
        val userMsg = ChatMessage(sender = "user", message = input)
        _chatMessages.update { it + userMsg }

        viewModelScope.launch {
            isChatLoading.value = true

            // Send context of current ledger summaries to the advisor for context-aware responses
            val currentExpenses = expensesState.value
            val totalIncome = currentExpenses.filter { it.type == "INCOME" }.sumOf { it.amount }
            val totalExpense = currentExpenses.filter { it.type == "EXPENSE" }.sumOf { it.amount }

            val prompt = """
                User asks: "$input"
                
                For context, the user's logged details are:
                - Current Monthly Income: ₹$totalIncome
                - Dynamic Expense Outflow: ₹$totalExpense
                - Financial Health Score: ${calculateFinancialHealthScore(currentExpenses, goalsState.value)}/100
                
                As their intelligent personal finance mentor, answer their query concisely. Be highly realistic, encouraging, and detail specific metrics.
            """.trimIndent()

            val reply = GeminiManager.generateFinancialAdvice(
                prompt = prompt,
                systemInstruction = "You are WealthWise AI personal wealth coach. Answer user queries concisely, giving tailored guidelines about Indian investment vehicles (SIP, PPF, NPS, sovereign gold bonds). Avoid generic answers."
            )

            val aiMsg = ChatMessage(sender = "ai", message = reply)
            _chatMessages.update { it + aiMsg }
            isChatLoading.value = false
        }
    }

    private fun setupInitialAdvisorMessages() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "ai",
                message = "Welcome to WealthWise AI Companion! 🇮🇳 I am your 24/7 personal wealth mentor, startup coach, and financial planner.\n\nAsk me anything! For example: \n• 'How can I save ₹10 Lakhs in 3 years?' \n• 'Should I invest in Gold or Index Mutual Funds?' \n• 'What local business can I start with ₹1.5 Lakh capital?'"
            )
        )
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
        setupInitialAdvisorMessages()
    }
}

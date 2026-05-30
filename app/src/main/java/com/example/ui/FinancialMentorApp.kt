package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialMentorApp(
    modifier: Modifier = Modifier,
    viewModel: FinanceViewModel = viewModel()
) {
    val activeTab by viewModel.activeTabInUi.collectAsState()
    val expenses by viewModel.expensesState.collectAsState()
    val goals by viewModel.goalsState.collectAsState()

    val healthScore = viewModel.calculateFinancialHealthScore(expenses, goals)

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCharcoal),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "WealthWise AI",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = PureWhite
                        )
                        Text(
                            text = "India's Intelligent Financial Mentor",
                            fontSize = 11.sp,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Small header badge displaying current financial score
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .background(
                                color = if (healthScore >= 75) ForestGreen else if (healthScore >= 50) IndianGold.copy(alpha = 0.2f) else SoftRed.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Health Score: $healthScore",
                            color = if (healthScore >= 75) EmeraldGreen else if (healthScore >= 50) IndianGold else SoftRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepCharcoal,
                    titleContentColor = PureWhite
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardBackground,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // Tab 1: Tracker Dashboard
                NavigationBarItem(
                    selected = activeTab == FinanceTab.DASHBOARD,
                    onClick = { viewModel.selectTab(FinanceTab.DASHBOARD) },
                    icon = { Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Ledger Tracker") },
                    label = { Text("Tracker", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepCharcoal,
                        selectedTextColor = EmeraldGreen,
                        indicatorColor = EmeraldGreen,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    ),
                    modifier = Modifier.testTag("tab_dashboard")
                )

                // Tab 2: Savings goals
                NavigationBarItem(
                    selected = activeTab == FinanceTab.SAVINGS,
                    onClick = { viewModel.selectTab(FinanceTab.SAVINGS) },
                    icon = { Icon(imageVector = Icons.Default.Savings, contentDescription = "Savings Planner") },
                    label = { Text("Savings", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepCharcoal,
                        selectedTextColor = EmeraldGreen,
                        indicatorColor = EmeraldGreen,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    ),
                    modifier = Modifier.testTag("tab_savings")
                )

                // Tab 3: Investment compounding SIP
                NavigationBarItem(
                    selected = activeTab == FinanceTab.INVESTMENT,
                    onClick = { viewModel.selectTab(FinanceTab.INVESTMENT) },
                    icon = { Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Investment Compound") },
                    label = { Text("SIP Vault", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepCharcoal,
                        selectedTextColor = EmeraldGreen,
                        indicatorColor = EmeraldGreen,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    ),
                    modifier = Modifier.testTag("tab_investment")
                )

                // Tab 4: Government schemes
                NavigationBarItem(
                    selected = activeTab == FinanceTab.GOVT_SCHEMES,
                    onClick = { viewModel.selectTab(FinanceTab.GOVT_SCHEMES) },
                    icon = { Icon(imageVector = Icons.Default.Assignment, contentDescription = "Schemes discovery") },
                    label = { Text("Schemes", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepCharcoal,
                        selectedTextColor = EmeraldGreen,
                        indicatorColor = EmeraldGreen,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    ),
                    modifier = Modifier.testTag("tab_schemes")
                )

                // Tab 5: AI Startup & Chat Coach
                NavigationBarItem(
                    selected = activeTab == FinanceTab.MENTOR_CHAT,
                    onClick = { viewModel.selectTab(FinanceTab.MENTOR_CHAT) },
                    icon = { Icon(imageVector = Icons.Default.Psychology, contentDescription = "AI Mentor Chat") },
                    label = { Text("AI Mentor", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepCharcoal,
                        selectedTextColor = EmeraldGreen,
                        indicatorColor = EmeraldGreen,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText
                    ),
                    modifier = Modifier.testTag("tab_mentor")
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = DeepCharcoal
        ) {
            when (activeTab) {
                FinanceTab.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    expenses = expenses,
                    healthScore = healthScore
                )
                FinanceTab.SAVINGS -> SavingsPlannerScreen(
                    viewModel = viewModel,
                    goals = goals
                )
                FinanceTab.INVESTMENT -> InvestmentMentorScreen(
                    viewModel = viewModel
                )
                FinanceTab.GOVT_SCHEMES -> GovtSchemesScreen()
                FinanceTab.MENTOR_CHAT -> StartUpMentorScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}

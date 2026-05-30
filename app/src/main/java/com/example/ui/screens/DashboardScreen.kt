package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseEntity
import com.example.ui.FinanceViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    expenses: List<ExpenseEntity>,
    healthScore: Int
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchKeyword by remember { mutableStateOf("") }
    var showOnlyFamilyShared by remember { mutableStateOf(false) }

    // Aggregate values
    val totalIncome = expenses.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = expenses.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val totalInvestment = expenses.filter { it.type == "INVEST" }.sumOf { it.amount }
    val netBalance = totalIncome - totalExpense - totalInvestment

    val filteredExpenses = expenses.filter {
        val matchesSearch = it.note.lowercase().contains(searchKeyword.lowercase()) ||
                it.category.lowercase().contains(searchKeyword.lowercase())
        val matchesFamily = !showOnlyFamilyShared || it.isFamilyShared
        matchesSearch && matchesFamily
    }

    val aiAdvice by viewModel.aiAppraisalAdviceText.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingSpendingInRealtime.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Core Balance Card - Elevated Premium design with gradient & subtle sleek border
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("balance_card")
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                EmeraldGreen.copy(alpha = 0.25f),
                                IndianGold.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    CardBackground,
                                    SoftGray.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NET CAPITAL VALUATION",
                                color = MutedText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ForestGreen.copy(alpha = 0.3f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldGreen)
                                    )
                                    Text(
                                        text = "Active Ledger",
                                        color = EmeraldGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "₹${"%,.2f".format(netBalance)}",
                            color = PureWhite,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = SoftGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(20.dp))

                        // Mini metrics row - balanced with subtle card backings
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SoftGray.copy(alpha = 0.4f))
                                    .padding(10.dp)
                            ) {
                                LedgerMetricItem(
                                    title = "Incomes",
                                    value = "₹${"%,.0f".format(totalIncome)}",
                                    icon = Icons.Default.TrendingUp,
                                    color = IncomeBlue
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SoftGray.copy(alpha = 0.4f))
                                    .padding(10.dp)
                            ) {
                                LedgerMetricItem(
                                    title = "Outlays",
                                    value = "₹${"%,.0f".format(totalExpense)}",
                                    icon = Icons.Default.TrendingDown,
                                    color = SoftRed
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SoftGray.copy(alpha = 0.4f))
                                    .padding(10.dp)
                            ) {
                                LedgerMetricItem(
                                    title = "SIP Invests",
                                    value = "₹${"%,.0f".format(totalInvestment)}",
                                    icon = Icons.Default.AccountBalance,
                                    color = IndianGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Financial Score Gauge & Family Filter Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val scoreColor = if (healthScore >= 75) EmeraldGreen else if (healthScore >= 50) IndianGold else SoftRed
                
                // Score metric - clean professional design with subtle border
                Card(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(120.dp)
                        .border(
                            width = 1.dp,
                            color = scoreColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "HEALTH INDEX SCORE",
                            color = MutedText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$healthScore",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                color = scoreColor
                            )
                            Text(
                                text = " /100",
                                color = MutedText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (healthScore >= 75) "Optimal Buffer" else if (healthScore >= 50) "Average Discipline" else "High Debt Strain",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        )
                    }
                }

                // Family Sync control
                val familyActiveColor = if (showOnlyFamilyShared) EmeraldGreen else MutedText
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clickable { showOnlyFamilyShared = !showOnlyFamilyShared }
                        .border(
                            width = 1.dp,
                            color = if (showOnlyFamilyShared) EmeraldGreen.copy(alpha = 0.3f) else SoftGray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (showOnlyFamilyShared) ForestGreen.copy(alpha = 0.2f) else CardBackground
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (showOnlyFamilyShared) EmeraldGreen.copy(alpha = 0.15f) else SoftGray.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = "Family",
                                tint = familyActiveColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (showOnlyFamilyShared) "Family Ledger" else "Personal Log",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (showOnlyFamilyShared) "Click to reset" else "Click to filter",
                            fontSize = 10.sp,
                            color = MutedText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 3. AI Generated Spend Analysis Card (Dynamic) - Outstanding Beautiful Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = IndianGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftGray.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(IndianGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "AI Mentor",
                                    tint = IndianGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "AI Spending Appraisal Plan",
                                fontWeight = FontWeight.ExtraBold,
                                color = PureWhite,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        IconButton(
                            onClick = { viewModel.triggerRealtimeSpendingAppraisal() },
                            enabled = !isAnalyzing,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SoftGray)
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = IndianGold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Recalculate advice",
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (aiAdvice.isNullOrBlank()) {
                        Text(
                            text = "Analyzing your budget patterns to generate immediate compound growth instructions...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText,
                            lineHeight = 20.sp
                        )
                    } else {
                        Text(
                            text = aiAdvice ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PureWhite,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = SoftGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Tip: Log more items or adjust investments to update suggestions",
                            fontSize = 10.sp,
                            color = MutedText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 4. Ledger Section Header & Search Input
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions Ledger",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = PureWhite,
                    letterSpacing = 0.5.sp
                )

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_expense_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = DeepCharcoal, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Log", color = DeepCharcoal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchKeyword,
                onValueChange = { searchKeyword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input"),
                placeholder = { Text("Filter items (e.g. food, salary)...", color = MutedText) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MutedText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldGreen,
                    unfocusedBorderColor = SoftGray,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // 5. Scrollable lists of items
        if (filteredExpenses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = "Empty",
                            tint = MutedText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matching transactions logged yet.",
                            color = MutedText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(filteredExpenses) { item ->
                ExpenseItemRow(item = item, onDelete = { viewModel.deleteExpense(item.id) })
            }
        }
    }

    // Interactive Dialog to Add Ledger Rows
    if (showAddDialog) {
        AddLedgerItemDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { amt, cat, note, isIncome, isFamily ->
                viewModel.addExpense(amt, cat, note, isIncome, isFamily)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun LedgerMetricItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = title, color = MutedText, fontSize = 11.sp)
        }
        Text(
            text = value,
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 18.dp)
        )
    }
}

@Composable
fun ExpenseItemRow(
    item: ExpenseEntity,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }

    val colorAccent = when (item.type) {
        "INCOME" -> IncomeBlue
        "INVEST" -> IndianGold
        else -> SoftRed
    }

    val iconAccent = when (item.type) {
        "INCOME" -> Icons.Default.ArrowCircleDown
        "INVEST" -> Icons.Default.Savings
        else -> Icons.Default.Payment
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("expense_row_${item.id}")
            .border(
                width = 1.dp,
                color = colorAccent.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.85f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(colorAccent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconAccent,
                        contentDescription = null,
                        tint = colorAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = item.note,
                        fontWeight = FontWeight.ExtraBold,
                        color = PureWhite,
                        fontSize = 14.sp,
                        letterSpacing = 0.2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.category.uppercase(),
                            color = colorAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(MutedText.copy(alpha = 0.6f))
                        )
                        Text(
                            text = item.dateString,
                            color = MutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (item.isFamilyShared) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ForestGreen.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FAMILY",
                                    color = EmeraldGreen,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${if (item.type == "INCOME") "+" else "-"}₹${"%,.0f".format(item.amount)}",
                    fontWeight = FontWeight.Black,
                    color = if (item.type == "INCOME") EmeraldGreen else PureWhite,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )

                if (confirmDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(28.dp)
                            .background(SoftRed.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirm",
                            tint = SoftRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { confirmDelete = false },
                        modifier = Modifier
                            .size(28.dp)
                            .background(SoftGray.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = MutedText,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { confirmDelete = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MutedText.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLedgerItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, category: String, note: String, isIncome: Boolean, isFamilyShared: Boolean) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var noteStr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var isIncomeSelected by remember { mutableStateOf(false) } // Default is Expense
    var isFamilyShared by remember { mutableStateOf(false) }

    val categories = listOf("Food", "Rent", "EMI", "Fuel", "Utilities", "Travel", "Investments", "Medical", "Shopping", "Entertainment", "Salary", "Other Business")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Log Financial Record", fontWeight = FontWeight.ExtraBold, color = PureWhite)
        },
        containerColor = CardBackground,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Inline toggle for Income vs Expense
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftGray)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = {
                            isIncomeSelected = false
                            // Default categories matching expense
                            if (selectedCategory == "Salary") selectedCategory = "Food"
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isIncomeSelected) SoftRed.copy(alpha = 0.8f) else Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Expense Outset", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            isIncomeSelected = true
                            selectedCategory = "Salary"
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isIncomeSelected) IncomeBlue.copy(alpha = 0.8f) else Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Income Cred", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (₹)", color = MutedText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = SoftGray,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = noteStr,
                    onValueChange = { noteStr = it },
                    label = { Text("Narration/Note", color = MutedText) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Weekly organic vegetables", color = MutedText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = SoftGray,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Category Selection Scroll Row
                Column {
                    Text("Select Category:", color = MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(categories.chunked(3)) { chunk ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    for (cat in chunk) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (selectedCategory == cat) EmeraldGreen else SoftGray)
                                                .clickable { selectedCategory = cat }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = cat,
                                                color = if (selectedCategory == cat) DeepCharcoal else PureWhite,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Family Sync Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Share with Family Deck", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Visible in shared finance mode on children tracking list", color = MutedText, fontSize = 10.sp)
                    }
                    Switch(
                        checked = isFamilyShared,
                        onCheckedChange = { isFamilyShared = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EmeraldGreen,
                            checkedTrackColor = ForestGreen
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (amt > 0.0 && noteStr.isNotEmpty()) {
                        onConfirm(amt, selectedCategory, noteStr, isIncomeSelected, isFamilyShared)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Text("Confirm & Record", color = DeepCharcoal, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedText)
            }
        }
    )
}

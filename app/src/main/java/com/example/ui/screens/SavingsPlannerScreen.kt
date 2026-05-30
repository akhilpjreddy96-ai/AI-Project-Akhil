package com.example.ui.screens

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
import com.example.data.GoalEntity
import com.example.ui.FinanceViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsPlannerScreen(
    viewModel: FinanceViewModel,
    goals: List<GoalEntity>
) {
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var selectedGoalForSimulate by remember { mutableStateOf<GoalEntity?>(null) }
    var simulatedContributionAmount by remember { mutableStateOf(5000f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Goal Simulation Header Card - Beautiful Premium interactive container
        item {
            val isGoalActive = selectedGoalForSimulate != null
            val accentCardBorderColor = if (isGoalActive) EmeraldGreen.copy(alpha = 0.25f) else IndianGold.copy(alpha = 0.15f)
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("simulation_banner_card")
                    .border(
                        width = 1.dp,
                        color = accentCardBorderColor,
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftGray.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(if (isGoalActive) EmeraldGreen.copy(alpha = 0.15f) else IndianGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = "Analysis",
                                    tint = if (isGoalActive) EmeraldGreen else IndianGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "AI GOAL ALLOCATION SIMULATOR",
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                fontSize = 12.sp,
                                letterSpacing = 0.8.sp
                            )
                        }
                        
                        if (isGoalActive) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE MODEL",
                                    color = EmeraldGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (selectedGoalForSimulate == null) {
                        Text(
                            text = "Select any saving commitment card below to model interactive monthly budget distributions and simulate top-up funds.",
                            color = MutedText,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    } else {
                        val goal = selectedGoalForSimulate!!
                        val progressLeft = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)
                        val requiredMonthlySim = if (goal.targetMonthsTimeline > 0) progressLeft / goal.targetMonthsTimeline else progressLeft

                        Text(
                            text = "Modeling: '${goal.title}'",
                            fontWeight = FontWeight.ExtraBold,
                            color = IndianGold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "To bridge the remaining ₹${"%,.0f".format(progressLeft)} in ${goal.targetMonthsTimeline} months, your minimum required savings allocation is ₹${"%,.0f".format(requiredMonthlySim)} / mo.",
                            color = PureWhite,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = SoftGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Sim Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Simulate Adding Funds:",
                                fontWeight = FontWeight.Bold,
                                color = MutedText,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "+₹${"%,.0f".format(simulatedContributionAmount)}",
                                fontWeight = FontWeight.Black,
                                color = EmeraldGreen,
                                fontSize = 14.sp
                            )
                        }
                        
                        Slider(
                            value = simulatedContributionAmount,
                            onValueChange = { simulatedContributionAmount = it },
                            valueRange = 1000f..50000f,
                            steps = 49,
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldGreen,
                                activeTrackColor = EmeraldGreen,
                                inactiveTrackColor = CardBackground
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { selectedGoalForSimulate = null }
                            ) {
                                Text("Clear Sim", color = MutedText, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    val currentGoal = selectedGoalForSimulate!!
                                    val newSaved = (currentGoal.savedAmount + simulatedContributionAmount).coerceAtMost(currentGoal.targetAmount)
                                    viewModel.updateGoalSavedProgress(
                                        id = currentGoal.id,
                                        title = currentGoal.title,
                                        targetAmount = currentGoal.targetAmount,
                                        additionalSaved = newSaved,
                                        timeline = currentGoal.targetMonthsTimeline,
                                        category = currentGoal.category
                                    )
                                    // Reset simulation
                                    selectedGoalForSimulate = null
                                    simulatedContributionAmount = 5000f
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = DeepCharcoal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Inject Funds", color = DeepCharcoal, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // 2. Goal Planner Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saving Commitments",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PureWhite
                )

                Button(
                    onClick = { showAddGoalDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    modifier = Modifier.testTag("add_goal_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal", tint = DeepCharcoal, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "New Milestone", color = DeepCharcoal, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        // 3. Goal cards
        if (goals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = "Empty goals",
                            tint = MutedText,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No savings goals tracked yet. Set one up!",
                            color = MutedText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(goals) { item ->
                GoalItemCard(
                    goal = item,
                    isSelected = selectedGoalForSimulate?.id == item.id,
                    onSelect = {
                        selectedGoalForSimulate = item
                        simulatedContributionAmount = 5000f
                    },
                    onDelete = { viewModel.deleteGoal(item.id) }
                )
            }
        }
    }

    if (showAddGoalDialog) {
        CreateGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { title, target, timeline, category ->
                viewModel.addGoal(title, target, timeline, category)
                showAddGoalDialog = false
            }
        )
    }
}

@Composable
fun GoalItemCard(
    goal: GoalEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val progressRatio = if (goal.targetAmount > 0) (goal.savedAmount / goal.targetAmount).coerceIn(0.0..1.0) else 0.0
    val progressPerc = (progressRatio * 100).toInt()

    val categoryIcon = when (goal.category) {
        "House" -> Icons.Default.Home
        "Car" -> Icons.Default.ElectricCar
        "Startup" -> Icons.Default.RocketLaunch
        "Education" -> Icons.Default.School
        "Marriage" -> Icons.Default.Favorite
        else -> Icons.Default.Savings
    }

    val itemThemeColor = if (isSelected) IndianGold else EmeraldGreen

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("goal_card_${goal.id}")
            .clickable { onSelect() }
            .border(
                width = 1.dp,
                color = if (isSelected) IndianGold.copy(alpha = 0.3f) else SoftGray.copy(alpha = 0.4f),
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SoftGray.copy(alpha = 0.6f) else CardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(itemThemeColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = goal.category,
                            tint = itemThemeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = goal.title,
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite,
                            fontSize = 15.sp,
                            letterSpacing = 0.2.sp
                        )
                        Text(
                            text = goal.category.uppercase(),
                            color = itemThemeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Money progress row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("Accrued Savings", color = MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("₹${"%,.0f".format(goal.savedAmount)}", color = EmeraldGreen, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Target Goal", color = MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("₹${"%,.0f".format(goal.targetAmount)}", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { progressRatio.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EmeraldGreen,
                    trackColor = SoftGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "$progressPerc% Achieved • ${goal.targetMonthsTimeline} Months target",
                            color = MutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(28.dp)
                            .background(SoftRed.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Goal",
                            tint = SoftRed,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, targetAmount: Double, timelineMonths: Int, category: String) -> Unit
) {
    var titleStr by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("") }
    var timelineMonths by remember { mutableStateOf(12) } // default 12m
    var selectedCategory by remember { mutableStateOf("Startup") }

    val categories = listOf("Startup", "House", "Car", "Education", "Marriage", "Retirement")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Set Savings Milestone", fontWeight = FontWeight.ExtraBold, color = PureWhite)
        },
        containerColor = CardBackground,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = titleStr,
                    onValueChange = { titleStr = it },
                    label = { Text("Milestone Focus Name", color = MutedText) },
                    placeholder = { Text("e.g. Agri-tech Startup Core Cap", color = MutedText) },
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
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("Target Capital Amount (₹)", color = MutedText) },
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

                // Timeline interactive slider in dialog
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Target Timeline", color = MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$timelineMonths Months", color = IndianGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                    Slider(
                        value = timelineMonths.toFloat(),
                        onValueChange = { timelineMonths = it.toInt() },
                        valueRange = 3f..60f,
                        steps = 57,
                        colors = SliderDefaults.colors(
                            thumbColor = EmeraldGreen,
                            activeTrackColor = EmeraldGreen,
                            inactiveTrackColor = SoftGray
                        )
                    )
                }

                // Category Grid Selection
                Column {
                    Text("Select Focus Category:", color = MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (cat in categories.take(3)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedCategory == cat) IndianGold else SoftGray)
                                    .clickable { selectedCategory = cat }
                                    .padding(vertical = 10.dp),
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (cat in categories.drop(3)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedCategory == cat) IndianGold else SoftGray)
                                    .clickable { selectedCategory = cat }
                                    .padding(vertical = 10.dp),
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
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = targetStr.toDoubleOrNull() ?: 0.0
                    if (amt > 0.0 && titleStr.isNotEmpty()) {
                        onConfirm(titleStr, amt, timelineMonths, selectedCategory)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Text("Lock Milestone", color = DeepCharcoal, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedText)
            }
        }
    )
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FinanceViewModel
import com.example.ui.theme.*
import kotlin.math.pow

@Composable
fun InvestmentMentorScreen(
    viewModel: FinanceViewModel
) {
    val selectedRisk by viewModel.selectedRiskProfile.collectAsState()
    val monthlySipAmt by viewModel.sipInvestmentAmount.collectAsState()
    val durationYrs by viewModel.sipDurationYears.collectAsState()

    // Map compound returns rate based on profile selection (standard realistic Indian guidelines)
    val interestRate = when (selectedRisk) {
        "Conservative" -> 0.082f // APV / PPF / FD rates (Sovereign guarantee)
        "Moderate" -> 0.125f // Balanced Index and FlexiCap SIP growth
        else -> 0.155f // Emerging aggressive smallcap/midcap equity portfolios
    }

    // Direct compounding formula: M = P * [ ( (1 + i)^n - 1 ) / i ] * (1 + i)
    // where P = monthly investment, i = annual / 12, n = years * 12
    val monthlyRate = interestRate / 12.0
    val totalMonths = (durationYrs * 12).toInt()
    val totalInvested = monthlySipAmt * totalMonths

    val futureValue = if (monthlyRate > 0.0) {
        monthlySipAmt * (((1.0 + monthlyRate).pow(totalMonths.toDouble()) - 1.0) / monthlyRate) * (1.0 + monthlyRate)
    } else {
        totalInvested.toDouble()
    }

    val wealthGain = (futureValue - totalInvested).coerceAtLeast(0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Interactive Risk Assessment Cards
        item {
            Column {
                Text(
                    text = "Risk Appetite Profile Setup",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = PureWhite,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your selected category calibrates investment returns and targets specific Indian asset vehicles.",
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val riskProfiles = listOf("Conservative", "Moderate", "Aggressive")
                    riskProfiles.forEach { profile ->
                        val isSelected = selectedRisk == profile
                        
                        val activeBorderColor = when (profile) {
                            "Conservative" -> EmeraldGreen
                            "Moderate" -> IndianGold
                            else -> SoftRed
                        }
                        
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectedRiskProfile.value = profile }
                                .testTag("risk_profile_${profile.lowercase()}")
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) activeBorderColor.copy(alpha = 0.40f) else SoftGray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) activeBorderColor.copy(alpha = 0.08f) else CardBackground
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = profile,
                                    color = if (isSelected) activeBorderColor else PureWhite,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.2.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = when (profile) {
                                        "Conservative" -> "~8.2% CAGR"
                                        "Moderate" -> "~12.5% CAGR"
                                        else -> "~15.5% CAGR"
                                    },
                                    color = if (isSelected) PureWhite else MutedText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Asset Allocation Breakdown Card based on selected risk
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = SoftGray.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Suggested Asset Allocation Mix",
                        fontWeight = FontWeight.ExtraBold,
                        color = PureWhite,
                        fontSize = 14.sp,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Represent dynamic horizontal bar mix
                    when (selectedRisk) {
                        "Conservative" -> {
                            AssetRowBar(label = "Sovereign PPF & APY Bonds", percentage = 60, color = EmeraldGreen)
                            AssetRowBar(label = "Liquid Debt Mutual Funds", percentage = 25, color = IncomeBlue)
                            AssetRowBar(label = "Sovereign Gold Bonds (SGB)", percentage = 15, color = IndianGold)
                        }
                        "Moderate" -> {
                            AssetRowBar(label = "Large-Cap Index & Flexi SIP", percentage = 50, color = EmeraldGreen)
                            AssetRowBar(label = "Liquid Reserves & Arbitrage", percentage = 20, color = IncomeBlue)
                            AssetRowBar(label = "Mid-Cap Growth Funds", percentage = 20, color = IndianGold)
                            AssetRowBar(label = "Digital Physical Gold", percentage = 10, color = SoftRed)
                        }
                        else -> {
                            AssetRowBar(label = "Aggressive Mid & Smallcap SIP", percentage = 65, color = EmeraldGreen)
                            AssetRowBar(label = "Direct Nifty Top-30 Stocks", percentage = 20, color = IncomeBlue)
                            AssetRowBar(label = "Digital Sovereign Gold", percentage = 10, color = IndianGold)
                            AssetRowBar(label = "High Growth Venture Bond Pool", percentage = 5, color = SoftRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "WealthWise AI structures these allocations aligned directly to tax exempt Sec 80C parameters.",
                            fontSize = 10.sp,
                            color = MutedText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 3. Compounding Interactive Simulator Dashboard
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = EmeraldGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SoftGray.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Simulate",
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "SIP Compound Calculator",
                                fontWeight = FontWeight.ExtraBold,
                                color = PureWhite,
                                fontSize = 16.sp,
                                letterSpacing = 0.3.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ForestGreen.copy(alpha = 0.35f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "CAGR ${"%.1f".format(interestRate * 100)}%",
                                color = EmeraldGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3.1 Live Sliders
                    // Monthly amount slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Monthly SIP Investment", color = MutedText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("₹${"%,.0f".format(monthlySipAmt)}", color = EmeraldGreen, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                        Slider(
                            value = monthlySipAmt,
                            onValueChange = { viewModel.sipInvestmentAmount.value = it },
                            valueRange = 1000f..100000f,
                            steps = 99,
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldGreen,
                                activeTrackColor = EmeraldGreen,
                                inactiveTrackColor = CardBackground
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Duration Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Investment Horizon Duration", color = MutedText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("${durationYrs.toInt()} Years", color = IndianGold, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                        Slider(
                            value = durationYrs,
                            onValueChange = { viewModel.sipDurationYears.value = it },
                            valueRange = 1f..40f,
                            steps = 39,
                            colors = SliderDefaults.colors(
                                thumbColor = IndianGold,
                                activeTrackColor = IndianGold,
                                inactiveTrackColor = CardBackground
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3.2 Compounding Math Outcomes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CompoundResultItem(label = "Total Invested", value = "₹${"%,.0f".format(totalInvested)}", color = MutedText)
                        CompoundResultItem(label = "Accrued Returns", value = "₹${"%,.0f".format(wealthGain)}", color = EmeraldGreen)
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = SoftGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(18.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ACCUMULATED WEALTH FUTURE VALUE",
                            color = MutedText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${"%,.0f".format(futureValue)}",
                            color = IndianGold,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.2.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AssetRowBar(
    label: String,
    percentage: Int,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "$percentage%", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(SoftGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .background(color)
            )
        }
    }
}

@Composable
fun CompoundResultItem(
    label: String,
    value: String,
    color: Color
) {
    Column {
        Text(text = label, color = MutedText, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

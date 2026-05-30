package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class Scheme(
    val id: String,
    val name: String,
    val tag: String, // "Business", "Pension", "Women", "Welfare"
    val miniDesc: String,
    val benefit: String,
    val checklistItems: List<String>,
    val docRequired: List<String>,
    val applicationSteps: String
)

@Composable
fun GovtSchemesScreen() {
    var selectedTagFilter by remember { mutableStateOf("All") }
    var expandedSchemeId by remember { mutableStateOf<String?>(null) }

    val schemesList = remember {
        listOf(
            Scheme(
                id = "mudra",
                name = "PM Mudra Yojana (PMMY)",
                tag = "Business",
                miniDesc = "Collateral-free commercial loans up to ₹10 Lakhs for setting up retail shops, small industries, green energy projects, or agricultural service agencies.",
                benefit = "• Loans split as Shishu (up to 50k), Kishor (50k - 5L), and Tarun (5L - 10L).\n• Collateral-free lending with nominal repayment periods up to 5 years.",
                checklistItems = listOf(
                    "You are a registered MSME / Udyam Proprietor",
                    "Your venture does not operate as a corporate entity",
                    "Applicant has a clean Credit Bureau / CIBIL history"
                ),
                docRequired = listOf("Aadhaar Card copy", "Udyam Registry certificate", "Business Layout proof", "Bank statements (6 months)"),
                applicationSteps = "1. Fill the Mudra Application Form (Shishu or joint form).\n2. Attach standard KYC & Udyam proofs.\n3. Submit directly to any Public/Private sector bank portal or online via JanSamarth."
            ),
            Scheme(
                id = "apy",
                name = "Atal Pension Yojana (APY)",
                tag = "Pension",
                miniDesc = "Sovereign guaranteed monthly retirement pension buffer of ₹1,000 to ₹5,000 co-contributed by the Central Government, providing long-term life stability.",
                benefit = "• Direct lifelong guaranteed monthly pension post age 60.\n• In case of demise, spouse receives identical pension corpus.",
                checklistItems = listOf(
                    "Your age matches the 18 to 40 years bracket",
                    "You hold an active savings bank account linked to Aadhaar",
                    "You do not qualify as a taxpayer under Indian income tax slabs"
                ),
                docRequired = listOf("Aadhaar Card", "Mobile number", "Savings bank savings passbook details"),
                applicationSteps = "1. Visit your linked local savings bank branch.\n2. Complete the APY registration form with auto-debit consent.\n3. Keep bank accounts sufficiently funded on monthly debit intervals."
            ),
            Scheme(
                id = "ssy",
                name = "Sukanya Samriddhi Yojana (SSY)",
                tag = "Women",
                miniDesc = "Sovereign-backed wealth builder for girl children currently carrying high interest (8.2% CAGR) with deep Sec 80C EEE income deduction advantages.",
                benefit = "• Direct triple tax savings (EEE): investment, returns, and maturity are tax exempt.\n• Matures at age 21, helping fund high-priority education/marriage plans.",
                checklistItems = listOf(
                    "Girl child age is under 10 years on opening date",
                    "Child qualifies as a resident citizen of India",
                    "Limited to maximum two girl children per household savings deck"
                ),
                docRequired = listOf("Girl child Birth Certificate", "Guardian Aadhaar & PAN Card copy", "Proof of address details"),
                applicationSteps = "1. Complete the SSY account creation form at any Indian Post Office cell or commercial bank.\n2. Submit physical copies of child's birth credential & guardian KYC.\n3. Open with a low minimum deposit of ₹250."
            ),
            Scheme(
                id = "svanidhi",
                name = "PM SVANidhi Loan Scheme",
                tag = "Business",
                miniDesc = "Collateral-free seed working capital loans up to ₹50,000 targeting urban street vendors and local artisans to optimize inventory scale.",
                benefit = "• Progressive loan brackets: ₹10k first tranche, ₹20k second, ₹50k third.\n• 7% interest subvention credited directly to bank accounts for timely payments.",
                checklistItems = listOf(
                    "You operate as an active micro-vendor / vendor with Certificate of Vending (CoV)",
                    "Aadhaar is linked to phone number for immediate OTP",
                    "Your venture is active in municipal corporation zones"
                ),
                docRequired = listOf("Aadhaar Card copy", "Certificate of Vending (CoV) / LoR", "Linked active savings bank details"),
                applicationSteps = "1. Visit PMSVANidhi official portal or consult a local common service circle (CSC).\n2. Input Aadhaar OTP to verify vendor card details.\n3. Submit digital bank information."
            )
        )
    }

    val tags = listOf("All", "Business", "Pension", "Women")
    val filteredSchemes = schemesList.filter { selectedTagFilter == "All" || it.tag == selectedTagFilter }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Tag selectors
        item {
            Column {
                Text(
                    text = "Indian Government Schemes Desk",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = PureWhite,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select tags below to filter sovereign schemes and check your custom eligibility checks.",
                    fontSize = 12.sp,
                    color = MutedText,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tags) { tag ->
                        val isSelected = selectedTagFilter == tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EmeraldGreen.copy(alpha = 0.3f) else SoftGray.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(if (isSelected) EmeraldGreen else CardBackground.copy(alpha = 0.8f))
                                .clickable { selectedTagFilter = tag }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("scheme_filter_tag_$tag"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tag.uppercase(),
                                color = if (isSelected) DeepCharcoal else PureWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Schemes List
        items(filteredSchemes) { scheme ->
            val isExpanded = expandedSchemeId == scheme.id
            SchemeItemCard(
                scheme = scheme,
                isExpanded = isExpanded,
                onClick = {
                    expandedSchemeId = if (isExpanded) null else scheme.id
                }
            )
        }
    }
}

@Composable
fun SchemeItemCard(
    scheme: Scheme,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    // Keep individual eligibility checklist states locally
    val checkboxStates = remember { mutableStateMapOf<String, Boolean>() }
    val allEligibleChecked = scheme.checklistItems.all { checkboxStates[it] == true }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scheme_card_${scheme.id}")
            .border(
                width = 1.dp,
                color = if (isExpanded) EmeraldGreen.copy(alpha = 0.25f) else SoftGray.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) CardBackground.copy(alpha = 0.9f) else CardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ForestGreen.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = scheme.tag.uppercase(),
                            color = EmeraldGreen,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = scheme.name,
                        fontWeight = FontWeight.Black,
                        color = PureWhite,
                        fontSize = 16.sp,
                        letterSpacing = 0.2.sp
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand info",
                    tint = if (isExpanded) EmeraldGreen else MutedText,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = scheme.miniDesc,
                color = MutedText,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )

            // Dynamic detail block
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    HorizontalDivider(color = SoftGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Benefits
                    Text("SOVEREIGN BENEFITS ELIGIBLE", color = IndianGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = scheme.benefit,
                        color = PureWhite,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Eligibility Box checklist
                    Text("ELIGIBILITY VERIFICATION CHECKLIST", color = IndianGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    scheme.checklistItems.forEach { item ->
                        val checked = checkboxStates[item] ?: false
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { checkboxStates[item] = !checked }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { checkboxStates[item] = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = EmeraldGreen,
                                    uncheckedColor = MutedText
                                )
                            )
                            Text(
                                text = item,
                                color = PureWhite,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (allEligibleChecked) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ForestGreen.copy(alpha = 0.25f))
                                .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Eligible", tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Optimal! You meet the parameter checklist.", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3. Documents Required
                    Text("MANDATORY DOCUMENT LIST", color = IndianGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        scheme.docRequired.take(2).forEach { doc ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, SoftGray.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .background(SoftGray.copy(alpha = 0.3f))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = doc, color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (scheme.docRequired.size > 2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            scheme.docRequired.drop(2).forEach { doc ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, SoftGray.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                        .background(SoftGray.copy(alpha = 0.3f))
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = doc, color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 4. Action Steps
                    Text("APPLICATION REGULATION PATH", color = IndianGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = scheme.applicationSteps,
                        color = MutedText,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

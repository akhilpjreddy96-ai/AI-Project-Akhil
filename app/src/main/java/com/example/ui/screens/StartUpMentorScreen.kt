package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChatMessage
import com.example.ui.FinanceViewModel
import com.example.ui.theme.*

@Composable
fun StartUpMentorScreen(
    viewModel: FinanceViewModel
) {
    var isChatView by remember { mutableStateOf(true) } // Switch between Startup Advice vs Chat Coach

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Switcher tabs at top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, SoftGray.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                .background(CardBackground)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = { isChatView = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isChatView) ForestGreen.copy(alpha = 0.7f) else androidx.compose.ui.graphics.Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isChatView) EmeraldGreen else MutedText
                    )
                    Text(
                        text = "24/7 AI MENTOR CHAT",
                        color = if (isChatView) PureWhite else MutedText,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Button(
                onClick = { isChatView = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isChatView) ForestGreen.copy(alpha = 0.7f) else androidx.compose.ui.graphics.Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (!isChatView) EmeraldGreen else MutedText
                    )
                    Text(
                        text = "AI BUSINESS MENTOR",
                        color = if (!isChatView) PureWhite else MutedText,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isChatView) {
            ChatCoachLayout(viewModel = viewModel)
        } else {
            StartupPlannerLayout(viewModel = viewModel)
        }
    }
}

@Composable
fun ChatCoachLayout(viewModel: FinanceViewModel) {
    val messages by viewModel.chatMessagesStream.collectAsState()
    val isThinking by viewModel.isChatLoading.collectAsState()
    var userTypedText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val actionSend = {
        if (userTypedText.isNotBlank()) {
            val queryText = userTypedText
            userTypedText = ""
            keyboardController?.hide()
            viewModel.sendChatMessage(queryText)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Coaching Conversation",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = PureWhite
            )
            TextButton(
                onClick = { viewModel.clearChat() },
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Chat", modifier = Modifier.size(14.dp), tint = SoftRed)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset Chat", color = SoftRed, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Chat lists scrollable
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = if (isUser) EmeraldGreen.copy(alpha = 0.25f) else SoftGray.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                )
                            )
                            .background(if (isUser) ForestGreen.copy(alpha = 0.45f) else CardBackground.copy(alpha = 0.85f))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = msg.message,
                            color = PureWhite,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardBackground)
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = IndianGold)
                                Text("Mentor compounding advice...", color = MutedText, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Float Send Bar at bottom
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userTypedText,
                    onValueChange = { userTypedText = it },
                    placeholder = { Text("Ask about savings, gold, SIP ratios...", color = MutedText, fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input"),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(onSend = { actionSend() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

                IconButton(
                    onClick = { actionSend() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                        .testTag("chat_send_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = DeepCharcoal)
                }
            }
        }
    }
}

@Composable
fun StartupPlannerLayout(viewModel: FinanceViewModel) {
    val capital by viewModel.startupCapital.collectAsState()
    val skills by viewModel.startupSkills.collectAsState()
    val location by viewModel.startupLocation.collectAsState()
    val isGenerating by viewModel.isStartupAdvisorLoading.collectAsState()
    val resultPlan by viewModel.activeGeneratedStartupPlan.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Form Controls CARD
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
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Establish Startup Parameters",
                        fontWeight = FontWeight.ExtraBold,
                        color = PureWhite,
                        fontSize = 14.sp,
                        letterSpacing = 0.3.sp
                    )

                    OutlinedTextField(
                        value = capital,
                        onValueChange = { viewModel.startupCapital.value = it },
                        label = { Text("Available Cash Capital (₹)", color = MutedText, fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = SoftGray,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = skills,
                        onValueChange = { viewModel.startupSkills.value = it },
                        label = { Text("Core Skills / Interests", color = MutedText, fontSize = 12.sp) },
                        placeholder = { Text("e.g. Sales, Cooking, Digital design", color = MutedText) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = SoftGray,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { viewModel.startupLocation.value = it },
                        label = { Text("Venture Region/Location", color = MutedText, fontSize = 12.sp) },
                        placeholder = { Text("e.g. Bengaluru, Tier-3 Odisha", color = MutedText) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = SoftGray,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { viewModel.generateInteractiveStartupPlan() },
                        enabled = !isGenerating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_startup_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = DeepCharcoal, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Assembling Business plan...", color = DeepCharcoal, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.PrecisionManufacturing, contentDescription = null, tint = DeepCharcoal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Startup Blueprint", color = DeepCharcoal, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Output CARD
        if (resultPlan != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = IndianGold.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftGray.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(IndianGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "Blueprint",
                                    tint = IndianGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "WealthWise AI Startup Blueprint",
                                fontWeight = FontWeight.ExtraBold,
                                color = PureWhite,
                                fontSize = 14.sp,
                                letterSpacing = 0.3.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = SoftGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = resultPlan ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PureWhite,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = SoftGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = IndianGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Note: You can secure collateral-free business loans up to ₹10 Lakhs through PM Mudra Yojana based on this blueprint.",
                                fontSize = 11.sp,
                                color = MutedText,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

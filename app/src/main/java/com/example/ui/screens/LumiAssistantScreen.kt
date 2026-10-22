package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.PremiumGradientBrush
import com.example.ui.components.CosmosBackgroundBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumiAssistantScreen(viewModel: ConnectLiveViewModel) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Chat, 1 = Writing & Safety
    val loading by viewModel.lumiLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBackgroundBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C071A))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("main") },
                    modifier = Modifier.testTag("lumi_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Lumi",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Lumi AI Workspace",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Your intelligent social companion",
                        fontSize = 11.sp,
                        color = Color.White.copy(0.5f)
                    )
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0F0C21),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF8E2DE2)
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Lumi Assistant Chat", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    modifier = Modifier.testTag("lumi_tab_chat")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Writing & Safety Suite", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    modifier = Modifier.testTag("lumi_tab_suite")
                )
            }

            // Workspace Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> LumiChatTab(viewModel = viewModel, loading = loading)
                    1 -> LumiWritingSuiteTab(viewModel = viewModel, loading = loading)
                }
            }
        }
    }
}

@Composable
fun LumiChatTab(viewModel: ConnectLiveViewModel, loading: Boolean) {
    val messages by viewModel.lumiChatMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    val suggestionChips = listOf(
        "✨ Optimize my bio caption",
        "🌍 Translate 'Hello my friend' to Japanese",
        "🎨 Suggest engaging story hashtags",
        "🛡️ Scan safety of 'Click this wire link'"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Conversation lists
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("lumi_chat_logs"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == "me"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    LumiMessageBubble(message = msg, isMe = isMe)
                }
            }

            if (loading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E1938))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    color = Color(0xFF00E5FF),
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Lumi is thinking...", color = Color.White.copy(0.7f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Suggestions row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF080415))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestionChips) { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF8E2DE2).copy(0.12f))
                        .border(1.dp, Color(0xFF8E2DE2).copy(0.2f), RoundedCornerShape(16.dp))
                        .clickable { inputText = chip.substring(2) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(chip, color = Color(0xFFD0BCFF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Bottom input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F0C21))
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask Lumi anything...", color = Color.White.copy(0.4f), fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF8E2DE2)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("lumi_chat_input"),
                shape = RoundedCornerShape(24.dp)
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendLumiChatMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = !loading,
                modifier = Modifier
                    .size(44.dp)
                    .background(PremiumGradientBrush, CircleShape)
                    .testTag("lumi_chat_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun LumiMessageBubble(message: ChatMessage, isMe: Boolean) {
    val shape = if (isMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
    }

    val background = if (isMe) PremiumGradientBrush else Brush.linearGradient(listOf(Color(0xFF1F1B35), Color(0xFF1F1B35)))

    Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(background)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumiWritingSuiteTab(viewModel: ConnectLiveViewModel, loading: Boolean) {
    var sourceText by remember { mutableStateOf("") }
    var selectedTone by remember { mutableStateOf("Professional") }
    var selectedLanguage by remember { mutableStateOf("Spanish") }
    
    val writingResult by viewModel.lumiWritingResult.collectAsState()
    val threatScore by viewModel.lumiSafetyThreatScore.collectAsState()

    val tones = listOf("Professional", "Casual", "Hype", "Empathetic")
    val languages = listOf("Spanish", "French", "German", "Japanese", "Portuguese")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("lumi_suite_tab"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input Card
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Source Content",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sourceText,
                        onValueChange = { sourceText = it },
                        placeholder = { Text("Enter post text, bio draft, message or website links to analyze...", color = Color.White.copy(0.4f), fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("lumi_source_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Tones & Translations Configuration
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Customize Styling Tone",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tones) { tone ->
                            val isSelected = selectedTone == tone
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF00E5FF).copy(0.15f) else Color.White.copy(0.04f))
                                    .border(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { selectedTone = tone }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(tone, color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Target Language",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(languages) { lang ->
                            val isSelected = selectedLanguage == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF8E2DE2).copy(0.15f) else Color.White.copy(0.04f))
                                    .border(1.dp, if (isSelected) Color(0xFF8E2DE2) else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { selectedLanguage = lang }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(lang, color = if (isSelected) Color(0xFFD0BCFF) else Color.White.copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Optimize
                Button(
                    onClick = { viewModel.runLumiWritingSuiteAction(sourceText, selectedTone, "rewrite") },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("lumi_btn_rewrite"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF).copy(0.2f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("✨ Rewrite", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Translate
                Button(
                    onClick = { viewModel.runLumiWritingSuiteAction(sourceText, selectedTone, "translate", selectedLanguage) },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("lumi_btn_translate"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2).copy(0.2f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🌍 Translate", color = Color(0xFFD0BCFF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Grammar
                Button(
                    onClick = { viewModel.runLumiWritingSuiteAction(sourceText, selectedTone, "grammar") },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("lumi_btn_grammar"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50).copy(0.2f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🛡️ Spellcheck", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Safety Scan trigger
        item {
            Button(
                onClick = { viewModel.runLumiWritingSuiteAction(sourceText, selectedTone, "safety") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("lumi_btn_safety"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(0.2f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lumi Spam, Scam & Threat Scanner", color = Color(0xFFFF5252), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Result and Meters block
        if (writingResult.isNotBlank()) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Lumi AI Output Result",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = writingResult,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.testTag("lumi_suite_result")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // If safety threat analysis is running, show custom threat dial
                        if (threatScore > 0) {
                            Text(
                                text = "Threat Security scan",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(0.04f))
                                    .padding(12.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    CircularProgressIndicator(
                                        progress = { threatScore / 100f },
                                        color = if (threatScore > 50) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                        trackColor = Color.White.copy(0.1f),
                                        strokeWidth = 5.dp,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Text(
                                        text = "$threatScore%",
                                        color = if (threatScore > 50) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = if (threatScore > 50) "⚠️ High Threat Score Detected!" else "✅ Safe & Verified Content",
                                        color = if (threatScore > 50) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = if (threatScore > 50) "Contains possible spam patterns, unsolicited scam wire demands, or inappropriate keyword triggers." else "This text was safely parsed without finding malicious scam vectors or high spam scores.",
                                        color = Color.White.copy(0.5f),
                                        fontSize = 10.sp,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

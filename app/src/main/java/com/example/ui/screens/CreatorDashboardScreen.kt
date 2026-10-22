package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.CosmosBackgroundBrush
import com.example.ui.components.GlassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorDashboardScreen(viewModel: ConnectLiveViewModel) {
    var activeTab by remember { mutableStateOf("insights") } // insights, monetization, voice_fx, scheduler

    // Selected sound preview
    var activeVoiceFx by remember { mutableStateOf("Studio Voice") }
    var micGain by remember { mutableStateOf(0.8f) }
    var noiseRedux by remember { mutableStateOf(true) }

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
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("main") },
                    modifier = Modifier.testTag("creator_dashboard_back")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Creator Studio Hub", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            }

            // Tabs Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.04f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    "insights" to "Insights",
                    "monetization" to "Earning",
                    "voice_fx" to "Voice FX",
                    "scheduler" to "Scheduler"
                ).forEach { (tabId, label) ->
                    val isSelected = activeTab == tabId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF8E2DE2) else Color.Transparent)
                            .clickable { activeTab = tabId }
                            .padding(vertical = 8.dp)
                            .testTag("creator_tab_$tabId"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (activeTab) {
                    "insights" -> {
                        item {
                            Text("Channel Analytics Summary", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // Analytics Cards Grid
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatCard("Total Views", "148,921", "+24% this week", Icons.Default.TrendingUp)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    StatCard("Live Co-hosts", "12 partners", "Connected", Icons.Default.People)
                                }
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatCard("Diamonds Accrued", "💎 25,400", "Value: $254.00", Icons.Default.Diamond)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    StatCard("Supporters Club", "48 VIPs", "Active subscription", Icons.Default.Stars)
                                }
                            }
                        }

                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Broadcast Quality Status", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00E676)))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("1080p Stream Capability Enabled (Ultra Bitrate)", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    "monetization" -> {
                        item {
                            Text("Virtual Gift Configurations", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // Customize coin exchange payouts
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF00E5FF).copy(0.2f), RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.02f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Convert Creator Diamonds", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Accumulated diamonds can be directly converted back into Coins, or withdrawn as USD.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("25,400 Diamonds Available", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Payout Rate: 100 💎 = $1.00", color = Color.White.copy(0.4f), fontSize = 9.sp)
                                        }
                                        Button(
                                            onClick = {},
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("Withdraw", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Virtual Gift List Showcase
                        item {
                            Text("Your Enabled Digital Gifts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                GiftConfigRow("Virtual Bouquet", "Coins: 50 | Stars: ⭐⭐", true)
                                GiftConfigRow("Luxury Chronograph", "Coins: 1,000 | Stars: ⭐⭐⭐⭐⭐", true)
                                GiftConfigRow("Legendary Supercar", "Coins: 5,000 | Stars: ⭐⭐⭐⭐⭐", true)
                            }
                        }
                    }

                    "voice_fx" -> {
                        item {
                            Text("Smart Audio Engine Mixer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // Sound Effects Selection
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf(
                                    "Studio Voice" to "Professional vocal compression & dynamic auto-gain",
                                    "Concert Hall" to "Expansive ambient reverb mirroring massive live arenas",
                                    "Radio DJ" to "Warm analog radio signal low-pass distortion",
                                    "Robot Synthesizer" to "Pitch-shifted futuristic robotic auto-tuner"
                                ).forEach { (fx, desc) ->
                                    val isSel = activeVoiceFx == fx
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSel) Color.White.copy(0.08f) else Color.White.copy(0.02f))
                                            .border(1.dp, if (isSel) Color(0xFF8E2DE2) else Color.Transparent, RoundedCornerShape(12.dp))
                                            .clickable { activeVoiceFx = fx }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.4f)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(fx, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(desc, color = Color.White.copy(0.4f), fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Mic parameters
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Microphone Dynamic Gain", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Slider(
                                        value = micGain,
                                        onValueChange = { micGain = it },
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color(0xFF8E2DE2),
                                            activeTrackColor = Color(0xFF8E2DE2)
                                        )
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Noise Reduction Control", color = Color.White, fontSize = 11.sp)
                                        Switch(
                                            checked = noiseRedux,
                                            onCheckedChange = { noiseRedux = it },
                                            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF8E2DE2))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "scheduler" -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Upcoming Broadcast Calendar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("New Broadcast", color = Color.White, fontSize = 9.sp)
                                }
                            }
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ScheduledEventItem("Saturdays Live Jam Sessions", "Aug 22 • Recurring", "Audio & Video Arena")
                                ScheduledEventItem("Digital Collectible drop", "Aug 28 • Single event", "Creator Drops panel")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GiftConfigRow(name: String, payout: String, active: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(0.02f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(payout, color = Color.White.copy(0.4f), fontSize = 10.sp)
        }
        Switch(
            checked = active,
            onCheckedChange = {},
            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF8E2DE2))
        )
    }
}

@Composable
fun ScheduledEventItem(title: String, time: String, tag: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(0.02f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📅", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("$time • $tag", color = Color.White.copy(0.4f), fontSize = 10.sp)
            }
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(0.3f))
        }
    }
}

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.CosmosBackgroundBrush
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// VIP Models
data class VipTier(
    val level: Int,
    val name: String,
    val requiredXp: Int,
    val color: Color,
    val benefits: List<String>
)

data class VipDurationPack(
    val id: String,
    val days: Int,
    val costCoins: Int,
    val label: String
)

data class VipMission(
    val id: String,
    val description: String,
    val xpReward: Int,
    val current: Int,
    val target: Int,
    val isCompleted: Boolean,
    val isClaimed: Boolean
)

data class VipStoreItem(
    val id: String,
    val name: String,
    val type: String, // Frame, Entrance, Bubble
    val costDiamonds: Int,
    val previewEmoji: String
)

data class VipReportEntry(
    val action: String,
    val details: String,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipSystemScreen(viewModel: ConnectLiveViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val walletCoinBalance by viewModel.coinBalance.collectAsState(initial = 1000)

    // VIP Database and Configuration Tiers
    val vipTiers = listOf(
        VipTier(1, "Bronze Star VIP", 100, Color(0xFFCD7F32), listOf("Bronze VIP Badge", "Copper Profile Border", "Standard Daily Rewards")),
        VipTier(2, "Silver Eclipse VIP", 300, Color(0xFFC0C0C0), listOf("Silver VIP Badge", "Priority Speaking seat in Voice rooms", "+5% Coin Recharge Bonus")),
        VipTier(3, "Golden Solar VIP", 800, Color(0xFFFFD700), listOf("Golden Badge & Name color", "Solar Flare Profile Frame", "+10% Coin Bonus", "Priority seat queue")),
        VipTier(4, "Platinum Aura VIP", 1500, Color(0xFF708090), listOf("Platinum Badge", "Aura Entrance Effect", "Exclusive Chat Bubble", "Audience Badge Priority")),
        VipTier(5, "Diamond Zenith VIP", 3000, Color(0xFF00E5FF), listOf("Zenith Diamond Badge", "Starlight Avatar Border", "Special Gift access", "1.5x Daily Rewards")),
        VipTier(6, "Ruby Sovereign VIP", 5000, Color(0xFFFF1744), listOf("Ruby Sovereign Badge", "Dynamic Custom Entrance Animation", "Sovereign Room background", "Speaking Priority Overrides")),
        VipTier(7, "Sapphire Overlord VIP", 8000, Color(0xFF2979FF), listOf("Sapphire Overlord Badge", "Total Room decoration control", "+15% Coin recharge bonus")),
        VipTier(8, "Emerald Archon VIP", 12000, Color(0xFF00E676), listOf("Emerald Archon Badge", "Unlimited custom stickers", "Profile avatar glow effects")),
        VipTier(9, "Celestial Phoenix VIP", 18000, Color(0xFFFF9100), listOf("Celestial Badge", "Full-screen Phoenix entrance", "Custom name color override")),
        VipTier(10, "Supreme Sovereign VIP", 25000, Color(0xFFD500F9), listOf("Supreme Badge & Frame", "Elite Crown Chat bubble", "Direct Admin VIP concierges", "Double Daily Reward multipliers"))
    )

    val durationPacks = listOf(
        VipDurationPack("p_01", 7, 250, "7 Days Pass"),
        VipDurationPack("p_02", 30, 950, "30 Days Month"),
        VipDurationPack("p_03", 90, 2500, "90 Days Quarter"),
        VipDurationPack("p_04", 180, 4800, "180 Days Half-Year"),
        VipDurationPack("p_05", 365, 8500, "365 Days Premium Year")
    )

    // --- Dynamic VIP State (Simulating Local Live Persistence linked with VM operations) ---
    val currentVipLevel = remember { mutableStateOf(3) } // Solar VIP
    val vipXp = remember { mutableStateOf(520) }
    val isAutoRenew = remember { mutableStateOf(true) }
    val remainingDays = remember { mutableStateOf(42) }
    val membershipStatus = remember { mutableStateOf("ACTIVE • PREMIUM") }
    val rewardsClaimed = remember { mutableStateOf(false) }

    val vipMissions = remember {
        mutableStateListOf(
            VipMission("M_01", "Daily Login to VIP Portal", 15, 1, 1, true, false),
            VipMission("M_02", "Gift Sender (Spend over 100 coins in room)", 35, 45, 100, false, false),
            VipMission("M_03", "Voice Room Audience Session (15 mins)", 25, 15, 15, true, false),
            VipMission("M_04", "Ambassador Invite (Apply custom referral)", 50, 0, 1, false, false)
        )
    }

    val securityReports = remember {
        mutableStateListOf(
            VipReportEntry("VIP_ACTIVATE", "Purchased 30 Days Pass via In-App Coin Wallet.", "2026-07-02 12:44"),
            VipReportEntry("REWARD_CLAIM", "Claimed VIP Daily Coins & Points reward bundle.", "2026-07-20 09:15"),
            VipReportEntry("LEVEL_UP", "Upgraded to Solar VIP Level 3 via experience progression.", "2026-07-15 16:30")
        )
    }

    // Tab manager
    var selectedTab by remember { mutableStateOf("dashboard") } // dashboard, tiers, claims, store, reports

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Lumexa VIP Club Portal", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo("profile") },
                        modifier = Modifier.testTag("vip_system_back")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0C071A))
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CosmosBackgroundBrush)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tab Selection Bar
                TabRow(
                    selectedTabIndex = listOf("dashboard", "tiers", "claims", "store", "reports").indexOf(selectedTab),
                    containerColor = Color(0xFF0C071A),
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[listOf("dashboard", "tiers", "claims", "store", "reports").indexOf(selectedTab)]),
                            color = Color(0xFFFFD700)
                        )
                    }
                ) {
                    val tabNames = listOf(
                        "dashboard" to "VIP Hub",
                        "tiers" to "Club Tiers",
                        "claims" to "Daily Missions",
                        "store" to "VIP Store",
                        "reports" to "Security Logs"
                    )
                    tabNames.forEach { (id, label) ->
                        Tab(
                            selected = selectedTab == id,
                            onClick = { selectedTab = id },
                            text = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("vip_tab_$id")
                        )
                    }
                }

                // Main Section Renders
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(14.dp)
                ) {
                    when (selectedTab) {
                        "dashboard" -> DashboardSegment(
                            vipLevel = currentVipLevel.value,
                            xp = vipXp.value,
                            isAutoRenew = isAutoRenew.value,
                            remainingDays = remainingDays.value,
                            status = membershipStatus.value,
                            walletCoins = walletCoinBalance ?: 1000,
                            durationPacks = durationPacks,
                            currentTierObj = vipTiers[currentVipLevel.value - 1],
                            onAutoRenewToggle = { isAutoRenew.value = !isAutoRenew.value },
                            onPurchasePack = { pack ->
                                val coins = walletCoinBalance ?: 1000
                                if (coins >= pack.costCoins) {
                                    // Deduct via transaction simulation or direct buy
                                    viewModel.buyCoins(-pack.costCoins, 0.0) // Deduct coins from wallet!
                                    remainingDays.value += pack.days
                                    vipXp.value += (pack.costCoins / 5) // 1 XP per 5 coins spent
                                    securityReports.add(0, VipReportEntry("VIP_RENEWAL", "Extended VIP subscription by ${pack.days} Days (-${pack.costCoins} Coins).", "Just Now"))
                                    Toast.makeText(context, "VIP Renewed Successfully! +${pack.costCoins / 5} VIP XP added!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Insufficient Coins in Wallet! Recharge coins first.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        "tiers" -> TiersSegment(vipTiers = vipTiers, currentVipLevel = currentVipLevel.value)
                        "claims" -> MissionsSegment(
                            missions = vipMissions,
                            rewardsClaimed = rewardsClaimed.value,
                            onClaimDailyRewards = {
                                rewardsClaimed.value = true
                                viewModel.buyCoins(150, 0.0) // +150 Coins from daily VIP!
                                vipXp.value += 25
                                securityReports.add(0, VipReportEntry("CLAIM_DAILY", "Claimed Daily VIP reward bundle (+150 Coins, +25 VIP XP).", "Just Now"))
                                Toast.makeText(context, "Rewards Claimed! +150 Coins and +25 VIP XP!", Toast.LENGTH_LONG).show()
                            },
                            onClaimMission = { mission ->
                                val idx = vipMissions.indexOf(mission)
                                if (idx != -1) {
                                    vipMissions[idx] = mission.copy(isClaimed = true)
                                    vipXp.value += mission.xpReward
                                    // Handle level progression automatic check
                                    val nextTier = vipTiers.firstOrNull { it.level == currentVipLevel.value + 1 }
                                    if (nextTier != null && vipXp.value >= nextTier.requiredXp) {
                                        currentVipLevel.value += 1
                                        securityReports.add(0, VipReportEntry("VIP_LEVEL_UP", "Upgraded to ${nextTier.name} Level ${currentVipLevel.value}!", "Just Now"))
                                        Toast.makeText(context, "CONGRATULATIONS! You reached VIP Level ${currentVipLevel.value}!", Toast.LENGTH_LONG).show()
                                    }
                                    securityReports.add(0, VipReportEntry("CLAIM_MISSION", "Claimed rewards for '${mission.description}' (+${mission.xpReward} VIP XP).", "Just Now"))
                                    Toast.makeText(context, "Mission claimed! +${mission.xpReward} VIP XP!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        "store" -> StoreSegment(currentVipLevel = currentVipLevel.value)
                        "reports" -> SecuritySegment(reports = securityReports)
                    }
                }
            }
        }
    }
}

// =================== COMPOSABLE SEGMENTS ===================

@Composable
fun DashboardSegment(
    vipLevel: Int,
    xp: Int,
    isAutoRenew: Boolean,
    remainingDays: Int,
    status: String,
    walletCoins: Int,
    durationPacks: List<VipDurationPack>,
    currentTierObj: VipTier,
    onAutoRenewToggle: () -> Unit,
    onPurchasePack: (VipDurationPack) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main Badge Display
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                border = BorderStroke(2.dp, Brush.linearGradient(listOf(currentTierObj.color, Color.White, currentTierObj.color))),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(status, color = currentTierObj.color, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            Text(currentTierObj.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                            Text("Membership ID: LX_VIP_998341", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        }

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(currentTierObj.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("V$vipLevel", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress indicators
                    Text("VIP Club XP: $xp Points", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = xp.toFloat() / 1500f, // relative to Level 4
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = currentTierObj.color,
                        trackColor = Color.White.copy(0.1f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Level $vipLevel", color = Color.White.copy(0.4f), fontSize = 9.sp)
                        Text("Next Level Upgrade at 1500 XP", color = Color.White.copy(0.4f), fontSize = 9.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.White.copy(0.1f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("VALIDITY REMAINING", color = Color.White.copy(0.4f), fontSize = 10.sp)
                            Text("$remainingDays Days Left", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Auto Renew", color = Color.White.copy(0.6f), fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = isAutoRenew,
                                onCheckedChange = { onAutoRenewToggle() },
                                colors = SwitchDefaults.colors(checkedThumbColor = currentTierObj.color)
                            )
                        }
                    }
                }
            }
        }

        // Active Benefits Quickview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ACTIVE LEVEL PRIVILEGES", color = currentTierObj.color, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    currentTierObj.benefits.forEach { benefit ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = currentTierObj.color, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(benefit, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Coins balance disclaimer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Extend / Purchase VIP Passes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Wallet Balance: 🪙 $walletCoins", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Extension duration packages
        items(durationPacks) { pack ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD700).copy(0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(pack.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Includes exclusive cosmetic packages", color = Color.White.copy(0.4f), fontSize = 10.sp)
                        }
                    }

                    Button(
                        onClick = { onPurchasePack(pack) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        modifier = Modifier.testTag("buy_vip_pack_${pack.id}")
                    ) {
                        Text("🪙 ${pack.costCoins}", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TiersSegment(vipTiers: List<VipTier>, currentVipLevel: Int) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Ecosystem VIP 1-10 Progression Hierarchy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(vipTiers) { tier ->
            val isActive = tier.level == currentVipLevel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (isActive) Color(0xFF1E1338) else Color.White.copy(0.04f)),
                border = if (isActive) BorderStroke(1.dp, tier.color) else null
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(tier.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("V${tier.level}", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(tier.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        tier.benefits.forEach { b ->
                            Text("• $b", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("${tier.requiredXp} XP", color = tier.color, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("Required", color = Color.White.copy(0.4f), fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MissionsSegment(
    missions: List<VipMission>,
    rewardsClaimed: Boolean,
    onClaimDailyRewards: () -> Unit,
    onClaimMission: (VipMission) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Daily login static rewards claim
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1C05)),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("DAILY VIP REWARD CHEST", color = Color(0xFFFFD700), fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Text("Claim daily 150 Coins and 25 VIP XP!", color = Color.White.copy(0.7f), fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onClaimDailyRewards() },
                        enabled = !rewardsClaimed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            disabledContainerColor = Color.White.copy(0.08f)
                        ),
                        modifier = Modifier.testTag("claim_daily_vip")
                    ) {
                        Text(if (rewardsClaimed) "Claimed ✔" else "Claim", color = if (rewardsClaimed) Color.White.copy(0.4f) else Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("VIP Activity Missions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(missions) { mission ->
            val comp = mission.current >= mission.target
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(mission.description, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Progress: ${mission.current}/${mission.target}", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("+${mission.xpReward} XP", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Button(
                            onClick = { onClaimMission(mission) },
                            enabled = comp && !mission.isClaimed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00E676),
                                disabledContainerColor = Color.White.copy(0.06f)
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(if (mission.isClaimed) "Claimed" else if (comp) "Claim" else "Go", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoreSegment(currentVipLevel: Int) {
    val items = listOf(
        VipStoreItem("v_01", "Dragon Archon Frame", "Frame", 500, "👑"),
        VipStoreItem("v_02", "Galaxy Starfall Entrance", "Entrance", 1200, "🌠"),
        VipStoreItem("v_03", "Golden Crown Chat Bubble", "Bubble", 300, "💬"),
        VipStoreItem("v_04", "Cosmic Sovereign Room Background", "Entrance", 1800, "🌌")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("VIP Club Store - Exclusive Cosmetics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Only VIP members of adequate level can purchase premium decorations.", color = Color.White.copy(0.5f), fontSize = 11.sp)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { item ->
                val adequateVip = currentVipLevel >= 3
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.previewEmoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Type: ${item.type} • Requires VIP 3+", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = { /* simulated purchase */ },
                            enabled = adequateVip,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                        ) {
                            Text("💎 ${item.costDiamonds}", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecuritySegment(reports: List<VipReportEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("VIP Access Audit and Fraud Prevention Logs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(reports) { r ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.02f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFD700).copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(r.action, color = Color(0xFFFFD700), fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                        Text(r.date, color = Color.White.copy(0.4f), fontSize = 9.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(r.details, color = Color.White.copy(0.8f), fontSize = 11.sp)
                }
            }
        }
    }
}

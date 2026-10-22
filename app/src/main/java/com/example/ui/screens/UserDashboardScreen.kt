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
import com.example.ui.components.PremiumGradientBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(viewModel: ConnectLiveViewModel) {
    val meProfile by viewModel.meProfile.collectAsState()
    var selectedTab by remember { mutableStateOf("overview") } // overview, missions, referrals, content_stats

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
                    modifier = Modifier.testTag("user_dashboard_back")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("My Personal Arena Dashboard", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            }

            // Quick Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.04f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("overview" to "Overview", "missions" to "Missions", "referrals" to "Referrals", "content_stats" to "Analytics").forEach { (tabId, label) ->
                    val isSelected = selectedTab == tabId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF00E5FF) else Color.Transparent)
                            .clickable { selectedTab = tabId }
                            .padding(vertical = 8.dp)
                            .testTag("user_dashboard_tab_$tabId"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = if (isSelected) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                when (selectedTab) {
                    "overview" -> {
                        // User XP Level Card
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Pioneer Status • LVL 12", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(meProfile?.name ?: "Alex Mercer", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(PremiumGradientBrush),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("👑", fontSize = 18.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // XP Bar
                                    Text("XP Progress: 3,450 / 5,000 XP (69%)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = 0.69f,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(CircleShape),
                                        color = Color(0xFF8E2DE2),
                                        trackColor = Color.White.copy(0.1f)
                                    )
                                }
                            }
                        }

                        // Social Graphs stats row
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatCard("Followers", "1,248", "Suggested creators", Icons.Default.People)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    StatCard("Following", "410", "Active following", Icons.Default.PersonAdd)
                                }
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatCard("Friends", "180", "Mutual friends", Icons.Default.Group)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    StatCard("VIP Subscription", "ACTIVE", "Tier 2 Gold", Icons.Default.Stars)
                                }
                            }
                        }

                        // Wallet Mini Overview
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFFFD700).copy(0.3f), RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("CURRENT BALANCE", color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text("🪙 500 COINS", color = Color(0xFFFFD700), fontSize = 22.sp, fontWeight = FontWeight.Black)
                                        Text("Includes welcome token rewards", color = Color.White.copy(0.4f), fontSize = 9.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.setTab("wallet") ; viewModel.navigateTo("main") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Wallet", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Upcoming Events
                        item {
                            Column {
                                Text("Upcoming Live Events", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))
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
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF8E2DE2).copy(0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF8E2DE2))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Synthesizer Masterclass", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Today, 4:00 PM • Hosted by Sato", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                        }
                                    }
                                    IconButton(onClick = {}) {
                                        Icon(Icons.Default.Notifications, contentDescription = "Notify", tint = Color(0xFF00E5FF))
                                    }
                                }
                            }
                        }
                    }

                    "missions" -> {
                        // Missions Section
                        item {
                            Text("Daily Quests & Challenges", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                MissionRow("Daily Check-in", "Login to Lumexa", "10/10 XP", completed = true)
                                MissionRow("Join Voice Party", "Spend 5 minutes in any audio room", "50 XP", completed = false)
                                MissionRow("Send Virtual Flower", "Send 1 gift from digital gift store", "100 XP", completed = false)
                            }
                        }

                        item {
                            Text("Weekly Milestone Goals", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                MissionRow("Live Stream Watcher", "Watch streams for 30 mins", "250 XP", completed = false)
                                MissionRow("Social Connector", "Add 3 suggested friends", "150 XP", completed = false)
                            }
                        }
                    }

                    "referrals" -> {
                        // Referral System Overview
                        item {
                            val count by viewModel.referredUsersCount.collectAsState()
                            val myCode by viewModel.myReferralCode.collectAsState()
                            val recruiter by viewModel.recruiterName.collectAsState()
                            val role by viewModel.recruitmentRole.collectAsState()
                            val context = androidx.compose.ui.platform.LocalContext.current

                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Ambassador Referral & Recruitment Program", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                
                                // Referral Code Display Card
                                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("YOUR PERSONAL REFERRAL CODE", color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = myCode,
                                                color = Color(0xFFFFD700),
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = {
                                                        android.widget.Toast.makeText(context, "Copied code: $myCode to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.height(34.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Copy", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Button(
                                                    onClick = {
                                                        viewModel.simulateNewRecruit()
                                                        android.widget.Toast.makeText(context, "Simulating signup! Awarded you +50 points!", android.widget.Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.height(34.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                                ) {
                                                    Text("Simulate Partner", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Share this code with partners. You receive 50 points, 100 XP, and 10% commission on their diamond earnings when they join!", color = Color.White.copy(0.4f), fontSize = 9.sp)
                                    }
                                }

                                // Recruitment level / connection
                                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.AccountTree, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Recruitment Hierarchy", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Role: $role • Recruiter: $recruiter", color = Color.White.copy(0.6f), fontSize = 11.sp)
                                            Text("Direct lineage secured under Super Manager Sato hierarchy.", color = Color.White.copy(0.4f), fontSize = 9.sp)
                                        }
                                    }
                                }

                                // Milestones and Rewards progress
                                Text("Referral Milestone Goals", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                val claimedMilestones by viewModel.claimedReferralMilestones.collectAsState()

                                val milestonesList = listOf(
                                    Triple("milestone_1", "Recruit 1 Partner", 100 to 100),
                                    Triple("milestone_2", "Recruit 5 Partners", 300 to 200),
                                    Triple("milestone_3", "Recruit 15 Partners", 1000 to 500)
                                )

                                milestonesList.forEach { (id, title, rewards) ->
                                    val (coins, xp) = rewards
                                    val isClaimed = claimedMilestones.contains(id)
                                    val targetCount = if (id == "milestone_1") 1 else if (id == "milestone_2") 5 else 15
                                    val canClaim = count >= targetCount && !isClaimed

                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.03f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("Progress: $count / $targetCount partners recruited", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                                Text("Reward: 🪙 $coins Coins & $xp XP", color = Color(0xFFFFD700), fontSize = 9.sp)
                                            }
                                            Button(
                                                onClick = {
                                                    val success = viewModel.claimMilestoneReward(id, coins, xp)
                                                    if (success) {
                                                        android.widget.Toast.makeText(context, "Claimed $coins Coins and $xp XP!", android.widget.Toast.LENGTH_LONG).show()
                                                    }
                                                },
                                                enabled = canClaim,
                                                colors = ButtonDefaults.buttonColors(containerColor = if (isClaimed) Color.White.copy(0.08f) else Color(0xFFFFD700)),
                                                modifier = Modifier.height(28.dp),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp)
                                            ) {
                                                Text(
                                                    text = if (isClaimed) "Claimed" else if (canClaim) "Claim" else "Locked",
                                                    color = if (canClaim) Color.Black else Color.White.copy(0.4f),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                // Referred Partners list
                                Text("My Recruited Partners Network ($count)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                val referredList by viewModel.referredUsers.collectAsState()

                                if (referredList.isEmpty()) {
                                    Text("No recruits registered yet. Share your code to get started!", color = Color.White.copy(0.4f), fontSize = 11.sp)
                                } else {
                                    referredList.forEach { (name, status, earnings) ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.02f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFF8E2DE2).copy(0.15f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        Text(status, color = Color.White.copy(0.5f), fontSize = 9.sp)
                                                    }
                                                }
                                                Text(earnings, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "content_stats" -> {
                        // Statistics Overview
                        item {
                            Text("Channel Content Performance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                AnalyticsBar("Story Views", "4,821 views", 0.78f, Color(0xFF00E5FF))
                                AnalyticsBar("Reels Engagement", "230 likes", 0.45f, Color(0xFFFF4081))
                                AnalyticsBar("Live Stream Peak", "42 co-listeners", 0.32f, Color(0xFFFFD700))
                            }
                        }

                        // Draft Content
                        item {
                            Column {
                                Text("Saved Drafts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))
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
                                        Text("📝", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Draft: Synth Lofi Music Set #1", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Saved 2 hours ago • Audio post", color = Color.White.copy(0.4f), fontSize = 10.sp)
                                        }
                                    }
                                    IconButton(onClick = {}) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Draft", tint = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, subtitle: String, icon: ImageVector) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color.White.copy(0.6f), fontSize = 11.sp)
                Icon(icon, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(0.4f), fontSize = 9.sp)
        }
    }
}

@Composable
fun MissionRow(title: String, subtitle: String, reward: String, completed: Boolean) {
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
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (completed) Color(0xFF00E676).copy(0.15f) else Color.White.copy(0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (completed) Icons.Default.Check else Icons.Default.Star,
                    contentDescription = null,
                    tint = if (completed) Color(0xFF00E676) else Color.White.copy(0.4f),
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(0.5f), fontSize = 10.sp)
            }
        }
        Text(reward, color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AnalyticsBar(label: String, valStr: String, fillPercent: Float, barColor: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(valStr, color = Color.White.copy(0.6f), fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color.White.copy(0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fillPercent)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(barColor)
            )
        }
    }
}

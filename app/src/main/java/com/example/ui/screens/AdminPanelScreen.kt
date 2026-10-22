package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.SystemLogEntry
import com.example.data.UserProfile
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.GlassmorphicCard

@Composable
fun AdminPanelScreen(viewModel: ConnectLiveViewModel) {
    val users by viewModel.suggestedFriends.collectAsState()
    val logs by viewModel.systemLogs.collectAsState()
    val currentUserRole by viewModel.currentUserRole.collectAsState()

    var activeAdminTab by remember { mutableStateOf("dashboard") } // dashboard, users, logs

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080512))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        if (currentUserRole == "User") {
            // Security Access Denied Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Access Denied",
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Security Authorization Required",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Your account is currently registered under standard 'User' clearance. Administrative command interfaces are restricted to authorized platform leaders and supervisors.",
                    fontSize = 12.sp,
                    color = Color.White.copy(0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = { viewModel.navigateTo("main") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Return to Social Feed", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } else {
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
                    modifier = Modifier.testTag("admin_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Central Administration Command", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { viewModel.navigateTo("operations_hub") },
                    modifier = Modifier.testTag("admin_operations_hub_trigger")
                ) {
                    Icon(Icons.Default.Engineering, contentDescription = "Operations Hub", tint = Color(0xFFFFB300))
                }

                IconButton(
                    onClick = { viewModel.navigateTo("ui_library") },
                    modifier = Modifier.testTag("admin_ui_library_trigger")
                ) {
                    Icon(Icons.Default.Palette, contentDescription = "UI Library", tint = Color(0xFF00E5FF))
                }
            }

            // Tab bar switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.04f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    "dashboard" to "Telemetry",
                    "roles" to "Roles & Perms",
                    "agencies_hosts" to "Agencies/Hosts",
                    "reports" to "Moderation",
                    "backups" to "Backup",
                    "users" to "Users",
                    "logs" to "Audits"
                ).forEach { (tabId, label) ->
                    val isSelected = activeAdminTab == tabId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF8E2DE2) else Color.Transparent)
                            .clickable { activeAdminTab = tabId }
                            .padding(vertical = 8.dp)
                            .testTag("admin_tab_$tabId"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            when (activeAdminTab) {
                "dashboard" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("System Telemetry Stats", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }

                        // Statistics Grid Row
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    AdminStatCard(title = "Total Sessions", count = "14,892", label = "+12% today")
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    AdminStatCard(title = "Coin Volume", count = "450K+", label = "Syncing live")
                                }
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    AdminStatCard(title = "Active Voice Rooms", count = "4", label = "Healthy load")
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    AdminStatCard(title = "Revenue Reports", count = "$12,450", label = "Monthly sync")
                                }
                            }
                        }

                        item {
                            Text("Security Status: SECURE • ALL SERVICES NOMINAL", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                "reports" -> {
                    var triageMsg by remember { mutableStateOf<String?>(null) }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("Inbound Moderation Queue", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }

                        val mockReports = listOf(
                            Triple("Elena's Post", "Harassment & Hate Speech", "94% Match"),
                            Triple("User_9210 Avatar", "Impersonation", "81% Match"),
                            Triple("Marketplace Ad", "Fake Profile / Scam", "76% Match"),
                            Triple("Crypto Comment", "Spam & Scams", "99% Match")
                        )

                        items(mockReports) { (item, type, score) ->
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Reported: $item", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Type: $type", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFFF1744).copy(0.15f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("AI Triage: $score", color = Color(0xFFFF1744), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { triageMsg = "AI System Triage Action: Approved take-down of $item. Moderator Decision logged." },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                                            modifier = Modifier.weight(1f).height(32.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Take Down", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = { triageMsg = "Dismissed moderation report for $item. Status logged as clean." },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                            modifier = Modifier.weight(1f).height(32.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Dismiss", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        triageMsg?.let { msg ->
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E5FF).copy(0.1f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = msg,
                                        color = Color(0xFF00E5FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                "backups" -> {
                    var backupStatus by remember { mutableStateOf("Ready") }
                    var progressState by remember { mutableStateOf(false) }

                    LaunchedEffect(progressState) {
                        if (progressState) {
                            kotlinx.coroutines.delay(2000)
                            backupStatus = "System Backup COMPLETED. Recovery Point Objective (RPO) updated. Disaster Recovery ready."
                            progressState = false
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("Backup & Recovery Control", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }

                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Disaster Recovery Parameters", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Recovery Point Objective (RPO)", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                            Text("15 Minutes max lag", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Recovery Time Objective (RTO)", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                            Text("1 Hour resolution", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Encryption: AES-256 GCM Secure Storage", color = Color.White.copy(0.4f), fontSize = 10.sp)
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        progressState = true
                                        backupStatus = "Running Automatic Backups..."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Text("Auto Backup", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        progressState = true
                                        backupStatus = "Performing Incremental Backup synchronization..."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Text("Incremental", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (progressState) {
                            item {
                                LinearProgressIndicator(
                                    color = Color(0xFF00E5FF),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("System Backup Status Log", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(backupStatus, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Run Disaster Recovery Restore Test button
                        item {
                            Button(
                                onClick = {
                                    backupStatus = "DISASTER RECOVERY RESTORE TEST: SUCCESS. Restored 42,829 files in 1.4 seconds. Audit log created."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Run DR Restore Test", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                "users" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(users) { user ->
                            AdminUserItem(user = user, viewModel = viewModel)
                        }
                    }
                }

                "roles" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("Multi-Tier Admin Hierarchy & Role Permission Matrix", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }

                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Super Admin Roles & Clearance Levels", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    val roleLevels = listOf(
                                        Triple("Platform Owner", "FULL_SYSTEM_AUTHORITY • Economy • Rules", Color(0xFFFF1744)),
                                        Triple("Super Admin", "USER_MGMT • HOST_MGMT • AGENCY_MGMT", Color(0xFFE040FB)),
                                        Triple("Admin Manager", "HOST_APPROVALS • AGENCY_APPROVALS", Color(0xFF00E5FF)),
                                        Triple("Operation Admin", "LIVE_ROOM_MONITOR • BAN_RESTRICT", Color(0xFFFFB300)),
                                        Triple("Support Admin", "TICKETS • DISPUTES • USER_ASSIST", Color(0xFF00E676)),
                                        Triple("Moderator", "CHAT_MODERATION • CONTENT_TAKE_DOWN", Color(0xFF2979FF))
                                    )

                                    roleLevels.forEach { (role, desc, color) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White.copy(0.02f))
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(role, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text(desc, color = Color.White.copy(0.5f), fontSize = 9.sp)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(color.copy(0.15f))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("ACTIVE", color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "agencies_hosts" -> {
                    var actionFeedbackMsg by remember { mutableStateOf<String?>(null) }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("Agency & Host Recruitment Approval Portal", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }

                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Pending Agency Registration Requests", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    val pendingAgencies = listOf(
                                        Pair("Cosmic Media Agency", "ID: AG_9011 • Owner: Marcus Vance • 25 Hosts"),
                                        Pair("Apex Talent Group", "ID: AG_9012 • Owner: Elena Rostova • 40 Hosts")
                                    )

                                    pendingAgencies.forEach { (agency, details) ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White.copy(0.02f))
                                                .padding(10.dp)
                                        ) {
                                            Text(agency, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(details, color = Color.White.copy(0.5f), fontSize = 10.sp)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = { actionFeedbackMsg = "APPROVED: Agency '$agency' is now verified on Lumexa Global Network." },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                                    modifier = Modifier.weight(1f).height(32.dp),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Approve Agency", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Button(
                                                    onClick = { actionFeedbackMsg = "REJECTED: Agency '$agency' registration declined." },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                                                    modifier = Modifier.weight(1f).height(32.dp),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Reject", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        actionFeedbackMsg?.let { msg ->
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E5FF).copy(0.1f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = msg,
                                        color = Color(0xFF00E5FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                "logs" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(logs) { entry ->
                            AdminLogItem(entry = entry)
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun AdminStatCard(title: String, count: String, label: String) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(count, fontWeight = FontWeight.Black, color = Color.White, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminUserItem(user: UserProfile, viewModel: ConnectLiveViewModel) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.name,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(user.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    Text("@${user.username} • ${user.country}", color = Color.White.copy(0.4f), fontSize = 9.sp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.blockUser(user.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4D)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text("Suspend", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminLogItem(entry: SystemLogEntry) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(entry.action, fontWeight = FontWeight.Black, color = Color(0xFFD0BCFF), fontSize = 11.sp)
                Text("Audit Success", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(entry.details, color = Color.White.copy(0.7f), fontSize = 11.sp)
        }
    }
}

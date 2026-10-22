package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.CosmosBackgroundBrush
import com.example.ui.components.PremiumGradientBrush
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationsHubScreen(viewModel: ConnectLiveViewModel) {
    EnterpriseSystemsHub(viewModel = viewModel)
}

// ==========================================
// 1. RBAC MODULE COMPOSABLE
// ==========================================
@Composable
fun RbacPermissionsTab() {
    var selectedRole by remember { mutableStateOf("Super Manager") }
    var createRoleName by remember { mutableStateOf("") }
    var cloneFromRole by remember { mutableStateOf("Super Manager") }
    var countryFilter by remember { mutableStateOf("All Countries") }
    var limitTimeAccess by remember { mutableStateOf(false) }
    var rbacMessage by remember { mutableStateOf<String?>(null) }

    val roles = listOf(
        "Platform Owner",
        "Super Manager Leader",
        "Super Manager",
        "BD Leader",
        "BD Official",
        "Merchant Reseller Leader",
        "Merchant",
        "Coin Seller",
        "Coin Reseller",
        "Agency Owner",
        "Host",
        "Moderator"
    )

    // Permission Categories
    val permissionsMap = remember {
        mutableStateMapOf(
            "User Management" to mutableStateMapOf(
                "View Users" to true, "Edit Profile" to true, "Suspend User" to true, "Ban User" to true
            ),
            "Wallet / Fin" to mutableStateMapOf(
                "View Wallet" to true, "Freeze Wallet" to false, "Add Coins" to false, "Approve Withdrawals" to false
            ),
            "Voice Room" to mutableStateMapOf(
                "Create Rooms" to true, "Lock Rooms" to true, "Close Rooms" to false, "Room Logs" to true
            ),
            "Admin Center" to mutableStateMapOf(
                "View Dashboard" to true, "Configure Targets" to false, "Edit RBAC" to false, "Export Reports" to true
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("rbac_scroll_container"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Central Role-Based Access Control (RBAC)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Assign, clone, and configure precise system permissions per role dynamically.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Selected Role Info
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Select Role to Configure", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Horizontal scroll of roles
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        roles.forEach { role ->
                            val isSelected = selectedRole == role
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFFFFB300) else Color.White.copy(0.05f))
                                    .clickable { selectedRole = role }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = role,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Permissions Checkboxes
        item {
            Text("Permissions Matrix: $selectedRole", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
        }

        items(permissionsMap.keys.toList()) { category ->
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(category, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val itemsMap = permissionsMap[category] ?: emptyMap()
                    itemsMap.keys.forEach { permission ->
                        val isChecked = itemsMap[permission] ?: false
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(permission, fontSize = 11.sp, color = Color.White.copy(0.8f))
                            Switch(
                                checked = isChecked,
                                onCheckedChange = { permissionsMap[category]?.put(permission, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFFFB300),
                                    checkedTrackColor = Color(0xFFFFB300).copy(0.3f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Access Restrictions
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Access & Territorial Restrictions", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Country Filter", fontSize = 11.sp, color = Color.White.copy(0.7f))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("All", "USA", "Spain", "Japan").forEach { c ->
                                val active = (c == "All" && countryFilter == "All Countries") || countryFilter.contains(c)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (active) Color(0xFF8E2DE2) else Color.White.copy(0.05f))
                                        .clickable { countryFilter = if (c == "All") "All Countries" else "Restricted to $c" }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(c, color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Business Hours Access Only", fontSize = 11.sp, color = Color.White.copy(0.7f))
                        Checkbox(
                            checked = limitTimeAccess,
                            onCheckedChange = { limitTimeAccess = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFB300))
                        )
                    }
                }
            }
        }

        // Clone & Custom Role creation
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Create or Clone Role Template", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = createRoleName,
                        onValueChange = { createRoleName = it },
                        label = { Text("New Role Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFFB300)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (createRoleName.isNotBlank()) {
                                    rbacMessage = "SUCCESS: Created custom role '$createRoleName' cloned from '$cloneFromRole'. All permissions loaded."
                                    createRoleName = ""
                                } else {
                                    rbacMessage = "ERROR: Please enter a valid role name first."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Create Custom Role", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        rbacMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (msg.startsWith("ERR")) Color(0xFFFF5252).copy(0.1f) else Color(0xFF00E676).copy(0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = if (msg.startsWith("ERR")) Color(0xFFFF5252) else Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ==========================================
// 2. TARGETS & COMMISSIONS COMPOSABLE
// ==========================================
@Composable
fun TargetsCommissionsTab() {
    var targetRole by remember { mutableStateOf("BD Official") }
    var dailyTargetVal by remember { mutableStateOf("5") }
    var weeklyTargetVal by remember { mutableStateOf("25") }
    var monthlyTargetVal by remember { mutableStateOf("100") }
    var targetEnabled by remember { mutableStateOf(true) }

    var commType by remember { mutableStateOf("Percentage") } // Percentage, Fixed, Tier-Based
    var commAmount by remember { mutableStateOf("8.5%") }
    var penaltyInactivity by remember { mutableStateOf("15 Days") }

    var feedbackMsg by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("KPI Targets, Commissions & Promotion Rules", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Define dynamic target quotas, automatic promotion rules, and commission schedules.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Configuration Form
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Select Target Entity", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("BD Leader", "BD Official", "Merchant Reseller Leader", "Merchant", "Coin Seller", "Agency Owner", "Host").forEach { role ->
                            val active = targetRole == role
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) Color(0xFF8E2DE2) else Color.White.copy(0.05f))
                                    .clickable { targetRole = role }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(role, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Target System", fontSize = 11.sp, color = Color.White)
                        Switch(checked = targetEnabled, onCheckedChange = { targetEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFB300)))
                    }

                    if (targetEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = dailyTargetVal,
                            onValueChange = { dailyTargetVal = it },
                            label = { Text("Daily Onboarding Goal") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = weeklyTargetVal,
                            onValueChange = { weeklyTargetVal = it },
                            label = { Text("Weekly Quota Target") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = monthlyTargetVal,
                            onValueChange = { monthlyTargetVal = it },
                            label = { Text("Monthly Cumulative Goal") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Commissions & Penalties
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Commission Configuration", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Percentage", "Fixed Amount", "Tier-Based").forEach { type ->
                            val active = commType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (active) Color(0xFFFFB300).copy(0.2f) else Color.White.copy(0.04f))
                                    .border(1.dp, if (active) Color(0xFFFFB300) else Color.Transparent, RoundedCornerShape(6.dp))
                                    .clickable { commType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(type, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = commAmount,
                        onValueChange = { commAmount = it },
                        label = { Text("Commission Rate (e.g. 8.5% or $10)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Automated Demotion Parameters", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFFF5252).copy(0.9f))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = penaltyInactivity,
                        onValueChange = { penaltyInactivity = it },
                        label = { Text("Max Inactive Period before Auto-Demote") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Save Controls
        item {
            Button(
                onClick = {
                    feedbackMsg = "SUCCESS: Commission and target specs updated for $targetRole: Daily=$dailyTargetVal, Monthly=$monthlyTargetVal. Commission Rate: $commAmount."
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Save Operations Configuration", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        feedbackMsg?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ==========================================
// 3. SUPER MANAGER LEADER TAB
// ==========================================
@Composable
fun SuperManagerLeaderTab() {
    var announcementText by remember { mutableStateOf("") }
    var annOutcome by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Super Manager Leader Command Dashboard", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Highest oversight dashboard for regional activity, recruitment tree, and agency growth channels.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Overall Stats Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    SmlStatCard("Super Managers", "14 Active", "3 regions")
                }
                Box(modifier = Modifier.weight(1f)) {
                    SmlStatCard("BD Leaders", "38 Active", "+4% growth")
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    SmlStatCard("Coin Resellers", "112 Live", "98.4% online")
                }
                Box(modifier = Modifier.weight(1f)) {
                    SmlStatCard("Active Agencies", "248 Groups", "99% compliant")
                }
            }
        }

        // Regional growth report representation
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Regional Performance Matrix (Monthly)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    listOf(
                        Triple("APAC Region", "4.2M Coins Distributed", "98% Target met"),
                        Triple("LATAM Region", "2.8M Coins Distributed", "91% Target met"),
                        Triple("EMEA Region", "3.9M Coins Distributed", "96% Target met"),
                        Triple("North America", "5.1M Coins Distributed", "102% Target met")
                    ).forEach { (region, volume, met) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(region, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(volume, color = Color.White.copy(0.7f), fontSize = 11.sp)
                            Text(met, color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Broadcast System
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Super-Leader Broadcast Center", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = announcementText,
                        onValueChange = { announcementText = it },
                        label = { Text("Compose Regional Alert Message") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (announcementText.isNotBlank()) {
                                annOutcome = "ALERT BROADCASTED: Sent emergency notification to all Super Managers and BD Leaders globally."
                                announcementText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Broadcast Announcement", color = Color.Black)
                    }

                    annOutcome?.let { outcome ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(outcome, color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SmlStatCard(title: String, value: String, sub: String) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(sub, color = Color(0xFF00E676), fontSize = 9.sp)
        }
    }
}

// ==========================================
// 4. SUPER MANAGER TAB
// ==========================================
@Composable
fun SuperManagerTab(viewModel: ConnectLiveViewModel) {
    var selectedRegion by remember { mutableStateOf("APAC Region") }
    var actionLogged by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    // Admin permissions states
    var manageBdAllowed by remember { mutableStateOf(true) }
    var manageMerchantAllowed by remember { mutableStateOf(true) }
    var manageRevenueAllowed by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Super Manager Operational Control Center", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Assigned Territory: Global East • Supervise BD leaders, merchants, host agencies, and territorial analytics.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // 1. TERRITORIAL COUNTERS
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("BD Leaders", "24 Authorized", "EMEA & APAC")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Merchant Leaders", "12 Active", "Syncing ledger")
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Total Merchants", "180 Network", "Active resellers")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Today's Revenue", "$14,820.00", "Growth: +18%")
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Active Agencies", "45 Registered", "Verified status")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Target Completion", "94.6% Rate", "KPI: Achieved")
                    }
                }
            }
        }

        // Region Selection
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("APAC Territory", "LATAM Territory", "EMEA Territory").forEach { reg ->
                    val active = selectedRegion == reg
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) Color(0xFFFFB300) else Color.White.copy(0.04f))
                            .clickable { selectedRegion = reg }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(reg, color = if (active) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. BD & TEAM RECRUITMENT MANAGEMENT
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("BD Leader & Official Administration Tree", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(10.dp))

                    val bds = listOf(
                        Triple("BD-Leader Sato", "EMEA Branch", "Active • 24 invites"),
                        Triple("BD-Leader Phoenix", "APAC Branch", "Active • 18 invites")
                    )

                    bds.forEach { (leader, branch, stats) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(0.02f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(leader, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("$branch • $stats", color = Color.White.copy(0.5f), fontSize = 9.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        if (manageBdAllowed) {
                                            actionLogged = "BD ACTION: Suspended recruitment pipeline for $leader."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                    modifier = Modifier.height(24.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("Suspend", fontSize = 8.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // 3. TARGET METERS & REVENUE MONITORING
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Territorial Targets & Revenue Meters", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("BD Recruitment Quota (86%)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                    LinearProgressIndicator(progress = { 0.86f }, color = Color(0xFF00E5FF), modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Merchant Distribution Target (65%)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                    LinearProgressIndicator(progress = { 0.65f }, color = Color(0xFFFFB300), modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Active Hosts Online Quota (92%)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                    LinearProgressIndicator(progress = { 0.92f }, color = Color(0xFF00E676), modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // 4. REPORTS & EXPORTS TRIGGER
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Generate Territorial Operational Reports", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Super Managers can instantly export verified performance audit logs to PDF/Excel/CSV format.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (manageRevenueAllowed) {
                                    android.widget.Toast.makeText(context, "SUCCESS: Exported PDF Regional Performance Report.", android.widget.Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("Export PDF", color = Color.White, fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                android.widget.Toast.makeText(context, "SUCCESS: Exported CSV Merchant Distribution Report.", android.widget.Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("Export CSV", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // 5. COMPLAINT & APPEAL CENTER RESOLVER
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Complaint, Dispute & Appeals Resolver Panel", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    val appeals = listOf(
                        Triple("APP-2290", "Merchant Wallet Dispute", "Escalated by Sato"),
                        Triple("APP-4589", "Agency Suspended Appeal", "Under review")
                    )

                    appeals.forEach { (id, title, status) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(0.02f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("$id: $title", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Status: $status", color = Color.White.copy(0.5f), fontSize = 9.sp)
                            }
                            Button(
                                onClick = {
                                    actionLogged = "RESOLVED CASE: $id marked solved. Notice sent to corresponding parties."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                modifier = Modifier.height(26.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Text("Resolve", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // 6. SUPER MANAGER CONTROL & SECURITY TOGGLES
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Super Manager Permissions Status", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Permit BD Recruitment Management", color = Color.White, fontSize = 11.sp)
                        Switch(checked = manageBdAllowed, onCheckedChange = { manageBdAllowed = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Permit Merchant Reseller Ledger Access", color = Color.White, fontSize = 11.sp)
                        Switch(checked = manageMerchantAllowed, onCheckedChange = { manageMerchantAllowed = it })
                    }
                }
            }
        }

        // Action logger feedback
        actionLogged?.let { log ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB300).copy(0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(log, color = Color(0xFFFFB300), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ==========================================
// 5. SUPPORT & APPROVALS COMPOSABLE
// ==========================================
@Composable
fun SupportApprovalsTab() {
    var outcomeMsg by remember { mutableStateOf<String?>(null) }
    
    // Approvals State
    val approvals = remember {
        mutableStateListOf(
            Triple("app_1", "Agency Application - Sarah Connor", "Requires BD confirmation"),
            Triple("app_2", "Host Application - Alex Mercer", "ID Verified"),
            Triple("app_3", "Merchant Status Request - User_8820", "Pending bank approval"),
            Triple("app_4", "VIP Level Verification - User_2291", "High profile user")
        )
    }

    // Tickets State
    val tickets = remember {
        mutableStateListOf(
            Triple("tick_1", "Coin Recharge Failure", "Priority: HIGH"),
            Triple("tick_2", "Host Stream Disconnection", "Priority: MED"),
            Triple("tick_3", "Agency Settlement Issue", "Priority: HIGH")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Platform Support, Applications & Approvals Center", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Process incoming application queues, manage user support tickets, and review tasks.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Dynamic Approvals Center
        item {
            Text("Approval Queue (${approvals.size} items)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
        }

        if (approvals.isEmpty()) {
            item {
                Text("All applications processed successfully! Queue is empty.", color = Color(0xFF00E676), fontSize = 11.sp)
            }
        } else {
            items(approvals) { item ->
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.second, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                Text(item.third, color = Color.White.copy(0.5f), fontSize = 10.sp)
                            }
                            IconButton(onClick = {
                                approvals.remove(item)
                                outcomeMsg = "APPROVED: ${item.second} verified and status granted."
                            }) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Approve", tint = Color(0xFF00E676))
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    approvals.remove(item)
                                    outcomeMsg = "REJECTED: ${item.second} application declined."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.2f)),
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("Reject", color = Color.Red, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    outcomeMsg = "REVIEW REQUEST: Asked candidate for additional verification documentation."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.06f)),
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("Request Info", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Tickets Center
        item {
            Text("Support Tickets (${tickets.size} unresolved)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
        }

        items(tickets) { tick ->
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(tick.second, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text(tick.third, color = Color.White.copy(0.5f), fontSize = 10.sp)
                    }
                    Button(
                        onClick = {
                            tickets.remove(tick)
                            outcomeMsg = "TICKET RESOLVED: '${tick.second}' solved. Resolution alert dispatched."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text("Resolve", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Action outcome toast
        outcomeMsg?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                }
            }
        }
    }
}

// ==========================================
// 6. INVITATIONS & REFERRALS COMPOSABLE
// ==========================================
// ==========================================
// 6. REFERRAL, INVITE & REWARD CENTER (HA)
// ==========================================
@Composable
fun ReferralRewardCenterTab(viewModel: ConnectLiveViewModel) {
    var responseMsg by remember { mutableStateOf<String?>(null) }
    var selectedLeaderboardTab by remember { mutableStateOf("Daily") } // Daily, Weekly, Monthly, Yearly, All-Time
    var referralInputCode by remember { mutableStateOf("") }
    
    // Referral Wheel state
    var isSpinning by remember { mutableStateOf(false) }
    var wonPrize by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Analytics state
    var enableProgram by remember { mutableStateOf(true) }
    var fraudBlockerActive by remember { mutableStateOf(true) }

    val context = androidx.compose.ui.platform.LocalContext.current

    val shareChannels = listOf(
        "WhatsApp" to Color(0xFF25D366),
        "Telegram" to Color(0xFF0088CC),
        "Messenger" to Color(0xFF0084FF),
        "Instagram" to Color(0xFFE1306C),
        "Facebook" to Color(0xFF1877F2),
        "X (Twitter)" to Color(0xFF1DA1F2),
        "TikTok" to Color(0xFF000000),
        "Snapchat" to Color(0xFFFFFC00),
        "SMS" to Color(0xFF4CAF50),
        "Email" to Color(0xFFEA4335),
        "Nearby Share" to Color(0xFF3F51B5)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Complete Referral, Invite & Reward Center", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Scale the Lumexa userbase. Track direct and nested networks, spin the referral bonus wheel, and check live analytics.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // 1. STATS GRID ROW (Total Invites, Success Reg, Pending, Coins, Diamonds, Level, Rank)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Total Invites", "128", "Clicks: 342", Color(0xFF00E5FF))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Successful Regs", "84", "65.6% Conv. Rate", Color(0xFF00E676))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Pending", "44", "Awaiting APK install", Color(0xFFFFB300))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Coins Earned", "🪙 42.5K", "Value: 425 USD", Color(0xFFFFD700))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Diamonds Earned", "💎 1,240", "10% Commission Active", Color(0xFFE040FB))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Referral Level / Rank", "LvL 5 • Rank #8", "Top 1% Referrer", Color(0xFF90CAF9))
                    }
                }
            }
        }

        // 2. MY REFERRAL CODE, LINK & PERSONAL QR CODE
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Permanent Referral Credentials", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("REFERRAL ID & CODE", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            Text("LMX784521", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("REFERRAL INVITE LINK", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            Text("https://invite.lumexa.app/LMX784521", color = Color(0xFF00E5FF), fontSize = 11.sp)
                        }

                        // Stylized QR code generator box
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(4.dp)
                                .clickable {
                                    android.widget.Toast.makeText(context, "Scanning personal QR code simulation triggered!", android.widget.Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Draw QR code matrix dots
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(8) { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        repeat(8) { col ->
                                            val isDark = (row + col) % 2 == 0 || (row < 2 && col < 2) || (row > 5 && col < 2) || (row < 2 && col > 5)
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(RoundedCornerShape(1.dp))
                                                    .background(if (isDark) Color.Black else Color.White)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Share Credentials Instantly via:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(shareChannels) { (name, color) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color.copy(0.15f))
                                    .border(1.dp, color.copy(0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        android.widget.Toast.makeText(context, "Shared referral invitation link to $name successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(name, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. DOWNLOAD THROUGH INVITE LINK FLOW GRAPHIC
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Automatic Download & Installation Flow Chart", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    val steps = listOf(
                        "Tap Invite Link" to "User navigates to Lumexa download portal.",
                        "Direct APK Download" to "System serves the optimized APK package (42 MB).",
                        "Automatic Code Detect" to "Unique installer ID tracks 'LMX784521' via CDN session.",
                        "Signup & Save" to "Upon signup, the referral link is permanently committed."
                    )

                    steps.forEachIndexed { idx, (title, desc) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF8E2DE2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${idx + 1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(desc, color = Color.White.copy(0.5f), fontSize = 9.sp)
                            }
                        }
                        if (idx < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .padding(vertical = 2.dp)
                                    .width(2.dp)
                                    .height(14.dp)
                                    .background(Color.White.copy(0.15f))
                            )
                        }
                    }
                }
            }
        }

        // 4. MANUAL CODE ENTRY SANDBOX (With Validations)
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Manually Redeem Referral Code", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Did a friend recruit you? Insert their code manually to bind the relationship and earn welcome rewards.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = referralInputCode,
                        onValueChange = { referralInputCode = it },
                        placeholder = { Text("e.g. LMX784521", color = Color.White.copy(0.3f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val code = referralInputCode.trim().uppercase()
                            if (code.isEmpty()) {
                                responseMsg = "ERROR: Code input cannot be blank."
                            } else if (code == "LMX784521") {
                                responseMsg = "ERROR: Fraud prevented! You cannot use your own referral code (Self-Referral)."
                            } else if (!code.startsWith("LMX") && !code.startsWith("LUM")) {
                                responseMsg = "ERROR: Invalid referral code prefix. Must start with LMX or LUM."
                            } else if (code.length < 8 || code.length > 10) {
                                responseMsg = "ERROR: Code has expired or holds invalid length."
                            } else {
                                responseMsg = "SUCCESS: Referral Code '$code' validated! You received 200 Welcome Coins and direct alignment saved."
                                viewModel.claimMilestoneReward("welcome_manual", 200, 50)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Validate & Bind Code", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 5. REFERRAL WHEEL (DAILY SPIN)
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Referral Spinner Wheel", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFB300), modifier = Modifier.align(Alignment.Start))
                    Text("Earn 1 Spin Token per active friend recruited. Win up to 1000 Coins, VIP, or Lucky Boxes!", color = Color.White.copy(0.5f), fontSize = 10.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Draw stylized visual wheel
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFFE040FB), Color(0xFF00E5FF),
                                        Color(0xFFFFB300), Color(0xFF00E676),
                                        Color(0xFFFF5252), Color(0xFF8E2DE2),
                                        Color(0xFFE040FB)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pointer
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    wonPrize?.let { prize ->
                        Text("🎉 CONGRATULATIONS! Won prize: $prize", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            if (!isSpinning) {
                                isSpinning = true
                                scope.launch {
                                    kotlinx.coroutines.delay(1200)
                                    isSpinning = false
                                    val prizes = listOf("🪙 500 Coins", "💎 50 Diamonds", "7-Day VIP Days Pass", "1x Premium Lucky Box", "Avatar Profile Frame", "1000 Experience Points")
                                    wonPrize = prizes.random()
                                    android.widget.Toast.makeText(context, "Won prize: $wonPrize!", android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        enabled = !isSpinning,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isSpinning) "Spinning..." else "SPIN REFERRAL WHEEL (1 Token)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 6. MULTI-LEVEL COMMISSION SCHEME
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Multi-Level Referral Commission Net", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Earn secondary and tertiary commissions passive on all partner achievements.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).background(Color.White.copy(0.04f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Level 1", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Direct Invites", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("10% Comm.", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("+84 users", color = Color(0xFF00E676), fontSize = 8.sp)
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(Color.White.copy(0.04f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Level 2", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Friends' Invites", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("5% Comm.", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("+210 users", color = Color(0xFF00E676), fontSize = 8.sp)
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(Color.White.copy(0.04f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Level 3", color = Color(0xFFE040FB), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Extended Net", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("2% Comm.", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("+540 users", color = Color(0xFF00E676), fontSize = 8.sp)
                            }
                        }
                    }
                }
            }
        }

        // 7. LEADERBOARD & RANKINGS
        item {
            Text("Referral Leaderboard Arena", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(0.04f))
                    .padding(4.dp)
            ) {
                listOf("Daily", "Weekly", "Monthly", "Yearly", "All-Time").forEach { lTab ->
                    val isSel = selectedLeaderboardTab == lTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) Color(0xFF8E2DE2) else Color.Transparent)
                            .clickable { selectedLeaderboardTab = lTab }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(lTab, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        val leadList = listOf(
            Triple("1", "S-Leader Sato", "512 Referrals • 🪙 50K Won"),
            Triple("2", "Agent Phoenix", "430 Referrals • 🪙 42K Won"),
            Triple("3", "Zayn Malik", "380 Referrals • 🪙 36K Won"),
            Triple("4", "Irina Shayk", "290 Referrals • 🪙 28K Won"),
            Triple("5", "David Beckham", "210 Referrals • 🪙 19K Won")
        )

        items(leadList) { (rank, name, desc) ->
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (rank == "1") Color(0xFFFFD700) else Color.White.copy(0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(rank, color = if (rank == "1") Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(desc, color = Color.White.copy(0.5f), fontSize = 9.sp)
                        }
                    }
                    Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                }
            }
        }

        // 8. REFERRAL HISTORY LOGS (Filterable, Searchable, CSV Export)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Referrals Registration History Queue", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Button(
                    onClick = {
                        android.widget.Toast.makeText(context, "SUCCESS: Referral history exported successfully to 'referral_report_July_2026.csv'.", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(26.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Export CSV", color = Color.White, fontSize = 9.sp)
                }
            }
        }

        val referredHistory = listOf(
            Triple("Selena Gomez", "Active • Reg: 2026-07-18", "Processed ✔"),
            Triple("Katy Perry", "Active • Reg: 2026-07-17", "Processed ✔"),
            Triple("Justin Bieber", "Active • Reg: 2026-07-15", "Processed ✔"),
            Triple("Shakira", "Onboarding • Reg: 2026-07-14", "Awaiting Target"),
            Triple("Dua Lipa", "Onboarding • Reg: 2026-07-12", "Awaiting Target")
        )

        items(referredHistory) { (name, reg, reward) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.02f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(reg, color = Color.White.copy(0.5f), fontSize = 9.sp)
                    }
                    Text(
                        text = reward,
                        color = if (reward.startsWith("Processed")) Color(0xFF00E676) else Color(0xFFFFB300),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 9. FRAUD PROTECTION AND OWNER CONTROLS
        item {
            Text("Referral Program Settings & Anti-Fraud Shield", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Referral Program", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Toggle program wide system availability", color = Color.White.copy(0.5f), fontSize = 8.sp)
                        }
                        Switch(
                            checked = enableProgram,
                            onCheckedChange = { enableProgram = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dynamic Device Fraud Protection Shield", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Blocks duplicate registration, emulator, loop & bot referrals", color = Color.White.copy(0.5f), fontSize = 8.sp)
                        }
                        Switch(
                            checked = fraudBlockerActive,
                            onCheckedChange = { fraudBlockerActive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                        )
                    }
                }
            }
        }

        // Output logger box
        responseMsg?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ==========================================
// 6B. COMPLETE OPERATIONAL MANAGER CONSOLE (HB)
// ==========================================
@Composable
fun ManagerConsoleTab(viewModel: ConnectLiveViewModel) {
    var managerActionLog by remember { mutableStateOf<String?>(null) }
    var userSearchId by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current

    // Permissions toggles for manager
    var allowUserEdit by remember { mutableStateOf(true) }
    var allowWalletMonitoring by remember { mutableStateOf(true) }
    var allowVoiceRoomMod by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Complete Operational Manager Console", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Assigned Region: APAC-South • Team Supervisor: Sato. Supervise platform, users, coin reselling channels, and voice logs.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // 1. MANAGER STATS MATRIX
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Online Users", "1,489 Active", "68.2% peak load")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Active Hosts", "120 Stream", "94% target met")
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Active Agencies", "14 Active", "APAC Territory")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Coin Resellers", "8 Authorized", "99.8% compliance")
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Pending Appeals", "12 Queue", "Emergency SLA: 2h")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SmlStatCard("Operational Revenue", "$4,850.00", "Updated: Realtime")
                    }
                }
            }
        }

        // 2. USER MANAGEMENT MODULE
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Interactive User Management Directory", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = userSearchId,
                            onValueChange = { userSearchId = it },
                            placeholder = { Text("Search User ID (e.g. 100235)", color = Color.White.copy(0.3f)) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (userSearchId.isNotBlank()) {
                                    managerActionLog = "SEARCH RESULT: Found User '$userSearchId' • Status: ACTIVE • Devices: Pixel 8, iPhone 15 • IP: 192.168.1.144 • Balance: 🪙 5,000 Coins"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("Query", color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Actions on Selected/Queried User Profile:", color = Color.White.copy(0.6f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (allowUserEdit) {
                                    managerActionLog = "USER SUSPENDED: User ID $userSearchId suspended for 24 hours (Reason: Spam)."
                                } else {
                                    managerActionLog = "SECURITY WARNING: You do not possess User Management permissions."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                            modifier = Modifier.weight(1f).height(32.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Suspend User", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                managerActionLog = "USER MUTED: User ID $userSearchId voice/text muted for 1 hour."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                            modifier = Modifier.weight(1f).height(32.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Mute User", color = Color.White, fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                managerActionLog = "USER RESTORED: User ID $userSearchId restored to active standing."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            modifier = Modifier.weight(1f).height(32.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Restore User", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. VOICE ROOM & MODERATION HUB
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Voice Room Global Supervision Matrix", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    val rooms = listOf(
                        Triple("VR-001", "Gaming Guild APAC", "Owner: Sarah • 142 seats occupied"),
                        Triple("VR-120", "Urdu Poetry Room", "Owner: Hassan • 92 seats occupied")
                    )

                    rooms.forEach { (roomId, name, details) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(0.02f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("$roomId: $name", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(details, color = Color.White.copy(0.5f), fontSize = 9.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        if (allowVoiceRoomMod) {
                                            managerActionLog = "ROOM MODERATED: Locked $roomId permanently due to policy audit."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                    modifier = Modifier.height(24.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("Lock Room", fontSize = 8.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // 4. ANNOUNCEMENT BROADCAST CENTER
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Announcement & Alert Dispatcher", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(8.dp))

                    var annMsg by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = annMsg,
                        onValueChange = { annMsg = it },
                        placeholder = { Text("Type emergency notice / global text...", color = Color.White.copy(0.3f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (annMsg.isNotBlank()) {
                                managerActionLog = "SUCCESS: Broadcasted alert: '$annMsg' to all active APAC nodes."
                                annMsg = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Emergency Global Notice", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 5. MANAGER PERMISSIONS (Platform Owner Managed)
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Assigned Role Permissions Configuration", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Allow User Management Controls", color = Color.White, fontSize = 11.sp)
                        Switch(checked = allowUserEdit, onCheckedChange = { allowUserEdit = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Allow Wallet Supervision & Limits", color = Color.White, fontSize = 11.sp)
                        Switch(checked = allowWalletMonitoring, onCheckedChange = { allowWalletMonitoring = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Allow Voice Room Closing & Moderation", color = Color.White, fontSize = 11.sp)
                        Switch(checked = allowVoiceRoomMod, onCheckedChange = { allowVoiceRoomMod = it })
                    }
                }
            }
        }

        // Action logger feedback
        managerActionLog?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E5FF).copy(0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ==========================================
// UTILITY HELPERS FOR MODULE CARDS
// ==========================================
@Composable
fun MiniStatCard(title: String, value: String, subtext: String, tintColor: Color) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 9.sp, color = Color.White.copy(0.6f))
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = tintColor)
            Spacer(modifier = Modifier.height(1.dp))
            Text(subtext, fontSize = 8.sp, color = Color.White.copy(0.4f))
        }
    }
}



// ==========================================
// 7. APK DOWNLOADS & UPDATES COMPOSABLE
// ==========================================
@Composable
fun ApkDownloadUpdatesTab(viewModel: ConnectLiveViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Tracked APK releases (mutableStateListOf so admin can prepend new releases!)
    val releases = remember {
        mutableStateListOf(
            ApkRelease(
                versionName = "4.8.2-Stable",
                versionCode = 482,
                releaseDate = "2026-07-15",
                fileSize = "42.8 MB",
                isStable = true,
                isForceUpdate = false,
                changelog = "• Added high-frame-rate voice room seat transition effects.\n• Upgraded live streaming stream alignment mechanism.\n• Fixed real-time ledger sync latency in APAC territory.\n• Patched memory leakage in interactive gift canvas.",
                md5 = "e10adc3949ba59abbe56e057f20f883e",
                signature = "SHA256:LumexaCorpOfficialAndroidReleaseKey"
            ),
            ApkRelease(
                versionName = "4.8.3-Beta",
                versionCode = 483,
                releaseDate = "2026-07-18",
                fileSize = "43.1 MB",
                isStable = false,
                isForceUpdate = false,
                changelog = "• Experimental multi-host split-screen layout for voice rooms.\n• Embedded dynamic device fingerprint fraud blocker.\n• Improved support dispute resolution ticket queue integration.",
                md5 = "ab56e057f20f883ee10adc3949ba59ab",
                signature = "SHA256:LumexaCorpOfficialAndroidBetaKey"
            ),
            ApkRelease(
                versionName = "4.8.0-Stable (LTS)",
                versionCode = 480,
                releaseDate = "2026-06-28",
                fileSize = "41.5 MB",
                isStable = true,
                isForceUpdate = true,
                changelog = "• Mandatory core security upgrade for global financial resale ledgers.\n• Re-designed client-side database persistence using Room framework.\n• Optimized memory overhead by 40% on low-end Android devices.",
                md5 = "9ba59abbe56e057f20f883ee10adc394",
                signature = "SHA256:LumexaCorpOfficialAndroidReleaseKey"
            )
        )
    }

    // Selected release to download
    var selectedRelease by remember { mutableStateOf(releases[0]) }
    
    // Download Simulator State
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloading by remember { mutableStateOf(false) }
    var downloadComplete by remember { mutableStateOf(false) }
    var simulatedSpeed by remember { mutableStateOf("0.0 MB/s") }
    var statusText by remember { mutableStateOf("Idle - Ready to begin official CDN handshake") }

    // Referral token state
    var referralTokenInput by remember { mutableStateOf("LMX784521") }
    var referralTokenDetected by remember { mutableStateOf("") }
    
    // Installation Simulation States
    var installing by remember { mutableStateOf(false) }
    var installStepIndex by remember { mutableIntStateOf(-1) }
    val installSteps = listOf(
        "Verifying APK Package Integrity (MD5 Handshake)...",
        "Extracting referral token payloads from CDN Referrer...",
        "Validating referral partner status in Super Manager ledger...",
        "Writing app system files & creating secure sandbox...",
        "Finalizing token binding & initializing Lumexa first-run..."
    )
    var installComplete by remember { mutableStateOf(false) }
    var bindingLogMessage by remember { mutableStateOf("") }

    // Admin Upload Section State
    var adminVersionName by remember { mutableStateOf("4.8.4-Beta") }
    var adminReleaseNotes by remember { mutableStateOf("• Integrated 3D avatar frames.\n• Upgraded system liveness feedback.") }
    var adminIsStable by remember { mutableStateOf(false) }
    var adminIsForceUpdate by remember { mutableStateOf(false) }
    var adminFileSize by remember { mutableStateOf("43.5 MB") }

    // Expandable details state for release history
    var expandedReleaseIndex by remember { mutableIntStateOf(0) }

    // Handle downloading coroutine
    LaunchedEffect(downloading) {
        if (downloading) {
            downloadProgress = 0f
            downloadComplete = false
            statusText = "Performing secure handshake with nearest edge CDN node..."
            kotlinx.coroutines.delay(1000)
            
            // Extract token during CDN handshake simulation
            referralTokenDetected = referralTokenInput.trim().uppercase()
            statusText = "Downloading official Lumexa encrypted package (Token: $referralTokenDetected detected)..."
            
            var progress = 0f
            while (progress < 1.0f) {
                kotlinx.coroutines.delay(180)
                progress += (0.05f + (0.05f * (0..5).random()))
                if (progress > 1f) progress = 1f
                downloadProgress = progress
                
                // Random speed variance to look extremely authentic
                val speed = (8..15).random() + ((0..9).random() / 10.0)
                simulatedSpeed = "$speed MB/s"
            }
            downloading = false
            downloadComplete = true
            statusText = "SUCCESS: Encrypted payload downloaded! Saved temporarily to system cache."
        }
    }

    // Handle installing coroutine
    LaunchedEffect(installing) {
        if (installing) {
            installComplete = false
            installStepIndex = 0
            while (installStepIndex < installSteps.size - 1) {
                kotlinx.coroutines.delay(1200)
                installStepIndex++
            }
            kotlinx.coroutines.delay(1000)
            installing = false
            installComplete = true
            
            // Earn referral verification reward
            val milestoneId = "apk_referral_${referralTokenDetected}_${selectedRelease.versionName}"
            val claimed = viewModel.claimMilestoneReward(milestoneId, 500, 100)
            bindingLogMessage = if (claimed) {
                "🎉 Verified First-Run! Linked to Partner: $referralTokenDetected. Welcome bonus of 500 Coins & 100 XP added to your secure wallet."
            } else {
                "✔ Re-installation verified. Referral alignment is securely active with Partner $referralTokenDetected."
            }

            // Save referral token to SharedPreferences and update global ViewModel state
            val prefs = context.getSharedPreferences("lumexa_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString("captured_referral_token", referralTokenDetected).apply()
            viewModel.setPendingReferralCode(referralTokenDetected)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Core Header
        item {
            Text("Official APK Download & Direct Distribution Center", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Download authentic Lumexa releases directly. Custom CDN bypasses app store friction while tracking recruit alignments.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // 0. QUICK ACCESS TO USER-FACING REFERRAL LANDING PAGE
        item {
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo("referral_download") }
                    .border(1.dp, Color(0xFFFFB300).copy(0.4f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Referral Landing Page & Direct Installer",
                            color = Color(0xFFFFB300),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Simulate how newly invited users experience downloading your direct APK while retaining their custom inviter cookie token.",
                            color = Color.White.copy(0.6f),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { viewModel.navigateTo("referral_download") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Open Page", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Launch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }

        // 1. SELECTABLE VERSION LIST (Interactive Tracked Releases)
        item {
            Text("Available Releases on Lumexa CDN Network", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(releases.size) { index ->
            val release = releases[index]
            val isSelected = selectedRelease == release
            val isExpanded = expandedReleaseIndex == index
            
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = if (isSelected) Color(0xFF00E5FF) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Stable or Beta chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (release.isStable) Color(0xFF00E676).copy(0.15f)
                                        else Color(0xFFFFB300).copy(0.15f)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (release.isStable) "STABLE" else "BETA",
                                    color = if (release.isStable) Color(0xFF00E676) else Color(0xFFFFB300),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = release.versionName,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (release.isForceUpdate) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFF5252).copy(0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("MANDATORY", color = Color(0xFFFF5252), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            
                            IconButton(
                                onClick = { expandedReleaseIndex = if (isExpanded) -1 else index },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Toggle Details",
                                    tint = Color.White.copy(0.7f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Released: ${release.releaseDate}", fontSize = 10.sp, color = Color.White.copy(0.5f))
                        Text("Size: ${release.fileSize}", fontSize = 10.sp, color = Color.White.copy(0.5f))
                        Text("Code: ${release.versionCode}", fontSize = 10.sp, color = Color.White.copy(0.5f))
                    }

                    // Expanded detail with complete changelogs and signature info
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(0.1f)))
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("CHANGELOG / REVISIONS:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(release.changelog, fontSize = 11.sp, color = Color.White.copy(0.8f))
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("INTEGRITY CHECKSUMS:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(0.5f))
                        Text("MD5: ${release.md5}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color.White.copy(0.4f))
                        Text("Key: ${release.signature}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color.White.copy(0.4f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            selectedRelease = release
                            if (!downloading) {
                                downloading = true
                                downloadComplete = false
                                installComplete = false
                                installStepIndex = -1
                            }
                        },
                        enabled = !downloading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(0.08f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isSelected && downloading) "Downloading ${release.versionName}..." 
                                   else if (isSelected && downloadComplete) "Download Completed ✔"
                                   else "Select & Download this Release", 
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 2. SIMULATED BROWSER & LOCAL STORAGE PERSISTENCE ENGINE
        item {
            var simulatedBrowserUrl by remember { mutableStateOf("https://download.lumexa.app/invite/LMX784521") }
            
            // Auto-parse token from URL on changes
            LaunchedEffect(simulatedBrowserUrl) {
                val parsedUri = android.net.Uri.parse(simulatedBrowserUrl)
                var token = ""
                try {
                    if (parsedUri.pathSegments.contains("invite")) {
                        val idx = parsedUri.pathSegments.indexOf("invite")
                        if (idx >= 0 && idx < parsedUri.pathSegments.size - 1) {
                            token = parsedUri.pathSegments[idx + 1]
                        }
                    } else if (parsedUri.path == "/get") {
                        token = parsedUri.getQueryParameter("token") ?: ""
                    }
                } catch (e: Exception) {
                    // Ignore parse errors on half-typed URLs
                }
                if (token.isNotBlank()) {
                    referralTokenInput = token.trim().uppercase()
                }
            }

            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simulated Desktop/Mobile Browser", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFB300))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5252)))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFB300)))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00E676)))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Interactive sandbox showing how web URL params are captured, saved to the browser's localStorage, and preserved for the app.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Address Bar OutlinedTextField
                    OutlinedTextField(
                        value = simulatedBrowserUrl,
                        onValueChange = { simulatedBrowserUrl = it },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(0.04f),
                            unfocusedContainerColor = Color.White.copy(0.04f),
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color.White.copy(0.12f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Secure Connection", tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                        },
                        trailingIcon = {
                            if (simulatedBrowserUrl.isNotBlank()) {
                                IconButton(onClick = { simulatedBrowserUrl = "" }, modifier = Modifier.size(16.dp)) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear URL", tint = Color.White.copy(0.5f), modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Preloaded URL shortcuts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Path (LMX784)" to "https://download.lumexa.app/invite/LMX784521",
                            "Query (LMX990)" to "https://download.lumexa.app/get?token=LMX990011",
                            "VIP Promotion" to "https://download.lumexa.app/invite/VIP_RECRUIT_BONUS"
                        ).forEach { (label, url) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(0.08f))
                                    .clickable { simulatedBrowserUrl = url }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(label, color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(0.1f)))
                    Spacer(modifier = Modifier.height(12.dp))

                    // BROWSER CONTENT ENGINE VIEWER
                    Text("RENDERED BROWSER PAGE VIEW:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(0.5f))
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(0.2f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF8E2DE2).copy(0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF8E2DE2), modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Lumexa Recruiter Invite Gateway", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Verifying recruit alignment & caching partner session...", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Local Storage Inspection Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(0.04f), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("💾 Browser LocalStorage Cache", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF00E676).copy(0.15f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text("ACTIVE_STATE", color = Color(0xFF00E676), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    // Row 1: referral_token
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("localStorage.getItem('referral_token')", color = Color.White.copy(0.6f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text(
                                            text = if (referralTokenInput.isNotBlank()) "\"$referralTokenInput\"" else "null (no token detected)", 
                                            color = if (referralTokenInput.isNotBlank()) Color(0xFF00E5FF) else Color(0xFFFF5252), 
                                            fontSize = 9.sp, 
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    // Row 2: invite_timestamp
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("localStorage.getItem('invite_timestamp')", color = Color.White.copy(0.6f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text("\"2026-07-19T08:25:49\"", color = Color.White.copy(0.8f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    // Row 3: storage_source
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("localStorage.getItem('utm_medium')", color = Color.White.copy(0.6f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text("\"direct_sideload_cdn_parameter\"", color = Color.White.copy(0.8f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }

                            if (referralTokenInput.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✔ Referral detected from parameters! Ready to bundle. Session matches partner $referralTokenInput.",
                                    color = Color(0xFF00E676),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (!downloading) {
                                            downloading = true
                                            downloadComplete = false
                                            installComplete = false
                                            installStepIndex = -1
                                        }
                                    },
                                    enabled = !downloading,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (downloading) "Downloading Sideload APK..." else "Download Sideload APK via Browser CDN",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "⚠ No referral parameter detected. Type a token or click a shortcut above to simulate tracking.",
                                    color = Color(0xFFFFB300),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. INTERACTIVE PROGRESS METERS (CDN Download Simulation)
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Lumexa Dedicated CDN Download Meter", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Active Handshake Status:", color = Color.White.copy(0.5f), fontSize = 9.sp)
                    Text(statusText, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    
                    if (downloading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Speed: $simulatedSpeed", fontSize = 9.sp, color = Color.White.copy(0.5f))
                            Text("${(downloadProgress * 100).toInt()}% Complete", fontSize = 9.sp, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                        }
                    }

                    if (downloadComplete) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF00E676).copy(0.08f), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("DOWNLOAD COMPLETED SUCCESSFULLY", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Saved: /storage/emulated/0/Download/Lumexa-${selectedRelease.versionName}.apk", color = Color.White.copy(0.6f), fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }

        // 4. INSTALLATION GUIDE & STEP-BY-STEP FLOW
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Android Sideloading & Installation Protocol", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    val guides = listOf(
                        "Step 1: Obtain Secured Package" to "Ensure the APK file is fully downloaded from the verified CDN node above.",
                        "Step 2: Permit Unknown Sources" to "If prompted by Android, go to Settings -> Security and enable 'Install from Unknown Sources' for your browser or file manager.",
                        "Step 3: Play Protect Safe-Bypass" to "Because this app is self-distributed to avoid play store commissions, tap 'Install Anyway' if Google Play Protect issues an alert.",
                        "Step 4: Launch & Verify Referral" to "Open the installed Lumexa application. The CDN session tracker automatically injects the referral token on first startup."
                    )

                    guides.forEachIndexed { idx, (heading, body) ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF8E2DE2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${idx + 1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(heading, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(body, color = Color.White.copy(0.5f), fontSize = 9.sp)
                            }
                        }
                        if (idx < guides.size - 1) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // 5. REFERRAL TOKEN EXTRACTION & INSTALLATION BINDING LOGS (The core functional simulator!)
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Device-Level Referral Integration Hub", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Simulate system-level verification, token parsing, and wallet rewards during installation.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (downloadComplete) {
                        Button(
                            onClick = {
                                if (!installing) {
                                    installing = true
                                    bindingLogMessage = ""
                                }
                            },
                            enabled = !installing,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (installing) "Simulating Installation..." else "Simulate Installation & Extract Referral Token",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (installing || installStepIndex >= 0) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text("Installer Real-Time Operations Logs:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White.copy(0.7f))
                            Spacer(modifier = Modifier.height(6.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(0.3f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                installSteps.forEachIndexed { sIdx, step ->
                                    val isLogged = sIdx <= installStepIndex
                                    val isCurrent = sIdx == installStepIndex
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isLogged && !isCurrent) Color(0xFF00E676)
                                                    else if (isCurrent) Color(0xFF00E5FF)
                                                    else Color.White.copy(0.2f)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = step,
                                            color = if (isLogged) Color.White else Color.White.copy(0.4f),
                                            fontSize = 9.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }

                        if (installComplete) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("INSTALLATION COMPLETED & VERIFIED", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = bindingLogMessage,
                                        color = Color.White,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    } else {
                        // User needs to download first
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(0.03f), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Please select a release and download it above to enable installation simulation.",
                                    color = Color.White.copy(0.6f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. ADMINISTRATOR RELEASE CREATOR / REGISTRY (Lets user add custom releases to the center)
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Administrator Release Management Console", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Inject a custom mock APK release to the CDN database. It will immediately appear in the selectable history above.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminVersionName,
                        onValueChange = { adminVersionName = it },
                        label = { Text("Version Name (e.g. 4.8.4-Beta)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adminFileSize,
                        onValueChange = { adminFileSize = it },
                        label = { Text("APK File Size (e.g. 43.5 MB)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adminReleaseNotes,
                        onValueChange = { adminReleaseNotes = it },
                        label = { Text("Release Notes / Revisions Changelog") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Stable Production Release", fontSize = 11.sp, color = Color.White)
                        Checkbox(
                            checked = adminIsStable,
                            onCheckedChange = { adminIsStable = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFB300))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mandatory Force Update", fontSize = 11.sp, color = Color.White)
                        Checkbox(
                            checked = adminIsForceUpdate,
                            onCheckedChange = { adminIsForceUpdate = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFB300))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val vName = adminVersionName.trim()
                            if (vName.isNotBlank()) {
                                val randomMd5 = (1..32).map { "0123456789abcdef".random() }.joinToString("")
                                val newRelease = ApkRelease(
                                    versionName = vName,
                                    versionCode = (releaseCodeTracker++),
                                    releaseDate = "2026-07-19",
                                    fileSize = adminFileSize,
                                    isStable = adminIsStable,
                                    isForceUpdate = adminIsForceUpdate,
                                    changelog = adminReleaseNotes,
                                    md5 = randomMd5,
                                    signature = "SHA256:LumexaCorpOfficialAndroidCustomReleaseKey"
                                )
                                releases.add(0, newRelease) // Prepend to history
                                selectedRelease = newRelease
                                expandedReleaseIndex = 0
                                android.widget.Toast.makeText(context, "SUCCESS: Version $vName published to CDN!", android.widget.Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publish Release to CDN Database", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Global variable to keep tracking of custom version codes
private var releaseCodeTracker = 484

data class ApkRelease(
    val versionName: String,
    val versionCode: Int,
    val releaseDate: String,
    val fileSize: String,
    val isStable: Boolean,
    val isForceUpdate: Boolean,
    val changelog: String,
    val md5: String,
    val signature: String
)

// ==========================================
// 8. CREATOR CENTER & ROOM OWNER MODULE
// ==========================================
@Composable
fun CreatorCenterVoiceRoomTab() {
    var seatLayoutSize by remember { mutableStateOf("8 Seats") } // 4, 6, 8, 10
    var feedbackLog by remember { mutableStateOf<String?>(null) }
    
    // Dynamic seats state
    val seats = remember {
        mutableStateListOf(
            "Host (Locked)" to "Muted",
            "Speaker #1" to "Active",
            "Speaker #2" to "Active",
            "Seat 3 (Locked)" to "Muted",
            "Speaker #4" to "Active",
            "Seat 5 (Empty)" to "Muted",
            "Seat 6 (Empty)" to "Muted",
            "Seat 7 (Locked)" to "Muted"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Creator Center & Room Owner Panel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Configure interactive room layout, moderate active participants, and toggle seat locks.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Room seat setup
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Voice Room Grid Configurations", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("4 Seats", "6 Seats", "8 Seats", "10 Seats").forEach { layout ->
                            val active = seatLayoutSize == layout
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) Color(0xFF8E2DE2) else Color.White.copy(0.04f))
                                    .clickable { seatLayoutSize = layout }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(layout, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { feedbackLog = "SUCCESS: Updated active voice room layout to $seatLayoutSize. Restructuring grid positions." },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Layout Configuration", color = Color.White)
                    }
                }
            }
        }

        // Dynamic visual seats
        item {
            Text("Room Seat Status Matrix ($seatLayoutSize)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(seats) { (seatName, status) ->
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (status == "Active") Color(0xFF00E676).copy(0.2f) else Color.White.copy(0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (status == "Active") Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = null,
                                tint = if (status == "Active") Color(0xFF00E676) else Color.White.copy(0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(seatName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            Text("Microphone status: $status", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = {
                            feedbackLog = "MODERATION: Toggled LOCK status on $seatName."
                        }) {
                            Icon(Icons.Default.LockOpen, contentDescription = "Toggle Lock", tint = Color.White.copy(0.5f))
                        }
                        IconButton(onClick = {
                            feedbackLog = "MODERATION: Toggled MUTE status on $seatName."
                        }) {
                            Icon(Icons.Default.VolumeMute, contentDescription = "Toggle Mute", tint = Color.White.copy(0.5f))
                        }
                        IconButton(onClick = {
                            feedbackLog = "MODERATION: Kicked user from $seatName."
                        }) {
                            Icon(Icons.Default.CancelPresentation, contentDescription = "Kick User", tint = Color(0xFFFF5252))
                        }
                    }
                }
            }
        }

        feedbackLog?.let { log ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(log, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                }
            }
        }
    }
}

// ============================================================================
// 9. MERCHANT RESELLER LEADER PANEL (Section HM / HN)
// ============================================================================
@Composable
fun MerchantResellerLeaderTab() {
    var inviteEmail by remember { mutableStateOf("") }
    var distributionAmount by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("mrl_scroll_container"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Merchant Reseller Leader Dashboard", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Supervise coin inventory, distribute wholesale coins, invite merchants, and track sales targets.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Stats Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Coin Inventory", "2,500,000 LMX", "Wholesale stock", Color(0xFFFFB300))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Total Merchants", "34 Active", "+3 this week", Color(0xFF00E676))
                }
            }
        }

        // Coin Wholesale Distribution
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Distribute Wholesale Coin Supply", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFE082))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = distributionAmount,
                        onValueChange = { distributionAmount = it },
                        label = { Text("Amount of Coins (LMX)", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFB300),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (distributionAmount.isNotEmpty()) {
                                feedbackMessage = "SUCCESS: Distributed $distributionAmount LMX to Merchant Pool. Inventory updated."
                                distributionAmount = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Initiate Distribution Pool", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Invite Merchant
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Recruit New Merchant Reseller", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inviteEmail,
                        onValueChange = { inviteEmail = it },
                        label = { Text("Merchant Email or Username", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8E2DE2),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (inviteEmail.isNotEmpty()) {
                                feedbackMessage = "SUCCESS: Dispatched Merchant Invitation token to $inviteEmail."
                                inviteEmail = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Send Reseller Invitation", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ============================================================================
// 10. MERCHANT PANEL (Section HM / HN)
// ============================================================================
@Composable
fun MerchantTab() {
    var transferAmount by remember { mutableStateOf("") }
    var recipientId by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("merchant_scroll_container"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Merchant Coin Distribution & Sales", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Manage your reseller inventory, process consumer transfers, and check sales commissions.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Coin Seller Commission Status", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White.copy(0.5f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Accumulated Commission: $3,240.00 USD", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF00E676))
                    Text("Commission rate: 12.5% per volume transfer", fontSize = 10.sp, color = Color.White.copy(0.4f))
                }
            }
        }

        // Balance & Actions
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("My Balance", "420,000 LMX", "Ready stock", Color(0xFF00E676))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Sales Volume", "1,850,000 LMX", "This month", Color(0xFF8E2DE2))
                }
            }
        }

        // Transfer Coins UI
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Transfer Coins to Consumer / Reseller", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = recipientId,
                        onValueChange = { recipientId = it },
                        label = { Text("Recipient User ID", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFB300),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = transferAmount,
                        onValueChange = { transferAmount = it },
                        label = { Text("LMX Amount", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFB300),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (transferAmount.isNotEmpty() && recipientId.isNotEmpty()) {
                                feedbackMessage = "SUCCESS: Transferred $transferAmount LMX directly to user '$recipientId'. Receipt generated."
                                transferAmount = ""
                                recipientId = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Confirm Ledger Transfer", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ============================================================================
// 11. BD LEADER PANEL (Section HM / HN)
// ============================================================================
@Composable
fun BdLeaderTab() {
    var officialName by remember { mutableStateOf("") }
    var officialEmail by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("bd_leader_scroll"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Business Development Leader Center", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Coordinate BD official agents, supervise recruitment targets, and evaluate expansion campaigns.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Stats Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Active Officials", "12 BD Agents", "Assigned tree", Color(0xFF8E2DE2))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Recruit Target", "84% Reached", "Quarter goal", Color(0xFF00E676))
                }
            }
        }

        // Add Official Form
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Register New BD Official Agent", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = officialName,
                        onValueChange = { officialName = it },
                        label = { Text("Agent Full Name", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8E2DE2),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = officialEmail,
                        onValueChange = { officialEmail = it },
                        label = { Text("Agent Professional Email", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8E2DE2),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (officialName.isNotEmpty() && officialEmail.isNotEmpty()) {
                                feedbackMessage = "SUCCESS: Enrolled $officialName as BD Official under your regional leadership. Credentials dispatched."
                                officialName = ""
                                officialEmail = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Complete Agent Enrollment", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Recruitment Tree Section
        item {
            Text("Regional BD Hierarchy tree", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        listOf("Agent Chloe Zhang (S-Grade, 14 agencies)", "Agent Dave Miller (A-Grade, 8 agencies)", "Agent Sarah Connor (A-Grade, 9 agencies)").forEach { node ->
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(node, color = Color.White, fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF00E676).copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("ACTIVE", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ============================================================================
// 12. BD OFFICIAL PANEL (Section HM / HN)
// ============================================================================
@Composable
fun BdOfficialTab() {
    var inviteCodeLink by remember { mutableStateOf("https://download.lumexa.app/invite/LMX_BDO_449") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("bd_official_scroll"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("BD Official Command Hub", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Invite host agencies and content creators, inspect recruitment milestones, and monitor commissions.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Stats Card
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Agencies Invited", "14 Registered", "Goal: 20", Color(0xFFFFB300))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Earned Commission", "$1,450.00 USD", "Commission scale", Color(0xFF00E676))
                }
            }
        }

        // Invite Link Generator
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Your Agency Recruitment Token & Link", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inviteCodeLink,
                        onValueChange = { inviteCodeLink = it },
                        readOnly = true,
                        label = { Text("Recruitment Hyperlink", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8E2DE2),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            feedbackMessage = "SUCCESS: Referral Link captured and copied to clip. Share link to recruit new agencies automatically."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Copy Recruitment Link", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Target progress indicators
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("My Recruitment Milestone Targets", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Agency Recruitment (14/20)", color = Color.White.copy(0.7f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { 0.7f },
                        color = Color(0xFF00E676),
                        trackColor = Color.White.copy(0.08f),
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Total Creator Hosts Recruited (48/50)", color = Color.White.copy(0.7f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { 0.96f },
                        color = Color(0xFF8E2DE2),
                        trackColor = Color.White.copy(0.08f),
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ============================================================================
// 13. AGENCY OWNER PANEL (Section HM / HN)
// ============================================================================
@Composable
fun AgencyOwnerTab() {
    var hostInviteId by remember { mutableStateOf("") }
    var targetEarningsThreshold by remember { mutableStateOf("10,000 LMX") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("agency_scroll_container"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Host Agency Manager Suite", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Oversee professional hosts, track daily attendance and live timings, review earnings, and invite talent.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Stats
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Total Hosts", "18 Live Hosts", "Active agency roster", Color(0xFFFFB300))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Monthly Revenue", "$4,850.00 USD", "+18% from last month", Color(0xFF00E676))
                }
            }
        }

        // Invite Host
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Recruit New Host to Agency", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = hostInviteId,
                        onValueChange = { hostInviteId = it },
                        label = { Text("Host Creator ID / Username", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8E2DE2),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (hostInviteId.isNotEmpty()) {
                                feedbackMessage = "SUCCESS: Issued official agency recruitment offer to creator '$hostInviteId'."
                                hostInviteId = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Send Agency Offer", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Host Roster & Performance Check
        item {
            Text("Agency Host Performance Ledger", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        listOf(
            Triple("Host Ruby (ID: 9021)", "Active 4.2h today • 14,200 coins", "Earnings Status: S-Grade"),
            Triple("Host Jasper (ID: 3204)", "Active 3.0h today • 8,400 coins", "Earnings Status: A-Grade"),
            Triple("Host Pearl (ID: 1109)", "Active 0.5h today • 1,200 coins", "Earnings Status: C-Grade")
        ).forEach { (host, details, status) ->
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(host, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF8E2DE2).copy(0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(status, color = Color(0xFFD0BCFF), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(details, color = Color.White.copy(0.5f), fontSize = 10.sp)
                    }
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ============================================================================
// 14. FAMILY LEADER PANEL (Section HM / HN)
// ============================================================================
@Composable
fun FamilyLeaderTab() {
    var familyAnnouncement by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("family_scroll_container"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Family Union Leadership Center", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Administer your virtual family unit, launch family events, approve members, and track rankings.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Stats Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Family Members", "148 Members", "Cap: 200", Color(0xFF8E2DE2))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Global Ranking", "#12 Worldwide", "Top 5% guild", Color(0xFFFFB300))
                }
            }
        }

        // Post family notice
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Broadcast Family Notice / Event Announcement", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = familyAnnouncement,
                        onValueChange = { familyAnnouncement = it },
                        label = { Text("Family Notice Details...", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFB300),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (familyAnnouncement.isNotEmpty()) {
                                feedbackMessage = "SUCCESS: Dispatched announcement to all 148 Family Members."
                                familyAnnouncement = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Broadcast Notice", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ============================================================================
// 15. COIN SELLER PANEL (Section HM / HN)
// ============================================================================
@Composable
fun CoinSellerTab() {
    var sellAmount by remember { mutableStateOf("") }
    var customerId by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("coin_seller_scroll"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Coin Seller Ledger", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Liquidate wholesale coin inventory directly to consumer accounts with instantaneous delivery.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Stats Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("My Seller stock", "180,000 LMX", "Ready for sale", Color(0xFFFFB300))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Today's Volume", "24,000 LMX", "4 orders completed", Color(0xFF00E676))
                }
            }
        }

        // Sales terminal
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Instant Retail Coin Sales Terminal", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF00E676))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customerId,
                        onValueChange = { customerId = it },
                        label = { Text("Customer Account ID", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sellAmount,
                        onValueChange = { sellAmount = it },
                        label = { Text("LMX Amount to Deliver", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (sellAmount.isNotEmpty() && customerId.isNotEmpty()) {
                                feedbackMessage = "SUCCESS: Delivered $sellAmount LMX to User '$customerId'. Balance deducted."
                                sellAmount = ""
                                customerId = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Deliver Retail Coins", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ============================================================================
// 16. COIN RESELLER PANEL (Section HM / HN)
// ============================================================================
@Composable
fun CoinResellerTab() {
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("coin_reseller_scroll"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Coin Reseller Dashboard", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Process incoming wholesale coin purchase orders and review historical merchant invoices.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Stats Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Resell Inventory", "1,200,000 LMX", "Main reserve", Color(0xFF8E2DE2))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Pending Orders", "2 Claims", "Awaiting verification", Color(0xFFFFB300))
                }
            }
        }

        // Process Queue
        item {
            Text("Reseller Sales Order Queue", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        listOf(
            "ORDER #2094 - 150,000 LMX requested by Merchant_90" to "Pending Receipt Verification",
            "ORDER #2093 - 200,000 LMX requested by Merchant_33" to "Awaiting Reserve Transfer"
        ).forEach { (order, details) ->
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(order, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFB300).copy(0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("AWAITING", color = Color(0xFFFFB300), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(details, color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    feedbackMessage = "SUCCESS: Cleared order and transferred supply."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Approve & Ship", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    feedbackMessage = "DECLINED: Cancelled order reserve."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Decline Order", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ============================================================================
// 17. SUPER ADMIN PANEL (Section HM / HN)
// ============================================================================
@Composable
fun SuperAdminTab() {
    var noticeDispatchText by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("super_admin_scroll"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Super Administration Console", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Supervise live platform activities, moderate content flag queues, check system telemetry, and publish system bulletins.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Stats Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Live Users", "84,209 online", "99.98% SLA", Color(0xFF00E676))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Reports Queue", "4 Pending", "Urgent escalation", Color(0xFFFF5252))
                }
            }
        }

        // Moderation Queue
        item {
            Text("Critical Moderation & Content Review", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        listOf(
            Triple("Flagged Voice Room: ID 2933", "User_8819 reported for terms breach", "Harmful Content (High Match)"),
            Triple("Flagged Comment: Post 11903", "Suspicious link posted in description", "Security Threat Alert")
        ).forEach { (report, description, tag) ->
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(report, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFF5252).copy(0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(tag, color = Color(0xFFFF5252), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(description, color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { feedbackMessage = "MODERATION: Suspended item and notified security." },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Ban / Delete", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { feedbackMessage = "MODERATION: Dismissed report." },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Dismiss Alert", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Global Announcement text field
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Publish System Announcement", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noticeDispatchText,
                        onValueChange = { noticeDispatchText = it },
                        label = { Text("Announcement text...", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8E2DE2),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (noticeDispatchText.isNotEmpty()) {
                                feedbackMessage = "SUCCESS: Broadcasted global system notification to all platform clients."
                                noticeDispatchText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Dispatch Platform Bulletins", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ============================================================================
// 18. ADMIN PANEL TAB (Section HM / HN)
// ============================================================================
@Composable
fun AdminTab() {
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("admin_tab_scroll"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Administrative Operations Suite", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Process consumer complaints, verify profile updates, supervise dynamic voice rooms, and inspect telemetry logs.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Open Disputes", "12 Pending", "Needs attention", Color(0xFFFFB300))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Verification Claims", "8 profiles", "Awaiting review", Color(0xFF8E2DE2))
                }
            }
        }

        // Active verifications
        item {
            Text("Profile Verification Approvals Queue", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        listOf(
            "User @MaxMercer - Claim: Celebrated Creator Badge" to "Max Mercer verified identity",
            "User @LunaStar - Claim: Verified Host Badge" to "Luna Star submitted attendance log"
        ).forEach { (user, details) ->
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(user, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(details, color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { feedbackMessage = "SUCCESS: Approved profile badge credentials." },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Approve Badge", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { feedbackMessage = "REJECTED: Denied verification claim." },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Reject Claim", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ============================================================================
// 19. HOST PANEL (Section HM / HN)
// ============================================================================
@Composable
fun HostTab() {
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var attendanceClaimed by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("host_tab_scroll"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Professional Host Hub", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Track your active streaming minutes, inspect gift conversions, lock in daily check-ins, and prepare for PK battles.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Stats Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Live Duration", "180 mins today", "Daily goal: 120m", Color(0xFF00E676))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniStatCard("Received Gifts", "14,800 Coins", "Earnings balance", Color(0xFFFFB300))
                }
            }
        }

        // Attendance logging
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Daily Streaming Duty Attendance Log", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Log your streaming attendance today to secure salary bonuses.", fontSize = 10.sp, color = Color.White.copy(0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            attendanceClaimed = true
                            feedbackMessage = "SUCCESS: Logged streaming attendance today (180 minutes verified). Salary bonus ledger updated."
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (attendanceClaimed) Color.White.copy(0.12f) else Color(0xFF8E2DE2)
                        ),
                        enabled = !attendanceClaimed,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (attendanceClaimed) "Attendance Logged ✅" else "Log Streaming Attendance",
                            color = if (attendanceClaimed) Color.White.copy(0.6f) else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Daily Tasks List
        item {
            Text("Host Duty: Daily Tasks Guild", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        listOf(
            "Stream live for at least 120 minutes" to "180 / 120 mins (COMPLETED)",
            "Acquire at least 5,000 gift coins" to "14,800 / 5,000 coins (COMPLETED)",
            "Initiate or participate in 2 PK Battles" to "1 / 2 battles (Pending)"
        ).forEach { (task, progress) ->
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(task, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(progress, fontSize = 9.sp, color = if (progress.contains("COMPLETED")) Color(0xFF00E676) else Color(0xFFFFB300))
                        }
                        Icon(
                            imageVector = if (progress.contains("COMPLETED")) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (progress.contains("COMPLETED")) Color(0xFF00E676) else Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        feedbackMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E676).copy(0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// ==========================================
// 8. PROMOTION CENTER MODULE COMPOSABLE (HO)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PromotionCenterTab(viewModel: ConnectLiveViewModel) {
    val promotions by viewModel.promotions.collectAsState()
    val demotions by viewModel.demotions.collectAsState()
    val transfers by viewModel.transfers.collectAsState()
    val approvalType by viewModel.promotionApprovalType.collectAsState()
    val profiles by viewModel.permissionProfiles.collectAsState()
    val systemLogs by viewModel.systemLogs.collectAsState()

    var activeSubTab by remember { mutableStateOf("dashboard") } // dashboard, promote, demote, transfer, profile, history, audit

    // Promote Form States
    var promoUserId by remember { mutableStateOf("") }
    var promoCurrentRole by remember { mutableStateOf("User") }
    var promoNewRole by remember { mutableStateOf("Host") }
    var promoEffectiveDate by remember { mutableStateOf("") }
    var promoReason by remember { mutableStateOf("") }
    var promoNotes by remember { mutableStateOf("") }
    var promoIsTemp by remember { mutableStateOf(false) }
    var promoTempStart by remember { mutableStateOf("2026-07-21") }
    var promoTempEnd by remember { mutableStateOf("2026-10-21") }
    var promoResultMsg by remember { mutableStateOf<String?>(null) }

    // Demote Form States
    var demoteUserId by remember { mutableStateOf("") }
    var demoteNewRole by remember { mutableStateOf("User") }
    var demoteReason by remember { mutableStateOf("") }
    var demoteResultMsg by remember { mutableStateOf<String?>(null) }

    // Transfer Form States
    var transferType by remember { mutableStateOf("Transfer Agency") }
    var transferSource by remember { mutableStateOf("") }
    var transferDest by remember { mutableStateOf("") }
    var transferRemarks by remember { mutableStateOf("") }
    var transferResultMsg by remember { mutableStateOf<String?>(null) }

    // History Filters
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState())) {
        // Module Title
        Text(
            text = "Promotion, Demotion & Transfer Control Desk",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
        )
        Text(
            text = "Enforce operational hierarchy, adjust security permission matrix & record transfers.",
            color = Color.White.copy(0.5f),
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Sub Tab Navigation
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val subTabs = listOf(
                "dashboard" to "Dashboard",
                "promote" to "Promote",
                "demote" to "Demote",
                "transfer" to "Transfer",
                "profile" to "Permission Profiles",
                "history" to "History Logs",
                "audit" to "Audit Center"
            )
            subTabs.forEach { (tab, label) ->
                val isSelected = activeSubTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFFFFB300).copy(0.18f) else Color.White.copy(0.04f))
                        .clickable { activeSubTab = tab }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("promo_sub_tab_$tab")
                ) {
                    Text(
                        label,
                        color = if (isSelected) Color(0xFFFFB300) else Color.White.copy(0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Body Content Dispatches
        when (activeSubTab) {
            "dashboard" -> {
                // Statistics Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val pendingCount = promotions.count { it.status == "Pending" }
                    val approvedCount = promotions.count { it.status == "Approved" }
                    val scheduledCount = promotions.count { it.status == "Scheduled" }

                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Pending Promos", "$pendingCount", "Needs Review", Color(0xFFFFB300))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Active Promos", "$approvedCount", "Completed", Color(0xFF4CAF50))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Scheduled", "$scheduledCount", "Temporary/Future", Color(0xFF00E5FF))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pipeline Approval Configurations (Platform Owner Action)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF160F30)),
                    border = BorderStroke(1.dp, Color.White.copy(0.05f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Platform Approval Pipeline Config", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text("Set validation requirements for upcoming promotions.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        val pipelines = listOf("Automatic", "Manual Approval", "Two-Step Approval")
                        pipelines.forEach { mode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setPromotionApprovalType(mode) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = approvalType == mode,
                                    onClick = { viewModel.setPromotionApprovalType(mode) },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFB300))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(mode, color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Recent Pending Approvals Box
                Text("Pending Approvals Queue", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                val pendingList = promotions.filter { it.status == "Pending" }
                if (pendingList.isEmpty()) {
                    Text("No pending promotions requiring authorization.", color = Color.White.copy(0.4f), fontSize = 11.sp)
                } else {
                    pendingList.forEach { record ->
                        GlassmorphicCard(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("User ID: ${record.userId}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFFB300).copy(0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("PENDING APPROVAL", color = Color(0xFFFFB300), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("Promotion: ${record.prevRole} → ${record.newRole}", color = Color.White.copy(0.8f), fontSize = 11.sp)
                                Text("Reason: ${record.reason}", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { viewModel.rejectPromotion(record.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(0.15f)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Reject", color = Color(0xFFFF5252), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.approvePromotion(record.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Approve", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "promote" -> {
                // Promote Form Panel
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Promote Operational Account", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("User ID (e.g. 100123456)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = promoUserId,
                            onValueChange = { promoUserId = it },
                            modifier = Modifier.fillMaxWidth().testTag("promo_user_id"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Current Role Selector
                        Text("Current Role", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            val currentRoles = listOf("User", "Host", "BD Official", "Merchant", "Manager", "Super Manager", "Admin")
                            currentRoles.forEach { role ->
                                val isSel = promoCurrentRole == role
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) Color(0xFFFFB300) else Color.White.copy(0.05f))
                                        .clickable { promoCurrentRole = role }
                                        .padding(8.dp)
                                ) {
                                    Text(role, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Target Promo Role Selector
                        Text("Promote To (Target Role)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            val targetRoles = listOf("Host", "Family Leader", "Coin Seller", "Coin Reseller", "Merchant", "BD Official", "Agency Owner", "BD Leader", "Merchant Reseller Leader", "Super Manager", "Super Manager Leader", "Super Admin")
                            targetRoles.forEach { role ->
                                val isSel = promoNewRole == role
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) Color(0xFF00E5FF) else Color.White.copy(0.05f))
                                        .clickable { promoNewRole = role }
                                        .padding(8.dp)
                                ) {
                                    Text(role, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Temp Promotion Options
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = promoIsTemp,
                                onCheckedChange = { promoIsTemp = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFB300))
                            )
                            Text("Temporary Role Assignment", color = Color.White, fontSize = 11.sp)
                        }

                        if (promoIsTemp) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Start Date", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                    OutlinedTextField(
                                        value = promoTempStart,
                                        onValueChange = { promoTempStart = it },
                                        singleLine = true,
                                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 11.sp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("End Date (Auto Expire)", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                    OutlinedTextField(
                                        value = promoTempEnd,
                                        onValueChange = { promoTempEnd = it },
                                        singleLine = true,
                                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 11.sp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Effective Date (Optional - Blank for immediate)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = promoEffectiveDate,
                            onValueChange = { promoEffectiveDate = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Promotion Reason (Required)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = promoReason,
                            onValueChange = { promoReason = it },
                            modifier = Modifier.fillMaxWidth().testTag("promo_reason"),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Administrative Notes (Optional)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = promoNotes,
                            onValueChange = { promoNotes = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                promoResultMsg = viewModel.submitPromotion(
                                    userId = promoUserId,
                                    currentRole = promoCurrentRole,
                                    newRole = promoNewRole,
                                    effectiveDate = promoEffectiveDate,
                                    reason = promoReason,
                                    notes = promoNotes,
                                    isTemp = promoIsTemp,
                                    tempStart = promoTempStart,
                                    tempEnd = promoTempEnd
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Submit Promotion Order", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        promoResultMsg?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = msg,
                                color = if (msg.contains("ERROR")) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            "demote" -> {
                // Demotion Panel
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Execute Account Demotion / Revocation", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("Instantly demote, strip special management permissions, or restore back to standard user.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("User ID", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = demoteUserId,
                            onValueChange = { demoteUserId = it },
                            modifier = Modifier.fillMaxWidth().testTag("demote_user_id"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("New Assigned Role Target", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            val demoteTargets = listOf("User", "Host", "BD Official", "Merchant")
                            demoteTargets.forEach { role ->
                                val isSel = demoteNewRole == role
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) Color(0xFFFF5252) else Color.White.copy(0.05f))
                                        .clickable { demoteNewRole = role }
                                        .padding(8.dp)
                                ) {
                                    Text(role, color = if (isSel) Color.White else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Mandatory Reason for Demotion", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = demoteReason,
                            onValueChange = { demoteReason = it },
                            modifier = Modifier.fillMaxWidth().testTag("demote_reason"),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                demoteResultMsg = viewModel.demoteUser(demoteUserId, demoteNewRole, demoteReason)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Apply Revocation / Demotion", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        demoteResultMsg?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = msg,
                                color = if (msg.contains("ERROR")) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            "transfer" -> {
                // Transfer Panel
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Transfer Management Network Responsibilities", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Select Network Type", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        val types = listOf("Transfer Agency", "Transfer Merchant Network", "Transfer Coin Seller Network", "Transfer BD Team", "Transfer Family Leadership")
                        types.forEach { t ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { transferType = t }.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = transferType == t,
                                    onClick = { transferType = t },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFB300))
                                )
                                Text(t, color = Color.White, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("From (Source Owner Username)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = transferSource,
                            onValueChange = { transferSource = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("To (Destination Owner Username)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = transferDest,
                            onValueChange = { transferDest = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Transfer Remarks & Authorization Notes", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = transferRemarks,
                            onValueChange = { transferRemarks = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                transferResultMsg = viewModel.transferRole(transferType, transferSource, transferDest, transferRemarks)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Execute Network Transfer", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        transferResultMsg?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = msg,
                                color = if (msg.contains("ERROR")) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            "profile" -> {
                // Permission profiles matrix view & customize
                Text("Role Permission Profiles Configurator", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Text("Toggle access rights per platform tier on the fly without patching binaries.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                Spacer(modifier = Modifier.height(12.dp))

                profiles.forEach { (role, perms) ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF191338))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(role, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300), fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Granted Rights: ${perms.joinToString(", ")}", color = Color.White.copy(0.7f), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Action button to customize
                            var isCustomizing by remember { mutableStateOf(false) }
                            if (isCustomizing) {
                                val allPermsList = listOf("View", "Create", "Edit", "Delete", "Approve", "Reject", "Export", "Invite", "Promote", "Demote", "Transfer", "Suspend", "Restore")
                                FlowRow(modifier = Modifier.fillMaxWidth()) {
                                    allPermsList.forEach { perm ->
                                        val hasPerm = perms.contains(perm)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(4.dp).clickable {
                                                val next = perms.toMutableSet()
                                                if (hasPerm) next.remove(perm) else next.add(perm)
                                                viewModel.updatePermissionsForRole(role, next)
                                            }
                                        ) {
                                            Checkbox(
                                                checked = hasPerm,
                                                onCheckedChange = {
                                                    val next = perms.toMutableSet()
                                                    if (hasPerm) next.remove(perm) else next.add(perm)
                                                    viewModel.updatePermissionsForRole(role, next)
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFB300))
                                            )
                                            Text(perm, color = Color.White, fontSize = 10.sp)
                                        }
                                    }
                                }
                                TextButton(onClick = { isCustomizing = false }) {
                                    Text("Done Customizing", color = Color(0xFF00E5FF), fontSize = 11.sp)
                                }
                            } else {
                                TextButton(onClick = { isCustomizing = true }) {
                                    Text("Customize Rights Matrix", color = Color(0xFFFFB300), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            "history" -> {
                // Interactive logs list with search / filter / simulate export
                Text("Search Promotion Logs Archive", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter by User ID or Promoted By", color = Color.White.copy(0.4f)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Historical Entries", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                    Button(
                        onClick = {
                            android.widget.Toast.makeText(viewModel.getApplication(), "Simulating Excel/CSV Audit Log Export... Successfully exported 15 entries!", android.widget.Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export CSV", color = Color.White, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                promotions.filter {
                    it.userId.contains(searchQuery) || it.promotedBy.contains(searchQuery, true)
                }.forEach { p ->
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("User ID: ${p.userId}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                Text(p.dateTime, fontSize = 10.sp, color = Color.White.copy(0.4f))
                            }
                            Text("Change: ${p.prevRole} → ${p.newRole}", color = Color(0xFFFFB300), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Executed By: ${p.promotedBy}", color = Color.White.copy(0.7f), fontSize = 10.sp)
                            Text("Reason: ${p.reason}", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            if (p.notes.isNotEmpty()) {
                                Text("Notes: ${p.notes}", color = Color.White.copy(0.4f), fontSize = 9.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (p.status) {
                                            "Approved" -> Color(0xFF4CAF50).copy(0.12f)
                                            "Rejected" -> Color(0xFFFF5252).copy(0.12f)
                                            else -> Color(0xFFFFB300).copy(0.12f)
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    p.status.uppercase(),
                                    color = when (p.status) {
                                        "Approved" -> Color(0xFF4CAF50)
                                        "Rejected" -> Color(0xFFFF5252)
                                        else -> Color(0xFFFFB300)
                                    },
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Demotions History Archive", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                demotions.forEach { d ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1919))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("User ID: ${d.userId}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                Text(d.dateTime, fontSize = 10.sp, color = Color.White.copy(0.4f))
                            }
                            Text("Stripped Role: ${d.prevRole} → Reset To: ${d.newRole}", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Authorized By: ${d.demotedBy}", color = Color.White.copy(0.7f), fontSize = 10.sp)
                            Text("Reason: ${d.reason}", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        }
                    }
                }
            }

            "audit" -> {
                // Unified Read-Only Security Audit Logs
                Text("Cryptographic Security Log Book", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Text("System-level ledger capturing credential actions. Tamper-proof, read-only.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // Display system logs from repository
                systemLogs.filter {
                    it.action.contains("ROLE") || it.action.contains("PROMO") || it.action.contains("PERM") || it.action.contains("VERIFY")
                }.forEach { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.02f)),
                        border = BorderStroke(1.dp, Color.White.copy(0.04f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (log.action.contains("FAILED") || log.action.contains("PENALTY")) Color(0xFFFF5252) else Color(0xFFFFB300),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(log.action, color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(log.timestamp.toString(), color = Color.White.copy(0.3f), fontSize = 8.sp)
                                }
                                Text(log.details, color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 9. VERIFICATION & CERTIFICATION COMPOSABLE (HQ)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationCenterTab(viewModel: ConnectLiveViewModel) {
    val apps by viewModel.verificationApps.collectAsState()

    var activeSubTab by remember { mutableStateOf("dashboard") } // dashboard, apply, review, badges, report

    // Apply Form States
    var appUserId by remember { mutableStateOf("") }
    var appFullName by remember { mutableStateOf("") }
    var appDob by remember { mutableStateOf("1998-05-15") }
    var appCountry by remember { mutableStateOf("Pakistan") }
    var appPhone by remember { mutableStateOf("") }
    var appEmail by remember { mutableStateOf("") }
    var appIdType by remember { mutableStateOf("National ID") }
    var appDocNum by remember { mutableStateOf("") }
    var appRoleRequested by remember { mutableStateOf("Host") }
    var appLevelRequested by remember { mutableStateOf("Official Verified") }
    var appLicenseUrl by remember { mutableStateOf("") }
    var applyResultMsg by remember { mutableStateOf<String?>(null) }

    // Search and Filter
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState())) {
        Text(
            text = "Official Certification & Account Verification Hub",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
        )
        Text(
            text = "Audit government credentials, assign trust ratings & unlock elite limits.",
            color = Color.White.copy(0.5f),
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Navigation
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val subTabs = listOf(
                "dashboard" to "Verification Queue",
                "apply" to "Submit Application",
                "review" to "Review Desk",
                "badges" to "Badges & Trust Showcase",
                "report" to "Platform Audit Reports"
            )
            subTabs.forEach { (tab, label) ->
                val isSelected = activeSubTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF00E5FF).copy(0.18f) else Color.White.copy(0.04f))
                        .clickable { activeSubTab = tab }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("verify_sub_tab_$tab")
                ) {
                    Text(
                        label,
                        color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeSubTab) {
            "dashboard" -> {
                // Verification Stats Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val pendingCount = apps.count { it.status == "Pending" }
                    val approvedCount = apps.count { it.status == "Approved" }
                    val rejectedCount = apps.count { it.status == "Rejected" }

                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Pending", "$pendingCount", "Applications", Color(0xFFFFB300))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Approved", "$approvedCount", "Trust Badges Active", Color(0xFF4CAF50))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Rejected", "$rejectedCount", "Audit Denied", Color(0xFFFF5252))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by User ID, Name or Country", color = Color.White.copy(0.4f)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                apps.filter {
                    it.userId.contains(searchQuery) || it.fullName.contains(searchQuery, true) || it.country.contains(searchQuery, true)
                }.forEach { app ->
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("ID: ${app.userId} | ${app.fullName}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (app.status) {
                                                "Approved" -> Color(0xFF4CAF50).copy(0.15f)
                                                "Rejected" -> Color(0xFFFF5252).copy(0.15f)
                                                else -> Color(0xFFFFB300).copy(0.15f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        app.status,
                                        color = when (app.status) {
                                            "Approved" -> Color(0xFF4CAF50)
                                            "Rejected" -> Color(0xFFFF5252)
                                            else -> Color(0xFFFFB300)
                                        },
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text("Country: ${app.country} | Phone: ${app.phone}", color = Color.White.copy(0.6f), fontSize = 10.sp)
                            Text("Role: ${app.role} | Badge Class: ${app.level}", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Credentials: ${app.idType} (${app.docNumber})", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            if (app.licenseUrl.isNotEmpty()) {
                                Text("Business Document Link: ${app.licenseUrl}", color = Color(0xFFFFE082), fontSize = 9.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Submitted At: ${app.submittedAt}", color = Color.White.copy(0.4f), fontSize = 8.sp)
                        }
                    }
                }
            }

            "apply" -> {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Apply for Official Trust Verification", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("Submit authenticated identity documentation for verification by the regional super management reviewer desk.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("User ID (e.g. 100123456)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = appUserId,
                            onValueChange = { appUserId = it },
                            modifier = Modifier.fillMaxWidth().testTag("verify_user_id"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Full Name as per Government Card", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = appFullName,
                            onValueChange = { appFullName = it },
                            modifier = Modifier.fillMaxWidth().testTag("verify_full_name"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Date of Birth", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                OutlinedTextField(
                                    value = appDob,
                                    onValueChange = { appDob = it },
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 11.sp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Country", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                OutlinedTextField(
                                    value = appCountry,
                                    onValueChange = { appCountry = it },
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 11.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Phone Number", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                OutlinedTextField(
                                    value = appPhone,
                                    onValueChange = { appPhone = it },
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 11.sp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Email Address", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                OutlinedTextField(
                                    value = appEmail,
                                    onValueChange = { appEmail = it },
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 11.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("ID Document Type", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                OutlinedTextField(
                                    value = appIdType,
                                    onValueChange = { appIdType = it },
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 11.sp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("ID Document Number", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                OutlinedTextField(
                                    value = appDocNum,
                                    onValueChange = { appDocNum = it },
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 11.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Business License / Organization Link (For Business Roles)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = appLicenseUrl,
                            onValueChange = { appLicenseUrl = it },
                            placeholder = { Text("https://licensing.gov/cert", color = Color.White.copy(0.3f)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Platform Role Target", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            val rolesList = listOf("User", "Host", "Family Leader", "Agency Owner", "Merchant", "Coin Seller", "BD Official")
                            rolesList.forEach { r ->
                                val isSel = appRoleRequested == r
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) Color(0xFF00E5FF) else Color.White.copy(0.05f))
                                        .clickable { appRoleRequested = r }
                                        .padding(8.dp)
                                ) {
                                    Text(r, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Target Verification Level Class", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            val levelsList = listOf("Basic Verified", "Official Verified", "Premium Verified", "Elite Verified", "Partner Verified")
                            levelsList.forEach { l ->
                                val isSel = appLevelRequested == l
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) Color(0xFFFFB300) else Color.White.copy(0.05f))
                                        .clickable { appLevelRequested = l }
                                        .padding(8.dp)
                                ) {
                                    Text(l, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                applyResultMsg = viewModel.submitVerificationRequest(
                                    userId = appUserId,
                                    fullName = appFullName,
                                    dob = appDob,
                                    country = appCountry,
                                    phone = appPhone,
                                    email = appEmail,
                                    idType = appIdType,
                                    docNumber = appDocNum,
                                    role = appRoleRequested,
                                    level = appLevelRequested,
                                    license = appLicenseUrl
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Submit Secure Application Package", color = Color.Black, fontWeight = FontWeight.Black)
                        }

                        applyResultMsg?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                msg,
                                color = if (msg.contains("ERROR")) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            "review" -> {
                // Interactive Admin Review Panel
                Text("Verification Authority Review Panel", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Text("Process submitted identification dossiers and toggle verified badges live.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                Spacer(modifier = Modifier.height(12.dp))

                val pendingList = apps.filter { it.status == "Pending" }
                if (pendingList.isEmpty()) {
                    Text("No pending certification claims found.", color = Color.White.copy(0.4f), fontSize = 11.sp)
                } else {
                    pendingList.forEach { app ->
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Full Name: ${app.fullName}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("Requester User ID: ${app.userId}", color = Color.White.copy(0.7f), fontSize = 11.sp)
                                Text("Country Origin: ${app.country} | DOB: ${app.dob}", color = Color.White.copy(0.7f), fontSize = 11.sp)
                                Text("Requested Level Badge: ${app.level} (${app.role})", color = Color(0xFFFFB300), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.reviewVerification(app.id, "Rejected") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(0.15f)),
                                        modifier = Modifier.weight(1f).height(32.dp)
                                    ) {
                                        Text("Reject Claim", color = Color(0xFFFF5252), fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.reviewVerification(app.id, "Approved") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        modifier = Modifier.weight(1f).height(32.dp)
                                    ) {
                                        Text("Grant Badge Class", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "badges" -> {
                // Badge Showcase (M3 Styling)
                Text("Live Trust Level Badges", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                val showcaseBadges = listOf(
                    "Basic Verified" to Color(0xFFB0BEC5) to Icons.Default.Check,
                    "Official Verified" to Color(0xFF2196F3) to Icons.Default.Verified,
                    "Premium Verified" to Color(0xFFFF9800) to Icons.Default.Stars,
                    "Elite Verified" to Color(0xFF00E5FF) to Icons.Default.Diamond,
                    "Partner Verified" to Color(0xFF00E676) to Icons.Default.Shield
                )

                showcaseBadges.forEach { item ->
                    val title = item.first.first
                    val color = item.first.second
                    val vec = item.second
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141031))
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = vec, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("Displays in profile, chat nodes & ranking boards.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            "report" -> {
                // Platform report statistics exporter
                Text("Verification Demographics Audit Desk", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))

                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Approved Verifications Rate: 84.2%", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Total Certified Users: 1,420 accounts", color = Color.White, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                android.widget.Toast.makeText(viewModel.getApplication(), "Simulating PDF Export of Country Audit Demographics... Done!", android.widget.Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export Country Breakdown (PDF)", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 10. SALARY, COMMISSION & PAYROLL COMPOSABLE (HR)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaryPayrollTab(viewModel: ConnectLiveViewModel) {
    val configs by viewModel.salaryConfigs.collectAsState()
    val requests by viewModel.payoutRequests.collectAsState()
    val earnings by viewModel.earningsHistory.collectAsState()
    val budget by viewModel.monthlySalaryBudget.collectAsState()
    val paidSalaries by viewModel.totalSalariesPaid.collectAsState()

    var activeSubTab by remember { mutableStateOf("dashboard") } // dashboard, configure, request, review, deductions, history

    // Configure Form States
    var configRole by remember { mutableStateOf("Host") }
    var configModel by remember { mutableStateOf("Hybrid (Salary + Commission)") }
    var configBase by remember { mutableStateOf("500.0") }
    var configType by remember { mutableStateOf("Diamond Sales") }
    var configRate by remember { mutableStateOf("12.5") }
    var configResultMsg by remember { mutableStateOf<String?>(null) }

    // Request Form States
    var reqAmount by remember { mutableStateOf("") }
    var reqMethod by remember { mutableStateOf("Bank Transfer") }
    var reqResultMsg by remember { mutableStateOf<String?>(null) }

    // Deduction Form States
    var deductUserId by remember { mutableStateOf("") }
    var deductAmount by remember { mutableStateOf("") }
    var deductReason by remember { mutableStateOf("") }
    var deductResultMsg by remember { mutableStateOf<String?>(null) }

    // Search and Filters
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState())) {
        Text(
            text = "Payroll, Commission & Payout Control Center",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
        )
        Text(
            text = "Configure salary models, assess commission triggers, handle payout releases.",
            color = Color.White.copy(0.5f),
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Navigation
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val subTabs = listOf(
                "dashboard" to "Salary Center",
                "configure" to "Earning Models",
                "request" to "Request Payout",
                "review" to "Payout Review",
                "deductions" to "Deduction System",
                "history" to "Earnings Archive"
            )
            subTabs.forEach { (tab, label) ->
                val isSelected = activeSubTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFFFFB300).copy(0.18f) else Color.White.copy(0.04f))
                        .clickable { activeSubTab = tab }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("salary_sub_tab_$tab")
                ) {
                    Text(
                        label,
                        color = if (isSelected) Color(0xFFFFB300) else Color.White.copy(0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeSubTab) {
            "dashboard" -> {
                // Payroll Stats Dashboard
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Remaining Budget", "$${budget.toInt()}", "Allocated Pool", Color(0xFF00E5FF))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Paid Payroll", "$${paidSalaries.toInt()}", "LTD Salaries", Color(0xFF4CAF50))
                    }
                    val pendingAmt = requests.filter { it.status == "Pending" }.sumOf { it.amount }
                    Box(modifier = Modifier.weight(1f)) {
                        MiniStatCard("Pending Requests", "$${pendingAmt.toInt()}", "Awaiting Release", Color(0xFFFFB300))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payroll calendar and cycle schedule overview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B143F))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Current Payroll Calendar Cycle", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            Text("Monthly cycle ending July 31, 2026. Automatic calculation trigger active.", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Compensation Models Summary
                Text("Role Compensation Models Matrix", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                configs.forEach { config ->
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(config.role, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300), fontSize = 12.sp)
                                Text(config.model, color = Color.White.copy(0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Base Wage: $${config.baseAmount} | Trigger: ${config.commissionType} (${config.commissionRate}%)", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        }
                    }
                }
            }

            "configure" -> {
                // Configuration Form Panel
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Update Earning Framework Model", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("Alter salary models and reward rates globally without redeploying code.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Target Role", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            val rolesList = listOf("Host", "BD Official", "BD Leader", "Agency Owner", "Merchant", "Coin Seller", "Coin Reseller", "Super Manager")
                            rolesList.forEach { r ->
                                val isSel = configRole == r
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) Color(0xFFFFB300) else Color.White.copy(0.05f))
                                        .clickable { configRole = r }
                                        .padding(8.dp)
                                ) {
                                    Text(r, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Select Salary Structure Model", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            val modelsList = listOf("Fixed Monthly Salary", "Daily Salary", "Hybrid (Salary + Commission)", "Commission Only", "Performance Salary", "Revenue Sharing")
                            modelsList.forEach { m ->
                                val isSel = configModel == m
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) Color(0xFF00E5FF) else Color.White.copy(0.05f))
                                        .clickable { configModel = m }
                                        .padding(8.dp)
                                ) {
                                    Text(m, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Base Salary Amount ($)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = configBase,
                            onValueChange = { configBase = it },
                            modifier = Modifier.fillMaxWidth().testTag("salary_base_input"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Commission Target Trigger", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            val triggers = listOf("Coin Sales", "Diamond Sales", "Referral Program", "Gift Transactions", "User Recharge")
                            triggers.forEach { t ->
                                val isSel = configType == t
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) Color(0xFFFFB300) else Color.White.copy(0.05f))
                                        .clickable { configType = t }
                                        .padding(8.dp)
                                ) {
                                    Text(t, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Commission Rate / Bonus Percentage (%)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = configRate,
                            onValueChange = { configRate = it },
                            modifier = Modifier.fillMaxWidth().testTag("salary_rate_input"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val baseVal = configBase.toDoubleOrNull() ?: 0.0
                                val rateVal = configRate.toDoubleOrNull() ?: 0.0
                                configResultMsg = viewModel.configureSalaryModel(configRole, configModel, baseVal, configType, rateVal)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Apply Compensation Rule Updates", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        configResultMsg?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                msg,
                                color = if (msg.contains("ERROR")) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            "request" -> {
                // Request Payout Panel
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Submit Payout Disbursement Claim", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("Release accumulated platform earnings into certified external settlement vectors.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Amount to Request ($)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = reqAmount,
                            onValueChange = { reqAmount = it },
                            modifier = Modifier.fillMaxWidth().testTag("payout_amount"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Settlement Payout Vector", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        val methods = listOf("Easypaisa", "JazzCash", "NayaPay", "Bank Transfer", "Internal Wallet")
                        methods.forEach { m ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { reqMethod = m }.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = reqMethod == m,
                                    onClick = { reqMethod = m },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFB300))
                                )
                                Text(m, color = Color.White, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amtVal = reqAmount.toDoubleOrNull() ?: 0.0
                                reqResultMsg = viewModel.submitPayoutRequest(amtVal, reqMethod)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Dispatch Payout Claim", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        reqResultMsg?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                msg,
                                color = if (msg.contains("ERROR")) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            "review" -> {
                // Admin Payout Approvals queue
                Text("Settlement Authority Release Desk", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Text("Process settlement payouts. Approved requests will subtract from the master budget pool.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                Spacer(modifier = Modifier.height(12.dp))

                val pendingReqs = requests.filter { it.status == "Pending" }
                if (pendingReqs.isEmpty()) {
                    Text("No pending disbursement requests.", color = Color.White.copy(0.4f), fontSize = 11.sp)
                } else {
                    pendingReqs.forEach { req ->
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Claim ID: ${req.id}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                    Text(req.date, fontSize = 9.sp, color = Color.White.copy(0.4f))
                                }
                                Text("Settlement Amount: $${req.amount}", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Text("Settlement Vector: ${req.method} | User: ${req.userId}", color = Color.White.copy(0.7f), fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.reviewPayoutRequest(req.id, "Rejected") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(0.15f)),
                                        modifier = Modifier.weight(1f).height(32.dp)
                                    ) {
                                        Text("Reject Claim", color = Color(0xFFFF5252), fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.reviewPayoutRequest(req.id, "Approved") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        modifier = Modifier.weight(1f).height(32.dp)
                                    ) {
                                        Text("Release Funds", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "deductions" -> {
                // Apply Deductions/Penalties
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Deduction & Compliance Adjustment System", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("Impose fines, adjust manual chargebacks, or register performance penalties.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("User ID", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = deductUserId,
                            onValueChange = { deductUserId = it },
                            modifier = Modifier.fillMaxWidth().testTag("deduction_user_id"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Deduction Amount ($)", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = deductAmount,
                            onValueChange = { deductAmount = it },
                            modifier = Modifier.fillMaxWidth().testTag("deduction_amount"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Mandatory Deduction / Compliance Audit Reason", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        OutlinedTextField(
                            value = deductReason,
                            onValueChange = { deductReason = it },
                            modifier = Modifier.fillMaxWidth().testTag("deduction_reason"),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amtVal = deductAmount.toDoubleOrNull() ?: 0.0
                                val author = viewModel.meProfile.value?.name ?: "System Owner"
                                deductResultMsg = viewModel.applyDeduction(deductUserId, amtVal, deductReason, author)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Authorize Penalty Deduction", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        deductResultMsg?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                msg,
                                color = if (msg.contains("ERROR")) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            "history" -> {
                // Earnings history archive search and filter
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter by Period or Status", color = Color.White.copy(0.4f)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                earnings.filter {
                    it.period.contains(searchQuery, true) || it.status.contains(searchQuery, true)
                }.forEach { record ->
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(record.period, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                Text(record.date, fontSize = 10.sp, color = Color.White.copy(0.4f))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Base Wage: $${record.base}", color = Color.White.copy(0.7f), fontSize = 10.sp)
                                    Text("Commission: $${record.commission} | Bonus: $${record.bonus}", color = Color.White.copy(0.7f), fontSize = 10.sp)
                                    if (record.deduction > 0) {
                                        Text("Deduction: -$${record.deduction}", color = Color(0xFFFF5252), fontSize = 10.sp)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Net Paid: $${record.net}", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (record.status == "Paid") Color(0xFF4CAF50).copy(0.15f) else Color(0xFFFFB300).copy(0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(record.status.uppercase(), color = if (record.status == "Paid") Color(0xFF4CAF50) else Color(0xFFFFB300), fontSize = 8.sp, fontWeight = FontWeight.Bold)
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

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.GlassmorphicCard
import kotlinx.coroutines.launch
import androidx.compose.runtime.snapshots.SnapshotStateList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterpriseSystemsHub(viewModel: ConnectLiveViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Core Role Switcher State
    var activeRole by remember { mutableStateOf("Platform Owner") }
    val roles = listOf(
        "Platform Owner", "Super Admin", "Merchant Reseller Leader", 
        "Merchant", "BD Leader", "BD Official", "Coin Seller", 
        "Coin Reseller", "Room Owner"
    )
    
    // Enterprise Console Subsystem Modules
    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        Triple("Enterprise Media", Icons.Default.PermMedia, 0),
        Triple("Merchant Sys", Icons.Default.Storefront, 1),
        Triple("BD Ops", Icons.Default.Leaderboard, 2),
        Triple("Wallet Vault", Icons.Default.AccountBalanceWallet, 3),
        Triple("Support Center", Icons.Default.SupportAgent, 4),
        Triple("Security Control", Icons.Default.Security, 5),
        Triple("Compliance & Legal", Icons.Default.Gavel, 6),
        Triple("Multi-Tenant SaaS", Icons.Default.Domain, 7),
        Triple("Workflow Engine", Icons.Default.AccountTree, 8),
        Triple("Observability SRE", Icons.Default.QueryStats, 9),
        Triple("AI & ML Platform", Icons.Default.Psychology, 10),
        Triple("Universal Search", Icons.Default.Search, 11),
        Triple("Global i18n", Icons.Default.Translate, 12),
        Triple("Notifications", Icons.Default.Notifications, 13),
        Triple("Data & BI Hub", Icons.Default.Insights, 14),
        Triple("Developer SDK", Icons.Default.Code, 15),
        Triple("Remote Config", Icons.Default.Tune, 16),
        Triple("Disaster Recovery", Icons.Default.CloudSync, 17),
        Triple("Document EDMS", Icons.Default.FolderSpecial, 18),
        Triple("GRC Audit", Icons.Default.VerifiedUser, 19),
        Triple("Bulletins & Alerts", Icons.Default.Campaign, 20),
        Triple("Platform Settings", Icons.Default.Settings, 21),
        Triple("Discovery & Mic", Icons.Default.Mic, 22),
        Triple("Room Management", Icons.Default.Dashboard, 23),
        Triple("Billing & Rev", Icons.Default.ReceiptLong, 24),
        Triple("IAM Identity", Icons.Default.Badge, 25)
    )

    // Shared Permanent Action Logs
    val systemLogs = remember {
        mutableStateListOf(
            "SYSTEM_INIT: Enterprise Systems Consolidated Console Ready.",
            "SECURE_GATE: Platform Owner authenticated, full access granted."
        )
    }

    // --- SHARED BACKEND SIMULATED STATES ---
    
    // Feature Switches
    var isVoiceRoomEnabled by remember { mutableStateOf(true) }
    var isPkEnabled by remember { mutableStateOf(true) }
    var isGiftsEnabled by remember { mutableStateOf(true) }
    var isWalletEnabled by remember { mutableStateOf(true) }
    var isMerchantEnabled by remember { mutableStateOf(true) }
    var isFamilyEnabled by remember { mutableStateOf(true) }

    // Merchant State
    var merchantCoins by remember { mutableStateOf(1500000) }
    var coinSellerStock by remember { mutableStateOf(180000) }
    var coinResellerStock by remember { mutableStateOf(500000) }
    var currentMerchantTarget by remember { mutableStateOf("1,000,000 LMX") }
    var currentMerchantTargetProgress by remember { mutableStateOf(650000) }
    
    // BD State
    val bdInvitations = remember {
        mutableStateListOf(
            Triple("INV-492", "User_9021", "Pending"),
            Triple("INV-491", "Host_Sarah", "Accepted"),
            Triple("INV-490", "Agency_Midwest", "Accepted")
        )
    }
    
    // Wallet / Recharge Orders / Withdrawals
    val coinBalance by viewModel.coinBalance.collectAsState()
    val diamondBalance by viewModel.diamondBalance.collectAsState()
    var pendingRechargesCount by remember { mutableStateOf(3) }
    val withdrawalRequests = remember {
        mutableStateListOf(
            Triple("REQ-802", "Host_Alex (25,000 Diamonds)", "Pending Review"),
            Triple("REQ-801", "User_992 (10,000 Diamonds)", "Under Review")
        )
    }

    // Support Center Tickets
    val supportTickets = remember {
        mutableStateListOf(
            Triple("TCK-109", "Coin Recharge Failure (High)", "Open"),
            Triple("TCK-108", "Account Suspended Appeal (Urgent)", "In Progress")
        )
    }

    // Security Queue
    val safetyAlerts = remember {
        mutableStateListOf(
            Triple("ALERT-301", "Suspicious login attempt from untrusted IP", "Action Needed"),
            Triple("ALERT-300", "Spam reported in Room #9421", "Reviewing")
        )
    }

    // 15-Mic State
    val mics = remember {
        mutableStateListOf<String>().apply {
            addAll(List(15) { index -> if (index == 0) "Room Owner" else "Empty" })
        }
    }
    val micLocked = remember { mutableStateListOf<Boolean>().apply { addAll(List(15) { false }) } }
    val micMuted = remember { mutableStateListOf<Boolean>().apply { addAll(List(15) { false }) } }
    val seatRequests = remember { mutableStateListOf("User_Sarah", "Guest_User_9", "Vip_Star") }
    var pinnedNotice by remember { mutableStateOf("Welcome to the Official Lumexa Room! Please respect rules.") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080512))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Premium Header with Navigation & Role Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("main") },
                    modifier = Modifier.testTag("hub_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Lumexa Enterprise Console", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Text("Consolidated Centralized Multi-System Panel", color = Color.White.copy(0.5f), fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                
                // Export Button
                IconButton(
                    onClick = {
                        systemLogs.add(0, "EXPORT: Initiated backup spreadsheet for active consoles.")
                        Toast.makeText(context, "Reports exported to /Download/Lumexa_Ledgers.xlsx", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Export All Reports", tint = Color(0xFF00E5FF))
                }
            }

            // Role Switcher Bar
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text("Simulate Role Clearance Interface", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    roles.forEach { role ->
                        val isRoleSelected = activeRole == role
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isRoleSelected) Color(0xFFFFB300).copy(0.15f) else Color.White.copy(0.04f))
                                .border(1.dp, if (isRoleSelected) Color(0xFFFFB300) else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable {
                                    activeRole = role
                                    systemLogs.add(0, "CLEARANCE: Simulated active role switched to '$role'")
                                    Toast.makeText(context, "Clearance updated to $role", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(role, color = if (isRoleSelected) Color(0xFFFFD54F) else Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subsystem Tab Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .background(Color(0xFF0C071A))
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                tabs.forEach { (label, icon, index) ->
                    val isTabSelected = activeTab == index
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isTabSelected) Color(0xFF8E2DE2).copy(0.15f) else Color.White.copy(0.04f))
                            .border(1.dp, if (isTabSelected) Color(0xFF8E2DE2) else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { activeTab = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(icon, contentDescription = null, tint = if (isTabSelected) Color(0xFFBB86FC) else Color.White.copy(0.6f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(label, color = if (isTabSelected) Color.White else Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Central Consolidated Content Renderer
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                when (activeTab) {
                    0 -> EnterpriseMediaPlatformView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    1 -> MerchantSystemView(
                        activeRole = activeRole,
                        merchantCoins = merchantCoins,
                        sellerStock = coinSellerStock,
                        resellerStock = coinResellerStock,
                        target = currentMerchantTarget,
                        targetProgress = currentMerchantTargetProgress,
                        onUpdateLogs = { systemLogs.add(0, it) },
                        onUpdateMerchantCoins = { merchantCoins += it },
                        onUpdateSellerStock = { coinSellerStock += it },
                        onUpdateResellerStock = { coinResellerStock += it }
                    )
                    2 -> BdOperationsView(
                        activeRole = activeRole,
                        invitations = bdInvitations,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    3 -> WalletVaultView(
                        activeRole = activeRole,
                        coinBalance = coinBalance ?: 0,
                        diamondBalance = diamondBalance,
                        pendingRecharges = pendingRechargesCount,
                        withdrawalRequests = withdrawalRequests,
                        onUpdateLogs = { systemLogs.add(0, it) },
                        onClearRecharges = { pendingRechargesCount = 0 },
                        viewModel = viewModel
                    )
                    4 -> SupportCenterView(
                        activeRole = activeRole,
                        tickets = supportTickets,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    5 -> SecurityControlView(
                        activeRole = activeRole,
                        alerts = safetyAlerts,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    6 -> ComplianceAndLegalView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    7 -> MultiTenantSaaSView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    8 -> WorkflowAutomationView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    9 -> ObservabilityAndSreView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    10 -> AiPlatformView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    11 -> UniversalSearchView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    12 -> InternationalizationView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    13 -> NotificationEngineView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    14 -> DataWarehouseAndBiView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    15 -> DeveloperPlatformView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    16 -> RemoteConfigView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    17 -> DisasterRecoveryView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    18 -> DocumentEdmsView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    19 -> GrcAuditView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    20 -> BulletinsAlertsView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    21 -> PlatformSettingsView(
                        activeRole = activeRole,
                        isVoiceRoomEnabled = isVoiceRoomEnabled,
                        onVoiceRoomToggle = { isVoiceRoomEnabled = it },
                        isPkEnabled = isPkEnabled,
                        onPkToggle = { isPkEnabled = it },
                        isGiftsEnabled = isGiftsEnabled,
                        onGiftsToggle = { isGiftsEnabled = it },
                        isWalletEnabled = isWalletEnabled,
                        onWalletToggle = { isWalletEnabled = it },
                        isMerchantEnabled = isMerchantEnabled,
                        onMerchantToggle = { isMerchantEnabled = it },
                        isFamilyEnabled = isFamilyEnabled,
                        onFamilyToggle = { isFamilyEnabled = it },
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    22 -> DiscoveryMicView(
                        activeRole = activeRole,
                        mics = mics,
                        micLocked = micLocked,
                        micMuted = micMuted,
                        seatRequests = seatRequests,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    23 -> RoomManagementView(
                        activeRole = activeRole,
                        pinnedNotice = pinnedNotice,
                        onUpdateNotice = { pinnedNotice = it },
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    24 -> EnterpriseBillingPlatformView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                    25 -> EnterpriseIamPlatformView(
                        activeRole = activeRole,
                        onUpdateLogs = { systemLogs.add(0, it) }
                    )
                }
            }

            // Real-Time Permanent System Log Audits
            Text(
                text = "Live Activity Audit Ledger",
                color = Color.White.copy(0.4f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color(0xFF030109))
                    .padding(8.dp)
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(systemLogs) { log ->
                        Text(
                            text = "• $log",
                            color = if (log.contains("SUCCESS") || log.contains("APPROVED")) Color(0xFF00E676) else if (log.contains("SECURITY") || log.contains("ALERT")) Color(0xFFFF5252) else Color.White.copy(0.6f),
                            fontSize = 9.sp,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 1. MERCHANT, COIN SELLER & COIN RESELLER MANAGEMENT VIEW
// ============================================================================
@Composable
fun MerchantSystemView(
    activeRole: String,
    merchantCoins: Int,
    sellerStock: Int,
    resellerStock: Int,
    target: String,
    targetProgress: Int,
    onUpdateLogs: (String) -> Unit,
    onUpdateMerchantCoins: (Int) -> Unit,
    onUpdateSellerStock: (Int) -> Unit,
    onUpdateResellerStock: (Int) -> Unit
) {
    var userTransferId by remember { mutableStateOf("") }
    var transferAmount by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("merchant_system_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Merchant, Seller & Reseller Ecosystem", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Track merchant ledger pools, wholesale distribution, seller inventory targets, and customer deliveries.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Stats Dashboard
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniDashboardCard("Merchant Stock", "$merchantCoins LMX", "System Main Pool", Color(0xFF8E2DE2))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniDashboardCard("Seller Reserve", "$sellerStock LMX", "Direct to Consumers", Color(0xFF00E676))
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniDashboardCard("Reseller Pool", "$resellerStock LMX", "Wholesale Inventory", Color(0xFFFFB300))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniDashboardCard("Assigned Targets", "$target", "Progress: $targetProgress", Color(0xFF00E5FF))
                }
            }
        }

        // Action controls based on simulated active clearance
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Inventory Delivery Terminal", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = userTransferId,
                        onValueChange = { userTransferId = it },
                        label = { Text("Target User ID (Normal, Seller or Reseller)", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
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
                        label = { Text("LMX Amount to Distribute", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFB300),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val amount = transferAmount.toIntOrNull() ?: 0
                                if (userTransferId.isNotBlank() && amount > 0) {
                                    if (merchantCoins >= amount) {
                                        onUpdateMerchantCoins(-amount)
                                        onUpdateLogs("SUCCESS: Merchant transferred $amount LMX wholesale coins to User $userTransferId.")
                                        userTransferId = ""
                                        transferAmount = ""
                                    } else {
                                        onUpdateLogs("ALERT_REJECTED: Insufficient merchant coin reserve balance.")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Merchant Deliver", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val amount = transferAmount.toIntOrNull() ?: 0
                                if (userTransferId.isNotBlank() && amount > 0) {
                                    if (sellerStock >= amount) {
                                        onUpdateSellerStock(-amount)
                                        onUpdateLogs("SUCCESS: Coin Seller sold $amount LMX retail coins to User $userTransferId.")
                                        userTransferId = ""
                                        transferAmount = ""
                                    } else {
                                        onUpdateLogs("ALERT_REJECTED: Insufficient seller coin reserve balance.")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Seller Retail Sale", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Platform Owner Controls
        if (activeRole == "Platform Owner" || activeRole == "Super Admin") {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Platform Owner controls (Merchant, Coin Seller)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFBB86FC))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    onUpdateMerchantCoins(500000)
                                    onUpdateLogs("SUCCESS: Platform Owner minted 500,000 LMX wholesale coins into Merchant Vault.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Mint wholesale LMX", color = Color.White, fontSize = 10.sp)
                            }
                            
                            Button(
                                onClick = {
                                    onUpdateLogs("SUCCESS: Platform Owner set active commission rate to 12.5% for all authorized sellers.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f)),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Adjust Commission", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 2. BD (BUSINESS DEVELOPMENT) OPERATIONS VIEW
// ============================================================================
@Composable
fun BdOperationsView(
    activeRole: String,
    invitations: List<Triple<String, String, String>>,
    onUpdateLogs: (String) -> Unit
) {
    var recruitId by remember { mutableStateOf("") }
    var regionInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("bd_operations_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Business Development Recruitment Console", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Recruit BD Officials, invite Agencies/Hosts using Referral QR/Links, assign regional targets, and view performance metrics.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // BD Hierarchy Tracker
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("BD Performance Dashboard", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Active BD Officials", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            Text("14 Officials", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text("Recruited Agencies", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            Text("42 Agencies", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text("Hosts Recruited", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            Text("1,240 Hosts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Send Recruitment Invitations
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Send Professional Invitation", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = recruitId,
                        onValueChange = { recruitId = it },
                        label = { Text("Candidate User ID (Agency/Host/BDO)", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
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
                        value = regionInput,
                        onValueChange = { regionInput = it },
                        label = { Text("Assigned Region / Territory", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
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
                            if (recruitId.isNotBlank()) {
                                onUpdateLogs("SUCCESS: BD dispatched invitation to candidate User $recruitId for region $regionInput. Tracking link generated.")
                                recruitId = ""
                                regionInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Generate Invitation Link & QR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Active Queue
        item {
            Text("Active Recruits & Invitations Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(invitations) { inv ->
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Invite ID: ${inv.first}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Candidate: ${inv.second}", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (inv.third == "Accepted") Color(0xFF00E676).copy(0.15f) else Color(0xFFFFB300).copy(0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(inv.third, color = if (inv.third == "Accepted") Color(0xFF00E676) else Color(0xFFFFB300), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ============================================================================
// 3. WALLET, RECHARGE & WITHDRAWAL ECOSYSTEM
// ============================================================================
@Composable
fun WalletVaultView(
    activeRole: String,
    coinBalance: Int,
    diamondBalance: Int,
    pendingRecharges: Int,
    withdrawalRequests: SnapshotStateList<Triple<String, String, String>>,
    onUpdateLogs: (String) -> Unit,
    onClearRecharges: () -> Unit,
    viewModel: ConnectLiveViewModel
) {
    var packagePrice by remember { mutableStateOf("") }
    var packageBonusCoins by remember { mutableStateOf("") }
    var withdrawalAmountInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("wallet_vault_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Wallet Vault & Financial Ledger", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Manage digital wallets, process gold coin recharges, approve host diamond withdrawals, and audit revenue lines.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Multi-Asset Status Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniDashboardCard("My Coin Balance", "$coinBalance Coins", "Liquidation stock", Color(0xFFFFB300))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniDashboardCard("Host Diamonds", "$diamondBalance Diamonds", "Withdrawal Reserve", Color(0xFF00E5FF))
                }
            }
        }

        // Pending Recharge Approvals
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Manual Payment Recharges Awaiting Approval", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("There are currently $pendingRecharges unconfirmed deposits from retail merchants.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (pendingRecharges > 0) {
                                onClearRecharges()
                                onUpdateLogs("SUCCESS: Super Admin verified bank slip. $pendingRecharges manual recharge orders completed.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (pendingRecharges > 0) Color(0xFFFFB300) else Color.White.copy(0.1f)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (pendingRecharges > 0) "Verify & Credit $pendingRecharges Orders" else "All Recharges Logged", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Diamond Conversion Calculator
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Host Diamond Withdrawal Terminal", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF00E676))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = withdrawalAmountInput,
                        onValueChange = { withdrawalAmountInput = it },
                        label = { Text("Diamonds to Cash (Rate: 100D = $1)", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val diamonds = withdrawalAmountInput.toIntOrNull() ?: 0
                            if (diamonds > 0) {
                                val success = viewModel.submitWithdrawalRequest(diamonds)
                                if (success) {
                                    onUpdateLogs("SUCCESS: Submitted withdrawal request of $diamonds diamonds. Pending Super Admin validation.")
                                } else {
                                    onUpdateLogs("ALERT_REJECTED: Insufficient diamond balance inside active wallet.")
                                }
                                withdrawalAmountInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Request Payout Process", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Active Withdrawal Queue for Super Admin
        if (activeRole == "Platform Owner" || activeRole == "Super Admin") {
            item {
                Text("Withdrawal Request Queue", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            items(withdrawalRequests) { req ->
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(req.second, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF00E5FF).copy(0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(req.third, color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    withdrawalRequests.remove(req)
                                    onUpdateLogs("APPROVED: Withdrawal payout approved for ${req.second}.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Approve & Pay", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    withdrawalRequests.remove(req)
                                    onUpdateLogs("DECLINED: Withdrawal payout denied and diamonds returned to user vault.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Reject Request", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 4. CUSTOMER SUPPORT & TICKET HELP DESK
// ============================================================================
@Composable
fun SupportCenterView(
    activeRole: String,
    tickets: SnapshotStateList<Triple<String, String, String>>,
    onUpdateLogs: (String) -> Unit
) {
    var ticketCategory by remember { mutableStateOf("Coin Recharge Problem") }
    var ticketDescription by remember { mutableStateOf("") }
    val categories = listOf("Coin Recharge Problem", "VIP Badge Missing", "Voice Room Disconnection", "Account Appeal")

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("support_center_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Help Desk & Support Ticket System", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Submit official complaints, trace user disputes, review suspension appeals, and assign technical SLA support.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Create Ticket Form
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Submit Customer Complaint", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFD54F))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSel = ticketCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFFFFD54F).copy(0.2f) else Color.White.copy(0.05f))
                                    .border(1.dp, if (isSel) Color(0xFFFFD54F) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { ticketCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(cat, color = if (isSel) Color(0xFFFFD54F) else Color.White.copy(0.6f), fontSize = 9.sp)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ticketDescription,
                        onValueChange = { ticketDescription = it },
                        label = { Text("Describe the issue in detail...", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD54F),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (ticketDescription.isNotBlank()) {
                                onUpdateLogs("SUCCESS: Dispatched new customer support ticket in category '$ticketCategory' with reference ID: TCK-${System.currentTimeMillis() % 1000}.")
                                ticketDescription = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Create Support Ticket", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Active tickets queue for staff
        item {
            Text("Open Help Tickets Assigned to Staff", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(tickets) { tck ->
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(tck.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFD54F).copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(tck.third, color = Color(0xFFFFD54F), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(tck.second, color = Color.White.copy(0.5f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                tickets.remove(tck)
                                onUpdateLogs("SUCCESS: Solved help desk ticket ${tck.first}. Client notified.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            modifier = Modifier.weight(1f).height(30.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Solve & Close", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                onUpdateLogs("SUCCESS: Escalated ticket ${tck.first} to Senior Tier-3 Engineers.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                            modifier = Modifier.weight(1f).height(30.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Escalate Ticket", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 5. SECURITY, MODERATION & RISK CONTROL
// ============================================================================
@Composable
fun SecurityControlView(
    activeRole: String,
    alerts: SnapshotStateList<Triple<String, String, String>>,
    onUpdateLogs: (String) -> Unit
) {
    var userIdToBlock by remember { mutableStateOf("") }
    var blockReason by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("security_control_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Security Operations & Threat Defense Console", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Monitor real-time session safety parameters, block abusive nodes, ban hardware addresses, and review automated content triage flags.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Threat Telemetry Indicators
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    MiniDashboardCard("Security Level", "98.9% SAFE", "All nodes validated", Color(0xFF00E676))
                }
                Box(modifier = Modifier.weight(1f)) {
                    MiniDashboardCard("Banned Accounts", "182 nodes", "Malicious activity", Color(0xFFFF5252))
                }
            }
        }

        // Immediate Action Block
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Central Ban & Lock Command Terminal", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFF5252))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userIdToBlock,
                        onValueChange = { userIdToBlock = it },
                        label = { Text("Account / Device / IP ID", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF5252),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = blockReason,
                        onValueChange = { blockReason = it },
                        label = { Text("Breach Context / Penalty Duration", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF5252),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (userIdToBlock.isNotBlank()) {
                                onUpdateLogs("SECURITY_BAN: User node $userIdToBlock locked permanently. Reason: $blockReason. Device address blacklisted.")
                                userIdToBlock = ""
                                blockReason = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Apply Permanent Penalty", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Active alerts
        item {
            Text("Automated Security Threat Queue", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(alerts) { alert ->
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(alert.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFF5252).copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(alert.third, color = Color(0xFFFF5252), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(alert.second, color = Color.White.copy(0.5f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                alerts.remove(alert)
                                onUpdateLogs("SUCCESS: Confirmed threat neutralized for ${alert.first}.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            modifier = Modifier.weight(1f).height(30.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Acknowledge", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                alerts.remove(alert)
                                onUpdateLogs("DISMISSED: Security flag determined as false positive.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                            modifier = Modifier.weight(1f).height(30.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Dismiss", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 6. NOTIFICATION & ANNOUNCEMENT BULLETIN DISPATCHER
// ============================================================================
@Composable
fun BulletinsAlertsView(
    activeRole: String,
    onUpdateLogs: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTargetGroup by remember { mutableStateOf("All Platforms") }
    val targetGroups = listOf("All Platforms", "Verified VIPs", "Active Hosts", "Merchants")

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("bulletins_alerts_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Notification & Announcement Campaign Hub", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Publish system maintenance bulletins, launch customized reward events, and target push-notifications directly to specific roles.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Campaign Creator Form
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Compose Global Campaign Bulletin", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFBB86FC))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Select Target Audience Segment", color = Color.White.copy(0.5f), fontSize = 9.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        targetGroups.forEach { grp ->
                            val isSel = selectedTargetGroup == grp
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFFBB86FC).copy(0.2f) else Color.White.copy(0.05f))
                                    .border(1.dp, if (isSel) Color(0xFFBB86FC) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { selectedTargetGroup = grp }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(grp, color = if (isSel) Color(0xFFE1BEE7) else Color.White.copy(0.6f), fontSize = 9.sp)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Bulletin Subject / Header", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFBB86FC),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Publish detailed notification message body...", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFBB86FC),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onUpdateLogs("BULLETIN: Broadcasted bulletin campaign '$title' to target demographic '$selectedTargetGroup'.")
                                title = ""
                                description = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Dispatch Platform Announcement", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ============================================================================
// 7. PLATFORM SETTINGS, CONFIGURATIONS & FEATURE SWITCH CENTER
// ============================================================================
@Composable
fun PlatformSettingsView(
    activeRole: String,
    isVoiceRoomEnabled: Boolean,
    onVoiceRoomToggle: (Boolean) -> Unit,
    isPkEnabled: Boolean,
    onPkToggle: (Boolean) -> Unit,
    isGiftsEnabled: Boolean,
    onGiftsToggle: (Boolean) -> Unit,
    isWalletEnabled: Boolean,
    onWalletToggle: (Boolean) -> Unit,
    isMerchantEnabled: Boolean,
    onMerchantToggle: (Boolean) -> Unit,
    isFamilyEnabled: Boolean,
    onFamilyToggle: (Boolean) -> Unit,
    onUpdateLogs: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("platform_settings_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Centralized Settings & Switch Center", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Direct, instant configurations for all functional modules without software releases.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Live Switches List
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Instant Feature Toggles Center", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(10.dp))

                    FeatureSwitchRow("Voice Rooms Discovery Engine", isVoiceRoomEnabled) {
                        onVoiceRoomToggle(it)
                        onUpdateLogs("SETTINGS: Voice Rooms Discovery Engine set to ${if(it) "ENABLED" else "DISABLED"}")
                    }
                    FeatureSwitchRow("PK Battle Arena Modules", isPkEnabled) {
                        onPkToggle(it)
                        onUpdateLogs("SETTINGS: PK Arena set to ${if(it) "ENABLED" else "DISABLED"}")
                    }
                    FeatureSwitchRow("Interactive Gift Deliveries", isGiftsEnabled) {
                        onGiftsToggle(it)
                        onUpdateLogs("SETTINGS: Gifts engine set to ${if(it) "ENABLED" else "DISABLED"}")
                    }
                    FeatureSwitchRow("Wallet Recharge & Withdrawal", isWalletEnabled) {
                        onWalletToggle(it)
                        onUpdateLogs("SETTINGS: Main Wallets set to ${if(it) "ENABLED" else "DISABLED"}")
                    }
                    FeatureSwitchRow("Merchant Retail Operations", isMerchantEnabled) {
                        onMerchantToggle(it)
                        onUpdateLogs("SETTINGS: Merchants set to ${if(it) "ENABLED" else "DISABLED"}")
                    }
                    FeatureSwitchRow("Virtual Families & Events System", isFamilyEnabled) {
                        onFamilyToggle(it)
                        onUpdateLogs("SETTINGS: Families module set to ${if(it) "ENABLED" else "DISABLED"}")
                    }
                }
            }
        }

        // Dynamic System Backup Tool
        if (activeRole == "Platform Owner") {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Disaster Recovery & System Redundancy Backup", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Initiate structural database dumps and log checkpoints instantly.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                onUpdateLogs("SUCCESS: Scheduled cloud backup created. MD5 checksum validated.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Backup Systems Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 11.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF00E5FF),
                checkedTrackColor = Color(0xFF00E5FF).copy(0.3f)
            )
        )
    }
}

// ============================================================================
// 8. DISCOVERY, PROFILE & 15-MIC CONSOLE
// ============================================================================
@Composable
fun DiscoveryMicView(
    activeRole: String,
    mics: SnapshotStateList<String>,
    micLocked: SnapshotStateList<Boolean>,
    micMuted: SnapshotStateList<Boolean>,
    seatRequests: SnapshotStateList<String>,
    onUpdateLogs: (String) -> Unit
) {
    var selectedSeat by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("discovery_mic_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Voice Room Discovery & 15-Mic Console", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Trace live rooms, manage 15 speaker seats, unlock/lock microphones, and queue attendee hand-raises.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Seat Waiting Queue
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Speaker Seat Waiting Queue", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFD54F))
                    Spacer(modifier = Modifier.height(8.dp))
                    if (seatRequests.isEmpty()) {
                        Text("Queue empty. No attendee has raised hands currently.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    } else {
                        seatRequests.forEach { req ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(req, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            val emptyIndex = mics.indexOf("Empty")
                                            if (emptyIndex != -1) {
                                                mics[emptyIndex] = req
                                                seatRequests.remove(req)
                                                onUpdateLogs("SUCCESS: Approved speaker $req into microphone seat #${emptyIndex + 1}.")
                                            } else {
                                                onUpdateLogs("ALERT_REJECTED: All speaker microphone seats are currently fully occupied.")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                        modifier = Modifier.height(28.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Approve", color = Color.Black, fontSize = 9.sp)
                                    }
                                    Button(
                                        onClick = {
                                            seatRequests.remove(req)
                                            onUpdateLogs("SUCCESS: Declined microphone seat request for candidate $req.")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                        modifier = Modifier.height(28.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Decline", color = Color.White, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 15-Mic Grid
        item {
            Text("Interactive 15-Mic Speaker Board", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(280.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(15) { index ->
                    val occupant = mics[index]
                    val isLocked = micLocked[index]
                    val isMuted = micMuted[index]
                    val isSelected = selectedSeat == index

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF8E2DE2).copy(0.2f) else Color.White.copy(0.04f))
                            .border(1.dp, if (isSelected) Color(0xFFBB86FC) else Color.White.copy(0.1f), RoundedCornerShape(12.dp))
                            .clickable { selectedSeat = index }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isLocked) Icons.Default.Lock else if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (occupant != "Empty") Color(0xFF00E676) else Color.White.copy(0.3f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mic ${index + 1}", color = Color.White.copy(0.5f), fontSize = 9.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = occupant,
                                color = if (occupant != "Empty") Color.White else Color.White.copy(0.3f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Seat controls
        selectedSeat?.let { index ->
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Manage Speaker Seat #${index + 1} (${mics[index]})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFBB86FC))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    micLocked[index] = !micLocked[index]
                                    onUpdateLogs("SETTINGS: Speaker seat #${index + 1} lock status set to: ${micLocked[index]}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f)),
                                modifier = Modifier.weight(1f).height(32.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(if (micLocked[index]) "Unlock Mic" else "Lock Mic", color = Color.White, fontSize = 9.sp)
                            }

                            Button(
                                onClick = {
                                    micMuted[index] = !micMuted[index]
                                    onUpdateLogs("SETTINGS: Speaker seat #${index + 1} mute status set to: ${micMuted[index]}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f)),
                                modifier = Modifier.weight(1f).height(32.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(if (micMuted[index]) "Unmute Mic" else "Mute Mic", color = Color.White, fontSize = 9.sp)
                            }

                            if (mics[index] != "Empty" && index != 0) {
                                Button(
                                    onClick = {
                                        onUpdateLogs("SETTINGS: Evicted speaker '${mics[index]}' from microphone seat #${index + 1}.")
                                        mics[index] = "Empty"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                    modifier = Modifier.weight(1f).height(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Kick Speaker", color = Color.White, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 9. ROOM ROLES & PINNED ANNOUNCEMENTS (IK)
// ============================================================================
@Composable
fun RoomManagementView(
    activeRole: String,
    pinnedNotice: String,
    onUpdateNotice: (String) -> Unit,
    onUpdateLogs: (String) -> Unit
) {
    var textNoticeInput by remember { mutableStateOf(pinnedNotice) }
    var managerCandidateId by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("room_management_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Room Owner & Administration Dashboard", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Assign official Room Managers/Moderators, pin welcome announcements, configure audience whitelists, and review security limits.", fontSize = 11.sp, color = Color.White.copy(0.5f))
        }

        // Pinned Notice Panel
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Edit Welcome Bulletin Notice", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFBB86FC))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = textNoticeInput,
                        onValueChange = { textNoticeInput = it },
                        label = { Text("Pinned Message displayed at top", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFBB86FC),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onUpdateNotice(textNoticeInput)
                            onUpdateLogs("SUCCESS: Room Owner updated welcome notice to: '$textNoticeInput'")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Pin Bulletin Message", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Role Management Panel
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Promote / Appoint Official Staff", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = managerCandidateId,
                        onValueChange = { managerCandidateId = it },
                        label = { Text("Candidate User ID", fontSize = 11.sp, color = Color.White.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8E2DE2),
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (managerCandidateId.isNotBlank()) {
                                    onUpdateLogs("SUCCESS: Appointed user $managerCandidateId as official Room Manager.")
                                    managerCandidateId = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Make Manager", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (managerCandidateId.isNotBlank()) {
                                    onUpdateLogs("SUCCESS: Appointed user $managerCandidateId as official Room Moderator.")
                                    managerCandidateId = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Make Moderator", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// CORE CUSTOM HELPER WIDGETS
// ============================================================================
@Composable
fun MiniDashboardCard(title: String, value: String, subtitle: String, tintColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = tintColor, fontSize = 16.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = Color.White.copy(0.4f), fontSize = 9.sp)
        }
    }
}

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Data Models
data class Family(
    val id: String,
    val name: String,
    val logoEmoji: String,
    val level: Int,
    val owner: String,
    val leader: String,
    val totalMembers: Int,
    val onlineMembers: Int,
    val popularity: Int,
    val points: Int,
    val diamonds: Int,
    val ranking: Int,
    val creationDate: String,
    val description: String,
    val language: String,
    val country: String,
    val isOfficial: Boolean = false,
    val isSuspended: Boolean = false
)

data class FamilyMember(
    val id: String,
    val name: String,
    val role: String,
    val level: Int,
    val joinedDate: String,
    val isOnline: Boolean
)

data class FamilyTask(
    val id: String,
    val title: String,
    val description: String,
    val progress: Int,
    val maxProgress: Int,
    val pointsReward: Int,
    val coinsReward: Int,
    val isClaimed: Boolean
)

data class FamilyAuditLog(
    val action: String,
    val details: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilySystemScreen(viewModel: ConnectLiveViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- State Managers ---
    var selectedTab by remember { mutableStateOf("dashboard") } // dashboard, members, tasks, rankings, pk, store, owner_panel, logs
    val showCreateDialog = remember { mutableStateOf(false) }

    // Family State
    val myFamily = remember {
        mutableStateOf<Family?>(
            Family(
                id = "FAM_98741",
                name = "Apex Legends",
                logoEmoji = "🐉",
                level = 3,
                owner = "Alex Mercer",
                leader = "Alex Mercer",
                totalMembers = 48,
                onlineMembers = 12,
                popularity = 25400,
                points = 1850,
                diamonds = 6700,
                ranking = 4,
                creationDate = "2026-03-12",
                description = "We conquer arenas together! Join for active PKs, parties and strategic alliances.",
                language = "English",
                country = "United States",
                isOfficial = true
            )
        )
    }

    val familyList = remember {
        mutableStateListOf(
            Family("FAM_001", "Cosmos Syndicate", "🌟", 5, "Alpha_One", "Omega", 120, 32, 98000, 5200, 14200, 1, "2025-12-01", "Official elite syndicate.", "English", "United Kingdom", true),
            Family("FAM_002", "Titanium Titans", "🛡️", 4, "Ares", "Diana", 88, 20, 56000, 3100, 9500, 2, "2026-01-15", "Steadfast defense and PK dominance.", "Spanish", "Spain"),
            Family("FAM_003", "Phoenix Rising", "🔥", 4, "Ignis", "Blaze", 94, 25, 49000, 2900, 8100, 3, "2026-02-10", "Rising from the ashes in ultimate victory.", "Japanese", "Japan"),
            Family("FAM_004", "Apex Legends", "🐉", 3, "Alex Mercer", "Alex Mercer", 48, 12, 25400, 1850, 6700, 4, "2026-03-12", "We conquer arenas together! Join for active PKs, parties.", "English", "United States", true),
            Family("FAM_005", "Cyber Ronins", "⚔️", 2, "Kenshin", "Saito", 35, 5, 12000, 900, 2200, 8, "2026-04-18", "No masters, only the blade and cyber highway.", "English", "Japan")
        )
    }

    val members = remember {
        mutableStateListOf(
            FamilyMember("1001", "Alex Mercer", "Family Owner", 54, "2026-03-12", true),
            FamilyMember("1002", "Elena Rostova", "Family Leader", 48, "2026-03-13", true),
            FamilyMember("1003", "Marcus Vance", "Family Co-Leader", 45, "2026-03-15", false),
            FamilyMember("1004", "Chloe Zhang", "Family Elder", 39, "2026-03-20", true),
            FamilyMember("1005", "Sasha Grey", "Family Manager", 35, "2026-03-25", true),
            FamilyMember("1006", "David Beckham", "Family Member", 32, "2026-04-01", false),
            FamilyMember("1007", "Irina Shayk", "Family Member", 28, "2026-04-05", true),
            FamilyMember("1008", "Zayn Malik", "Family Member", 25, "2026-04-10", false)
        )
    }

    val tasks = remember {
        mutableStateListOf(
            FamilyTask("T_01", "Daily Family Roll-Call", "Log in and check in to the family center.", 1, 1, 50, 10, true),
            FamilyTask("T_02", "Voice Room Union", "Join and spend 15 minutes inside private family voice room.", 8, 15, 150, 30, false),
            FamilyTask("T_03", "Ecosystem Support", "Send any gift inside any family hosted PK Battle.", 0, 1, 200, 50, false),
            FamilyTask("T_04", "Recruit Ambassadors", "Invite 2 new friends using your Family Code or Referral link.", 1, 2, 250, 100, false),
            FamilyTask("T_05", "Victory Anthem", "Complete 1 Family vs Family PK War Match.", 0, 1, 300, 150, false)
        )
    }

    val auditLogs = remember {
        mutableStateListOf(
            FamilyAuditLog("FAMILY_CREATE", "Family 'Apex Legends' created successfully with ID FAM_98741.", "2026-03-12 10:00"),
            FamilyAuditLog("ROLE_PROMOTE", "Elena Rostova promoted to Family Leader by Alex Mercer.", "2026-03-13 14:30"),
            FamilyAuditLog("MEMBER_JOIN", "David Beckham joined the family via invitation code '100258'.", "2026-04-01 09:15"),
            FamilyAuditLog("PK_VICTORY", "Won family war match against FAM_005 (+500 Points, +1200 Diamonds).", "2026-07-20 21:40")
        )
    }

    // Interactive custom role names and permissions state (for Platform customization rule)
    var ownerRoleName by remember { mutableStateOf("Family Owner") }
    var leaderRoleName by remember { mutableStateOf("Family Leader") }
    var coLeaderRoleName by remember { mutableStateOf("Family Co-Leader") }
    var elderRoleName by remember { mutableStateOf("Family Elder") }
    var memberRoleName by remember { mutableStateOf("Family Member") }

    // Join approval state
    var joinApprovalEnabled by remember { mutableStateOf(true) }
    var minUserLevelRequired by remember { mutableStateOf("Level 10") }
    var minVipLevelRequired by remember { mutableStateOf("VIP 0") }

    // Join Requests simulation
    val joinRequests = remember {
        mutableStateListOf(
            Pair("10099", "Sarah Connor (LvL 18)"),
            Pair("10085", "John Connor (LvL 22)")
        )
    }

    // Chat Simulation States
    val chatMessages = remember {
        mutableStateListOf(
            Triple("Alex Mercer", "Hey team! Ready for tonight's Family PK battle? 🔥", "14:10"),
            Triple("Elena Rostova", "Absolutely! Room background is setup, let's win this time!", "14:11"),
            Triple("Chloe Zhang", "I'll join with some voice cheers. Sending power! ⚔️", "14:13")
        )
    }
    var typedMessage by remember { mutableStateOf("") }

    // PK Simulation States
    var pkActive by remember { mutableStateOf(false) }
    var pkTimeLeft by remember { mutableStateOf(120) }
    var ourPkScore by remember { mutableStateOf(14500) }
    var opponentPkScore by remember { mutableStateOf(12100) }
    var pkOpponentName by remember { mutableStateOf("Titanium Titans") }

    // Store Unlock Items
    val unlockedItems = remember { mutableStateListOf<String>() }

    // Tick the PK Battle countdown
    LaunchedEffect(pkActive) {
        if (pkActive) {
            while (pkTimeLeft > 0) {
                delay(1000)
                pkTimeLeft--
                ourPkScore += (50..300).random()
                opponentPkScore += (40..280).random()
                if (pkTimeLeft == 0) {
                    pkActive = false
                    val win = ourPkScore > opponentPkScore
                    val pointsReward = if (win) 300 else 50
                    myFamily.value = myFamily.value?.let {
                        it.copy(
                            points = it.points + pointsReward,
                            popularity = it.popularity + 1500
                        )
                    }
                    auditLogs.add(0, FamilyAuditLog("PK_MATCH", "Family PK Match against $pkOpponentName completed. ${if (win) "VICTORY" else "DEFEAT"}! Score: $ourPkScore vs $opponentPkScore. Received +$pointsReward points.", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())))
                    Toast.makeText(context, if (win) "Victory! You won the PK Match!" else "Match finished!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Main Scaffold Layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Ecosystem Family Hub", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo("profile") },
                        modifier = Modifier.testTag("family_system_back")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog.value = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Family", tint = Color(0xFF00E5FF))
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
                // --- Top Category Selection Tabs ---
                ScrollableTabRow(
                    selectedTabIndex = listOf("dashboard", "members", "tasks", "rankings", "pk", "store", "owner_panel", "logs").indexOf(selectedTab),
                    containerColor = Color(0xFF0C071A),
                    contentColor = Color.White,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[listOf("dashboard", "members", "tasks", "rankings", "pk", "store", "owner_panel", "logs").indexOf(selectedTab)]),
                            color = Color(0xFF00E5FF)
                        )
                    }
                ) {
                    val tabNames = listOf(
                        "dashboard" to "Dashboard",
                        "members" to "Members",
                        "tasks" to "Tasks",
                        "rankings" to "Rankings",
                        "pk" to "Family PK",
                        "store" to "Store",
                        "owner_panel" to "Owner Control",
                        "logs" to "Audit Logs"
                    )
                    tabNames.forEach { (id, label) ->
                        Tab(
                            selected = selectedTab == id,
                            onClick = { selectedTab = id },
                            text = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("family_tab_$id")
                        )
                    }
                }

                // --- Main Content Segments ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(14.dp)
                ) {
                    when (selectedTab) {
                        "dashboard" -> DashboardSegment(
                            family = myFamily.value,
                            joinApprovalEnabled = joinApprovalEnabled,
                            minUserLevelRequired = minUserLevelRequired,
                            minVipLevelRequired = minVipLevelRequired,
                            joinRequests = joinRequests,
                            onAcceptJoin = { id, name ->
                                joinRequests.removeIf { it.first == id }
                                members.add(FamilyMember(id, name.substringBefore(" ("), "Family Member", 15, "2026-07-21", true))
                                myFamily.value = myFamily.value?.let { it.copy(totalMembers = it.totalMembers + 1) }
                                auditLogs.add(0, FamilyAuditLog("MEMBER_APPROVE", "Approved $name join request.", "Just Now"))
                                Toast.makeText(context, "$name approved!", Toast.LENGTH_SHORT).show()
                            },
                            onRejectJoin = { id, name ->
                                joinRequests.removeIf { it.first == id }
                                auditLogs.add(0, FamilyAuditLog("MEMBER_REJECT", "Rejected $name join request.", "Just Now"))
                                Toast.makeText(context, "$name rejected.", Toast.LENGTH_SHORT).show()
                            }
                        )
                        "members" -> MembersSegment(
                            members = members,
                            ownerRoleName = ownerRoleName,
                            leaderRoleName = leaderRoleName,
                            coLeaderRoleName = coLeaderRoleName,
                            elderRoleName = elderRoleName,
                            memberRoleName = memberRoleName,
                            onPromote = { member ->
                                val oldRole = member.role
                                val newRole = when (oldRole) {
                                    memberRoleName -> elderRoleName
                                    elderRoleName -> coLeaderRoleName
                                    coLeaderRoleName -> leaderRoleName
                                    else -> oldRole
                                }
                                val idx = members.indexOf(member)
                                if (idx != -1 && newRole != oldRole) {
                                    members[idx] = member.copy(role = newRole)
                                    auditLogs.add(0, FamilyAuditLog("MEMBER_PROMOTE", "Promoted ${member.name} from $oldRole to $newRole.", "Just Now"))
                                    Toast.makeText(context, "Promoted ${member.name} to $newRole!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDemote = { member ->
                                val oldRole = member.role
                                val newRole = when (oldRole) {
                                    leaderRoleName -> coLeaderRoleName
                                    coLeaderRoleName -> elderRoleName
                                    elderRoleName -> memberRoleName
                                    else -> oldRole
                                }
                                val idx = members.indexOf(member)
                                if (idx != -1 && newRole != oldRole) {
                                    members[idx] = member.copy(role = newRole)
                                    auditLogs.add(0, FamilyAuditLog("MEMBER_DEMOTE", "Demoted ${member.name} from $oldRole to $newRole.", "Just Now"))
                                    Toast.makeText(context, "Demoted ${member.name} to $newRole.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onRemove = { member ->
                                members.remove(member)
                                myFamily.value = myFamily.value?.let { it.copy(totalMembers = it.totalMembers - 1) }
                                auditLogs.add(0, FamilyAuditLog("MEMBER_REMOVE", "Removed ${member.name} from the family.", "Just Now"))
                                Toast.makeText(context, "Removed ${member.name}.", Toast.LENGTH_SHORT).show()
                            }
                        )
                        "tasks" -> TasksSegment(
                            tasks = tasks,
                            onClaim = { task ->
                                val idx = tasks.indexOf(task)
                                if (idx != -1) {
                                    tasks[idx] = task.copy(isClaimed = true)
                                    viewModel.buyCoins(task.coinsReward, 0.0) // Grant coins using view model directly
                                    myFamily.value = myFamily.value?.let {
                                        it.copy(
                                            points = it.points + task.pointsReward,
                                            popularity = it.popularity + (task.pointsReward * 10)
                                        )
                                    }
                                    auditLogs.add(0, FamilyAuditLog("TASK_CLAIM", "Claimed rewards for '${task.title}' (+${task.coinsReward} Coins, +${task.pointsReward} Family Points).", "Just Now"))
                                    Toast.makeText(context, "Claimed rewards! +${task.coinsReward} Coins and +${task.pointsReward} Family Points!", Toast.LENGTH_LONG).show()
                                }
                            }
                        )
                        "rankings" -> RankingsSegment(familyList = familyList)
                        "pk" -> PkSegment(
                            pkActive = pkActive,
                            pkTimeLeft = pkTimeLeft,
                            ourScore = ourPkScore,
                            opponentScore = opponentPkScore,
                            opponentName = pkOpponentName,
                            onStartPk = {
                                pkActive = true
                                pkTimeLeft = 120
                                ourPkScore = 14500
                                opponentPkScore = 12100
                                pkOpponentName = listOf("Titanium Titans", "Phoenix Rising", "Cosmos Syndicate").random()
                                auditLogs.add(0, FamilyAuditLog("PK_START", "Initiated Family PK match against $pkOpponentName.", "Just Now"))
                                Toast.makeText(context, "Family PK Battle Started! Send gifts to support!", Toast.LENGTH_SHORT).show()
                            }
                        )
                        "store" -> StoreSegment(
                            familyPoints = myFamily.value?.points ?: 0,
                            unlockedItems = unlockedItems,
                            onUnlock = { item, cost ->
                                val currentFam = myFamily.value
                                if (currentFam != null && currentFam.points >= cost) {
                                    myFamily.value = currentFam.copy(points = currentFam.points - cost)
                                    unlockedItems.add(item)
                                    auditLogs.add(0, FamilyAuditLog("STORE_UNLOCK", "Unlocked item '$item' for $cost points.", "Just Now"))
                                    Toast.makeText(context, "$item unlocked successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Insufficient Family Points!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        "owner_panel" -> OwnerPanelSegment(
                            ownerRoleName = ownerRoleName,
                            leaderRoleName = leaderRoleName,
                            coLeaderRoleName = coLeaderRoleName,
                            elderRoleName = elderRoleName,
                            memberRoleName = memberRoleName,
                            joinApprovalEnabled = joinApprovalEnabled,
                            minUserLevelRequired = minUserLevelRequired,
                            minVipLevelRequired = minVipLevelRequired,
                            onSaveRoles = { o, l, co, el, mb ->
                                ownerRoleName = o
                                leaderRoleName = l
                                coLeaderRoleName = co
                                elderRoleName = el
                                memberRoleName = mb
                                auditLogs.add(0, FamilyAuditLog("CONFIG_ROLES", "Custom Role titles updated.", "Just Now"))
                                Toast.makeText(context, "Role Customizations Applied!", Toast.LENGTH_SHORT).show()
                            },
                            onSaveJoinRules = { approval, userLv, vipLv ->
                                joinApprovalEnabled = approval
                                minUserLevelRequired = userLv
                                minVipLevelRequired = vipLv
                                auditLogs.add(0, FamilyAuditLog("CONFIG_RULES", "Join constraints updated: Approval=$approval, UserLv=$userLv, VipLv=$vipLv.", "Just Now"))
                                Toast.makeText(context, "Join settings updated!", Toast.LENGTH_SHORT).show()
                            }
                        )
                        "logs" -> LogsSegment(auditLogs = auditLogs)
                    }
                }

                // --- Live Interactive Chat Simulation Panel at Bottom ---
                AnimatedVisibility(visible = selectedTab == "dashboard") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1338)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00E676))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Family Room Live Voice Chat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Text("Join Voice", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                                    viewModel.navigateTo("voice_room")
                                })
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            // Chat scroll
                            Box(modifier = Modifier.height(90.dp)) {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(chatMessages) { (sender, msg, time) ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                "$sender: ",
                                                color = if (sender == "Alex Mercer") Color(0xFF00E5FF) else Color(0xFFE040FB),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                            Text(msg, color = Color.White.copy(0.85f), fontSize = 11.sp, modifier = Modifier.weight(1f))
                                            Text(time, color = Color.White.copy(0.4f), fontSize = 9.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextField(
                                    value = typedMessage,
                                    onValueChange = { typedMessage = it },
                                    placeholder = { Text("Send family message...", color = Color.White.copy(0.4f), fontSize = 11.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("family_chat_input"),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White.copy(0.04f),
                                        unfocusedContainerColor = Color.White.copy(0.04f),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        if (typedMessage.isNotBlank()) {
                                            chatMessages.add(Triple("Alex Mercer", typedMessage, "Just Now"))
                                            typedMessage = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E5FF))
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Create Family Dialog ---
    if (showCreateDialog.value) {
        var createName by remember { mutableStateOf("") }
        var createLogo by remember { mutableStateOf("🐉") }
        var createDesc by remember { mutableStateOf("") }
        var createLang by remember { mutableStateOf("English") }
        var createCountry by remember { mutableStateOf("United States") }

        AlertDialog(
            onDismissRequest = { showCreateDialog.value = false },
            title = { Text("Forge New Syndicate Family", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            containerColor = Color(0xFF160E2A),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = createName,
                        onValueChange = { createName = it },
                        label = { Text("Family Name", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = createLogo,
                        onValueChange = { createLogo = it },
                        label = { Text("Family Emoji Logo", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = createDesc,
                        onValueChange = { createDesc = it },
                        label = { Text("Family Mission / Description", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = createLang,
                            onValueChange = { createLang = it },
                            label = { Text("Language", color = Color.White.copy(0.6f)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = createCountry,
                            onValueChange = { createCountry = it },
                            label = { Text("Country", color = Color.White.copy(0.6f)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (createName.isNotBlank()) {
                            val newFam = Family(
                                id = "FAM_${(10000..99999).random()}",
                                name = createName,
                                logoEmoji = createLogo,
                                level = 1,
                                owner = "Alex Mercer",
                                leader = "Alex Mercer",
                                totalMembers = 1,
                                onlineMembers = 1,
                                popularity = 100,
                                points = 500,
                                diamonds = 0,
                                ranking = familyList.size + 1,
                                creationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                description = createDesc,
                                language = createLang,
                                country = createCountry
                            )
                            familyList.add(newFam)
                            myFamily.value = newFam
                            auditLogs.add(0, FamilyAuditLog("FAMILY_CREATE", "Family '${createName}' forged successfully by Alex Mercer.", "Just Now"))
                            Toast.makeText(context, "Family ${createName} created successfully!", Toast.LENGTH_LONG).show()
                            showCreateDialog.value = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("Forge Family", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog.value = false }) {
                    Text("Cancel", color = Color.White.copy(0.6f))
                }
            }
        )
    }
}

// =================== SEGMENTS ===================

@Composable
fun DashboardSegment(
    family: Family?,
    joinApprovalEnabled: Boolean,
    minUserLevelRequired: String,
    minVipLevelRequired: String,
    joinRequests: List<Pair<String, String>>,
    onAcceptJoin: (String, String) -> Unit,
    onRejectJoin: (String, String) -> Unit
) {
    if (family == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White.copy(0.2f), modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(14.dp))
            Text("You belong to no Family yet.", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Click '+' on the top-right to forge a new family!", color = Color.White.copy(0.5f), fontSize = 12.sp)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(0.2f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(0.06f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(family.logoEmoji, fontSize = 36.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(family.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                if (family.isOfficial) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Verified, contentDescription = "Official", tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                                }
                            }
                            Text("ID: ${family.id} • Founded: ${family.creationDate}", color = Color.White.copy(0.5f), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF00E5FF).copy(0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Level ${family.level}", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Popularity: ${family.popularity} pts", color = Color(0xFFE040FB), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(family.description, color = Color.White.copy(0.8f), fontSize = 12.sp)

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(0.1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("FOUNDER", color = Color.White.copy(0.4f), fontSize = 10.sp)
                            Text(family.owner, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text("COUNTRY", color = Color.White.copy(0.4f), fontSize = 10.sp)
                            Text(family.country, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text("LANGUAGE", color = Color.White.copy(0.4f), fontSize = 10.sp)
                            Text(family.language, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Stats Cards Grid
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("FAMILY COINS & POINTS", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Text("${family.points} Pts", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("DIAMOND EARNINGS", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Text("💎 ${family.diamonds}", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("MEMBERS CAPACITY", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Text("${family.totalMembers} / 50 (Max)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("GLOBAL RANKING", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Text("#${family.ranking} of all Families", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Active join approvals
        if (joinApprovalEnabled && joinRequests.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E131E)),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Pending Join Requests (${joinRequests.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        joinRequests.forEach { (reqId, reqName) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(reqName, color = Color.White, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { onAcceptJoin(reqId, reqName) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Accept", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { onRejectJoin(reqId, reqName) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Reject", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
fun MembersSegment(
    members: List<FamilyMember>,
    ownerRoleName: String,
    leaderRoleName: String,
    coLeaderRoleName: String,
    elderRoleName: String,
    memberRoleName: String,
    onPromote: (FamilyMember) -> Unit,
    onDemote: (FamilyMember) -> Unit,
    onRemove: (FamilyMember) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Ecosystem Rosters (${members.size} total)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(members) { member ->
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (member.isOnline) Color(0xFF00E676).copy(0.2f) else Color.White.copy(0.06f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (member.isOnline) "🟢" else "⚫", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(member.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(0.08f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("LvL ${member.level}", color = Color.White.copy(0.6f), fontSize = 8.sp)
                                }
                            }
                            Text("Role: ${member.role} • Joined: ${member.joinedDate}", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        }
                    }

                    // Administrative promotions/demotions
                    if (member.name != "Alex Mercer") { // Do not manage the owner
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { onPromote(member) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Promote", tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { onDemote(member) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = "Demote", tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { onRemove(member) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TasksSegment(tasks: List<FamilyTask>, onClaim: (FamilyTask) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Complete Tasks to Upgrade Family Power", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(tasks) { task ->
            val completed = task.progress >= task.maxProgress
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (task.isClaimed) Color.White.copy(0.02f) else Color.White.copy(0.04f)),
                border = if (completed && !task.isClaimed) BorderStroke(1.dp, Color(0xFF00E5FF).copy(0.3f)) else null
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title, color = if (task.isClaimed) Color.White.copy(0.5f) else Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(task.description, color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = task.progress.toFloat() / task.maxProgress.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFF00E5FF),
                            trackColor = Color.White.copy(0.08f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Progress: ${task.progress} / ${task.maxProgress}", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("+${task.coinsReward} Coins", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("+${task.pointsReward} Points", color = Color(0xFFE040FB), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = { onClaim(task) },
                            enabled = completed && !task.isClaimed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00E676),
                                disabledContainerColor = Color.White.copy(0.06f)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = if (task.isClaimed) "Claimed" else if (completed) "Claim" else "Active",
                                color = if (completed && !task.isClaimed) Color.Black else Color.White.copy(0.4f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankingsSegment(familyList: List<Family>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Global League Leaderboards", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(familyList.sortedBy { it.ranking }) { family ->
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when (family.ranking) {
                                        1 -> Color(0xFFFFD700)
                                        2 -> Color(0xFFC0C0C0)
                                        3 -> Color(0xFFCD7F32)
                                        else -> Color.White.copy(0.08f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = family.ranking.toString(),
                                color = if (family.ranking <= 3) Color.Black else Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(family.logoEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(family.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text("Members: ${family.totalMembers}/50 • Level ${family.level}", color = Color.White.copy(0.5f), fontSize = 11.sp)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${family.popularity} pts", color = Color(0xFFE040FB), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Popularity", color = Color.White.copy(0.4f), fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PkSegment(
    pkActive: Boolean,
    pkTimeLeft: Int,
    ourScore: Int,
    opponentScore: Int,
    opponentName: String,
    onStartPk: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Family vs Family PK Battle Arena", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text("Rally your family members and dominate other syndicates to secure rewards!", color = Color.White.copy(0.5f), textAlign = TextAlign.Center, fontSize = 11.sp)

        if (!pkActive) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE040FB).copy(0.1f))
                    .border(2.dp, Color(0xFFE040FB), CircleShape)
                    .clickable { onStartPk() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFFE040FB), modifier = Modifier.size(48.dp))
                    Text("START PK MATCH", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }
        } else {
            // Battle Scoreboard
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("WAR TIME REMAINING: ${pkTimeLeft}s", color = Color(0xFFFF5252), fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Our family
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🐉 Apex Legends", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(ourScore.toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                        }

                        Text("VS", color = Color.White.copy(0.4f), fontWeight = FontWeight.Black, fontSize = 16.sp)

                        // Opponent family
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛡️ $opponentName", color = Color(0xFFE040FB), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(opponentScore.toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar representing relative strength
                    val total = ourScore + opponentScore
                    val ratio = if (total > 0) ourScore.toFloat() / total.toFloat() else 0.5f

                    LinearProgressIndicator(
                        progress = ratio,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF00E5FF),
                        trackColor = Color(0xFFE040FB)
                    )
                }
            }
        }
    }
}

@Composable
fun StoreSegment(
    familyPoints: Int,
    unlockedItems: List<String>,
    onUnlock: (String, Int) -> Unit
) {
    val storeItems = listOf(
        Triple("ic_badge_01", "Dragon Elite Badge", 200),
        Triple("ic_frame_02", "Neon Galaxy Avatar Frame", 500),
        Triple("ic_bg_03", "Golden Glow Entrance Aura", 800),
        Triple("ic_chat_04", "Cosmos Custom Chat Bubble", 1200)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ecosystem Store", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Your Balance: $familyPoints pts", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(storeItems) { (id, name, cost) ->
                val alreadyUnlocked = unlockedItems.contains(name)
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E5FF).copy(0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Cost: $cost Points", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = { onUnlock(name, cost) },
                            enabled = !alreadyUnlocked && familyPoints >= cost,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00E5FF),
                                disabledContainerColor = Color.White.copy(0.06f)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = if (alreadyUnlocked) "Unlocked" else "Purchase",
                                color = if (alreadyUnlocked) Color.White.copy(0.4f) else Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OwnerPanelSegment(
    ownerRoleName: String,
    leaderRoleName: String,
    coLeaderRoleName: String,
    elderRoleName: String,
    memberRoleName: String,
    joinApprovalEnabled: Boolean,
    minUserLevelRequired: String,
    minVipLevelRequired: String,
    onSaveRoles: (String, String, String, String, String) -> Unit,
    onSaveJoinRules: (Boolean, String, String) -> Unit
) {
    // Local edits
    var ownerInput by remember { mutableStateOf(ownerRoleName) }
    var leaderInput by remember { mutableStateOf(leaderRoleName) }
    var coLeaderInput by remember { mutableStateOf(coLeaderRoleName) }
    var elderInput by remember { mutableStateOf(elderRoleName) }
    var memberInput by remember { mutableStateOf(memberRoleName) }

    var approvalInput by remember { mutableStateOf(joinApprovalEnabled) }
    var userLvInput by remember { mutableStateOf(minUserLevelRequired) }
    var vipLvInput by remember { mutableStateOf(minVipLevelRequired) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Custom Role Name Overrides", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ownerInput,
                        onValueChange = { ownerInput = it },
                        label = { Text("Owner Role Override", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = leaderInput,
                        onValueChange = { leaderInput = it },
                        label = { Text("Leader Role Override", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = coLeaderInput,
                        onValueChange = { coLeaderInput = it },
                        label = { Text("Co-Leader Override", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = elderInput,
                        onValueChange = { elderInput = it },
                        label = { Text("Elder Override", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = memberInput,
                        onValueChange = { memberInput = it },
                        label = { Text("Member Override", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { onSaveRoles(ownerInput, leaderInput, coLeaderInput, elderInput, memberInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Role Brand Customizations", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Ecosystem Join Constraints Configuration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Require Approval to Join", color = Color.White, fontSize = 12.sp)
                        Switch(
                            checked = approvalInput,
                            onCheckedChange = { approvalInput = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                        )
                    }

                    OutlinedTextField(
                        value = userLvInput,
                        onValueChange = { userLvInput = it },
                        label = { Text("Minimum User Level Requirement", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = vipLvInput,
                        onValueChange = { vipLvInput = it },
                        label = { Text("Minimum VIP Membership Level Required", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { onSaveJoinRules(approvalInput, userLvInput, vipLvInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Join Restriction Criteria", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LogsSegment(auditLogs: List<FamilyAuditLog>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Permanent Family Operations Ledger", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(auditLogs) { log ->
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
                                .background(Color(0xFF00E5FF).copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(log.action, color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                        Text(log.timestamp, color = Color.White.copy(0.4f), fontSize = 9.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(log.details, color = Color.White.copy(0.8f), fontSize = 11.sp)
                }
            }
        }
    }
}

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import java.text.SimpleDateFormat
import java.util.*

// Recruitment Models
data class HostApplication(
    val id: String,
    val userId: String,
    val nickname: String,
    val country: String,
    val language: String,
    val experience: String,
    val agencyId: String,
    val status: String // Pending, Under Review, Approved, Rejected
)

data class HostInvitation(
    val inviteId: String,
    val inviterId: String,
    val invitedId: String,
    val type: String, // Host, Agency Owner
    val date: String,
    val status: String // Pending, Accepted, Rejected
)

data class HostPerformance(
    val id: String,
    val name: String,
    val onlineHours: Int,
    val diamondsEarned: Int,
    val attendanceRate: Int,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostRecruitmentScreen(viewModel: ConnectLiveViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- Tab managers ---
    var selectedTab by remember { mutableStateOf("apply") } // apply, center, agency_mgt, hierarchy, targets, audit

    // Host Applications List (Simulated)
    val applications = remember {
        mutableStateListOf(
            HostApplication("APP_001", "100258741", "Zayn Malik", "USA", "English", "2 years streaming on Twitch", "AGENCY_77", "Approved"),
            HostApplication("APP_002", "100369852", "Selena Gomez", "Canada", "English", "Experienced Tiktok Creator", "AGENCY_77", "Under Review"),
            HostApplication("APP_003", "100147258", "Sonia Malik", "Pakistan", "Urdu", "New to live streaming", "", "Pending")
        )
    }

    // Invitations List (Simulated)
    val invitations = remember {
        mutableStateListOf(
            HostInvitation("INV_1001", "1001", "100458712", "Host", "2026-07-18", "Accepted"),
            HostInvitation("INV_1002", "1001", "100589632", "Host", "2026-07-20", "Pending")
        )
    }

    // Host Performance Data (Simulated)
    val performanceRoster = remember {
        mutableStateListOf(
            HostPerformance("H_01", "Zayn Malik", 72, 14200, 95, "Active"),
            HostPerformance("H_02", "Irina Shayk", 48, 8500, 88, "Active"),
            HostPerformance("H_03", "David Beckham", 25, 3100, 75, "Active"),
            HostPerformance("H_04", "Chloe Zhang", 12, 1100, 50, "Inactive")
        )
    }

    // Interactive form variables
    var nicknameInput by remember { mutableStateOf("") }
    var countryInput by remember { mutableStateOf("Pakistan") }
    var languageInput by remember { mutableStateOf("Urdu") }
    var experienceInput by remember { mutableStateOf("") }
    var agencyIdInput by remember { mutableStateOf("") }

    // Invite ID variables
    var inviteUserIdInput by remember { mutableStateOf("") }
    var inviteTypeInput by remember { mutableStateOf("Host") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Lumexa Talent Recruitment", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo("profile") },
                        modifier = Modifier.testTag("host_recruitment_back")
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
                // Category tabs
                ScrollableTabRow(
                    selectedTabIndex = listOf("apply", "center", "agency_mgt", "hierarchy", "targets", "audit").indexOf(selectedTab),
                    containerColor = Color(0xFF0C071A),
                    contentColor = Color.White,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[listOf("apply", "center", "agency_mgt", "hierarchy", "targets", "audit").indexOf(selectedTab)]),
                            color = Color(0xFF00E676)
                        )
                    }
                ) {
                    val tabNames = listOf(
                        "apply" to "Become Host",
                        "center" to "Talent Center",
                        "agency_mgt" to "Agency Roster",
                        "hierarchy" to "Hiring Managers",
                        "targets" to "Targets",
                        "audit" to "Hiring Audit"
                    )
                    tabNames.forEach { (id, label) ->
                        Tab(
                            selected = selectedTab == id,
                            onClick = { selectedTab = id },
                            text = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("recruitment_tab_$id")
                        )
                    }
                }

                // Inner content layouts
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(14.dp)
                ) {
                    when (selectedTab) {
                        "apply" -> ApplySegment(
                            nickname = nicknameInput,
                            country = countryInput,
                            language = languageInput,
                            experience = experienceInput,
                            agencyId = agencyIdInput,
                            onNicknameChange = { nicknameInput = it },
                            onCountryChange = { countryInput = it },
                            onLanguageChange = { languageInput = it },
                            onExperienceChange = { experienceInput = it },
                            onAgencyIdChange = { agencyIdInput = it },
                            onSubmit = {
                                if (nicknameInput.isNotBlank()) {
                                    val newApp = HostApplication(
                                        id = "APP_${(100..999).random()}",
                                        userId = "100258741", // Me User ID
                                        nickname = nicknameInput,
                                        country = countryInput,
                                        language = languageInput,
                                        experience = experienceInput,
                                        agencyId = agencyIdInput,
                                        status = "Pending"
                                    )
                                    applications.add(0, newApp)
                                    nicknameInput = ""
                                    experienceInput = ""
                                    agencyIdInput = ""
                                    Toast.makeText(context, "Application submitted! Reviewers will notify you.", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Please enter your Nickname!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            applications = applications
                        )
                        "center" -> CenterSegment(
                            applications = applications,
                            invitations = invitations,
                            inviteUserId = inviteUserIdInput,
                            inviteType = inviteTypeInput,
                            onInviteUserIdChange = { inviteUserIdInput = it },
                            onInviteTypeChange = { inviteTypeInput = it },
                            onSendInvite = {
                                if (inviteUserIdInput.isNotBlank()) {
                                    val newInv = HostInvitation(
                                        inviteId = "INV_${(1000..9999).random()}",
                                        inviterId = "100258741", // My ID
                                        invitedId = inviteUserIdInput,
                                        type = inviteTypeInput,
                                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                        status = "Pending"
                                    )
                                    invitations.add(0, newInv)
                                    inviteUserIdInput = ""
                                    Toast.makeText(context, "Invitation Dispatched successfully!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Enter target User ID!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onApprove = { app ->
                                val idx = applications.indexOf(app)
                                if (idx != -1) {
                                    applications[idx] = app.copy(status = "Approved")
                                    performanceRoster.add(HostPerformance(app.userId, app.nickname, 0, 0, 100, "Active"))
                                    Toast.makeText(context, "${app.nickname} Application Approved!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onReject = { app ->
                                val idx = applications.indexOf(app)
                                if (idx != -1) {
                                    applications[idx] = app.copy(status = "Rejected")
                                    Toast.makeText(context, "${app.nickname} Application Rejected.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        "agency_mgt" -> AgencyManagementSegment(roster = performanceRoster)
                        "hierarchy" -> HierarchySegment()
                        "targets" -> TargetsSegment()
                        "audit" -> AuditSegment()
                    }
                }
            }
        }
    }
}

// =================== SEGMENTS ===================

@Composable
fun ApplySegment(
    nickname: String,
    country: String,
    language: String,
    experience: String,
    agencyId: String,
    onNicknameChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onExperienceChange: (String) -> Unit,
    onAgencyIdChange: (String) -> Unit,
    onSubmit: () -> Unit,
    applications: List<HostApplication>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Become an Official Lumexa Host", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Complete this wizard to unlock professional streaming keys and earnings commissions.", color = Color.White.copy(0.5f), fontSize = 11.sp)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { onNicknameChange(it) },
                        label = { Text("Talent Nickname", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = country,
                            onValueChange = { onCountryChange(it) },
                            label = { Text("Country", color = Color.White.copy(0.6f)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = language,
                            onValueChange = { onLanguageChange(it) },
                            label = { Text("Main Language", color = Color.White.copy(0.6f)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }

                    OutlinedTextField(
                        value = experience,
                        onValueChange = { onExperienceChange(it) },
                        label = { Text("Previous Streaming Experience", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = agencyId,
                        onValueChange = { onAgencyIdChange(it) },
                        label = { Text("Agency ID Referral (Optional)", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { onSubmit() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Host Application", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Your Application Statuses", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(applications.filter { it.userId == "100258741" }) { app ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.02f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(app.nickname, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("ID: ${app.id} • Ref Agency: ${app.agencyId.ifBlank { "None" }}", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (app.status) {
                                    "Approved" -> Color(0xFF00E676).copy(0.15f)
                                    "Pending" -> Color(0xFFFFB300).copy(0.15f)
                                    "Under Review" -> Color(0xFF00E5FF).copy(0.15f)
                                    else -> Color.White.copy(0.08f)
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = app.status,
                            color = when (app.status) {
                                "Approved" -> Color(0xFF00E676)
                                "Pending" -> Color(0xFFFFB300)
                                "Under Review" -> Color(0xFF00E5FF)
                                else -> Color.White.copy(0.4f)
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CenterSegment(
    applications: List<HostApplication>,
    invitations: List<HostInvitation>,
    inviteUserId: String,
    inviteType: String,
    onInviteUserIdChange: (String) -> Unit,
    onInviteTypeChange: (String) -> Unit,
    onSendInvite: () -> Unit,
    onApprove: (HostApplication) -> Unit,
    onReject: (HostApplication) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Invite panel
        item {
            Text("Send Strategic Recruitment Invitations", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inviteUserId,
                        onValueChange = { onInviteUserIdChange(it) },
                        label = { Text("Target User ID Only (e.g. 100458712)", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Invitation Role", color = Color.White.copy(0.7f), fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Host", "Agency Owner").forEach { type ->
                                val active = inviteType == type
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) Color(0xFF00E676) else Color.White.copy(0.04f))
                                        .clickable { onInviteTypeChange(type) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(type, color = if (active) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { onSendInvite() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dispatch Invitation", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Pending approvals from other users (Admin/Agency Center perspective)
        item {
            Text("Incoming Host Applications Review Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        val pendingApps = applications.filter { it.status == "Pending" || it.status == "Under Review" }
        if (pendingApps.isEmpty()) {
            item {
                Text("No pending host applications to review.", color = Color.White.copy(0.4f), fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        } else {
            items(pendingApps) { app ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(app.nickname, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("User ID: ${app.userId} • Country: ${app.country}", color = Color.White.copy(0.5f), fontSize = 10.sp)
                            }
                            Text("Language: ${app.language}", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Experience: ${app.experience}", color = Color.White.copy(0.7f), fontSize = 11.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onApprove(app) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Approve", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { onReject(app) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
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

@Composable
fun AgencyManagementSegment(roster: List<HostPerformance>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Agency Talent Performance Dashboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(roster) { host ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                    .background(if (host.status == "Active") Color(0xFF00E676) else Color.White.copy(0.4f))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(host.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text("Attendance: ${host.attendanceRate}%", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ONLINE HOURS", color = Color.White.copy(0.4f), fontSize = 9.sp)
                            Text("${host.onlineHours} hrs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text("DIAMONDS EARNED", color = Color.White.copy(0.4f), fontSize = 9.sp)
                            Text("💎 ${host.diamondsEarned}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text("STATUS", color = Color.White.copy(0.4f), fontSize = 9.sp)
                            Text(host.status, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HierarchySegment() {
    val officials = listOf(
        Pair("Sovereign_Admin", "Super Manager Leader"),
        Pair("Alpha_Broker", "Super Manager"),
        Pair("BD_Official_East", "BD Official"),
        Pair("Elite_Agency_Co", "Agency Owner")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Talent Recruitment Organizational Hierarchy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(officials) { (name, role) ->
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
                                .background(Color(0xFF00E676).copy(0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SupervisedUserCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(role, color = Color.White.copy(0.5f), fontSize = 10.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(0.06f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Active", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TargetsSegment() {
    val targets = listOf(
        Triple("Agency Host Quota", "Recruit 50 active hosts to the agency network.", "42 / 50"),
        Triple("Broadcast Endurance", "Hosts complete 80 cumulative live broadcast hours.", "58 / 80 hrs"),
        Triple("Diamond Revenue Target", "Reach cumulative agency yield of 50,000 diamonds.", "💎 32,500 / 50,000")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Active Management & Agency Targets", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(targets) { (title, desc, status) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(desc, color = Color.White.copy(0.5f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Progress Metric: $status", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFB300).copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("In Progress", color = Color(0xFFFFB300), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditSegment() {
    val audits = listOf(
        Triple("INVITE_SEND", "BD Official Zayn Malik invited Sonia Malik as Host talent.", "2026-07-20 14:11"),
        Triple("MIGRATION", "Transferred Host 'David Beckham' from Agency 52 to Agency 77.", "2026-07-19 11:22"),
        Triple("TARGET_COMPLETE", "Agency Owner Zayn Malik completed 50 Host recruitment milestone.", "2026-07-18 10:00")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Recruitment Audit Trail Ledger", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        items(audits) { (action, detail, date) ->
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
                                .background(Color(0xFF00E676).copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(action, color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                        Text(date, color = Color.White.copy(0.4f), fontSize = 9.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(detail, color = Color.White.copy(0.8f), fontSize = 11.sp)
                }
            }
        }
    }
}

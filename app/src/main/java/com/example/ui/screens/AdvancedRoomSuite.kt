package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- INTERACTIVE SYSTEM DATA MODELS ---

data class WelcomeBannerData(
    val userId: String,
    val nickname: String,
    val avatarUrl: String,
    val isVip: Boolean = false,
    val isHost: Boolean = false,
    val isFamily: Boolean = false,
    val isAgency: Boolean = false
)

data class UserReport(
    val id: String,
    val reportedUserId: String,
    val reason: String,
    val details: String,
    val status: String = "Pending Review" // Pending Review, Resolved, Suspended
)

data class AuditLogItem(
    val timestamp: String,
    val userId: String,
    val action: String,
    val moderator: String? = null
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val category: String, // Friend Room, Milestone, Gift, Event, Seat
    val timestamp: String,
    var isRead: Boolean = false
)

data class ScheduledRoom(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val dateTime: String,
    val reminderOffsetMinutes: Int,
    val participants: List<String>
)

data class CalendarEvent(
    val id: String,
    val title: String,
    val hostName: String,
    val type: String, // Official, Community, PK
    val dateTime: String,
    val description: String,
    val durationMinutes: Int,
    val participantCount: Int
)

data class OutboundInvitation(
    val id: String,
    val invitedUserId: String,
    val status: String, // Pending, Accepted, Declined, Expired
    val type: String // Camera, Speaker, Event
)

data class PinnedAnnouncement(
    val id: String,
    val title: String,
    val content: String,
    val isPinned: Boolean = true
)

// --- MAIN ENHANCED SUITE DIALOG ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdvancedRoomSuiteHub(
    onDismissRequest: () -> Unit,
    // Live hooks to interact with main room state
    currentRoomMode: String,
    onUpdateRoomMode: (String) -> Unit,
    currentLayoutSize: Int,
    onUpdateLayoutSize: (Int) -> Unit,
    popularityScore: Int,
    onUpdatePopularity: (Int) -> Unit,
    boostActive: Boolean,
    onUpdateBoostActive: (Boolean) -> Unit,
    onTriggerWelcomeBanner: (WelcomeBannerData) -> Unit,
    waitingQueue: SnapshotStateList<String>,
    onTriggerToast: (String) -> Unit
) {
    var activeSuiteTab by remember { mutableStateOf("interaction") } // interaction, moderation, notifications

    // 1. Interaction suite state variables
    var welcomeEffectsEnabled by remember { mutableStateOf(true) }
    var roomStatusSetting by remember { mutableStateOf("Open") }
    val roomStatusOptions = listOf("Open", "Busy", "Scheduled", "Event Live", "PK Live", "Private", "Password Protected")
    
    var isRoomSaved by remember { mutableStateOf(false) }
    val savedRooms = remember { mutableStateListOf("Tech Talk with Sato", "Lofi Beats Chillout", "Gaming Guild #12") }
    val recentlyVisited = remember { mutableStateListOf("Deep Space Ambient", "VC Startup Pitch", "English Cafe Lounge") }
    val favoriteRooms = remember { mutableStateListOf("English Cafe Lounge", "Lofi Beats Chillout") }
    val mostVisited = remember { mutableStateListOf("Tech Talk with Sato", "Deep Space Ambient") }

    val categoriesList = remember { mutableStateListOf("Music", "Talk Show", "Gaming", "Study", "Entertainment", "Sports", "Technology", "Podcasts", "Community", "Culture") }
    var newCategoryInput by remember { mutableStateOf("") }

    val roomLanguages = listOf("English", "Spanish", "French", "Arabic", "Turkish", "Hindi", "Urdu", "Mandarin")
    val selectedLanguages = remember { mutableStateListOf("English", "French") }
    var languageFilterSelection by remember { mutableStateOf("All") }

    var welcomeMessageText by remember { mutableStateOf("Welcome to Lumexa Premium Lounge! Keep respect, stay awesome!") }
    var coverImageUrlInput by remember { mutableStateOf("https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80") }
    var selectedThemeName by remember { mutableStateOf("Dark Violet Glow") }
    var selectedAccentColorName by remember { mutableStateOf("Cyan Glow") }

    // Accessibility
    var fontSizeScaleLevel by remember { mutableFloatStateOf(1.0f) }
    var isHighContrastMode by remember { mutableStateOf(false) }
    var isReducedMotionEnabled by remember { mutableStateOf(false) }
    var isClosedCaptionsEnabled by remember { mutableStateOf(false) }

    // 2. Moderation suite states
    var selectedTemplateName by remember { mutableStateOf("Full Manager") }
    val templatesMap = remember {
        mapOf(
            "Full Manager" to mapOf("delete_chat" to true, "pin_message" to true, "slow_mode" to true, "speaker_mod" to true, "camera_mod" to true, "member_restrict" to true),
            "Event Manager" to mapOf("delete_chat" to false, "pin_message" to true, "slow_mode" to false, "speaker_mod" to true, "camera_mod" to true, "member_restrict" to false),
            "Music Manager" to mapOf("delete_chat" to false, "pin_message" to false, "slow_mode" to false, "speaker_mod" to true, "camera_mod" to false, "member_restrict" to false),
            "Chat Moderator" to mapOf("delete_chat" to true, "pin_message" to true, "slow_mode" to true, "speaker_mod" to false, "camera_mod" to false, "member_restrict" to true),
            "Audience Moderator" to mapOf("delete_chat" to false, "pin_message" to false, "slow_mode" to true, "speaker_mod" to false, "camera_mod" to false, "member_restrict" to true)
        )
    }
    val currentPermissions = remember {
        mutableStateMapOf(
            "delete_chat" to true,
            "pin_message" to true,
            "slow_mode" to true,
            "speaker_mod" to true,
            "camera_mod" to true,
            "member_restrict" to true
        )
    }

    var slowModeSliderDuration by remember { mutableFloatStateOf(0f) } // 0, 5s, 10s, 30s, 60s
    var isRoomChatMuted by remember { mutableStateOf(false) }
    val blacklistUsersList = remember { mutableStateListOf("user_spammer_x", "user_toxic_alpha", "user_flood_bot") }
    val userWarningCounts = remember { mutableStateMapOf("user_jake" to 1, "user_liam" to 0) }
    var selectedAccessRuleType by remember { mutableStateOf("Public") } // Public, Invitation Only, Password Protected, Followers, Friends, Family, Agency, VIP
    
    // Anti-Spam
    var floodProtectionOn by remember { mutableStateOf(true) }
    var rateLimitEnabled by remember { mutableStateOf(true) }
    var duplicateMessageBlockOn by remember { mutableStateOf(true) }
    var excessiveEmojiRestricted by remember { mutableStateOf(false) }
    var linkRestrictionsOn by remember { mutableStateOf(true) }

    // Reports list
    val submittedReports = remember {
        mutableStateListOf(
            UserReport("rep_01", "user_flood_bot", "Spam / Message Flooding", "Spammed 50 emojis in 2 seconds", "Pending Review"),
            UserReport("rep_02", "user_toxic_alpha", "Harassment", "Insulted other speakers on seat 2", "Pending Review")
        )
    }
    var reportTargetUserId by remember { mutableStateOf("") }
    var reportReasonSelection by remember { mutableStateOf("Harassment") }
    var reportExplanationDetails by remember { mutableStateOf("") }

    // Searchable Audit Log
    val completeAuditLogs = remember {
        mutableStateListOf(
            AuditLogItem("21:15:30", "owner_alex", "Enabled slow mode (10s)"),
            AuditLogItem("21:16:05", "moderator_Sofia", "Muted speaker seat 2 (user_ann)"),
            AuditLogItem("21:18:22", "manager_Sato", "Approved camera seat for user_liam"),
            AuditLogItem("21:20:10", "owner_alex", "Issued formal warning to user_jake"),
            AuditLogItem("21:22:45", "system_filter", "Blocked spam link from user_spam_bot"),
            AuditLogItem("21:24:12", "moderator_Sofia", "Removed user_toxic_alpha from room")
        )
    }
    var auditSearchQuery by remember { mutableStateOf("") }

    // Alert feeds
    val staffAlertNotifications = remember {
        mutableStateListOf(
            "New harassment report submitted against user_toxic_alpha",
            "Anti-spam threshold tripped by user_spam_bot: 10 messages/sec",
            "Room capacity limit has reached 90% (135/150 users online)"
        )
    }

    // 3. Notifications, reminders & events states
    val notificationsToggles = remember {
        mutableStateMapOf(
            "friend_started" to true,
            "family_started" to true,
            "agency_started" to true,
            "fav_host_started" to true,
            "scheduled_started" to true,
            "event_started" to true,
            "pk_started" to true,
            "pk_invite" to true,
            "speaker_invite" to true,
            "camera_invite" to true,
            "room_announcement" to true,
            "gift_received" to true,
            "milestone_reached" to true
        )
    }

    val liveNotificationInbox = remember {
        mutableStateListOf(
            NotificationItem("not_1", "Friend Joined Room", "Sofia Rodriguez just joined the audience list.", "Friend Room", "2 mins ago"),
            NotificationItem("not_2", "PK Challenge Received", "Broadcaster Sato has challenged you to an active PK battle!", "PK Challenge", "5 mins ago"),
            NotificationItem("not_3", "Speaker Spot Available", "Seat 4 is now open. Double tap to claim your microphone!", "Seat", "8 mins ago"),
            NotificationItem("not_4", "Scheduled Room Reminder", "Your scheduled room 'Lumexa Tech Round' begins in 30 minutes.", "Event", "12 mins ago")
        )
    }

    val scheduledRoomsList = remember {
        mutableStateListOf(
            ScheduledRoom("sch_01", "Lumexa Crypto Debate", "Discussing Bitcoin and Ethereum trends on high latency audio", "Technology", "July 22, 18:00", 15, listOf("user_alex", "user_sato", "user_lisa")),
            ScheduledRoom("sch_02", "Lofi Study Session", "Ambient sounds and silent workspace study lounge with timer", "Study", "July 23, 10:00", 30, listOf("user_alex", "user_sofia"))
        )
    }
    var scheduleTitleInput by remember { mutableStateOf("") }
    var scheduleDescInput by remember { mutableStateOf("") }
    var scheduleCategoryInput by remember { mutableStateOf("Technology") }
    var scheduleTimeInput by remember { mutableStateOf("July 24, 15:00") }
    var scheduleReminderOffset by remember { mutableStateOf(15) } // minutes

    val eventsCalendar = remember {
        mutableStateListOf(
            CalendarEvent("evt_01", "Official Lumexa Gala Party", "Lumexa Support Team", "Official", "July 21, 22:30", "Celebrate the launch of modern voice features with double coin drops!", 120, 485),
            CalendarEvent("evt_02", "Gaming Tournament PK Battle", "Esports Central", "PK", "July 22, 14:00", "Top 16 voice room hosts battle out live in gaming tournaments", 90, 240),
            CalendarEvent("evt_03", "Acoustic Sunset Show", "Sofia Rodriguez", "Community", "July 23, 19:30", "Live acoustic songs with virtual soundboards and reverb FXs", 60, 115)
        )
    }
    val registeredEventsList = remember { mutableStateListOf("evt_01") }

    val outboundInvites = remember {
        mutableStateListOf(
            OutboundInvitation("inv_01", "user_luke_42", "Pending", "Camera"),
            OutboundInvitation("inv_02", "user_sofia", "Accepted", "Speaker"),
            OutboundInvitation("inv_03", "user_amy", "Declined", "Event")
        )
    }
    var inviteTargetUserIdInput by remember { mutableStateOf("") }
    var inviteTargetRoleType by remember { mutableStateOf("Camera") } // Camera, Speaker, Event

    val pinnedAnnouncementsList = remember {
        mutableStateListOf(
            PinnedAnnouncement("ann_01", "🚨 Rules of the Lounge", "1. Speak one by one.\n2. Respect moderators.\n3. Keep active camera clean."),
            PinnedAnnouncement("ann_02", "🏆 Summer Live PK Rewards", "Top speaker on mic gets 2,000 coin bounty per hour during official party!")
        )
    }
    var newAnnouncementTitle by remember { mutableStateOf("") }
    var newAnnouncementContent by remember { mutableStateOf("") }

    val followedRoomsList = remember { mutableStateListOf("Tech Talk with Sato", "Lofi Beats Chillout", "Sofia Acoustic Studio") }
    
    var quietHoursEnabled by remember { mutableStateOf(false) }
    var quietHoursRangeText by remember { mutableStateOf("22:00 - 08:00") }
    var quietHoursPriorityOnly by remember { mutableStateOf(true) }

    val personalNotificationHistoryLog = remember {
        mutableStateListOf(
            "Sent room invitation to user_luke_42 (Pending)",
            "Received and accepted speaker invitation from owner_alex",
            "Announcement pinned: '🚨 Rules of the Lounge'",
            "Registered for event: Official Lumexa Gala Party"
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = Color(0xFF0F0A1F),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(22.dp))
                    Text("Lumexa Advanced Suite", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxSize()) {
                // TAB SELECTION ROW
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.White.copy(0.04f), RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    listOf(
                        "interaction" to "Room Interaction (JT)",
                        "moderation" to "Moderation & Safety (JU)",
                        "notifications" to "Events & Alerts (JV)"
                    ).forEach { (id, label) ->
                        val isSelected = activeSuiteTab == id
                        Box(
                            modifier = Modifier
                                .weight(1.0f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF8E2DE2) else Color.Transparent)
                                .clickable { activeSuiteTab = id }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label.split(" ")[0] + " " + label.split(" ").getOrNull(1)?.take(4),
                                color = if (isSelected) Color.White else Color.White.copy(0.6f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                // CORE TAB BODY WRAPPER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White.copy(0.02f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (activeSuiteTab) {
                            "interaction" -> {
                                // ==========================================
                                // JT: ADVANCED ROOM EXPERIENCE & INTERACTION
                                // ==========================================
                                SuiteSectionHeader("Room Welcome Experience", Icons.Default.Face)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Welcome Effects Banner Overlay", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Toggle display of entrance animations, VIP, Family, and Host badges when users join.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                    }
                                    Switch(
                                        checked = welcomeEffectsEnabled,
                                        onCheckedChange = { welcomeEffectsEnabled = it }
                                    )
                                }

                                // Interactive Welcome Simulation Button
                                Button(
                                    onClick = {
                                        if (welcomeEffectsEnabled) {
                                            val simulatedJoiner = WelcomeBannerData(
                                                userId = "lum_vip_user_${(100..999).random()}",
                                                nickname = listOf("Alpha Commander", "Cosmic Singer", "Queen Sofia", "Sato Legend").random(),
                                                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
                                                isVip = true,
                                                isHost = listOf(true, false).random(),
                                                isFamily = listOf(true, false).random(),
                                                isAgency = listOf(true, false).random()
                                            )
                                            onTriggerWelcomeBanner(simulatedJoiner)
                                            onTriggerToast("Simulating VIP Join entrance banner animation!")
                                        } else {
                                            onTriggerToast("Enable Welcome effects first to view animation!")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Simulate VIP User Joining Room", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Room Live Status Setting", Icons.Default.Info)
                                Text("Choose the current room operational badge visible to public directories:", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    roomStatusOptions.forEach { status ->
                                        val isCurrent = roomStatusSetting == status
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isCurrent) Color(0xFF00E5FF) else Color.White.copy(0.06f))
                                                .clickable {
                                                    roomStatusSetting = status
                                                    onTriggerToast("Room status changed to: $status")
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(status, color = if (isCurrent) Color.Black else Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Live Member Counters breakdown", Icons.Default.List)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        StatisticItem("Total Active Members", "148 Members")
                                        StatisticItem("Mics/Speakers occupied", "6/8 Seats")
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        StatisticItem("Camera Users streaming", "4/6 Cams")
                                        StatisticItem("Silent Audience listeners", "138 Users")
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        StatisticItem("Friends Online in room", "12 Friends")
                                        StatisticItem("Assigned Managers on duty", "3 Managers")
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Room Bookmark & Saved States", Icons.Default.Bookmark)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isRoomSaved) "⭐ Saved in My Bookmarks" else "Not Saved in Bookmarks",
                                        color = if (isRoomSaved) Color(0xFFFFD700) else Color.White.copy(0.6f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(
                                        onClick = {
                                            isRoomSaved = !isRoomSaved
                                            if (isRoomSaved) {
                                                savedRooms.add("Lounge Room: Alex Private")
                                                onTriggerToast("Room added to bookmarks!")
                                            } else {
                                                savedRooms.remove("Lounge Room: Alex Private")
                                                onTriggerToast("Room removed from bookmarks.")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isRoomSaved) Color.Red.copy(0.4f) else Color(0xFF8E2DE2))
                                    ) {
                                        Icon(if (isRoomSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isRoomSaved) "Remove Bookmark" else "Save Room", fontSize = 10.sp)
                                    }
                                }

                                // Personal Saved Rooms directory
                                Text("My Saved Rooms Directory:", color = Color.White.copy(0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    savedRooms.forEach { saved ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White.copy(0.04f), RoundedCornerShape(6.dp))
                                                .padding(6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(saved, color = Color.White, fontSize = 9.sp)
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color.Red.copy(0.6f),
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable {
                                                        savedRooms.remove(saved)
                                                        onTriggerToast("Deleted saved bookmark reference")
                                                    }
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Room History Tracker", Icons.Default.Refresh)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Recent, Favorites & Frequency", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        TextButton(onClick = {
                                            recentlyVisited.clear()
                                            favoriteRooms.clear()
                                            mostVisited.clear()
                                            onTriggerToast("My personal visited history cleared successfully.")
                                        }) {
                                            Text("Clear History", color = Color.Red, fontSize = 10.sp)
                                        }
                                    }

                                    Text("Recently Visited Lounge Rooms:", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        recentlyVisited.forEach { rv ->
                                            Text(
                                                text = rv,
                                                color = Color.White.copy(0.7f),
                                                fontSize = 8.sp,
                                                modifier = Modifier
                                                    .background(Color.White.copy(0.06f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text("Most Visited Lounge Rooms:", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        mostVisited.forEach { mv ->
                                            Text(
                                                text = "🔥 $mv",
                                                color = Color(0xFFFFB300),
                                                fontSize = 8.sp,
                                                modifier = Modifier
                                                    .background(Color.White.copy(0.06f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Room Categories Control", Icons.Default.List)
                                Text("Lounge Categories (Platforms admins can add dynamically):", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    categoriesList.forEach { cat ->
                                        Text(
                                            text = "#$cat",
                                            color = Color(0xFF00E5FF),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(Color(0xFF00E5FF).copy(0.1f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                // Platform Admin Category addition
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = newCategoryInput,
                                        onValueChange = { newCategoryInput = it },
                                        placeholder = { Text("Add Admin Category...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Button(
                                        onClick = {
                                            if (newCategoryInput.isNotBlank()) {
                                                categoriesList.add(newCategoryInput)
                                                onTriggerToast("Platform Admin created category: #$newCategoryInput")
                                                newCategoryInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2))
                                    ) {
                                        Text("Add", fontSize = 10.sp)
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Room Language & Filters", Icons.Default.Refresh)
                                Text("Filter lounge rooms by spoken broadcast languages:", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Select filter language:", color = Color.White, fontSize = 10.sp)
                                    listOf("All", "English", "Spanish", "French").forEach { lang ->
                                        val isCurrent = languageFilterSelection == lang
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isCurrent) Color(0xFF00E676) else Color.White.copy(0.05f))
                                                .clickable { languageFilterSelection = lang }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(lang, color = if (isCurrent) Color.Black else Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Active room languages
                                Text("Room Active Owner Languages (select one or more):", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    roomLanguages.forEach { lang ->
                                        val isChecked = selectedLanguages.contains(lang)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isChecked) Color(0xFF8E2DE2).copy(0.3f) else Color.White.copy(0.05f))
                                                .border(1.dp, if (isChecked) Color(0xFF8E2DE2) else Color.Transparent, RoundedCornerShape(8.dp))
                                                .clickable {
                                                    if (isChecked) selectedLanguages.remove(lang) else selectedLanguages.add(lang)
                                                    onTriggerToast("Room broadcasting languages updated!")
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(lang, color = if (isChecked) Color.White else Color.White.copy(0.6f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Room Capacity & Seats Indicators", Icons.Default.Info)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    CapacityBar("Audience Space", 85, 150, Color(0xFF00E676))
                                    CapacityBar("Speaker Seats occupied", 6, 8, Color(0xFF8E2DE2))
                                    CapacityBar("Active Camera streams in use", 4, 6, Color(0xFF00E5FF))
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Room Activity Live Timeline", Icons.Default.List)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(0.03f), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    TimelineEntry("12:00", "Room Started by @owner_alex")
                                    TimelineEntry("12:15", "Announcement Posted: '🚨 Rules of the Lounge'")
                                    TimelineEntry("12:30", "Event Started: Official Gala Night Session")
                                    TimelineEntry("12:45", "PK Started with Sato Agency Broadcaster")
                                    TimelineEntry("13:00", "PK Ended: Alex Mercer won with 12k Diamond votes!")
                                    TimelineEntry("13:15", "Milestone Reached: 15k aggregate popularity score!")
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("User Join Queue Manager", Icons.Default.Schedule)
                                Text("When room capacity is full, waitlisted queue kicks in:", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF130D2E), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Waiting Queue active", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("Total: ${waitingQueue.size} users waitlisted", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                    }
                                    Text("Est. Wait Time: ~4 mins", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Room Customize Appearance", Icons.Default.Settings)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = welcomeMessageText,
                                        onValueChange = { welcomeMessageText = it },
                                        label = { Text("Cover Custom Welcome Message", fontSize = 10.sp, color = Color.White.copy(0.6f)) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    OutlinedTextField(
                                        value = coverImageUrlInput,
                                        onValueChange = { coverImageUrlInput = it },
                                        label = { Text("Lounge Cover Backdrop URL", fontSize = 10.sp, color = Color.White.copy(0.6f)) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Text("Lounge Theme presets & accent color palette:", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("Dark Violet Glow", "Mystic Forest", "Ocean Deep Blue").forEach { theme ->
                                            val isCurrent = selectedThemeName == theme
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isCurrent) Color(0xFF8E2DE2) else Color.White.copy(0.05f))
                                                    .clickable {
                                                        selectedThemeName = theme
                                                        onTriggerToast("Room background theme customized to $theme!")
                                                    }
                                                    .padding(4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(theme, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Accessibility Controls", Icons.Default.Accessibility)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Adjust Font size scale:", color = Color.White, fontSize = 10.sp)
                                        Slider(
                                            value = fontSizeScaleLevel,
                                            onValueChange = { fontSizeScaleLevel = it },
                                            valueRange = 0.8f..1.5f,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text("${(fontSizeScaleLevel * 100).toInt()}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("High Contrast Mode theme", color = Color.White, fontSize = 10.sp)
                                        Switch(checked = isHighContrastMode, onCheckedChange = { isHighContrastMode = it })
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Reduced Motion animations", color = Color.White, fontSize = 10.sp)
                                        Switch(checked = isReducedMotionEnabled, onCheckedChange = { isReducedMotionEnabled = it })
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Enable Closed Captions stream", color = Color.White, fontSize = 10.sp)
                                        Switch(checked = isClosedCaptionsEnabled, onCheckedChange = { isClosedCaptionsEnabled = it })
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Lounge Interaction Analytics Dashboard", Icons.Default.TrendingUp)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF130D2E), RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Host Performance & Audience Trends (Live):", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        AnalyticMetricCell("Avg Session Length", "42 mins")
                                        AnalyticMetricCell("Peak Concurrent", "245 Users")
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        AnalyticMetricCell("Returning Visitors", "68.4%")
                                        AnalyticMetricCell("New Followers gained", "+124 Today")
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Hourly User Activity Trends (Simulated chart):", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        listOf(15, 30, 45, 90, 120, 80, 110, 148, 120, 95).forEach { value ->
                                            Box(
                                                modifier = Modifier
                                                    .width(18.dp)
                                                    .fillMaxHeight(value / 150f)
                                                    .background(Color(0xFF00E5FF), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                            )
                                        }
                                    }
                                }
                            }

                            "moderation" -> {
                                // ==========================================
                                // JU: COMPLETE ROOM MODERATION & SECURITY
                                // ==========================================
                                SuiteSectionHeader("Moderation Roles & Permissions Matrix", Icons.Default.Shield)
                                Text("Select template to instantly load preset access restrictions:", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    templatesMap.keys.forEach { template ->
                                        val isCurrent = selectedTemplateName == template
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isCurrent) Color(0xFF8E2DE2) else Color.White.copy(0.06f))
                                                .clickable {
                                                    selectedTemplateName = template
                                                    templatesMap[template]?.forEach { (perm, state) ->
                                                        currentPermissions[perm] = state
                                                    }
                                                    onTriggerToast("Loaded permission template: $template")
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(template, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Interactive Matrix checklist
                                Text("Independent Permissions Customization:", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    PermissionCheckrow("Delete Lounge Chat Messages", "delete_chat", currentPermissions) { currentPermissions["delete_chat"] = it }
                                    PermissionCheckrow("Pin Highlight Announcements", "pin_message", currentPermissions) { currentPermissions["pin_message"] = it }
                                    PermissionCheckrow("Apply Chat Slow Mode Rate Limits", "slow_mode", currentPermissions) { currentPermissions["slow_mode"] = it }
                                    PermissionCheckrow("Manage Speaking Seat mic locks", "speaker_mod", currentPermissions) { currentPermissions["speaker_mod"] = it }
                                    PermissionCheckrow("Approve/Lock Live Camera streams", "camera_mod", currentPermissions) { currentPermissions["camera_mod"] = it }
                                    PermissionCheckrow("Issue formal warnings & blacklists", "member_restrict", currentPermissions) { currentPermissions["member_restrict"] = it }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Chat Moderation Controls", Icons.Default.Chat)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            onTriggerToast("Cleared entire Room chat feed! Cleaned up visual logs.")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.6f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Delete & Flush All Room Chat Messages", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Pin Featured Message to Room Marquee:", color = Color.White, fontSize = 11.sp)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Mute Chat Entirely (Broadcast mode)", color = Color.White, fontSize = 11.sp)
                                        Switch(checked = isRoomChatMuted, onCheckedChange = {
                                            isRoomChatMuted = it
                                            onTriggerToast(if (it) "Lounge room chat muted entirely." else "Lounge room chat active!")
                                        })
                                    }

                                    Text("Apply Chat Slow Mode duration: ${slowModeSliderDuration.toInt()} seconds", color = Color.White, fontSize = 10.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Slider(
                                            value = slowModeSliderDuration,
                                            onValueChange = { slowModeSliderDuration = it },
                                            valueRange = 0f..60f,
                                            steps = 11,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(if (slowModeSliderDuration == 0f) "Off" else "${slowModeSliderDuration.toInt()}s", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Speaker Seat Moderation Desk", Icons.Default.Face)
                                Text("Quick Moderator Seat overrides:", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onTriggerToast("Locked all speaker seats in lobby.")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Lock All", fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = {
                                            onTriggerToast("Unlocked all mic seats.")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Unlock All", fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = {
                                            onTriggerToast("Muted all guest speaker microphones.")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.4f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Mute Guest", fontSize = 10.sp)
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Camera Stream Moderation", Icons.Default.Videocam)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Restrict New Camera Requests", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("Prevent guest users from turning on their cameras without approval", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                        }
                                        Switch(checked = true, onCheckedChange = {})
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Voice-Only Room Lock", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("Disable all camera seats instantly and force voice-only layout", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                        }
                                        Switch(
                                            checked = currentRoomMode == "Voice Only",
                                            onCheckedChange = {
                                                onUpdateRoomMode(if (it) "Voice Only" else "Voice + Video")
                                                onTriggerToast("Forced layout to voice-only!")
                                            }
                                        )
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Member Warning, Kicks & Blacklists", Icons.Default.Warning)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Issues formal warning, kicks, or blacklists user from re-entry:", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                    
                                    var restrictionUserId by remember { mutableStateOf("") }
                                    var formalWarningMsg by remember { mutableStateOf("Keep chat civil!") }

                                    OutlinedTextField(
                                        value = restrictionUserId,
                                        onValueChange = { restrictionUserId = it },
                                        placeholder = { Text("Enter User ID (e.g. user_jake)...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = {
                                                if (restrictionUserId.isNotBlank()) {
                                                    val count = userWarningCounts.getOrDefault(restrictionUserId, 0) + 1
                                                    userWarningCounts[restrictionUserId] = count
                                                    onTriggerToast("Issued Warning to $restrictionUserId ($count/3 Warnings)!")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Warn", fontSize = 10.sp, color = Color.Black)
                                        }

                                        Button(
                                            onClick = {
                                                if (restrictionUserId.isNotBlank()) {
                                                    onTriggerToast("Kicked $restrictionUserId! Blocked for 24h.")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.6f)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Kick user", fontSize = 10.sp)
                                        }

                                        Button(
                                            onClick = {
                                                if (restrictionUserId.isNotBlank()) {
                                                    blacklistUsersList.add(restrictionUserId)
                                                    onTriggerToast("Blacklisted $restrictionUserId permanently.")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Blacklist", fontSize = 10.sp)
                                        }
                                    }

                                    Text("Active Room Blacklist Directory:", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        blacklistUsersList.forEach { user ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.White.copy(0.04f), RoundedCornerShape(6.dp))
                                                    .padding(6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(user, color = Color.Red, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = "Unban",
                                                    color = Color(0xFF00E676),
                                                    fontSize = 9.sp,
                                                    modifier = Modifier.clickable {
                                                        blacklistUsersList.remove(user)
                                                        onTriggerToast("Unbanned user $user")
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Room Access Rules", Icons.Default.Lock)
                                Text("Limit lounge access using rules:", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("Public", "Invitation Only", "Password Protected", "Followers Only", "Friends Only", "Family Members", "Agency Members", "VIP Members").forEach { rule ->
                                        val isCurrent = selectedAccessRuleType == rule
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isCurrent) Color(0xFF8E2DE2) else Color.White.copy(0.06f))
                                                .clickable {
                                                    selectedAccessRuleType = rule
                                                    onTriggerToast("Room Access Rule changed to: $rule")
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(rule, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Anti-Spam & Flood Controls", Icons.Default.Shield)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    SpamControlToggle("Message Rate Limits (Rate restrict)", rateLimitEnabled) { rateLimitEnabled = it }
                                    SpamControlToggle("Repeated / Duplicated Text Filter", duplicateMessageBlockOn) { duplicateMessageBlockOn = it }
                                    SpamControlToggle("Excessive Emojis restriction", excessiveEmojiRestricted) { excessiveEmojiRestricted = it }
                                    SpamControlToggle("Web Links restriction", linkRestrictionsOn) { linkRestrictionsOn = it }
                                    SpamControlToggle("Spam Flooding Automatic Shield", floodProtectionOn) { floodProtectionOn = it }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Report Abuse & Harassment Centre", Icons.Default.Warning)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Report misconduct. Staff is alerted instantly:", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                    
                                    OutlinedTextField(
                                        value = reportTargetUserId,
                                        onValueChange = { reportTargetUserId = it },
                                        placeholder = { Text("Reported user ID...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Violation Type: ", color = Color.White, fontSize = 10.sp)
                                        listOf("Harassment", "Spam", "Impersonation", "Inappropriate").forEach { reason ->
                                            val isSelected = reportReasonSelection == reason
                                            Box(
                                                modifier = Modifier
                                                    .background(if (isSelected) Color(0xFF00E5FF) else Color.White.copy(0.05f), RoundedCornerShape(4.dp))
                                                    .clickable { reportReasonSelection = reason }
                                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                                            ) {
                                                Text(reason, color = if (isSelected) Color.Black else Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = reportExplanationDetails,
                                        onValueChange = { reportExplanationDetails = it },
                                        placeholder = { Text("Details of rule violation...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Button(
                                        onClick = {
                                            if (reportTargetUserId.isNotBlank()) {
                                                val newReport = UserReport("rep_${(10..99).random()}", reportTargetUserId, reportReasonSelection, reportExplanationDetails)
                                                submittedReports.add(newReport)
                                                staffAlertNotifications.add("🚨 Report received: $reportTargetUserId accused of $reportReasonSelection")
                                                onTriggerToast("Abuse Report filed successfully! Staff notified.")
                                                reportTargetUserId = ""
                                                reportExplanationDetails = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Submit Formal Report to Admin Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Searchable Room Audit Logs", Icons.Default.Search)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedTextField(
                                        value = auditSearchQuery,
                                        onValueChange = { auditSearchQuery = it },
                                        placeholder = { Text("Filter logs by user or action...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        val filteredLogs = completeAuditLogs.filter {
                                            it.userId.contains(auditSearchQuery, ignoreCase = true) ||
                                                    it.action.contains(auditSearchQuery, ignoreCase = true)
                                        }
                                        if (filteredLogs.isEmpty()) {
                                            Text("No matching audit logs found.", color = Color.White.copy(0.4f), fontSize = 10.sp)
                                        } else {
                                            filteredLogs.forEach { log ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color.White.copy(0.03f), RoundedCornerShape(6.dp))
                                                        .padding(6.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("[${log.timestamp}]", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    Text("@${log.userId}", color = Color(0xFFFFD700), fontSize = 9.sp)
                                                    Text(log.action, color = Color.White, fontSize = 9.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                                }
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Emergency Kill & Safety Controls", Icons.Default.Warning)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Red.copy(0.1f), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.Red, RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Immediate Lockdowns (Host only):", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    
                                    Button(
                                        onClick = {
                                            isRoomChatMuted = true
                                            onUpdateRoomMode("Voice Only")
                                            onTriggerToast("🚨 Emergency: ALL mics/mics/chat LOCKED instantly!")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("EMERGENCY HARD LOCK ROOM", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Staff Alerts Feed & Admin Desk", Icons.Default.Notifications)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    staffAlertNotifications.forEach { alert ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF2E1313), RoundedCornerShape(6.dp))
                                                .padding(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(alert, color = Color.White, fontSize = 9.sp, modifier = Modifier.weight(1f))
                                        }
                                    }

                                    Text("Submitted Platform Reports list:", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    submittedReports.forEach { rep ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("Report: ${rep.id}", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    Text(rep.status, color = Color(0xFFFFB300), fontSize = 9.sp)
                                                }
                                                Text("Accused: @${rep.reportedUserId} - Reason: ${rep.reason}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                Text("Detail: ${rep.details}", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                                
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Button(
                                                        onClick = {
                                                            submittedReports.remove(rep)
                                                            onTriggerToast("Room Suspended & Taken Offline for policy violation.")
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text("Suspend Room", fontSize = 8.sp)
                                                    }

                                                    Button(
                                                        onClick = {
                                                            submittedReports.remove(rep)
                                                            onTriggerToast("Report Dismissed. Room restored to public status.")
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text("Dismiss & Restore", fontSize = 8.sp, color = Color.Black)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            "notifications" -> {
                                // ==========================================
                                // JV: COMPLETE NOTIFICATIONS & EVENT SYSTEM
                                // ==========================================
                                SuiteSectionHeader("Smart Live Notifications preferences", Icons.Default.Notifications)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    NotificationToggleRow("Friend Starts Voice/Video Room", "friend_started", notificationsToggles)
                                    NotificationToggleRow("Family Member Starts Room", "family_started", notificationsToggles)
                                    NotificationToggleRow("Agency Partner Starts Room", "agency_started", notificationsToggles)
                                    NotificationToggleRow("Favorite Broadcaster/Host Live", "fav_host_started", notificationsToggles)
                                    NotificationToggleRow("Scheduled Event Reminders", "scheduled_started", notificationsToggles)
                                    NotificationToggleRow("PK Battle Arena Started", "pk_started", notificationsToggles)
                                    NotificationToggleRow("Inbound PK Challenge Invitation", "pk_invite", notificationsToggles)
                                    NotificationToggleRow("Co-Host Speaker Mic Invitation", "speaker_invite", notificationsToggles)
                                    NotificationToggleRow("Live Camera Seat Invitation", "camera_invite", notificationsToggles)
                                    NotificationToggleRow("Announcements / Welcome Message pinned", "room_announcement", notificationsToggles)
                                    NotificationToggleRow("Virtual Gift received on seat", "gift_received", notificationsToggles)
                                    NotificationToggleRow("Popularity Room Milestones", "milestone_reached", notificationsToggles)
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Lounge Notification Center inbox", Icons.Default.List)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Unread Live Notifications: ${liveNotificationInbox.count { !it.isRead }}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Row {
                                            TextButton(onClick = {
                                                liveNotificationInbox.forEach { it.isRead = true }
                                                onTriggerToast("Marked all notifications as read.")
                                            }) {
                                                Text("Mark All Read", color = Color(0xFF00E5FF), fontSize = 10.sp)
                                            }
                                            TextButton(onClick = {
                                                liveNotificationInbox.clear()
                                                onTriggerToast("Inbox notifications cleared.")
                                            }) {
                                                Text("Clear All", color = Color.Red, fontSize = 10.sp)
                                            }
                                        }
                                    }

                                    liveNotificationInbox.forEach { notif ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (notif.isRead) Color.White.copy(0.03f) else Color(0xFF1E132E), RoundedCornerShape(8.dp))
                                                .border(1.dp, if (notif.isRead) Color.Transparent else Color(0xFF8E2DE2).copy(0.4f), RoundedCornerShape(8.dp))
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .background(if (notif.isRead) Color.Transparent else Color(0xFF00E5FF), CircleShape)
                                                    )
                                                    Text(notif.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Text(notif.message, color = Color.White.copy(0.6f), fontSize = 10.sp)
                                                Text("${notif.category} • ${notif.timestamp}", color = Color.White.copy(0.4f), fontSize = 8.sp)
                                            }
                                            
                                            Row {
                                                IconButton(onClick = {
                                                    notif.isRead = true
                                                    onTriggerToast("Notification marked read.")
                                                }, modifier = Modifier.size(24.dp)) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                                                }
                                                IconButton(onClick = {
                                                    liveNotificationInbox.remove(notif)
                                                    onTriggerToast("Notification deleted.")
                                                }, modifier = Modifier.size(24.dp)) {
                                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red.copy(0.6f), modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Schedule a Room & Reminders", Icons.Default.DateRange)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = scheduleTitleInput,
                                        onValueChange = { scheduleTitleInput = it },
                                        placeholder = { Text("Lounge Room Title (e.g. Acoustic Night)...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    OutlinedTextField(
                                        value = scheduleDescInput,
                                        onValueChange = { scheduleDescInput = it },
                                        placeholder = { Text("Brief Description of topic...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedTextField(
                                            value = scheduleTimeInput,
                                            onValueChange = { scheduleTimeInput = it },
                                            placeholder = { Text("Date/Time (e.g. July 24, 15:00)...", color = Color.White.copy(0.3f), fontSize = 9.sp) },
                                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                            modifier = Modifier.weight(1.2f),
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        OutlinedTextField(
                                            value = scheduleCategoryInput,
                                            onValueChange = { scheduleCategoryInput = it },
                                            placeholder = { Text("Category...", color = Color.White.copy(0.3f), fontSize = 9.sp) },
                                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                            modifier = Modifier.weight(0.8f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }

                                    Text("Remind me before Room Starts:", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf(0, 5, 15, 30, 60).forEach { mins ->
                                            val isCurrent = scheduleReminderOffset == mins
                                            val text = if (mins == 0) "At Start" else "${mins}m before"
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isCurrent) Color(0xFF00E5FF) else Color.White.copy(0.05f))
                                                    .clickable { scheduleReminderOffset = mins }
                                                    .padding(4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text, color = if (isCurrent) Color.Black else Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if (scheduleTitleInput.isNotBlank()) {
                                                val newRoom = ScheduledRoom(
                                                    "sch_${(10..99).random()}",
                                                    scheduleTitleInput,
                                                    scheduleDescInput,
                                                    scheduleCategoryInput,
                                                    scheduleTimeInput,
                                                    scheduleReminderOffset,
                                                    listOf("user_alex")
                                                )
                                                scheduledRoomsList.add(newRoom)
                                                personalNotificationHistoryLog.add("Scheduled room: '$scheduleTitleInput'")
                                                onTriggerToast("Room Scheduled Successfully! Invite alerts queued.")
                                                scheduleTitleInput = ""
                                                scheduleDescInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Publish Scheduled Lounge Room", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text("Scheduled Rooms Timeline:", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    scheduledRoomsList.forEach { room ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.03f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text(room.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text(room.category, color = Color(0xFF00E5FF), fontSize = 9.sp)
                                                }
                                                Text(room.description, color = Color.White.copy(0.6f), fontSize = 9.sp)
                                                Text("🕒 Starts: ${room.dateTime} • Reminds ${room.reminderOffsetMinutes}m before", color = Color(0xFFFFB300), fontSize = 9.sp)
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Interactive Event Calendar", Icons.Default.DateRange)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    eventsCalendar.forEach { event ->
                                        val isJoined = registeredEventsList.contains(event.id)
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("[${event.type}] ${event.title}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text("🔥 ${event.participantCount} registered", color = Color(0xFF00E676), fontSize = 8.sp)
                                                }
                                                Text("Host: ${event.hostName} • Date: ${event.dateTime}", color = Color.White.copy(0.7f), fontSize = 9.sp)
                                                Text(event.description, color = Color.White.copy(0.5f), fontSize = 9.sp)
                                                
                                                // Real-time Countdown simulator display (JV)
                                                Text("⏳ Countdown: 0 Days, 2 Hours, 45 Minutes remaining", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)

                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Button(
                                                        onClick = {
                                                            if (isJoined) {
                                                                registeredEventsList.remove(event.id)
                                                                onTriggerToast("Left the event calendar.")
                                                            } else {
                                                                registeredEventsList.add(event.id)
                                                                personalNotificationHistoryLog.add("Registered for: ${event.title}")
                                                                onTriggerToast("Registered for: ${event.title}!")
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = if (isJoined) Color.Red.copy(0.4f) else Color(0xFF00E676)),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text(if (isJoined) "Leave Event" else "Register / Join Event", fontSize = 9.sp, color = if (isJoined) Color.White else Color.Black)
                                                    }

                                                    Button(
                                                        onClick = {
                                                            onTriggerToast("Copied event invite link to clipboard!")
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.05f)),
                                                        modifier = Modifier.weight(0.5f)
                                                    ) {
                                                        Text("Share", fontSize = 9.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Room Invitations Manager", Icons.Default.PersonAdd)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = inviteTargetUserIdInput,
                                        onValueChange = { inviteTargetUserIdInput = it },
                                        placeholder = { Text("Invite User ID (friend, family, agency)...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Invite type: ", color = Color.White, fontSize = 10.sp)
                                        listOf("Camera", "Speaker", "Event").forEach { type ->
                                            val isSelected = inviteTargetRoleType == type
                                            Box(
                                                modifier = Modifier
                                                    .background(if (isSelected) Color(0xFF00E5FF) else Color.White.copy(0.05f), RoundedCornerShape(4.dp))
                                                    .clickable { inviteTargetRoleType = type }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(type, color = if (isSelected) Color.Black else Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if (inviteTargetUserIdInput.isNotBlank()) {
                                                val newInvite = OutboundInvitation("inv_${(10..99).random()}", inviteTargetUserIdInput, "Pending", inviteTargetRoleType)
                                                outboundInvites.add(newInvite)
                                                personalNotificationHistoryLog.add("Sent $inviteTargetRoleType invitation to $inviteTargetUserIdInput")
                                                onTriggerToast("Lounge invitation dispatched! Status: Pending.")
                                                inviteTargetUserIdInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Send Room Invitation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text("Outbound Invitation History & Status:", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    outboundInvites.forEach { invite ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White.copy(0.03f), RoundedCornerShape(6.dp))
                                                .padding(6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("@${invite.invitedUserId}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("Type: ${invite.type}", color = Color(0xFF00E5FF), fontSize = 9.sp)
                                            Text(
                                                text = invite.status,
                                                color = when (invite.status) {
                                                    "Accepted" -> Color(0xFF00E676)
                                                    "Pending" -> Color(0xFFFFB300)
                                                    else -> Color.Red
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Lounge pinned Announcements", Icons.Default.Campaign)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = newAnnouncementTitle,
                                        onValueChange = { newAnnouncementTitle = it },
                                        placeholder = { Text("Announcement Title...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    OutlinedTextField(
                                        value = newAnnouncementContent,
                                        onValueChange = { newAnnouncementContent = it },
                                        placeholder = { Text("Pin notice text content...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Button(
                                        onClick = {
                                            if (newAnnouncementTitle.isNotBlank()) {
                                                val newAnn = PinnedAnnouncement("ann_${(10..99).random()}", newAnnouncementTitle, newAnnouncementContent)
                                                pinnedAnnouncementsList.add(newAnn)
                                                personalNotificationHistoryLog.add("Announcement pinned: '$newAnnouncementTitle'")
                                                onTriggerToast("Notice Board update pinned successfully!")
                                                newAnnouncementTitle = ""
                                                newAnnouncementContent = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Publish & Pin Announcement notice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    pinnedAnnouncementsList.forEach { ann ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E132E)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("📌 Pinned: " + ann.title, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text(
                                                        text = "Unpin",
                                                        color = Color.Red.copy(0.7f),
                                                        fontSize = 9.sp,
                                                        modifier = Modifier.clickable {
                                                            pinnedAnnouncementsList.remove(ann)
                                                            onTriggerToast("Announcement unpinned")
                                                        }
                                                    )
                                                }
                                                Text(ann.content, color = Color.White, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Followed Rooms & Notifications Setup", Icons.Default.Bookmark)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    followedRoomsList.forEach { rm ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White.copy(0.04f), RoundedCornerShape(6.dp))
                                                .padding(6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("⭐ $rm", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    "Mute",
                                                    color = Color.White.copy(0.5f),
                                                    fontSize = 9.sp,
                                                    modifier = Modifier.clickable {
                                                        onTriggerToast("Muted notifications from $rm")
                                                    }
                                                )
                                                Text(
                                                    "Unfollow",
                                                    color = Color.Red.copy(0.7f),
                                                    fontSize = 9.sp,
                                                    modifier = Modifier.clickable {
                                                        followedRoomsList.remove(rm)
                                                        onTriggerToast("Unfollowed room: $rm")
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Quiet Hours Manager & Silent settings", Icons.Default.Schedule)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Enable Quiet Hours", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("Silence voice invitations during specific times", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                        }
                                        Switch(checked = quietHoursEnabled, onCheckedChange = { quietHoursEnabled = it })
                                    }

                                    OutlinedTextField(
                                        value = quietHoursRangeText,
                                        onValueChange = { quietHoursRangeText = it },
                                        placeholder = { Text("e.g. 22:00 - 08:00...", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Priority Notifications Only", color = Color.White, fontSize = 10.sp)
                                        Switch(checked = quietHoursPriorityOnly, onCheckedChange = { quietHoursPriorityOnly = it })
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Personal Notification & Invitation history", Icons.Default.Refresh)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Action log of invites dispatched/received:", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                        TextButton(onClick = {
                                            personalNotificationHistoryLog.clear()
                                            onTriggerToast("My personal notification logs flushed.")
                                        }) {
                                            Text("Clear History Logs", color = Color.Red, fontSize = 9.sp)
                                        }
                                    }

                                    personalNotificationHistoryLog.forEach { log ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White.copy(0.02f), RoundedCornerShape(4.dp))
                                                .padding(4.dp)
                                        ) {
                                            Text("✅ $log", color = Color.White.copy(0.7f), fontSize = 9.sp)
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(0.08f))

                                SuiteSectionHeader("Platform Owner Controls & Templates configuration", Icons.Default.Settings)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(0.04f), RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Global Platform configurations (Admins only):", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Default reminder trigger", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                        Text("15 Minutes Before", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Max concurrent scheduled rooms", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                        Text("5 active slots/host", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Broadcaster alert template priorities", color = Color.White.copy(0.6f), fontSize = 9.sp)
                                        Text("CRITICAL_IMMEDIATE", color = Color.Red, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2))
            ) {
                Text("Close Settings Suite", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// --- SUITE UI HELPERS ---

@Composable
fun SuiteSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
        Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun StatisticItem(label: String, value: String) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .background(Color.White.copy(0.04f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(label, color = Color.White.copy(0.5f), fontSize = 9.sp)
            Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TimelineEntry(time: String, action: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("• [$time]", color = Color(0xFF8E2DE2), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(action, color = Color.White.copy(0.8f), fontSize = 9.sp)
    }
}

@Composable
fun CapacityBar(label: String, current: Int, max: Int, color: Color) {
    val progress = current.toFloat() / max
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White.copy(0.7f), fontSize = 10.sp)
            Text("$current/$max occupied", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color.White.copy(0.1f), RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
fun AnalyticMetricCell(label: String, value: String) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .background(Color.White.copy(0.04f), RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Text(label, color = Color.White.copy(0.5f), fontSize = 9.sp)
        Text(value, color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PermissionCheckrow(
    label: String,
    permissionKey: String,
    permissionsMap: Map<String, Boolean>,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White.copy(0.8f), fontSize = 10.sp)
        Checkbox(
            checked = permissionsMap[permissionKey] == true,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF8E2DE2))
        )
    }
}

@Composable
fun SpamControlToggle(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White.copy(0.8f), fontSize = 10.sp)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
        )
    }
}

@Composable
fun NotificationToggleRow(
    label: String,
    key: String,
    preferencesMap: MutableMap<String, Boolean>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White.copy(0.8f), fontSize = 10.sp)
        Checkbox(
            checked = preferencesMap[key] == true,
            onCheckedChange = { preferencesMap[key] = it },
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00E5FF))
        )
    }
}

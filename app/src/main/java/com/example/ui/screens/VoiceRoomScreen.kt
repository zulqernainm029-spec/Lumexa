package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.delay
import com.example.data.VoiceRoom
import com.example.ui.ConnectLiveViewModel
import com.example.ui.RoomChatMessage
import com.example.ui.components.FloatingHeartEffect
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.PremiumGradientBrush

@Composable
fun VoiceRoomsTab(viewModel: ConnectLiveViewModel) {
    val rooms by viewModel.voiceRooms.collectAsState()
    val zegoAppId by viewModel.zegoAppId.collectAsState()
    val zegoUserId by viewModel.zegoUserId.collectAsState()
    val zegoRoomId by viewModel.zegoRoomId.collectAsState()
    val zegoTempToken by viewModel.zegoTempToken.collectAsState()
    val isZegoConfigured by viewModel.isZegoConfigured.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    var showZegoConfigModal by remember { mutableStateOf(false) }

    val categories = listOf("All", "Music", "Language", "Tech", "Chat")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        // ZEGOCLOUD Engine Link Status Card
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clickable { showZegoConfigModal = true }
                .testTag("zego_engine_status_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF00E676).copy(0.15f), CircleShape)
                            .border(1.dp, Color(0xFF00E676), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "ZEGOCLOUD Engine",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ZEGOCLOUD Voice Engine",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF00E676), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text("CONNECTED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "App ID: $zegoAppId • User: $zegoUserId • Token Linked 🔐",
                            color = Color.White.copy(0.7f),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Config",
                    tint = Color.White.copy(0.6f)
                )
            }
        }

        // Categories list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0xFF8E2DE2) else Color.White.copy(0.05f))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("category_$cat"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Active listing
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("voice_rooms_list"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filtered = if (selectedCategory == "All") rooms else rooms.filter { it.categories.contains(selectedCategory) }
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No active rooms in this category.", color = Color.White.copy(0.4f))
                    }
                }
            } else {
                items(filtered) { room ->
                    VoiceRoomCard(room = room, onJoin = { viewModel.joinVoiceRoom(room.id) })
                }
            }
        }

        // ZEGOCLOUD Voice System Config & Token Details Modal
        if (showZegoConfigModal) {
            AlertDialog(
                onDismissRequest = { showZegoConfigModal = false },
                containerColor = Color(0xFF130A2A),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "ZEGOCLOUD RTC Voice Engine",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "Your ZEGOCLOUD real-time voice and video system is configured and linked with active credentials:",
                            color = Color.White.copy(0.7f),
                            fontSize = 12.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(0.05f))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("App ID:", color = Color.White.copy(0.6f), fontSize = 11.sp)
                                    Text(zegoAppId, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                HorizontalDivider(color = Color.White.copy(0.1f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Server Secret:", color = Color.White.copy(0.6f), fontSize = 11.sp)
                                    Text("6374c41e...97594842", color = Color.White, fontSize = 11.sp)
                                }
                                HorizontalDivider(color = Color.White.copy(0.1f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("User ID:", color = Color.White.copy(0.6f), fontSize = 11.sp)
                                    Text(zegoUserId, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                HorizontalDivider(color = Color.White.copy(0.1f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Room ID:", color = Color.White.copy(0.6f), fontSize = 11.sp)
                                    Text(zegoRoomId, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                HorizontalDivider(color = Color.White.copy(0.1f))
                                Column {
                                    Text("Active Temporary Token:", color = Color.White.copy(0.6f), fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        zegoTempToken.take(35) + "...",
                                        color = Color(0xFF00E676),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00E676).copy(0.12f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Voice stream pipeline linked! Audio latency < 45ms. Voice room is operational.",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showZegoConfigModal = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2))
                    ) {
                        Text("Close", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
fun VoiceRoomCard(room: VoiceRoom, onJoin: () -> Unit) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onJoin() }
            .testTag("room_card_${room.id}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(52.dp)) {
                AsyncImage(
                    model = room.hostAvatar,
                    contentDescription = room.hostName,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFF8E2DE2), CircleShape)
                        .border(1.5.dp, Color(0xFF0F0C21), CircleShape)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Host: ${room.hostName}",
                        color = Color.White.copy(0.5f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(0.08f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(room.categories, fontSize = 8.sp, color = Color.White.copy(0.7f))
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(room.listenersCount.toString(), color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text("Popularity: ${room.rankingValue}", fontSize = 9.sp, color = Color.White.copy(0.3f))
            }
        }
    }
}

// Full screen joined voice room
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinedVoiceRoomScreen(viewModel: ConnectLiveViewModel) {
    val room by viewModel.activeRoom.collectAsState()
    val isMuted by viewModel.activeRoomMuteStatus.collectAsState()
    val isHandRaised by viewModel.isHandRaised.collectAsState()
    val roomMessages by viewModel.roomChatMessages.collectAsState()
    val giftEffect by viewModel.activeGiftEffect.collectAsState()
    val coinBalance by viewModel.coinBalance.collectAsState()

    var showGiftSheet by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }

    val activeRoom = room ?: return

    // --- ENHANCED VIDEO ROOM & CAMERA SEAT SYSTEM STATES ---
    var roomMode by remember { mutableStateOf("Voice + Video") } // Voice Only, Video Only, Voice + Video
    var cameraLayoutSize by remember { mutableIntStateOf(6) } // 1, 2, 4, 6, 9, 12, 15 seats
    var maxSimultaneousCameras by remember { mutableIntStateOf(6) }
    var userCameraOn by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var cameraRotation by remember { mutableIntStateOf(0) }
    var isFullscreenVideo by remember { mutableStateOf(false) }
    var currentVideoQuality by remember { mutableStateOf("720p HD") } // 360p, 480p, 720p, 1080p (Premium)

    // Camera Effects
    var beautyFilterEnabled by remember { mutableStateOf(true) }
    var skinSmoothing by remember { mutableFloatStateOf(0.5f) }
    var brightness by remember { mutableFloatStateOf(0.6f) }
    var contrast by remember { mutableFloatStateOf(0.5f) }
    var backgroundBlur by remember { mutableStateOf(false) }
    var virtualBackground by remember { mutableStateOf("None") } // None, Cozy Café, Neon Studio, Sunset Beach

    // Modals and Overlays
    var showPreviewSheet by remember { mutableStateOf(false) }
    var joiningSeatIndex by remember { mutableIntStateOf(-1) }
    var showOwnerPanel by remember { mutableStateOf(false) }
    var showSidebar by remember { mutableStateOf(false) }
    var showMemberPanel by remember { mutableStateOf(false) }
    var showInvitePanel by remember { mutableStateOf(false) }
    var showQueuePanel by remember { mutableStateOf(false) }
    var showDiagnostics by remember { mutableStateOf(false) }
    var showAdvancedHub by remember { mutableStateOf(false) }
    var welcomeBannerUser by remember { mutableStateOf<WelcomeBannerData?>(null) }

    LaunchedEffect(welcomeBannerUser) {
        if (welcomeBannerUser != null) {
            delay(5000)
            welcomeBannerUser = null
        }
    }

    // Seat state management
    var userOnSeatIndex by remember { mutableIntStateOf(-1) } // -1 if not on seat
    val lockedSeats = remember { mutableStateListOf(4) } // Index of locked seats (e.g. seat 4 initially locked)
    var selectedSeatIndexForAction by remember { mutableIntStateOf(-1) }

    // Dashboard Tabs
    var activeTab by remember { mutableStateOf("chat") } // chat, controls, sound_fx, logs

    // Music & Sound Mixer states
    var isMusicPlaying by remember { mutableStateOf(false) }
    var selectedMusicTrack by remember { mutableStateOf("Acoustic Sunset") }
    var musicVolume by remember { mutableFloatStateOf(0.4f) }
    var voiceVolume by remember { mutableFloatStateOf(0.8f) }
    var selectedAudioEffect by remember { mutableStateOf("Original") } // Original, Studio, Concert, Bass Boost, Echo, Reverb, Noise Suppression

    // Promotion & Boost States
    var popularityScore by remember { mutableIntStateOf(12850) }
    var boostActive by remember { mutableStateOf(false) }
    var promotionDuration by remember { mutableStateOf("6 Hours") } // 1h, 6h, 12h, 24h, 3d, 7d

    // Queue & Invitation Lists
    val waitingQueue = remember { mutableStateListOf("user_sarah_99", "user_liam_tech", "user_jake_sing") }
    val invitationsHistory = remember {
        mutableStateListOf(
            CameraInvitation("user_sofia", "Sofia Rodriguez", "Friend", "Accepted"),
            CameraInvitation("user_liam", "Liam Vance", "Host", "Pending"),
            CameraInvitation("user_jessica", "Jessica Alba", "Agency Member", "Expired")
        )
    }

    // Diagnostics Telemetry
    var networkPing by remember { mutableIntStateOf(42) }
    var packetLoss by remember { mutableStateOf("0.1%") }
    var currentBitrate by remember { mutableStateOf("184 kbps") }

    // Notification Alerts Toast Simulation
    var activeNotification by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(activeNotification) {
        if (activeNotification != null) {
            delay(3000)
            activeNotification = null
        }
    }

    // Simulated network variations to show automatic quality adjustments
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000)
            networkPing = (30..75).random()
            currentBitrate = if (networkPing < 45) "720p HD - 240 kbps" else "480p SD - 120 kbps"
        }
    }

    // Seat data structures helper
    val seatNames = listOf(
        "Host: Chloe",
        "Co-Host: Liam",
        "Sofia (Singer)",
        "Empty Seat",
        "Locked Seat",
        "Empty Seat",
        "Audience Mic",
        "Audience Mic",
        "Empty Seat",
        "Premium Seat",
        "VIP Lounge",
        "Agency Space",
        "Family Seat",
        "Host Spot",
        "Co-Host Spot"
    )

    val seatAvatars = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070211))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- HEADER PANEL (JS. EVERY BUTTON FUNCTION) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C071A))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.leaveVoiceRoom() },
                        modifier = Modifier.testTag("leave_room_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Exit Room", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeRoom.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFB300), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("VIP", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                        Text(
                            text = "Mode: $roomMode • Seats: $cameraLayoutSize",
                            color = Color(0xFF00E676),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Interactive Quick Stats Toolbar
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Popularity Metric
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(0.08f), RoundedCornerShape(8.dp))
                            .clickable { showSidebar = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(12.dp))
                            Text(popularityScore.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Network indicator (Diagnostics trigger)
                    IconButton(
                        onClick = { showDiagnostics = !showDiagnostics },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Wifi, contentDescription = "Network Quality", tint = if (networkPing < 50) Color(0xFF00E676) else Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    }

                    // Advanced Hub Button (JT, JU, JV)
                    IconButton(
                        onClick = { showAdvancedHub = true },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF8E2DE2).copy(0.2f), CircleShape)
                            .border(1.dp, Color(0xFF00E5FF), CircleShape)
                            .testTag("advanced_hub_button")
                    ) {
                        Icon(Icons.Default.Dashboard, contentDescription = "Advanced Systems Hub", tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                    }

                    // Room Owner panel trigger
                    IconButton(
                        onClick = { showOwnerPanel = true },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(0.1f), CircleShape)
                            .testTag("owner_panel_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Owner Control Panel", tint = Color.White, modifier = Modifier.size(16.dp))
                    }

                    // Sidebar trigger
                    IconButton(
                        onClick = { showSidebar = true },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(0.1f), CircleShape)
                            .testTag("room_sidebar_button")
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Room Sidebar", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Simulated Notification Banner Overlay if active
            AnimatedVisibility(
                visible = activeNotification != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF8E2DE2))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = activeNotification ?: "",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Animated Welcome Experience Overlay (JT)
            AnimatedVisibility(
                visible = welcomeBannerUser != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                welcomeBannerUser?.let { user ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF8E2DE2).copy(0.9f),
                                        Color(0xFF4A00E0).copy(0.9f),
                                        Color(0xFF00E5FF).copy(0.9f)
                                    )
                                ),
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Sparkling glowing avatar border
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .border(1.5.dp, Color(0xFFFFD700), CircleShape)
                            ) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "✨ NEW USER JOINED THE LOUNGE ✨",
                                    color = Color(0xFF00E5FF),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "${user.nickname} (@${user.userId})",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Dynamic badge list
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (user.isVip) {
                                    BadgeTag("VIP", Color(0xFFFFB300), Color.Black)
                                }
                                if (user.isHost) {
                                    BadgeTag("HOST", Color(0xFF00E676), Color.Black)
                                }
                                if (user.isFamily) {
                                    BadgeTag("FAMILY", Color(0xFFE040FB), Color.White)
                                }
                                if (user.isAgency) {
                                    BadgeTag("AGENCY", Color(0xFF2979FF), Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // --- CAMERA SEATS / SPEAKERS STAGE (IW, IV, JN, JO) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .background(Color(0xFF090415))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (isFullscreenVideo && userOnSeatIndex != -1 && userCameraOn) {
                    // FULL SCREEN VIEWPORT SIMULATION (JO)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(16.dp))
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
                            contentDescription = "Full Screen Video Feed",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(cameraRotation.toFloat())
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(0.8f))
                                    )
                                )
                        )
                        // Info Overlays
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text("Alex Mercer (You)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("User ID: me_9281 • Quality: $currentVideoQuality (Beauty ON)", color = Color(0xFF00E5FF), fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = { isFullscreenVideo = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(Color.Black.copy(0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Exit Full Screen", tint = Color.White)
                        }
                    }
                } else {
                    // DYNAMIC GRID LAYOUT OF CAMERA SEATS (1, 2, 4, 6, 9, 12, 15 Cameras)
                    val cols = when {
                        cameraLayoutSize <= 2 -> 1
                        cameraLayoutSize <= 6 -> 2
                        else -> 3
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(cols),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cameraLayoutSize) { index ->
                            val isLocked = lockedSeats.contains(index)
                            val isMe = index == userOnSeatIndex
                            val isOccupied = index < 3 || isMe

                            val userName = when {
                                isMe -> "Alex Mercer (You)"
                                index < 3 -> seatNames[index]
                                else -> "Vacant Seat"
                            }

                            val userAvatar = when {
                                isMe -> "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80"
                                index < 3 -> seatAvatars[index]
                                else -> ""
                            }

                            val isCameraActive = when {
                                isMe -> userCameraOn
                                index == 1 -> true // Liam camera simulated ON
                                else -> false
                            }

                            val speakingStatus = index == 0 || (isMe && !isMuted)

                            GlassmorphicCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(if (cameraLayoutSize > 6) 90.dp else 115.dp)
                                    .border(
                                        width = if (speakingStatus) 1.5.dp else 1.dp,
                                        color = if (speakingStatus) Color(0xFF00E676) else Color.White.copy(0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        if (isLocked) {
                                            activeNotification = "This camera seat is locked by the Room Owner."
                                        } else if (isOccupied) {
                                            selectedSeatIndexForAction = index
                                        } else {
                                            joiningSeatIndex = index
                                            showPreviewSheet = true
                                        }
                                    }
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    if (isCameraActive && (roomMode != "Voice Only")) {
                                        // LIVE CAMERA ON DISPLAY (JO, JO)
                                        AsyncImage(
                                            model = userAvatar,
                                            contentDescription = "Live video stream",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .rotate(if (isMe) cameraRotation.toFloat() else 0f)
                                        )
                                        // Shadow cover
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(Color.Transparent, Color.Black.copy(0.6f))
                                                    )
                                                )
                                        )
                                        // Live Indicator tag
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(6.dp)
                                                .background(Color.Black.copy(0.6f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .background(Color(0xFF00E676), CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("LIVE", color = Color(0xFF00E676), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Camera Quality Overlay (JO)
                                        Text(
                                            text = if (isMe) currentVideoQuality else "720p HD",
                                            color = Color.White.copy(0.7f),
                                            fontSize = 7.sp,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                        )
                                    } else {
                                        // CAMERA OFF DISPLAY (AVATAR ONLY) (JO)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color(0xFF130D2E)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isOccupied) {
                                                AsyncImage(
                                                    model = userAvatar,
                                                    contentDescription = "Avatar",
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .clip(CircleShape)
                                                        .border(1.dp, Color.White.copy(0.2f), CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else if (isLocked) {
                                                Icon(Icons.Default.Lock, contentDescription = "Locked Seat", tint = Color.Red.copy(0.7f), modifier = Modifier.size(24.dp))
                                            } else {
                                                Icon(Icons.Default.VideocamOff, contentDescription = "Vacant", tint = Color.White.copy(0.2f), modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }

                                    // Avatar thumbnail in corner
                                    if (isCameraActive && isOccupied) {
                                        AsyncImage(
                                            model = userAvatar,
                                            contentDescription = "Profile Mini",
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(4.dp)
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .border(0.5.dp, Color.White, CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    // Overlay texts
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = userName,
                                                color = if (speakingStatus) Color(0xFF00E676) else Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (index == 0) {
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFFF5252), RoundedCornerShape(3.dp))
                                                        .padding(horizontal = 3.dp, vertical = 0.5.dp)
                                                ) {
                                                    Text("Owner", fontSize = 6.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                        }
                                        Text(
                                            text = if (isLocked) "Locked Seat" else if (isOccupied) "ID: user_${index}_active" else "Tap to Join",
                                            color = Color.White.copy(0.5f),
                                            fontSize = 7.sp
                                        )
                                    }

                                    // Mute Status indicator inside seat
                                    if (isOccupied && !isMe && index == 1) { // Liam is simulated muted
                                        Icon(
                                            imageVector = Icons.Default.MicOff,
                                            contentDescription = "Muted",
                                            tint = Color.Red,
                                            modifier = Modifier
                                                .size(12.dp)
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- BOTTOM INFO BAR AND AUDIO PRESETS Mixer (JN) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C071A))
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Audio: $selectedAudioEffect Presets • BGM: ${if (isMusicPlaying) selectedMusicTrack else "Off"}",
                        color = Color.White.copy(0.6f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Diagnostics: ping ${networkPing}ms • Loss: $packetLoss",
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp
                    )
                }
            }

            // --- NAVIGATION TABS CONTROLLER (JQ, JR) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF130D2E))
            ) {
                listOf(
                    TabConfig("chat", "Chat Panel", Icons.Default.Chat),
                    TabConfig("controls", "My Camera", Icons.Default.Videocam),
                    TabConfig("invites", "Invitations", Icons.Default.Queue),
                    TabConfig("music", "BGM Mixer", Icons.Default.MusicNote),
                    TabConfig("logs", "Activity Logs", Icons.Default.List)
                ).forEach { tab ->
                    val isSelected = activeTab == tab.id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = tab.id }
                            .background(if (isSelected) Color(0xFF8E2DE2).copy(0.15f) else Color.Transparent)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.label.split(" ")[0],
                                color = if (isSelected) Color.White else Color.White.copy(0.4f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // --- TAB CONTENT ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
                    .background(Color(0xFF080415))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                when (activeTab) {
                    "chat" -> {
                        // SCROLLABLE CHAT PANEL WITH SYSTEM ALERTS
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFE082).copy(0.12f), RoundedCornerShape(8.dp))
                                        .border(0.5.dp, Color(0xFFFFB300).copy(0.3f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "📢 SYSTEM NOTICE: Welcome to Lumexa Video Hub. Keep communications supportive and professional! Room owned by Chloe.",
                                        color = Color(0xFFFFD54F),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            items(roomMessages) { msg ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(0.02f))
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = "${msg.senderName}: ",
                                        fontWeight = FontWeight.Bold,
                                        color = if (msg.senderId == "system") Color(0xFFFFB300) else Color(0xFFD0BCFF),
                                        fontSize = 11.sp
                                    )
                                    Text(msg.content, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    "controls" -> {
                        // --- CAMERA CONTROLS (IW, JO) ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Active Seat Camera Options", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                            if (userOnSeatIndex == -1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(0.04f), RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("You are currently in the Audience. Tap an empty Camera Seat to join the stream stage!", color = Color.White.copy(0.5f), fontSize = 11.sp, textAlign = TextAlign.Center)
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { userCameraOn = !userCameraOn },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = if (userCameraOn) Color(0xFFFF5252) else Color(0xFF00E676))
                                    ) {
                                        Icon(imageVector = if (userCameraOn) Icons.Default.VideocamOff else Icons.Default.Videocam, contentDescription = null, tint = Color.Black)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (userCameraOn) "Turn Cam OFF" else "Turn Cam ON", color = Color.Black, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { isFrontCamera = !isFrontCamera },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f))
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isFrontCamera) "Rear Cam" else "Front Cam", color = Color.White, fontSize = 11.sp)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { cameraRotation = (cameraRotation + 90) % 360 },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f))
                                    ) {
                                        Icon(Icons.Default.RotateRight, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rotate Cam", color = Color.White, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { isFullscreenVideo = !isFullscreenVideo },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                                    ) {
                                        Icon(Icons.Default.Tv, contentDescription = null, tint = Color.Black)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Full Screen", color = Color.Black, fontSize = 11.sp)
                                    }
                                }

                                Button(
                                    onClick = {
                                        userOnSeatIndex = -1
                                        userCameraOn = false
                                        activeNotification = "Exited Camera Seat successfully."
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.2f))
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Exit Camera Seat", color = Color.Red, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    "invites" -> {
                        // --- CAMERA INVITATION & QUEUE PANEL (IW) ---
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Camera Entry & Queue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TextButton(onClick = { showInvitePanel = true }) {
                                        Text("+ Invite User", color = Color(0xFF00E5FF), fontSize = 10.sp)
                                    }
                                    TextButton(onClick = { showQueuePanel = true }) {
                                        Text("Queue (${waitingQueue.size})", color = Color(0xFF00E676), fontSize = 10.sp)
                                    }
                                }
                            }

                            Text("Recent Live Stream Invitations:", color = Color.White.copy(0.6f), fontSize = 10.sp)
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(invitationsHistory) { invite ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White.copy(0.04f), RoundedCornerShape(6.dp))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(invite.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("Role type: ${invite.roleType}", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    when (invite.status) {
                                                        "Accepted" -> Color(0xFF00E676).copy(0.2f)
                                                        "Pending" -> Color(0xFFFFB300).copy(0.2f)
                                                        else -> Color.White.copy(0.1f)
                                                    },
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = invite.status,
                                                color = when (invite.status) {
                                                    "Accepted" -> Color(0xFF00E676)
                                                    "Pending" -> Color(0xFFFFB300)
                                                    else -> Color.White.copy(0.5f)
                                                },
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "music" -> {
                        // --- BACKGROUND MUSIC & SOUND MIXER (IV, JN) ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Room Sound Mixer & Playlists", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                IconButton(
                                    onClick = { isMusicPlaying = !isMusicPlaying },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isMusicPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                                        contentDescription = "Play BGM",
                                        tint = Color(0xFF00E5FF)
                                    )
                                }
                            }

                            // BGM Track choice
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Acoustic Sunset", "Synthwave Chill", "Lofi Beats").forEach { track ->
                                    val isCurrent = selectedMusicTrack == track
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isCurrent) Color(0xFF8E2DE2) else Color.White.copy(0.04f))
                                            .clickable {
                                                selectedMusicTrack = track
                                                isMusicPlaying = true
                                                activeNotification = "Playing background track: $track"
                                            }
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(track, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Volume sliders
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("BGM Music Volume: ${(musicVolume * 100).toInt()}%", color = Color.White.copy(0.7f), fontSize = 10.sp)
                                }
                                Slider(
                                    value = musicVolume,
                                    onValueChange = { musicVolume = it },
                                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                                )
                            }

                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Speakers Voice Volume: ${(voiceVolume * 100).toInt()}%", color = Color.White.copy(0.7f), fontSize = 10.sp)
                                }
                                Slider(
                                    value = voiceVolume,
                                    onValueChange = { voiceVolume = it },
                                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00E676), activeTrackColor = Color(0xFF00E676))
                                )
                            }

                            // Vocal effects presets
                            Text("Audio Engine Reverb & Vocal Effects Presets", color = Color.White.copy(0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Original", "Studio", "Concert", "Bass Boost").forEach { fx ->
                                    val isSel = selectedAudioEffect == fx
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) Color(0xFF00E676).copy(0.2f) else Color.White.copy(0.04f))
                                            .border(1.dp, if (isSel) Color(0xFF00E676) else Color.Transparent, RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedAudioEffect = fx
                                                activeNotification = "Applied Voice FX: $fx"
                                            }
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(fx, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    "logs" -> {
                        // --- ROOM AUDIT LOGS & PROMOTION HISTORY ---
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Room Audit Logs & Analytics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Icon(Icons.Default.Analytics, contentDescription = null, tint = Color.White.copy(0.4f), modifier = Modifier.size(16.dp))
                            }

                            Text("Automatic room ranking score is updated dynamically based on gift volume and live camera usage metrics.", color = Color.White.copy(0.5f), fontSize = 9.sp)

                            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                item {
                                    AuditLogEntry("21:03:10", "Chloe Dubois created camera seat grid.")
                                }
                                item {
                                    AuditLogEntry("21:03:22", "Liam Vance activated front camera viewport stream.")
                                }
                                item {
                                    AuditLogEntry("21:03:45", "Platform Owner restricted seat 4 security access.")
                                }
                                item {
                                    AuditLogEntry("21:03:59", "Sofia Rodriguez accepted camera invitation successfully.")
                                }
                                item {
                                    AuditLogEntry("21:04:12", "Emergency Lock trigger released by Owner.")
                                }
                            }
                        }
                    }
                }
            }

            // --- BOTTOM ACTIONS BAR (JS. EVERY BUTTON FUNCTION) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C071A))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mic Trigger
                IconButton(
                    onClick = { viewModel.toggleMuteVoiceRoom() },
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (isMuted) Color(0xFFFF4D4D) else Color.White.copy(0.1f), CircleShape)
                        .testTag("mute_mic_button")
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Camera Shortcut Preview Button (IW, JO)
                IconButton(
                    onClick = {
                        if (userOnSeatIndex == -1) {
                            joiningSeatIndex = 3 // pick seat 3 by default
                            showPreviewSheet = true
                        } else {
                            userCameraOn = !userCameraOn
                            activeNotification = if (userCameraOn) "Live Camera Stream ON!" else "Live Camera stream paused."
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (userCameraOn) Color(0xFF00E676) else Color.White.copy(0.1f), CircleShape)
                        .testTag("room_camera_button")
                ) {
                    Icon(
                        imageVector = if (userCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Camera Toggle",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Comment input
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = { Text("Post chat to room...", color = Color.White.copy(0.4f), fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8E2DE2)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(21.dp)
                )

                // Send icon
                IconButton(
                    onClick = {
                        if (chatInput.isNotBlank()) {
                            viewModel.sendVoiceRoomMessage(chatInput)
                            chatInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .background(PremiumGradientBrush, CircleShape)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
                }

                // Gift launcher
                IconButton(
                    onClick = { showGiftSheet = true },
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFFFFD700), CircleShape)
                        .testTag("open_gift_sheet")
                ) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = "Gifts Shop", tint = Color.Black, modifier = Modifier.size(18.dp))
                }
            }
        }

        // --- CAMERA PREVIEW MODAL BOTTOM SHEET (IW) ---
        if (showPreviewSheet) {
            AlertDialog(
                onDismissRequest = { showPreviewSheet = false },
                containerColor = Color(0xFF140D26),
                title = { Text("Camera Stream Setup & Effects", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Live Preview Window:", color = Color.White.copy(0.6f), fontSize = 11.sp)

                        // SIMULATED CAMERA PREVIEW (IW)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
                                contentDescription = "My Preview Feed",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Quality stamp
                            Text(
                                text = "Preview ($currentVideoQuality)",
                                color = Color.White,
                                fontSize = 8.sp,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color.Black.copy(0.6f))
                                    .padding(4.dp)
                            )
                            // Center blinking blue dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF00E5FF), CircleShape)
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                            )
                        }

                        // BEAUTY EFFECTS CONTROLS (IW, JO)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = beautyFilterEnabled,
                                onCheckedChange = { beautyFilterEnabled = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00E676))
                            )
                            Text("Enable Beauty Skin Smoothing Filter", color = Color.White, fontSize = 11.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = backgroundBlur,
                                onCheckedChange = { backgroundBlur = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00E676))
                            )
                            Text("Enable Virtual Background Blur", color = Color.White, fontSize = 11.sp)
                        }

                        // Virtual backgrounds picker
                        Text("Select Virtual Theme Background:", color = Color.White.copy(0.7f), fontSize = 10.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("None", "Café", "Sunset", "Beach").forEach { bg ->
                                val isCurrent = virtualBackground == bg
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isCurrent) Color(0xFF00E5FF) else Color.White.copy(0.08f))
                                        .clickable { virtualBackground = bg }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(bg, color = if (isCurrent) Color.Black else Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Video Resolution
                        Text("Select Output Video Quality (Platform config):", color = Color.White.copy(0.7f), fontSize = 10.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("360p", "480p", "720p", "1080p").forEach { quality ->
                                val isCurrent = currentVideoQuality.startsWith(quality)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isCurrent) Color(0xFF00E676) else Color.White.copy(0.08f))
                                        .clickable {
                                            currentVideoQuality = if (quality == "1080p") "1080p (Premium)" else "$quality HD"
                                        }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(quality, color = if (isCurrent) Color.Black else Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            userOnSeatIndex = joiningSeatIndex
                            userCameraOn = true
                            showPreviewSheet = false
                            activeNotification = "Joined Camera Seat $joiningSeatIndex with customized filters!"
                            viewModel.sendVoiceRoomMessage("turned on their camera stream in seat $joiningSeatIndex!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                    ) {
                        Text("CONFIRM & STREAM 🎉", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPreviewSheet = false }) {
                        Text("Cancel", color = Color.White.copy(0.6f), fontSize = 11.sp)
                    }
                }
            )
        }

        // --- CAMERA SEAT MODERATION DIALOG (IW, JP) ---
        if (selectedSeatIndexForAction != -1) {
            val index = selectedSeatIndexForAction
            val isMe = index == userOnSeatIndex
            val userName = if (isMe) "Your Seat" else seatNames.getOrElse(index) { "User" }

            AlertDialog(
                onDismissRequest = { selectedSeatIndexForAction = -1 },
                containerColor = Color(0xFF140D26),
                title = { Text("Seat #$index Control ($userName)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Manage participants, mute audio streams, or restrict camera seat visibility on this specific grid tile.", color = Color.White.copy(0.6f), fontSize = 11.sp)

                        if (!isMe) {
                            Button(
                                onClick = {
                                    activeNotification = "Turned OFF user's camera in seat $index according to platform policy."
                                    selectedSeatIndexForAction = -1
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                            ) {
                                Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Turn OFF User Camera", color = Color.White, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    activeNotification = "Muted user's voice feed in seat $index."
                                    selectedSeatIndexForAction = -1
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f))
                            ) {
                                Icon(Icons.Default.MicOff, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mute Microphone", color = Color.White, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    activeNotification = "User moved to Audio Only Seat."
                                    selectedSeatIndexForAction = -1
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f))
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Move to Audio Seat", color = Color.White, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    activeNotification = "Kicked user from camera seat to audience."
                                    selectedSeatIndexForAction = -1
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.2f))
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Red)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Move to Audience", color = Color.Red, fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = {
                                if (lockedSeats.contains(index)) {
                                    lockedSeats.remove(index)
                                    activeNotification = "Unlocked camera seat #$index successfully."
                                } else {
                                    lockedSeats.add(index)
                                    activeNotification = "Locked camera seat #$index. Audience can no longer occupy."
                                }
                                selectedSeatIndexForAction = -1
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
                        ) {
                            Icon(imageVector = if (lockedSeats.contains(index)) Icons.Default.LockOpen else Icons.Default.Lock, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (lockedSeats.contains(index)) "Unlock Camera Seat" else "Lock Camera Seat", color = Color.Black, fontSize = 11.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedSeatIndexForAction = -1 }) {
                        Text("Dismiss", color = Color.White)
                    }
                }
            )
        }

        // --- ROOM OWNER CONTROL PANEL DIALOG (JP) ---
        if (showOwnerPanel) {
            AlertDialog(
                onDismissRequest = { showOwnerPanel = false },
                containerColor = Color(0xFF140D26),
                title = { Text("Room Control Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Room operating layout and security policies:", color = Color.White.copy(0.6f), fontSize = 11.sp)

                        // 1. Room operating modes
                        Text("Active Room Mode Type (Voice vs Video):", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Voice Only", "Video Only", "Voice + Video").forEach { mode ->
                                val isSelected = roomMode == mode
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF8E2DE2) else Color.White.copy(0.06f))
                                        .clickable {
                                            roomMode = mode
                                            activeNotification = "Switched operating room mode to $mode."
                                        }
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(mode, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // 2. Camera Layout seats sizing
                        Text("Configurable Camera Tile Count (Layout):", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(1, 2, 4, 6, 9, 12, 15).take(4).forEach { size ->
                                val isSelected = cameraLayoutSize == size
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF00E676).copy(0.2f) else Color.White.copy(0.06f))
                                        .clickable {
                                            cameraLayoutSize = size
                                            activeNotification = "Adjusted active layout grid to $size tiles."
                                        }
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$size Cam", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(1, 2, 4, 6, 9, 12, 15).drop(4).forEach { size ->
                                val isSelected = cameraLayoutSize == size
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF00E676).copy(0.2f) else Color.White.copy(0.06f))
                                        .clickable {
                                            cameraLayoutSize = size
                                            activeNotification = "Adjusted active layout grid to $size tiles."
                                        }
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$size Cam", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // 3. Lock all seats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Lock All Camera Seats", color = Color.White, fontSize = 11.sp)
                            Switch(
                                checked = lockedSeats.size >= cameraLayoutSize,
                                onCheckedChange = { lockAll ->
                                    if (lockAll) {
                                        lockedSeats.clear()
                                        for (i in 0 until cameraLayoutSize) lockedSeats.add(i)
                                        activeNotification = "Locked all camera seats in room."
                                    } else {
                                        lockedSeats.clear()
                                        activeNotification = "Unlocked all camera seats."
                                    }
                                }
                            )
                        }

                        // 4. Featured Room Promotion Center
                        Text("Featured Room Promotion System", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White.copy(0.05f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        boostActive = !boostActive
                                        if (boostActive) popularityScore += 5000
                                        activeNotification = if (boostActive) "Activated Instant Popularity Boost! +5000 Score" else "Boost off."
                                    }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (boostActive) "🚀 Boost Active" else "Activate Room Boost", color = if (boostActive) Color(0xFF00E676) else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White.copy(0.05f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Popularity: $popularityScore", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Emergency Button
                        Button(
                            onClick = {
                                lockedSeats.clear()
                                for (i in 0 until cameraLayoutSize) lockedSeats.add(i)
                                userCameraOn = false
                                userOnSeatIndex = -1
                                isMusicPlaying = false
                                activeNotification = "🔥 EMERGENCY CONTROL: All mics and cameras disabled!"
                                showOwnerPanel = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("EMERGENCY KILL ROOM", color = Color.White, fontSize = 11.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showOwnerPanel = false }) {
                        Text("Close", color = Color.White)
                    }
                }
            )
        }

        // --- CAMERA INVITATION SETUP OVERLAY (IW) ---
        if (showInvitePanel) {
            AlertDialog(
                onDismissRequest = { showInvitePanel = false },
                containerColor = Color(0xFF140D26),
                title = { Text("Invite User to Live Camera", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        var searchUserId by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = searchUserId,
                            onValueChange = { searchUserId = it },
                            placeholder = { Text("Enter User ID to Invite...", color = Color.White.copy(0.4f), fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = Color(0xFF00E5FF)),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Invite Categories & Members:", color = Color.White.copy(0.6f), fontSize = 11.sp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    invitationsHistory.add(CameraInvitation("friend_99", "Best Friend Liam", "Friend", "Pending"))
                                    activeNotification = "Friend camera invitation dispatched."
                                    showInvitePanel = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Friend", fontSize = 9.sp, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    invitationsHistory.add(CameraInvitation("agency_owner", "Manager Sato", "Agency Member", "Pending"))
                                    activeNotification = "Agency camera invitation dispatched."
                                    showInvitePanel = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Agency", fontSize = 9.sp, color = Color.White)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    invitationsHistory.add(CameraInvitation("fam_3", "Brother Mercer", "Family Member", "Pending"))
                                    activeNotification = "Family camera invitation dispatched."
                                    showInvitePanel = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Family", fontSize = 9.sp, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    if (searchUserId.isNotBlank()) {
                                        invitationsHistory.add(CameraInvitation(searchUserId, "User $searchUserId", "User ID", "Pending"))
                                        activeNotification = "Camera invitation sent to ID: $searchUserId"
                                        showInvitePanel = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Invite ID", fontSize = 9.sp, color = Color.Black)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showInvitePanel = false }) {
                        Text("Back", color = Color.White)
                    }
                }
            )
        }

        // --- QUEUE LIST MANAGEMENT (IW) ---
        if (showQueuePanel) {
            AlertDialog(
                onDismissRequest = { showQueuePanel = false },
                containerColor = Color(0xFF140D26),
                title = { Text("Live Camera Waiting Queue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Managers can approve queue positions or invite users directly to camera spots.", color = Color.White.copy(0.6f), fontSize = 11.sp)

                        if (waitingQueue.isEmpty()) {
                            Text("No users waiting in queue right now.", color = Color.White.copy(0.4f), fontSize = 11.sp)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(waitingQueue) { user ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White.copy(0.04f), RoundedCornerShape(6.dp))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(user, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = {
                                                    waitingQueue.remove(user)
                                                    userOnSeatIndex = 3 // place on seat 3
                                                    userCameraOn = true
                                                    activeNotification = "Approved camera request for: $user"
                                                    showQueuePanel = false
                                                },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(Color(0xFF00E676), CircleShape)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                            }

                                            IconButton(
                                                onClick = {
                                                    waitingQueue.remove(user)
                                                    activeNotification = "Rejected camera request for: $user"
                                                },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(Color.Red.copy(0.2f), CircleShape)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showQueuePanel = false }) {
                        Text("Done", color = Color.White)
                    }
                }
            )
        }

        // --- DIAGNOSTICS & TELEMETRY DIALOG (JN) ---
        if (showDiagnostics) {
            AlertDialog(
                onDismissRequest = { showDiagnostics = false },
                containerColor = Color(0xFF140D26),
                title = { Text("VoIP & Live Stream Diagnostics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Automatic packet recovery, echo cancellation, and network optimization logs are running live.", color = Color.White.copy(0.6f), fontSize = 11.sp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Connection Latency (Ping):", color = Color.White.copy(0.7f), fontSize = 11.sp)
                            Text("${networkPing} ms (Stable)", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimated Packet Loss:", color = Color.White.copy(0.7f), fontSize = 11.sp)
                            Text(packetLoss, color = Color(0xFF00E676), fontSize = 11.sp)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Simulated Bitrate Sizing:", color = Color.White.copy(0.7f), fontSize = 11.sp)
                            Text(currentBitrate, color = Color(0xFF00E5FF), fontSize = 11.sp)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("AEC Echo Cancellation:", color = Color.White.copy(0.7f), fontSize = 11.sp)
                            Text("Enabled (AEC3 High)", color = Color.White, fontSize = 11.sp)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Automatic Gain Control:", color = Color.White.copy(0.7f), fontSize = 11.sp)
                            Text("Enabled (Mobile Adaptive)", color = Color.White, fontSize = 11.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDiagnostics = false }) {
                        Text("OK", color = Color.White)
                    }
                }
            )
        }

        // --- RIGHT SIDEBAR OPERATIONS CENTER (JR) ---
        if (showSidebar) {
            AlertDialog(
                onDismissRequest = { showSidebar = false },
                containerColor = Color(0xFF110A22),
                title = { Text("Room Sidebar Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Active Room Daily Tasks & Rewards:", color = Color.White.copy(0.7f), fontSize = 11.sp)

                        // Senders Rankings
                        Text("🏆 Current Gift Contributors:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("1. Sato Agency Owner", color = Color.White.copy(0.8f), fontSize = 10.sp)
                                Text("💎 12,500", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("2. Alex Mercer (You)", color = Color.White.copy(0.8f), fontSize = 10.sp)
                                Text("💎 4,500", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("3. Sofia Rodriguez", color = Color.White.copy(0.8f), fontSize = 10.sp)
                                Text("💎 2,200", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Daily Mission Task List
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("📋 Daily Engagement Missions:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            TaskRow("Join Any Video Room", true)
                            TaskRow("Stay Online For 15 Minutes", true)
                            TaskRow("Send Virtual rose to Host", false)
                            TaskRow("Enable beauty filter preview", true)
                        }

                        // Featured Banners
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("🔥 Live Platform Banners:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF8E2DE2).copy(0.3f))
                                .padding(8.dp)
                        ) {
                            Text("Summer Live Event: Top broadcasters earn up to 50,000 extra diamond rewards! Ends July 24.", color = Color.White, fontSize = 9.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSidebar = false }) {
                        Text("Close", color = Color.White)
                    }
                }
            )
        }

        // --- ADVANCED SUITE HUB MODAL (JT, JU, JV) ---
        if (showAdvancedHub) {
            AdvancedRoomSuiteHub(
                onDismissRequest = { showAdvancedHub = false },
                currentRoomMode = roomMode,
                onUpdateRoomMode = { roomMode = it },
                currentLayoutSize = cameraLayoutSize,
                onUpdateLayoutSize = { cameraLayoutSize = it },
                popularityScore = popularityScore,
                onUpdatePopularity = { popularityScore = it },
                boostActive = boostActive,
                onUpdateBoostActive = { boostActive = it },
                onTriggerWelcomeBanner = { welcomeBannerUser = it },
                waitingQueue = waitingQueue,
                onTriggerToast = { activeNotification = it }
            )
        }

        // --- VIRTUAL GIFTS SHEET CONTROLLER (JS) ---
        if (showGiftSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGiftSheet = false },
                containerColor = Color(0xFF0D0820),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp, start = 20.dp, end = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Send Virtual Gift", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Circle, contentDescription = "Coins Balance", tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$coinBalance Coins", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val giftItems = listOf(
                        GiftOption("Rose", 10, "🌹"),
                        GiftOption("Ice Cream", 20, "🍦"),
                        GiftOption("Love Arrow", 50, "💘"),
                        GiftOption("Super Tesla", 200, "⚡"),
                        GiftOption("Luxury Yacht", 500, "🚢"),
                        GiftOption("Gold Mansion", 1000, "🏰")
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(giftItems) { gift ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(0.04f))
                                    .clickable {
                                        if ((coinBalance ?: 0) >= gift.cost) {
                                            viewModel.sendGiftToFriend(
                                                activeRoom.hostId,
                                                activeRoom.hostName,
                                                gift.cost,
                                                gift.name
                                            )
                                            viewModel.sendVoiceRoomMessage("sent a '${gift.icon} ${gift.name}' gift to the stage!")
                                            popularityScore += gift.cost * 10
                                            activeNotification = "Sent a ${gift.name} successfully! Room score +${gift.cost * 10}"
                                            showGiftSheet = false
                                        } else {
                                            activeNotification = "Insufficient coin balance."
                                        }
                                    }
                                    .padding(12.dp)
                                    .testTag("gift_${gift.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(gift.icon, fontSize = 32.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(gift.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Circle, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(gift.cost.toString(), color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

// --- CORE UTILITY HELPER COMPOSABLES ---
@Composable
fun AuditLogEntry(timestamp: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("[$timestamp]", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text, color = Color.White.copy(0.7f), fontSize = 9.sp)
    }
}

@Composable
fun TaskRow(title: String, isCompleted: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White.copy(0.8f), fontSize = 10.sp)
        Box(
            modifier = Modifier
                .background(
                    if (isCompleted) Color(0xFF00E676).copy(0.2f) else Color.White.copy(0.1f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isCompleted) "Completed" else "Todo",
                color = if (isCompleted) Color(0xFF00E676) else Color.White.copy(0.5f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class TabConfig(
    val id: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private data class CameraInvitation(
    val id: String,
    val name: String,
    val roleType: String,
    val status: String
)


@Composable
fun SpeakerCard(speaker: SpeakerData) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(46.dp)) {
                AsyncImage(
                    model = speaker.avatar,
                    contentDescription = speaker.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (speaker.isMuted) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MicOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = speaker.name,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = speaker.role,
                    color = Color.White.copy(0.5f),
                    fontSize = 9.sp
                )
            }
        }
    }
}

data class SpeakerData(
    val id: String,
    val name: String,
    val avatar: String,
    val isMuted: Boolean,
    val role: String
)

data class GiftOption(
    val name: String,
    val cost: Int,
    val icon: String
)

@Composable
fun BadgeTag(text: String, containerColor: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = contentColor)
    }
}

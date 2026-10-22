package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * Participant model for the Voice/Video room.
 */
data class RoomParticipant(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isHost: Boolean = false,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val isVideoOn: Boolean = false,
    val role: String = "Participant"
)

/**
 * A highly polished, custom-styled Voice and Video Room component that satisfies Material Design 3 guidelines.
 * Includes user avatars, live status Indicators, mute button, video stream toggle, and leave room trigger.
 */
@Composable
fun VoiceVideoRoomComponent(
    roomTitle: String = "Design & Tech Lounge",
    category: String = "Tech & Creative",
    initialParticipants: List<RoomParticipant> = listOf(
        RoomParticipant(
            id = "user_chloe",
            name = "Chloe Dubois",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
            isHost = true,
            isSpeaking = true,
            role = "Host",
            isVideoOn = true
        ),
        RoomParticipant(
            id = "user_liam",
            name = "Liam Vance",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
            isSpeaking = false,
            isMuted = true,
            role = "Speaker"
        ),
        RoomParticipant(
            id = "user_sarah",
            name = "Sarah Connor",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
            isSpeaking = true,
            role = "Speaker"
        ),
        RoomParticipant(
            id = "user_alex",
            name = "Alex Mercer (You)",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
            role = "You"
        )
    ),
    onMuteToggled: (Boolean) -> Unit = {},
    onCameraToggled: (Boolean) -> Unit = {},
    onLeaveRoomTriggered: () -> Unit = {}
) {
    var participants by remember { mutableStateOf(initialParticipants) }
    var isMuted by remember { mutableStateOf(false) }
    var isCameraOn by remember { mutableStateOf(false) }
    var isScreenSharing by remember { mutableStateOf(false) }
    var activeSpeaker by remember { mutableStateOf<RoomParticipant?>(initialParticipants.firstOrNull { it.isSpeaking }) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Periodically simulate talking states for active/engaged user feedback
    LaunchedEffect(Unit) {
        var count = 0
        while (true) {
            kotlinx.coroutines.delay(3500)
            count++
            participants = participants.map { participant ->
                when (participant.id) {
                    "user_chloe" -> participant.copy(isSpeaking = count % 2 == 0)
                    "user_sarah" -> participant.copy(isSpeaking = count % 3 == 0)
                    "user_alex" -> participant.copy(isSpeaking = !isMuted && count % 2 == 1)
                    else -> participant
                }
            }
            activeSpeaker = participants.firstOrNull { it.isSpeaking && !it.isMuted }
        }
    }

    // Interactive helper to toast action status
    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage != null) {
            kotlinx.coroutines.delay(2000)
            snackbarMessage = null
        }
    }

    // Outer premium glassmorphic border casing
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("voice_video_room_container"),
        cornerRadius = 24.dp,
        borderWidth = 1.5.dp,
        borderColor = Color.White.copy(0.12f),
        backgroundColor = Color(0xFF0F0C21).copy(0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Live Status Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Blinking Live dot
                    val infiniteTransition = rememberInfiniteTransition(label = "live_indicator")
                    val alphaAnim by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "live_dot"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(alphaAnim)
                            .background(Color(0xFF00E676), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•  $category",
                        color = Color.White.copy(0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Active audience metric
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(0.06f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Active Listeners",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "240",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Room Metadata Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = roomTitle,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Broadcasting HD VoIP & Live Video Feeds",
                        color = Color.White.copy(0.5f),
                        fontSize = 11.sp
                    )
                }
                
                // Signal Quality Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(modifier = Modifier.size(3.dp, 6.dp).background(Color(0xFF00E676), RoundedCornerShape(1.dp)))
                    Box(modifier = Modifier.size(3.dp, 9.dp).background(Color(0xFF00E676), RoundedCornerShape(1.dp)))
                    Box(modifier = Modifier.size(3.dp, 12.dp).background(Color(0xFF00E676), RoundedCornerShape(1.dp)))
                    Box(modifier = Modifier.size(3.dp, 15.dp).background(Color(0xFF00E676), RoundedCornerShape(1.dp)))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Live Video Feed Viewport (Simulated Active Speaker Video or Avatar Wave)
            AnimatedVisibility(
                visible = isCameraOn || activeSpeaker?.isVideoOn == true,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                val currentVideoPresenter = if (isCameraOn) {
                    RoomParticipant("user_alex", "Alex Mercer (You)", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80", isVideoOn = true, role = "You")
                } else {
                    activeSpeaker
                }

                if (currentVideoPresenter != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, Color(0xFF00E5FF).copy(0.4f), RoundedCornerShape(16.dp))
                            .testTag("video_feed_viewport")
                    ) {
                        // Simulated high-quality video backdrop stream
                        AsyncImage(
                            model = currentVideoPresenter.avatarUrl,
                            contentDescription = "Active Speaker Video Feed",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient Overlay for readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(0.8f))
                                    )
                                )
                        )

                        // Top bar video telemetry status tags
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(0.5f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("VIDEO ACTIVE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF8E2DE2))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("FPS: 60 • 1080p", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Bottom speaker meta name tag
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Talking",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentVideoPresenter.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = currentVideoPresenter.role,
                                        color = Color.White,
                                        fontSize = 8.sp
                                    )
                                }
                            }

                            if (isScreenSharing && currentVideoPresenter.id == "user_alex") {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFF9100))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Screen Sharing", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Grid Layout: Speakers and Participants
            Text(
                text = "STAGE SPEAKERS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                color = Color.White.copy(0.4f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("speakers_grid"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(participants) { participant ->
                    val isUserMe = participant.id == "user_alex"
                    // Compute speaking pulse ring animation
                    val transition = rememberInfiniteTransition(label = "pulse")
                    val pulseScale by if (participant.isSpeaking && !participant.isMuted) {
                        transition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.15f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(700, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulse_scale"
                        )
                    } else {
                        remember { mutableStateOf(1f) }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (participant.isSpeaking && !participant.isMuted) {
                                    Color(0xFF8E2DE2).copy(0.12f)
                                } else {
                                    Color.White.copy(0.04f)
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (participant.isSpeaking && !participant.isMuted) {
                                    Color(0xFF8E2DE2).copy(0.5f)
                                } else {
                                    Color.White.copy(0.05f)
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                snackbarMessage = if (isUserMe) {
                                    "You clicked on your profile dashboard."
                                } else {
                                    "Showing profile card for ${participant.name}."
                                }
                            }
                            .padding(8.dp)
                            .testTag("avatar_item_${participant.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar section with voice states
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .scale(pulseScale),
                                contentAlignment = Alignment.Center
                            ) {
                                // Pulsing background wave for visual activity
                                if (participant.isSpeaking && !participant.isMuted) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF00E5FF).copy(0.15f), CircleShape)
                                            .border(1.5.dp, Color(0xFF00E5FF).copy(0.6f), CircleShape)
                                    )
                                }

                                AsyncImage(
                                    model = participant.avatarUrl,
                                    contentDescription = "${participant.name} Avatar",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.dp,
                                            color = if (participant.isSpeaking && !participant.isMuted) Color(0xFF00E5FF) else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentScale = ContentScale.Crop
                                )

                                // Micro-badges overlay
                                if (participant.isHost) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .background(Color(0xFFFFD700), CircleShape)
                                            .align(Alignment.TopStart),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stars,
                                            contentDescription = "Host",
                                            tint = Color.Black,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }

                                if (participant.isMuted || (isUserMe && isMuted)) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .background(Color(0xFFFF4D4D), CircleShape)
                                            .align(Alignment.BottomEnd),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MicOff,
                                            contentDescription = "Muted",
                                            tint = Color.White,
                                            modifier = Modifier.size(9.dp)
                                        )
                                    }
                                } else if (participant.isSpeaking) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .background(Color(0xFF00E676), CircleShape)
                                            .align(Alignment.BottomEnd),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Talking",
                                            tint = Color.White,
                                            modifier = Modifier.size(9.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Name and role subheaders
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = participant.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (participant.isVideoOn || (isUserMe && isCameraOn)) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = "Cam enabled",
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                    }
                                    Text(
                                        text = if (isUserMe) "You" else participant.role,
                                        color = if (participant.isSpeaking && !participant.isMuted) Color(0xFF00E5FF) else Color.White.copy(0.4f),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action status notification banner
            AnimatedVisibility(
                visible = snackbarMessage != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF8E2DE2).copy(0.15f))
                        .border(1.dp, Color(0xFF8E2DE2).copy(0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = snackbarMessage ?: "",
                        color = Color(0xFFD0BCFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Controls Panel Row: Includes Mute and Leave triggers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(0.03f))
                    .border(1.dp, Color.White.copy(0.04f), RoundedCornerShape(20.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Mute Button Trigger (Visually Reactive, touch target size compliant)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            isMuted = !isMuted
                            onMuteToggled(isMuted)
                            snackbarMessage = if (isMuted) "Microphone muted" else "Microphone unmuted"
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isMuted) Color(0xFFFF4D4D) else Color.White.copy(0.08f),
                                CircleShape
                            )
                            .testTag("room_mute_button")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isMuted) "Unmute Microphone" else "Mute Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isMuted) "Unmute" else "Mute",
                        color = Color.White.copy(0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 2. Camera Toggle Trigger
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            isCameraOn = !isCameraOn
                            onCameraToggled(isCameraOn)
                            snackbarMessage = if (isCameraOn) "Simulated HD Video Cam On" else "Video stream disabled"
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isCameraOn) Color(0xFF00E5FF).copy(0.2f) else Color.White.copy(0.08f),
                                CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = if (isCameraOn) Color(0xFF00E5FF) else Color.Transparent,
                                shape = CircleShape
                            )
                            .testTag("room_camera_button")
                    ) {
                        Icon(
                            imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = "Toggle Video Stream",
                            tint = if (isCameraOn) Color(0xFF00E5FF) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isCameraOn) "Cam On" else "Cam Off",
                        color = Color.White.copy(0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 3. Screen Share Toggle
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            isScreenSharing = !isScreenSharing
                            snackbarMessage = if (isScreenSharing) "Sharing your screen feed to room" else "Screen share stopped"
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isScreenSharing) Color(0xFFFF9100).copy(0.2f) else Color.White.copy(0.08f),
                                CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = if (isScreenSharing) Color(0xFFFF9100) else Color.Transparent,
                                shape = CircleShape
                            )
                            .testTag("room_screenshare_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ScreenShare,
                            contentDescription = "Toggle Screen Share",
                            tint = if (isScreenSharing) Color(0xFFFF9100) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Share",
                        color = Color.White.copy(0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 4. Leave Room Trigger
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { showLeaveConfirmation = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFF3D00).copy(0.15f), CircleShape)
                            .border(1.5.dp, Color(0xFFFF3D00), CircleShape)
                            .testTag("room_leave_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Leave Room Trigger",
                            tint = Color(0xFFFF3D00),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Leave",
                        color = Color(0xFFFF3D00),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }

    // Interactive confirmation overlay sheet / dialog on leave trigger
    if (showLeaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmation = false },
            containerColor = Color(0xFF0F0C21),
            icon = {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = Color(0xFFFF4D4D),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Leave Broadcast Room?",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "You will disconnect from the active VoIP, voice, and simulated video feeds immediately.",
                    color = Color.White.copy(0.6f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLeaveConfirmation = false
                        onLeaveRoomTriggered()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4D)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_leave_room_button")
                ) {
                    Text("Disconnect Now", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLeaveConfirmation = false }
                ) {
                    Text("Stay Connected", color = Color.White.copy(0.5f))
                }
            }
        )
    }
}

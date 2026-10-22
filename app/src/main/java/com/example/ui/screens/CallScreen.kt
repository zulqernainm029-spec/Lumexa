package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.GlassmorphicCard

@Composable
fun CallScreen(viewModel: ConnectLiveViewModel) {
    val callSession by viewModel.activeCallSession.collectAsState()

    val session = callSession ?: return

    val formattedDuration = remember(session.durationSeconds) {
        val mins = session.durationSeconds / 60
        val secs = session.durationSeconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070415))
    ) {
        // Full screen video camera simulation OR blur background
        if (!session.isVoiceOnly) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (session.isBackgroundBlurred) Modifier.blur(20.dp) else Modifier)
            ) {
                // Large simulated camera preview background
                AsyncImage(
                    model = session.peerAvatar,
                    contentDescription = "Video call stream",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // High definition tag
                Box(
                    modifier = Modifier
                        .padding(top = 54.dp, start = 24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(0.6f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF00E676), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("HD 1080p Connected", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Audio calling solid gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF140D2F), Color(0xFF070415))
                        )
                    )
            )
        }

        // Main info panel
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top peer description
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                if (session.isVoiceOnly) {
                    AsyncImage(
                        model = session.peerAvatar,
                        contentDescription = session.peerName,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(0.04f)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = session.peerName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (session.status) {
                        "calling" -> "Calling peer..."
                        "ringing" -> "Incoming Lumexa Call..."
                        else -> "Session Active • $formattedDuration"
                    },
                    fontSize = 13.sp,
                    color = if (session.status == "ringing") Color(0xFF00E676) else Color.White.copy(0.6f),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Central interactive adjust panels (Beauty Filter & Background Blur sliders for video!)
            if (!session.isVoiceOnly && session.status == "connected") {
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Realtime Beauty Filter", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${(session.beautyFilterLevel * 100).toInt()}%", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Slider(
                            value = session.beautyFilterLevel,
                            onValueChange = { viewModel.updateBeautyFilter(it) },
                            colors = SliderDefaults.colors(
                                activeTrackColor = Color(0xFF8E2DE2),
                                thumbColor = Color.White
                            ),
                            modifier = Modifier.testTag("beauty_filter_slider")
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(0.04f))
                                .clickable { viewModel.toggleBackgroundBlur() }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Simulate Cinematic Background Blur", color = Color.White, fontSize = 11.sp)
                            Switch(
                                checked = session.isBackgroundBlurred,
                                onCheckedChange = { viewModel.toggleBackgroundBlur() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(1.dp))
            }

            // Bottom action controller row
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (session.status == "ringing") {
                    // Incoming buttons (Accept / Reject)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally)
                    ) {
                        IconButton(
                            onClick = { viewModel.rejectOrHangUpCall() },
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFFF4D4D), CircleShape)
                                .testTag("decline_call_button")
                        ) {
                            Icon(Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White)
                        }

                        IconButton(
                            onClick = { viewModel.acceptIncomingCall() },
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF00E676), CircleShape)
                                .testTag("accept_call_button")
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Accept", tint = Color.White)
                        }
                    }
                } else {
                    // Connected/calling controller utilities (Mute, Speaker, Switch Camera, Hang Up)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mute
                        IconButton(
                            onClick = { viewModel.toggleMuteCall() },
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (session.isMuted) Color.White.copy(0.3f) else Color.White.copy(0.05f), CircleShape)
                                .testTag("mute_call_button")
                        ) {
                            Icon(
                                imageVector = if (session.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = Color.White
                            )
                        }

                        // Hang Up
                        IconButton(
                            onClick = { viewModel.rejectOrHangUpCall() },
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFFFF4D4D), CircleShape)
                                .testTag("hangup_call_button")
                        ) {
                            Icon(Icons.Default.CallEnd, contentDescription = "Hang Up", tint = Color.White)
                        }

                        // Speaker
                        IconButton(
                            onClick = { viewModel.toggleSpeakerCall() },
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (session.isSpeakerOn) Color.White.copy(0.3f) else Color.White.copy(0.05f), CircleShape)
                                .testTag("speaker_call_button")
                        ) {
                            Icon(
                                imageVector = if (session.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                                contentDescription = "Speaker",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

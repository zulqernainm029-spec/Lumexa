package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import coil.compose.AsyncImage
import com.example.data.CallHistoryEntry
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GoldenVipBrush
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(viewModel: ConnectLiveViewModel) {
    val meProfile by viewModel.meProfile.collectAsState()
    val callLogs by viewModel.callLogs.collectAsState()
    val googleAccount by viewModel.googleLinkedAccount.collectAsState()

    val profile = meProfile ?: return

    val isAccountVerified by viewModel.isAccountVerified.collectAsState()
    val verificationType by viewModel.userVerificationType.collectAsState()

    // Advanced profile customization states
    val statusMessage by viewModel.myStatusMessage.collectAsState()
    val mood by viewModel.myMood.collectAsState()
    val availability by viewModel.myAvailability.collectAsState()
    val usernameColorStr by viewModel.myUsernameColor.collectAsState()
    val profileThemeStr by viewModel.myProfileTheme.collectAsState()
    val profileMusicStr by viewModel.myProfileMusic.collectAsState()
    val subLevel by viewModel.mySubscriptionLevel.collectAsState()
    val cloudFiles by viewModel.cloudStorageFiles.collectAsState()
    val cloudUsedBytes by viewModel.cloudStorageUsedBytes.collectAsState()

    var showCustomizer by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    var showCloudVault by remember { mutableStateOf(false) }
    var showSubscriptions by remember { mutableStateOf(false) }

    // Customizer local inputs
    var tempStatus by remember(statusMessage) { mutableStateOf(statusMessage) }
    var tempMood by remember(mood) { mutableStateOf(mood) }
    var tempAvailability by remember(availability) { mutableStateOf(availability) }
    var tempColor by remember(usernameColorStr) { mutableStateOf(usernameColorStr) }
    var tempTheme by remember(profileThemeStr) { mutableStateOf(profileThemeStr) }
    var tempMusic by remember(profileMusicStr) { mutableStateOf(profileMusicStr) }

    val nameColor = when (usernameColorStr) {
        "Gold" -> Color(0xFFFFD700)
        "Neon Cyan" -> Color(0xFF00E5FF)
        "Hot Pink" -> Color(0xFFFF4081)
        "Emerald" -> Color(0xFF00E676)
        else -> Color.White
    }

    val themeBrush = when (profileThemeStr) {
        "Sunset Neon" -> Brush.linearGradient(listOf(Color(0xFFFF4081), Color(0xFFFF8E53)))
        "Space Dust" -> Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFF8E2DE2)))
        "Vibrant Gold" -> GoldenVipBrush
        "Light Mint" -> Brush.linearGradient(listOf(Color(0xFF00E676), Color(0xFF00B0FF)))
        else -> null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .testTag("profile_screen_viewport"),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // Cover and Avatar overlapping
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                // Cover
                if (themeBrush != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(themeBrush)
                    )
                } else {
                    AsyncImage(
                        model = profile.coverUrl,
                        contentDescription = "Cover Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                // Avatar overlapping
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = "Avatar Image",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color(0xFF080512), CircleShape)
                        .align(Alignment.BottomCenter),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Name, Badges, Status & Bio
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = nameColor
                    )
                    if (profile.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (subLevel != "Free") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(GoldenVipBrush, RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.5.dp)
                        ) {
                            Text(subLevel.uppercase(), fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    } else if (profile.isVip) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(GoldenVipBrush, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("VIP", fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = "@${profile.username} • ${profile.city}, ${profile.country}",
                    color = Color.White.copy(0.4f),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAccountVerified) Color(0xFF00E676).copy(0.12f) else Color(0xFFFF9100).copy(0.12f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAccountVerified) Color(0xFF00E676).copy(0.4f) else Color(0xFFFF9100).copy(0.4f)
                    ),
                    modifier = Modifier
                        .clickable {
                            if (!isAccountVerified) {
                                viewModel.navigateTo("auth")
                            }
                        }
                        .testTag("verification_status_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAccountVerified) Icons.Default.Verified else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isAccountVerified) Color(0xFF00E676) else Color(0xFFFF9100),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAccountVerified) "Verified ($verificationType)" else "Unverified Guest • Tap to Verify Account",
                            color = if (isAccountVerified) Color(0xFF00E676) else Color(0xFFFF9100),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mood & status bubble
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(0.05f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("$mood ", fontSize = 11.sp)
                    Text(statusMessage, color = Color.White.copy(0.75f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                when (availability) {
                                    "🟢 Online" -> Color(0xFF00E676)
                                    "🟡 Busy" -> Color(0xFFFFD700)
                                    "🔴 Do Not Disturb" -> Color(0xFFFF4D4D)
                                    else -> Color.White.copy(0.4f)
                                }
                            )
                    )
                }

                // Profile Music Track Indicator
                if (profileMusicStr != "None") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(0.5.dp, Color(0xFF00E5FF).copy(0.3f), RoundedCornerShape(8.dp))
                            .background(Color(0xFF00E5FF).copy(0.06f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Profile Music: $profileMusicStr", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = profile.bio,
                    color = Color.White.copy(0.8f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatCol("Followers", "1,240")
                    ProfileStatCol("Following", "430")
                    ProfileStatCol("Friends", "89")
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Google Account Security Status Card
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("google_account_status_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF4285F4))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (googleAccount != null) "Google Account Linked" else "Google Identity",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (googleAccount != null) Color(0xFF00E676).copy(0.2f) else Color.White.copy(0.1f)
                                ) {
                                    Text(
                                        text = if (googleAccount != null) "OAuth 2.0" else "Unlinked",
                                        color = if (googleAccount != null) Color(0xFF00E676) else Color.White.copy(0.6f),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = googleAccount?.email ?: "Connect Google account for 1-tap OAuth",
                                color = Color.White.copy(0.6f),
                                fontSize = 11.sp
                            )
                        }

                        if (googleAccount != null) {
                            TextButton(
                                onClick = { viewModel.unlinkGoogleAccount() },
                                modifier = Modifier.testTag("unlink_google_button")
                            ) {
                                Text("Unlink", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.navigateTo("auth") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp).testTag("link_google_button")
                            ) {
                                Text("Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // NEW MULTI-ACTION TOOLBAR GRID (Customizer, Cloud Vault, VIP tiers, Share QR)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showCustomizer = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("customizer_toggle")
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Aesthetic Style", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showCloudVault = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("cloud_vault_toggle")
                        ) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cloud Space", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showSubscriptions = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("subscriptions_toggle")
                        ) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VIP Club Tiers", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showQrDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("qr_toggle")
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Profile QR", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.navigateTo("user_dashboard") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE040FB)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("user_dashboard_toggle")
                        ) {
                            Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Personal Arena", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo("creator_dashboard") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF651FFF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("creator_dashboard_toggle")
                        ) {
                            Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Creator Studio", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.navigateTo("business_dashboard") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("business_dashboard_toggle")
                        ) {
                            Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Business Hub", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo("onboarding") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9100)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("onboarding_tour_toggle")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Onboarding Tour", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.navigateTo("lumi_assistant") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("lumi_assistant_toggle")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Lumi AI Chat", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo("event_calendar") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("event_calendar_toggle")
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Event Calendar", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.navigateTo("family_system") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("family_system_toggle")
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Family Hub", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo("vip_system") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("vip_system_toggle")
                        ) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VIP Elite Club", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.navigateTo("economy_system") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("economy_system_toggle")
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Economy & Gifts", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo("host_recruitment") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("host_recruitment_toggle")
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Recruitment", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { viewModel.navigateTo("edit_profile") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.04f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp).testTag("edit_profile_trigger")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Bio & Locations", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Interests Tags list
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    "Selected Interests",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(profile.interests.split(",")) { interest ->
                        if (interest.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(0.04f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(interest.trim(), color = Color(0xFFD0BCFF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Profile Links & Achievements
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Achievements & Badges",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text("3 unlocked", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFD700).copy(0.15f))
                            .border(1.dp, Color(0xFFFFD700).copy(0.4f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pioneer", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00E5FF).copy(0.15f))
                            .border(1.dp, Color(0xFF00E5FF).copy(0.4f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Top Speaker", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFF4081).copy(0.15f))
                            .border(1.dp, Color(0xFFFF4081).copy(0.4f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Super Influencer", color = Color(0xFFFF4081), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Featured Media: Favorite Photos & Videos
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    "Featured Photos & Videos",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(110.dp, 80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(0.06f))
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=150",
                                contentDescription = "Studio Live Session",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color.Black.copy(0.6f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Vlog #4", color = Color.White, fontSize = 9.sp)
                            }
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .size(110.dp, 80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(0.06f))
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=150",
                                contentDescription = "DJ Console set",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Video",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center)
                                    .background(Color.Black.copy(0.4f), CircleShape)
                                    .padding(4.dp)
                            )
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .size(110.dp, 80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(0.06f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White.copy(0.4f))
                        }
                    }
                }
            }
        }

        // Featured Friends & Communities
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    "Featured Friends & Groups",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100",
                                contentDescription = "Sarah",
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Sarah K.", color = Color.White.copy(0.7f), fontSize = 10.sp)
                        }
                    }
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100",
                                contentDescription = "Alex",
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Alex M.", color = Color.White.copy(0.7f), fontSize = 10.sp)
                        }
                    }
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF8E2DE2).copy(0.15f))
                                    .border(1.dp, Color(0xFF8E2DE2), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎧", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Synth Beats", color = Color.White.copy(0.7f), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // External Profile Links
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    "Profile Links",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(0.04f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("linktree/synth_soul", color = Color(0xFF00E5FF), fontSize = 11.sp)
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(0.04f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("patreon/ambient_vip", color = Color(0xFFFF4081), fontSize = 11.sp)
                    }
                }
            }
        }

        // Visitor History Section (Subject to Privacy Setting)
        item {
            var showVisitorsByPrivacy by remember { mutableStateOf(true) }
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Visitors",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showVisitorsByPrivacy = !showVisitorsByPrivacy }
                    ) {
                        Icon(
                            imageVector = if (showVisitorsByPrivacy) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.White.copy(0.4f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showVisitorsByPrivacy) "Public Mode" else "Private",
                            color = Color.White.copy(0.4f),
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (showVisitorsByPrivacy) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100",
                                contentDescription = "Elena",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Elena Rostova", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Viewed profile 15m ago", color = Color.White.copy(0.4f), fontSize = 9.sp)
                            }
                            Text("🇷🇺", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=100",
                                contentDescription = "Ken",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Kenji Sato", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Viewed profile 2h ago", color = Color.White.copy(0.4f), fontSize = 9.sp)
                            }
                            Text("🇯🇵", fontSize = 12.sp)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(0.02f), RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Visitor history is paused in private stealth mode.",
                            color = Color.White.copy(0.35f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Call Logs list
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    "Call Logs & Sessions",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        if (callLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No call logs available.", color = Color.White.copy(0.4f), fontSize = 12.sp)
                }
            }
        } else {
            items(callLogs) { entry ->
                CallHistoryLogItem(entry = entry, viewModel = viewModel)
            }
        }
    }

    // 1. AESTHETIC CUSTOMIZER DIALOG
    if (showCustomizer) {
        AlertDialog(
            onDismissRequest = { showCustomizer = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFF8E2DE2))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Personalize Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Status Customizer
                    Column {
                        Text("Status Message", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = tempStatus,
                            onValueChange = { tempStatus = it },
                            placeholder = { Text("Write status...", color = Color.White.copy(0.3f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF8E2DE2)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Mood Customizer
                    Column {
                        Text("Current Mood", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("🧘 Focused", "🚀 Energized", "🎧 Chilling", "😴 Sleepy").forEach { moodOpt ->
                                val isSel = tempMood == moodOpt
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.05f))
                                        .clickable { tempMood = moodOpt }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(moodOpt, color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    // Availability Customizer
                    Column {
                        Text("Availability", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("🟢 Online", "🟡 Busy", "🔴 Do Not Disturb").forEach { availOpt ->
                                val isSel = tempAvailability == availOpt
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF00E5FF) else Color.White.copy(0.05f))
                                        .clickable { tempAvailability = availOpt }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(availOpt, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Username Color Customizer
                    Column {
                        Text("Username Color Accent", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Default", "Gold", "Neon Cyan", "Hot Pink", "Emerald").forEach { colOpt ->
                                val isSel = tempColor == colOpt
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color.White else Color.White.copy(0.05f))
                                        .clickable { tempColor = colOpt }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = colOpt,
                                        color = if (isSel) Color.Black else when (colOpt) {
                                            "Gold" -> Color(0xFFFFD700)
                                            "Neon Cyan" -> Color(0xFF00E5FF)
                                            "Hot Pink" -> Color(0xFFFF4081)
                                            "Emerald" -> Color(0xFF00E676)
                                            else -> Color.White
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Profile Background Themes
                    Column {
                        Text("Profile Cover Background Theme", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Default", "Sunset Neon", "Space Dust", "Vibrant Gold", "Light Mint").forEach { thOpt ->
                                val isSel = tempTheme == thOpt
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.05f))
                                        .clickable { tempTheme = thOpt }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(thOpt, color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    // Profile soundtrack customizer
                    Column {
                        Text("Profile Music Overlay", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("None", "Lofi Beats", "Synthwave Drive", "Ambient Dreams").forEach { musOpt ->
                                val isSel = tempMusic == musOpt
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF00E5FF) else Color.White.copy(0.05f))
                                        .clickable { tempMusic = musOpt }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(musOpt, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateAdvancedProfile(tempStatus, tempMood, tempAvailability, tempColor, tempTheme, tempMusic)
                        showCustomizer = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2))
                ) {
                    Text("Apply Layout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomizer = false }) {
                    Text("Discard", color = Color.White.copy(0.6f))
                }
            },
            containerColor = Color(0xFF1E1B24)
        )
    }

    // 2. CLOUD VAULT DIALOG
    if (showCloudVault) {
        var uploadInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCloudVault = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cloud Space Vault", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Quota Meter
                    val usedGB = cloudUsedBytes / 1_000_000_000.0
                    val totalGB = when (subLevel) {
                        "Free" -> 5.0
                        "Plus" -> 20.0
                        "Premium" -> 100.0
                        else -> 500.0
                    }
                    val percent = (usedGB / totalGB).coerceIn(0.0, 1.0)

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Storage Used", color = Color.White.copy(0.6f), fontSize = 11.sp)
                            Text("${String.format("%.2f", usedGB)} GB / ${totalGB.toInt()} GB", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = percent.toFloat(),
                            color = Color(0xFF00E5FF),
                            trackColor = Color.White.copy(0.1f),
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                        )
                    }

                    // Add simulation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uploadInput,
                            onValueChange = { uploadInput = it },
                            placeholder = { Text("new_file.mp4", color = Color.White.copy(0.3f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF00E5FF)
                            ),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (uploadInput.isNotBlank()) {
                                    viewModel.simulateCloudUpload(uploadInput)
                                    uploadInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                        }
                    }

                    // Files list
                    Text("Secure Cloud Files", color = Color.White.copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (cloudFiles.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No files saved in your Cloud Space.", color = Color.White.copy(0.3f), fontSize = 11.sp)
                            }
                        } else {
                            cloudFiles.forEach { file ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(0.04f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White.copy(0.6f), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(file, color = Color.White, fontSize = 11.sp)
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteCloudFile(file) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF4D4D), modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCloudVault = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("Done", color = Color.Black)
                }
            },
            containerColor = Color(0xFF131E24)
        )
    }

    // 3. VIP CLUB SUBSCRIPTION DIALOG
    if (showSubscriptions) {
        AlertDialog(
            onDismissRequest = { showSubscriptions = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select VIP Level", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val plans = listOf(
                        "Free" to "5 GB storage • Standard customizers • 480p streams",
                        "Plus" to "20 GB storage • Basic themed badge • 720p streams",
                        "Premium" to "100 GB storage • Full customization presets • 1080p stream resolution",
                        "VIP" to "500 GB storage • Unique username colors • Golden neon VIP badges",
                        "Creator Pro" to "Unlimited storage • Analytics panel access • Interactive audio overlays",
                        "Business Pro" to "Commercial product inventory • Campaign scheduling engine"
                    )

                    plans.forEach { (tier, benefits) ->
                        val isCurrent = subLevel == tier
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) Color(0xFFFFD700).copy(0.12f) else Color.White.copy(0.04f))
                                .border(
                                    width = if (isCurrent) 1.5.dp else 0.5.dp,
                                    color = if (isCurrent) Color(0xFFFFD700) else Color.White.copy(0.1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.upgradeSubscriptionTier(tier) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(tier, fontWeight = FontWeight.Black, color = if (isCurrent) Color(0xFFFFD700) else Color.White, fontSize = 13.sp)
                                    if (isCurrent) {
                                        Icon(Icons.Default.Check, contentDescription = "Active", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(benefits, color = Color.White.copy(0.5f), fontSize = 10.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSubscriptions = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("Confirm Tier", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF221F14)
        )
    }

    // 4. QR CODE DIALOG
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan to Share", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Simulated beautiful high tech neon QR Code
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            repeat(4) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    repeat(4) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color.Black, RoundedCornerShape(2.dp))
                                        )
                                    }
                                }
                            }
                        }
                        // Middle logo
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8E2DE2))
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    Text(
                        "connectlive.com/u/${profile.username}",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Share this personal QR matrix so friends can instantly bypass searching and view your personalized profile experience.",
                        color = Color.White.copy(0.6f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showQrDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f))
                ) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = Color(0xFF18181C)
        )
    }
}

@Composable
fun ProfileStatCol(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, color = Color.White, fontSize = 16.sp)
        Text(label, color = Color.White.copy(0.4f), fontSize = 11.sp)

    }
}

@Composable
fun CallHistoryLogItem(entry: CallHistoryEntry, viewModel: ConnectLiveViewModel) {
    val isMissed = entry.callStatus == "missed"
    val color = if (isMissed) Color(0xFFFF4D4D) else Color.White
    val icon = if (entry.isVoiceOnly) Icons.Default.Call else Icons.Default.Videocam

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = entry.peerAvatar,
                contentDescription = entry.peerName,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(entry.peerName, fontWeight = FontWeight.Bold, color = color, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (entry.isIncoming) Icons.Default.ArrowBack else Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White.copy(0.4f),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isMissed) "Missed session" else "Connected (${entry.durationSeconds}s)",
                        color = Color.White.copy(0.4f),
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(
                onClick = { viewModel.initiateCall(entry.peerId, entry.peerName, entry.peerAvatar, entry.isVoiceOnly) },
                modifier = Modifier.testTag("callback_${entry.peerId}")
            ) {
                Icon(icon, contentDescription = "Call back", tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
            }
        }
    }
}

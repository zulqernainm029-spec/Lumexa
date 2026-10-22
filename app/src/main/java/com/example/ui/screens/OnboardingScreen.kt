package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.CosmosBackgroundBrush
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.PremiumGradientBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: ConnectLiveViewModel) {
    var step by remember { mutableStateOf(1) } // 1: Welcome Slider, 2: Preferences, 3: Permissions, 4: Interests, 5: Profile/Avatar, 6: Suggested, 7: Welcome Rewards

    // State Variables for selections
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedRegion by remember { mutableStateOf("United States") }
    var selectedTheme by remember { mutableStateOf("Cosmos Dark") }

    // Permissions Status
    var notifPermission by remember { mutableStateOf(false) }
    var cameraPermission by remember { mutableStateOf(false) }
    var micPermission by remember { mutableStateOf(false) }
    var contactsPermission by remember { mutableStateOf(false) }
    var photosPermission by remember { mutableStateOf(false) }

    // Intermediary explanation dialogs
    var permissionExplanation by remember { mutableStateOf<String?>(null) }
    var pendingPermissionToEnable by remember { mutableStateOf<String?>(null) }

    // Interests & categories
    val allInterests = listOf("Music & Beats", "DJ & Live Sets", "Gaming Arena", "Tech & Coding", "Global Language", "Art & Design", "Cryptocurrency", "Anime & Manga")
    val selectedInterests = remember { mutableStateListOf<String>() }

    val allCategories = listOf("Live Streaming", "Voice Party Rooms", "Creators Studio", "Local Hangouts", "Marketplace Hub", "VIP Clubs")
    val selectedCategories = remember { mutableStateListOf<String>() }

    // Profile Info
    var customUsername by remember { mutableStateOf("") }
    var selectedAvatarIndex by remember { mutableStateOf(0) }

    val avatarPresets = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
        "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150"
    )

    // Suggestion selections
    val followedUsers = remember { mutableStateListOf<String>() }
    val joinedCommunities = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBackgroundBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Step Indicator & Skip Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step Dots
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (i in 1..7) {
                        Box(
                            modifier = Modifier
                                .size(if (i == step) 16.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (i == step) Color(0xFF00E5FF) else Color.White.copy(0.2f))
                        )
                    }
                }

                TextButton(
                    onClick = { viewModel.navigateTo("main") },
                    modifier = Modifier.testTag("onboarding_skip")
                ) {
                    Text("Skip", color = Color.White.copy(0.5f), fontSize = 12.sp)
                }
            }

            // Main Contents based on step
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(targetState = step, label = "onboarding_steps") { currentStep ->
                    when (currentStep) {
                        1 -> WelcomeStep()
                        2 -> PreferencesStep(
                            selectedLanguage = selectedLanguage,
                            onLanguageSelected = { selectedLanguage = it },
                            selectedRegion = selectedRegion,
                            onRegionSelected = { selectedRegion = it },
                            selectedTheme = selectedTheme,
                            onThemeSelected = { selectedTheme = it }
                        )
                        3 -> PermissionsStep(
                            notif = notifPermission,
                            onNotifClick = {
                                pendingPermissionToEnable = "notification"
                                permissionExplanation = "Lumexa needs Notification permissions to notify you of instant messages, live events, and video calls in real-time."
                            },
                            camera = cameraPermission,
                            onCameraClick = {
                                pendingPermissionToEnable = "camera"
                                permissionExplanation = "Lumexa requests Camera access to allow seamless high-definition video calling, stories, and live video streaming."
                            },
                            mic = micPermission,
                            onMicClick = {
                                pendingPermissionToEnable = "microphone"
                                permissionExplanation = "Lumexa requests Microphone access so your voice sounds clear and natural during voice parties, podcasts, and video calls."
                            },
                            contacts = contactsPermission,
                            onContactsClick = {
                                pendingPermissionToEnable = "contacts"
                                permissionExplanation = "Lumexa requests Contacts read permissions to automatically discover mutual friends who are already hanging out on the platform."
                            },
                            photos = photosPermission,
                            onPhotosClick = {
                                pendingPermissionToEnable = "photos"
                                permissionExplanation = "Lumexa requests Media Library access to let you easily share original moments, cover art, and profile soundtracks."
                            }
                        )
                        4 -> InterestsStep(
                            allInterests = allInterests,
                            selectedInterests = selectedInterests,
                            allCategories = allCategories,
                            selectedCategories = selectedCategories
                        )
                        5 -> ProfileStep(
                            username = customUsername,
                            onUsernameChange = { customUsername = it },
                            avatarIndex = selectedAvatarIndex,
                            onAvatarSelected = { selectedAvatarIndex = it },
                            avatars = avatarPresets
                        )
                        6 -> SuggestionsStep(
                            followed = followedUsers,
                            communities = joinedCommunities
                        )
                        7 -> RewardsStep(
                            username = if (customUsername.isBlank()) "Creator" else customUsername,
                            avatarUrl = avatarPresets[selectedAvatarIndex],
                            themeSelected = selectedTheme,
                            onComplete = {
                                viewModel.navigateTo("main")
                            }
                        )
                    }
                }
            }

            // Bottom Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1 && step < 7) {
                    Button(
                        onClick = { step-- },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("onboarding_back")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }

                if (step < 7) {
                    Button(
                        onClick = {
                            if (step == 5 && customUsername.isNotBlank()) {
                                viewModel.updateMyProfile(
                                    name = customUsername,
                                    bio = "Joined Lumexa cosmic community. Music is my soul. 🌌",
                                    country = selectedRegion,
                                    city = "Metropolis",
                                    interests = selectedInterests.joinToString(", "),
                                    languages = selectedLanguage
                                )
                            }
                            step++
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .height(48.dp)
                            .background(PremiumGradientBrush, RoundedCornerShape(24.dp))
                            .testTag("onboarding_next")
                    ) {
                        Text(
                            text = if (step == 6) "Claim Rewards" else "Continue",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = "Forward", tint = Color.White)
                    }
                }
            }
        }
    }

    // Interactive Permission Explanation Dialog
    permissionExplanation?.let { text ->
        AlertDialog(
            onDismissRequest = { permissionExplanation = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Permission Explanation", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Text(text, color = Color.White.copy(0.7f), fontSize = 12.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (pendingPermissionToEnable) {
                            "notification" -> notifPermission = true
                            "camera" -> cameraPermission = true
                            "microphone" -> micPermission = true
                            "contacts" -> contactsPermission = true
                            "photos" -> photosPermission = true
                        }
                        permissionExplanation = null
                        pendingPermissionToEnable = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("Approve Permission", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    permissionExplanation = null
                    pendingPermissionToEnable = null
                }) {
                    Text("Deny", color = Color.White.copy(0.5f))
                }
            },
            containerColor = Color(0xFF131F24)
        )
    }
}

@Composable
fun WelcomeStep() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(PremiumGradientBrush),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Welcome to Lumexa",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "A premium space designed for creators, businesses, and friends to connect, host events, stream, and trade digital assets seamlessly.",
            color = Color.White.copy(0.7f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Live Video Arenas", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("HD streaming & live interactive chatrooms.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Digital Gift Store", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Send premium virtual gifts with stunning music.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Encrypted Cloud Vault", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Your media, backup storage, fully secured.", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PreferencesStep(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    selectedRegion: String,
    onRegionSelected: (String) -> Unit,
    selectedTheme: String,
    onThemeSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Personalize Experience", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Select your default setup parameters to auto-customize the feed, languages, and accessibility UI.", color = Color.White.copy(0.6f), fontSize = 12.sp)

        // Languages selection
        Column {
            Text("App Language", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("English", "Español", "日本語", "Deutsch").forEach { lang ->
                    val isSel = selectedLanguage == lang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.04f))
                            .border(1.dp, if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.1f), RoundedCornerShape(10.dp))
                            .clickable { onLanguageSelected(lang) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(lang, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Region Selection
        Column {
            Text("Content Discovery Region", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("United States", "Brazil", "Japan", "Germany").forEach { reg ->
                    val isSel = selectedRegion == reg
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) Color(0xFF00E5FF) else Color.White.copy(0.04f))
                            .border(1.dp, if (isSel) Color(0xFF00E5FF) else Color.White.copy(0.1f), RoundedCornerShape(10.dp))
                            .clickable { onRegionSelected(reg) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(reg, color = if (isSel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Theme Selection
        Column {
            Text("Display Visual Theme", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Cosmos Dark" to "Encrypted deep cosmic slate theme",
                    "Midnight AMOLED" to "Pure energy saving battery black layout",
                    "Vibrant Neon" to "Retro synthwave and glow ambient accents"
                ).forEach { (thm, desc) ->
                    val isSel = selectedTheme == thm
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) Color.White.copy(0.08f) else Color.White.copy(0.02f))
                            .border(1.dp, if (isSel) Color(0xFF8E2DE2) else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { onThemeSelected(thm) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSel,
                            onClick = { onThemeSelected(thm) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF8E2DE2))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(thm, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(desc, color = Color.White.copy(0.5f), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionsStep(
    notif: Boolean, onNotifClick: () -> Unit,
    camera: Boolean, onCameraClick: () -> Unit,
    mic: Boolean, onMicClick: () -> Unit,
    contacts: Boolean, onContactsClick: () -> Unit,
    photos: Boolean, onPhotosClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Hardware Permissions", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Enable device features below to unlock full real-time networking. Tapping an item displays clear explainers of data use.", color = Color.White.copy(0.6f), fontSize = 11.sp)

        val items = listOf(
            Triple("Push Notifications", "System alerts, alerts & call notifications", notif to onNotifClick),
            Triple("Camera Access", "Live broadcasts and avatar creation features", camera to onCameraClick),
            Triple("Microphone Stream", "Voice rooms and audio calls", mic to onMicClick),
            Triple("Contacts Search", "Automatically locate friends who joined", contacts to onContactsClick),
            Triple("Photo Library", "Cover backgrounds & stories attachment", photos to onPhotosClick)
        )

        items.forEach { (name, label, togglePair) ->
            val (enabled, onClick) = togglePair
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.03f))
                    .border(0.5.dp, if (enabled) Color(0xFF00E676).copy(0.4f) else Color.White.copy(0.1f), RoundedCornerShape(12.dp))
                    .clickable { onClick() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (enabled) Color(0xFF00E676).copy(0.15f) else Color.White.copy(0.06f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (enabled) Icons.Default.Check else Icons.Default.Security,
                            contentDescription = null,
                            tint = if (enabled) Color(0xFF00E676) else Color.White.copy(0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(label, color = Color.White.copy(0.4f), fontSize = 9.sp)
                    }
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = { onClick() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = Color(0xFF00E676)
                    )
                )
            }
        }
    }
}

@Composable
fun InterestsStep(
    allInterests: List<String>,
    selectedInterests: MutableList<String>,
    allCategories: List<String>,
    selectedCategories: MutableList<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Your Creative Goals", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Help Lumexa personalize your recommended feed and suggested friends by selecting topics of interest.", color = Color.White.copy(0.6f), fontSize = 11.sp)

        // Interests Multi select
        Column {
            Text("Aesthetic Topics", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    FlowRowHelper(spacing = 8.dp) {
                        allInterests.forEach { interest ->
                            val isSel = selectedInterests.contains(interest)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.05f))
                                    .border(1.dp, if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.1f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        if (isSel) selectedInterests.remove(interest) else selectedInterests.add(interest)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(interest, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Categories Multi select
        Column {
            Text("Core Platforms", color = Color(0xFFFF4081), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    FlowRowHelper(spacing = 8.dp) {
                        allCategories.forEach { cat ->
                            val isSel = selectedCategories.contains(cat)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) Color(0xFFFF4081) else Color.White.copy(0.05f))
                                    .border(1.dp, if (isSel) Color(0xFFFF4081) else Color.White.copy(0.1f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        if (isSel) selectedCategories.remove(cat) else selectedCategories.add(cat)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(cat, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStep(
    username: String,
    onUsernameChange: (String) -> Unit,
    avatarIndex: Int,
    onAvatarSelected: (Int) -> Unit,
    avatars: List<String>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Complete Profile Wizard", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.align(Alignment.Start))
        Text("Set up your unique username and choose an avatar profile preview.", color = Color.White.copy(0.6f), fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))

        // Large Preview Avatar
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .border(3.dp, Color(0xFF00E5FF), CircleShape)
        ) {
            AsyncImage(
                model = avatars[avatarIndex],
                contentDescription = "Selected Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Choose preset avatar
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Choose Avatar Preset", color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(avatars.size) { index ->
                    val isSel = avatarIndex == index
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSel) Color(0xFF00E5FF) else Color.Transparent, CircleShape)
                            .clickable { onAvatarSelected(index) }
                    ) {
                        AsyncImage(
                            model = avatars[index],
                            contentDescription = "Preset Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // Set Username
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Enter Screen Name", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = { Text("e.g. SynthVibe_Pro", color = Color.White.copy(0.3f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00E5FF)
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SuggestionsStep(
    followed: MutableList<String>,
    communities: MutableList<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Suggested Connections", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Get started on Lumexa by following popular creators and joining featured voice party rooms.", color = Color.White.copy(0.6f), fontSize = 11.sp)

        // Creators suggestions
        Column {
            Text("Suggested Creators & Friends", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            val creators = listOf(
                "Elena Rostova" to "VJ Ambient Designer • 🇷🇺",
                "Kenji Sato" to "Synthesizer Pro • 🇯🇵",
                "Marcus Aurelius" to "Voice Room Host • 🇺🇸"
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                creators.forEach { (name, bio) ->
                    val isFol = followed.contains(name)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(0.03f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(0.1f))) {
                                Text("👤", modifier = Modifier.align(Alignment.Center), fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(bio, color = Color.White.copy(0.4f), fontSize = 9.sp)
                            }
                        }
                        Button(
                            onClick = { if (isFol) followed.remove(name) else followed.add(name) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isFol) Color.White.copy(0.1f) else Color(0xFFFFD700)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text(if (isFol) "Following" else "Follow", color = if (isFol) Color.White else Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Suggested Communities
        Column {
            Text("Featured Voice Groups", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            val groups = listOf(
                "Lofi Study Hangout" to "12.4K members • Chilling beats",
                "Tech Enthusiasts Chat" to "8.1K members • Coding stream"
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                groups.forEach { (name, desc) ->
                    val isJoined = communities.contains(name)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(0.03f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(0.1f))) {
                                Text("🎧", modifier = Modifier.align(Alignment.Center), fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(desc, color = Color.White.copy(0.4f), fontSize = 9.sp)
                            }
                        }
                        Button(
                            onClick = { if (isJoined) communities.remove(name) else communities.add(name) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isJoined) Color.White.copy(0.1f) else Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text(if (isJoined) "Joined" else "Join", color = if (isJoined) Color.White else Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RewardsStep(
    username: String,
    avatarUrl: String,
    themeSelected: String,
    onComplete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(GoldenVipBrush),
            contentAlignment = Alignment.Center
        ) {
            Text("🎁", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Welcome Rewards Claimed!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Welcome $username! As a first-time pioneer, we have credited your Lumexa balance with:",
            color = Color.White.copy(0.7f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Large gold coin reward token card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🪙 500 COINS", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700))
                Text("Pre-loaded inside your secure personal Wallet", color = Color.White.copy(0.4f), fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // New User Tips list
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("First-Login Quick Tips", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))

            listOf(
                "💬 Host live voice party rooms via the Voice Rooms tab.",
                "🛍️ Buy customized avatar frames & backgrounds in Settings.",
                "🎨 Set status emoji and background themes on your profile screen."
            ).forEach { tip ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tip, color = Color.White.copy(0.6f), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onComplete,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("complete_onboarding_wizard")
        ) {
            Text("Complete & Enter Arena", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

// Layout helper for FlowRow since standard FlowRow is in experimental layouts
@Composable
fun FlowRowHelper(
    spacing: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(content = content) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val layoutWidth = constraints.maxWidth
        var rowHeight = 0
        var currentX = 0
        var currentY = 0
        val positions = mutableListOf<Pair<Int, Int>>()

        placeables.forEach { placeable ->
            if (currentX + placeable.width > layoutWidth) {
                currentX = 0
                currentY += rowHeight + spacing.roundToPx()
                rowHeight = 0
            }
            positions.add(Pair(currentX, currentY))
            currentX += placeable.width + spacing.roundToPx()
            rowHeight = maxOf(rowHeight, placeable.height)
        }

        layout(layoutWidth, currentY + rowHeight) {
            placeables.forEachIndexed { index, placeable ->
                val (x, y) = positions[index]
                placeable.placeRelative(x, y)
            }
        }
    }
}

val GoldenVipBrush = androidx.compose.ui.graphics.Brush.linearGradient(
    colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
)

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GoldenVipBrush
import com.example.ui.components.PremiumGradientBrush
import com.example.ui.components.CosmosBackgroundBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: ConnectLiveViewModel) {
    var activeCategory by remember { mutableStateOf<String?>(null) } // "account", "privacy", "notifications", "appearance", "engagement", "help"
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBackgroundBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (activeCategory != null) {
                            activeCategory = null
                        } else {
                            viewModel.navigateTo("main")
                        }
                    },
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (activeCategory) {
                        "account" -> "Account Security"
                        "privacy" -> "Privacy Controls"
                        "notifications" -> "Alert Preferences"
                        "appearance" -> "Aesthetic Customizer"
                        "engagement" -> "Gamified Engagement Lounge"
                        "help" -> "Help & Safety Center"
                        else -> "System Configuration"
                    },
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            AnimatedContent(
                targetState = activeCategory,
                transitionSpec = {
                    slideInHorizontally { width -> if (targetState != null) width else -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> if (targetState == null) width else -width } + fadeOut()
                },
                label = "settings_navigation"
            ) { category ->
                when (category) {
                    "account" -> AccountSettingsScreen(viewModel)
                    "privacy" -> PrivacySettingsScreen()
                    "notifications" -> NotificationSettingsScreen()
                    "appearance" -> AppearanceSettingsScreen()
                    "engagement" -> EngagementHubScreen(viewModel)
                    "help" -> HelpCenterScreen()
                    else -> MainSettingsMenu(onCategorySelect = { activeCategory = it }, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainSettingsMenu(onCategorySelect: (String) -> Unit, viewModel: ConnectLiveViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        item {
            Text("General Categories", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingsCategoryItem(
                    title = "Account Security",
                    subtitle = "Manage identity, password, 2FA & active devices",
                    icon = Icons.Default.Person,
                    onClick = { onCategorySelect("account") }
                )
                SettingsCategoryItem(
                    title = "Privacy Controls",
                    subtitle = "Decide who can message, call, see posts & view followers",
                    icon = Icons.Default.Lock,
                    onClick = { onCategorySelect("privacy") }
                )
                SettingsCategoryItem(
                    title = "Alert Preferences",
                    subtitle = "Configure push, email, and live system notifications",
                    icon = Icons.Default.Notifications,
                    onClick = { onCategorySelect("notifications") }
                )
                SettingsCategoryItem(
                    title = "Aesthetic Customizer",
                    subtitle = "Toggle themes, fluid animations, font sizes & translation languages",
                    icon = Icons.Default.Palette,
                    onClick = { onCategorySelect("appearance") }
                )
            }
        }

        item {
            Text("Incentive & Loyalty Hub", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            SettingsCategoryItem(
                title = "Gamified Engagement Lounge",
                subtitle = "Claim streaks, badges, compete in rankings & referral contests",
                icon = Icons.Default.EmojiEvents,
                iconTint = Color(0xFFFFD700),
                onClick = { onCategorySelect("engagement") }
            )
        }

        item {
            Text("Support & Legal guidelines", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            SettingsCategoryItem(
                title = "Help & Safety Center",
                subtitle = "Resolve payment issues, report toxic users & view guidelines",
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                onClick = { onCategorySelect("help") }
            )
        }

        item {
            Text("Developers Workspace", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Secure Cloud Infrastructure", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "To access private cloud APIs or streaming enclaves, insert secure tokens in AI Studio's 'Secrets' sidebar. Your Android project is built securely with auto-indexing Spanner backends & secure key configurations.",
                        color = Color.White.copy(0.5f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("logout_button")
            ) {
                Text("Sign Out Secure Session", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SettingsCategoryItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = Color(0xFF00E5FF),
    onClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(0.04f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(0.45f), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(0.3f))
        }
    }
}

// --- ACCOUNT SECURITY SUB SCREEN ---
@Composable
fun AccountSettingsScreen(viewModel: ConnectLiveViewModel) {
    val googleAccount by viewModel.googleLinkedAccount.collectAsState()
    var username by remember { mutableStateOf("alex_mercer") }
    var email by remember { mutableStateOf("alex.mercer@lumexa.net") }
    var phone by remember { mutableStateOf("+1 (555) 019-2831") }
    var password by remember { mutableStateOf("••••••••••••") }
    var is2FaEnabled by remember { mutableStateOf(true) }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeactivateDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Text("Editable Personal Credentials", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccountInputField(label = "Username Handle", value = username, onValueChange = { username = it })
                    AccountInputField(label = "Primary Email address", value = email, onValueChange = { email = it })
                    AccountInputField(label = "Phone Number", value = phone, onValueChange = { phone = it })
                    AccountInputField(label = "Account Password", value = password, onValueChange = { password = it }, isPassword = true)
                    
                    Button(
                        onClick = { Toast.makeText(context, "Account credentials updated successfully!", Toast.LENGTH_SHORT).show() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text("Apply Credentials Change", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Authentication & Identity Integrations", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth().testTag("google_settings_integration_card")) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                            Text("Google Identity One-Tap", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = googleAccount?.email ?: "Not connected (Click to link Google OAuth)",
                                color = Color.White.copy(0.5f),
                                fontSize = 10.sp
                            )
                        }
                        if (googleAccount != null) {
                            OutlinedButton(
                                onClick = { viewModel.unlinkGoogleAccount() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Unlink", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.navigateTo("auth") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Connect", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(0.08f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Two-Factor Authentication (2FA)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Secure your account with multi-factor SMS & App codes.", color = Color.White.copy(0.4f), fontSize = 10.sp)
                        }
                        Switch(
                            checked = is2FaEnabled,
                            onCheckedChange = { is2FaEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                        )
                    }

                    Divider(color = Color.White.copy(0.06f))

                    Text("Linked OAuth Identity Accounts", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Google" to true, "Apple" to false, "GitHub" to true).forEach { (platform, linked) ->
                            var isLinked by remember { mutableStateOf(linked) }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isLinked) Color(0xFF8E2DE2).copy(0.2f) else Color.White.copy(0.03f))
                                    .border(1.dp, if (isLinked) Color(0xFF8E2DE2) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { isLinked = !isLinked }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isLinked) "Linked $platform" else "Link $platform",
                                    color = if (isLinked) Color.White else Color.White.copy(0.4f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Connected Devices & Session Logs", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DeviceSessionItem(device = "Samsung Galaxy S24 Ultra (This Phone)", location = "San Francisco, USA", active = true)
                    Divider(color = Color.White.copy(0.06f))
                    DeviceSessionItem(device = "Google Pixel 8 Pro", location = "New York, USA", active = false)
                    Divider(color = Color.White.copy(0.06f))
                    DeviceSessionItem(device = "Apple iPad Pro 12.9", location = "London, UK", active = false)
                }
            }
        }

        item {
            Text("Danger Zone", color = Color(0xFFFF4D4D).copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showDeactivateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text("Deactivate Account", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4D)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text("Delete Account Permanently", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = { Text("Deactivate Account?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Your account will be temporarily frozen. You can reactivate it at any time by logging back in.", color = Color.White.copy(0.7f), fontSize = 12.sp) },
            containerColor = Color(0xFF140D2F),
            confirmButton = {
                Button(onClick = { showDeactivateDialog = false; Toast.makeText(context, "Account deactivated successfully.", Toast.LENGTH_LONG).show() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) {
                    Text("Deactivate", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) { Text("Cancel", color = Color.White) }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Permanently Delete Account?", color = Color.Red, fontWeight = FontWeight.Bold) },
            text = { Text("Are you absolutely sure? This action is irreversible. All of your streams, coins, chats, and followers will be deleted permanently from our cloud database.", color = Color.White.copy(0.7f), fontSize = 12.sp) },
            containerColor = Color(0xFF140D2F),
            confirmButton = {
                Button(onClick = { showDeleteDialog = false; Toast.makeText(context, "Account deletion request submitted.", Toast.LENGTH_LONG).show() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Delete Account", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = Color.White) }
            }
        )
    }
}

@Composable
fun AccountInputField(label: String, value: String, onValueChange: (String) -> Unit, isPassword: Boolean = false) {
    Column {
        Text(label, color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF8E2DE2)
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun DeviceSessionItem(device: String, location: String, active: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Devices, contentDescription = null, tint = if (active) Color(0xFF00E676) else Color.White.copy(0.4f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(device, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(location, color = Color.White.copy(0.4f), fontSize = 10.sp)
        }
        if (active) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF00E676).copy(0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("ACTIVE", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                Text("Revoke", color = Color(0xFFFF4D4D), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- PRIVACY CONTROLS SUB SCREEN ---
@Composable
fun PrivacySettingsScreen() {
    var messagePrivacy by remember { mutableStateOf("Everyone") }
    var callPrivacy by remember { mutableStateOf("Friends Only") }
    var profilePrivacy by remember { mutableStateOf("Everyone") }
    var followersPrivacy by remember { mutableStateOf("Friends Only") }
    var followingPrivacy by remember { mutableStateOf("Friends Only") }
    var storiesPrivacy by remember { mutableStateOf("Everyone") }
    var streamsPrivacy by remember { mutableStateOf("Everyone") }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Text("Who can communicate with me", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrivacySelectorRow(label = "Who can message me", selected = messagePrivacy, options = listOf("Everyone", "Friends Only", "No One"), onSelected = { messagePrivacy = it })
                    Divider(color = Color.White.copy(0.06f))
                    PrivacySelectorRow(label = "Who can call me", selected = callPrivacy, options = listOf("Everyone", "Friends Only", "No One"), onSelected = { callPrivacy = it })
                }
            }
        }

        item {
            Text("Social Graph & Visibility Permissions", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrivacySelectorRow(label = "Who can see my profile", selected = profilePrivacy, options = listOf("Everyone", "Friends Only"), onSelected = { profilePrivacy = it })
                    Divider(color = Color.White.copy(0.06f))
                    PrivacySelectorRow(label = "Who can see my followers", selected = followersPrivacy, options = listOf("Everyone", "Friends Only", "No One"), onSelected = { followersPrivacy = it })
                    Divider(color = Color.White.copy(0.06f))
                    PrivacySelectorRow(label = "Who can see my following", selected = followingPrivacy, options = listOf("Everyone", "Friends Only", "No One"), onSelected = { followingPrivacy = it })
                    Divider(color = Color.White.copy(0.06f))
                    PrivacySelectorRow(label = "Who can see my stories", selected = storiesPrivacy, options = listOf("Everyone", "Friends Only"), onSelected = { storiesPrivacy = it })
                    Divider(color = Color.White.copy(0.06f))
                    PrivacySelectorRow(label = "Who can see my live streams", selected = streamsPrivacy, options = listOf("Everyone", "Friends Only"), onSelected = { streamsPrivacy = it })
                }
            }
        }

        item {
            Text("Modulations & Blocklists", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    UserRestrictionRow(type = "Blocked Users", count = 3, items = listOf("spammer_alex", "bot_9921", "toxic_streamer"), onAction = { Toast.makeText(context, "$it Unblocked!", Toast.LENGTH_SHORT).show() })
                    Divider(color = Color.White.copy(0.06f))
                    UserRestrictionRow(type = "Muted Users", count = 2, items = listOf("noisy_room_mod", "chatterbox_v2"), onAction = { Toast.makeText(context, "$it Unmuted!", Toast.LENGTH_SHORT).show() })
                    Divider(color = Color.White.copy(0.06f))
                    UserRestrictionRow(type = "Restricted Users", count = 1, items = listOf("creepy_dm_guy"), onAction = { Toast.makeText(context, "$it Unrestricted!", Toast.LENGTH_SHORT).show() })
                }
            }
        }
    }
}

@Composable
fun PrivacySelectorRow(label: String, selected: String, options: List<String>, onSelected: (String) -> Unit) {
    Column {
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { opt ->
                val isSel = selected == opt
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSel) Color(0xFF00E5FF) else Color.White.copy(0.03f))
                        .clickable { onSelected(opt) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opt,
                        color = if (isSel) Color.Black else Color.White.copy(0.5f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun UserRestrictionRow(type: String, count: Int, items: List<String>, onAction: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(type, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("$count restricted user accounts", color = Color.White.copy(0.4f), fontSize = 10.sp)
            }
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White.copy(0.3f)
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(0.02f))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("@$user", color = Color.White.copy(0.8f), fontSize = 11.sp)
                        Button(
                            onClick = { onAction(user) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.08f)),
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Text("Remove", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- ALERT PREFERENCES SUB SCREEN ---
@Composable
fun NotificationSettingsScreen() {
    var messages by remember { mutableStateOf(true) }
    var calls by remember { mutableStateOf(true) }
    var friendRequests by remember { mutableStateOf(true) }
    var comments by remember { mutableStateOf(true) }
    var likes by remember { mutableStateOf(false) }
    var tags by remember { mutableStateOf(true) }
    var stories by remember { mutableStateOf(false) }
    var liveStreams by remember { mutableStateOf(true) }
    var marketing by remember { mutableStateOf(false) }
    var emailAlerts by remember { mutableStateOf(true) }
    var pushAlerts by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Text("In-App Event Triggers", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    NotificationToggleItem(title = "Instant Chat Messages", checked = messages, onCheckedChange = { messages = it })
                    Divider(color = Color.White.copy(0.06f))
                    NotificationToggleItem(title = "Voice & Video Calls", checked = calls, onCheckedChange = { calls = it })
                    Divider(color = Color.White.copy(0.06f))
                    NotificationToggleItem(title = "Friend Requests", checked = friendRequests, onCheckedChange = { friendRequests = it })
                    Divider(color = Color.White.copy(0.06f))
                    NotificationToggleItem(title = "Post Comments", checked = comments, onCheckedChange = { comments = it })
                    Divider(color = Color.White.copy(0.06f))
                    NotificationToggleItem(title = "Post Likes & Reactions", checked = likes, onCheckedChange = { likes = it })
                    Divider(color = Color.White.copy(0.06f))
                    NotificationToggleItem(title = "Direct Photo Tags", checked = tags, onCheckedChange = { tags = it })
                }
            }
        }

        item {
            Text("Broadcast & Broadcaster alerts", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    NotificationToggleItem(title = "New Creator Stories", checked = stories, onCheckedChange = { stories = it })
                    Divider(color = Color.White.copy(0.06f))
                    NotificationToggleItem(title = "Live Stream Broadcasters", checked = liveStreams, onCheckedChange = { liveStreams = it })
                }
            }
        }

        item {
            Text("Alert Delivery Systems", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    NotificationToggleItem(title = "Receive Push Notifications", checked = pushAlerts, onCheckedChange = { pushAlerts = it })
                    Divider(color = Color.White.copy(0.06f))
                    NotificationToggleItem(title = "Weekly Email Digests", checked = emailAlerts, onCheckedChange = { emailAlerts = it })
                    Divider(color = Color.White.copy(0.06f))
                    NotificationToggleItem(title = "Marketing & Sponsoring Alerts", checked = marketing, onCheckedChange = { marketing = it })
                }
            }
        }
    }
}

@Composable
fun NotificationToggleItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
        )
    }
}

// --- AESTHETIC CUSTOMIZER SUB SCREEN ---
@Composable
fun AppearanceSettingsScreen() {
    var isDarkMode by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("Deep Purple Space") }
    var fontSizeSlider by remember { mutableStateOf(1.0f) } // 0.8f = Small, 1.0f = Default, 1.2f = Large, 1.4f = Extra Large
    var currentLanguage by remember { mutableStateOf("English (US)") }
    var animationsEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Text("Colors & Core Themes", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Enforce Dark Mode", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Reduce OLED battery strain", color = Color.White.copy(0.4f), fontSize = 10.sp)
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { isDarkMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                        )
                    }

                    Divider(color = Color.White.copy(0.06f))

                    Text("Active Visual Scheme", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Deep Purple Space", "Light Mint", "Vibrant Gold VIP").forEach { t ->
                            val isSel = selectedTheme == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.03f))
                                    .clickable { selectedTheme = t; Toast.makeText(context, "$t scheme applied!", Toast.LENGTH_SHORT).show() }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(t, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Typography & Translation", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column {
                        Text("Text Heading Scale", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        val textScaleLabel = when {
                            fontSizeSlider < 0.9f -> "Small Fonts (80%)"
                            fontSizeSlider < 1.1f -> "Default Scale (100%)"
                            fontSizeSlider < 1.3f -> "Large Scale (120%)"
                            else -> "Accessibly Massive (140%)"
                        }
                        Text(textScaleLabel, color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = fontSizeSlider,
                            onValueChange = { fontSizeSlider = it },
                            valueRange = 0.8f..1.4f,
                            steps = 2,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF8E2DE2))
                        )
                    }

                    Divider(color = Color.White.copy(0.06f))

                    Column {
                        Text("Preferred Translation Language", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("English (US)", "Español", "Français", "Deutsch", "日本語", "العربية").forEach { lang ->
                                val isSel = currentLanguage == lang
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF00E5FF) else Color.White.copy(0.03f))
                                        .clickable { currentLanguage = lang; Toast.makeText(context, "System UI translated to $lang", Toast.LENGTH_SHORT).show() }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(lang, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Engine Motion Preferences", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enable Fluid Animations", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Disable to save CPU & battery performance", color = Color.White.copy(0.4f), fontSize = 10.sp)
                    }
                    Switch(
                        checked = animationsEnabled,
                        onCheckedChange = { animationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                    )
                }
            }
        }
    }
}

// --- GAMIFIED ENGAGEMENT LOUNGE ---
@Composable
fun EngagementHubScreen(viewModel: ConnectLiveViewModel) {
    val xpCount by viewModel.xpCount.collectAsState()
    val rewardPoints by viewModel.rewardPoints.collectAsState()
    val dailyClaimed by viewModel.dailyRewardClaimed.collectAsState()
    val weeklyClaimed by viewModel.weeklyRewardClaimed.collectAsState()
    val referralApplied by viewModel.referralApplied.collectAsState()

    var refCodeInput by remember { mutableStateOf("") }
    var activeLeaderboardTab by remember { mutableStateOf("global") } // "global", "country", "city", "friends"
    var showBirthdayDialog by remember { mutableStateOf(false) }
    var showAnniversaryDialog by remember { mutableStateOf(false) }
    var showWelcomeDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 50.dp)
    ) {
        // Core levels and loyalty stats
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("LUMEXA VIP LEVEL 14", fontWeight = FontWeight.Black, color = Color.White, fontSize = 16.sp)
                            Text("Streak: 5 consecutive days live!", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PremiumGradientBrush),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Whatshot, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // XP bar
                    val nextLevelXp = 1000
                    val progress = xpCount.toFloat() / nextLevelXp.toFloat()
                    LinearProgressIndicator(
                        progress = progress,
                        color = Color(0xFF00E5FF),
                        trackColor = Color.White.copy(0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$xpCount / $nextLevelXp XP", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        Text("250 XP to Level 15", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color.White.copy(0.06f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$rewardPoints", fontWeight = FontWeight.Black, color = Color(0xFFFFD700), fontSize = 16.sp)
                            Text("Reward Points", color = Color.White.copy(0.4f), fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("5 Days", fontWeight = FontWeight.Black, color = Color(0xFF00E676), fontSize = 16.sp)
                            Text("Login Streak", color = Color.White.copy(0.4f), fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("12", fontWeight = FontWeight.Black, color = Color(0xFF00E5FF), fontSize = 16.sp)
                            Text("Referrals", color = Color.White.copy(0.4f), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Seasonal and Birthday VIP rewards claiming
        item {
            Text("Milestone & Promotional Gifts", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showWelcomeDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Welcome Claim", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { showBirthdayDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Birthday Bonus", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Button(
                    onClick = { showAnniversaryDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Anniversary 🎁", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        // Daily Attendance Rewards (Days 1 to 7)
        item {
            Text("Attendance Calendar Streaks", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple(1, "Day 1", true),
                        Triple(2, "Day 2", false),
                        Triple(3, "Day 3", false),
                        Triple(4, "Day 4", false),
                        Triple(5, "Day 5", false),
                        Triple(6, "Day 6", false),
                        Triple(7, "Day 7 VIP", false)
                    ).forEach { (day, title, claimed) ->
                        val isDay2Claimable = day == 2 && !dailyClaimed
                        Box(
                            modifier = Modifier
                                .size(width = 80.dp, height = 90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (claimed || (day == 2 && dailyClaimed)) Color(0xFF00E676).copy(0.12f)
                                    else if (isDay2Claimable) Color(0xFF8E2DE2).copy(0.2f)
                                    else Color.White.copy(0.03f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (claimed || (day == 2 && dailyClaimed)) Color(0xFF00E676)
                                    else if (isDay2Claimable) Color(0xFF8E2DE2)
                                    else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable(enabled = isDay2Claimable) {
                                    viewModel.claimDailyReward()
                                    Toast.makeText(context, "Day 2 Attendance claimed successfully!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Icon(
                                    imageVector = if (claimed || (day == 2 && dailyClaimed)) Icons.Default.CheckCircle else Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = if (claimed || (day == 2 && dailyClaimed)) Color(0xFF00E676) else Color(0xFFFFD700),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (day == 7) "+100 pts" else "+15 pts",
                                    color = Color.White.copy(0.5f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Weekly streak card
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Weekly Milestone Reward", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Get 50 points and 100 XP for keeping up!", color = Color.White.copy(0.4f), fontSize = 9.sp)
                        }
                        Button(
                            onClick = {
                                viewModel.claimWeeklyReward()
                                Toast.makeText(context, "Weekly milestone claimed successfully!", Toast.LENGTH_SHORT).show()
                            },
                            enabled = !weeklyClaimed,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            modifier = Modifier.height(28.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = if (weeklyClaimed) "Claimed" else "Claim",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Enter Referral code & Invite Competitions
        item {
            Text("Ref Code & Competitions", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Apply Invitation Referral code", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Redeem codes from friends to receive instant XP & points", color = Color.White.copy(0.4f), fontSize = 9.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = refCodeInput,
                            onValueChange = { refCodeInput = it },
                            placeholder = { Text("LMX-XXXX-XP", color = Color.White.copy(0.2f), fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF8E2DE2)
                            ),
                            singleLine = true,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Button(
                            onClick = {
                                val ok = viewModel.applyReferralCode(refCodeInput)
                                if (ok) {
                                    Toast.makeText(context, "Referral Code Redeemed! +150 XP & +50 Points!", Toast.LENGTH_LONG).show()
                                    refCodeInput = ""
                                } else {
                                    Toast.makeText(context, "Invalid or already applied referral code.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !referralApplied,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.White.copy(0.06f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Weekly Invite Competition Leaderboard", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        InviteLeaderboardRow(rank = 1, name = "Alex Mercer", count = 48)
                        InviteLeaderboardRow(rank = 2, name = "Sofia Lopez", count = 39)
                        InviteLeaderboardRow(rank = 3, name = "Chloe Dubois", count = 27)
                        InviteLeaderboardRow(rank = 14, name = "You (Alex)", count = 12, isMe = true)
                    }
                }
            }
        }

        // Active Challenges & Global rankings
        item {
            Text("Active Challenges & Rankings", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(0.03f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("global" to "Global", "country" to "USA", "city" to "San Francisco", "friends" to "Friends").forEach { (tabId, label) ->
                    val isSel = activeLeaderboardTab == tabId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) Color(0xFF8E2DE2) else Color.Transparent)
                            .clickable { activeLeaderboardTab = tabId }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = if (isSel) Color.White else Color.White.copy(0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${activeLeaderboardTab.uppercase()} ACTIVE CREATOR RANKING", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    
                    when (activeLeaderboardTab) {
                        "global" -> {
                            InviteLeaderboardRow(rank = 1, name = "DJ Aura (Tokyo)", count = 12500, label = "pts")
                            InviteLeaderboardRow(rank = 2, name = "Acoustic Beats (London)", count = 11400, label = "pts")
                            InviteLeaderboardRow(rank = 3, name = "Tech Maven (NYC)", count = 9800, label = "pts")
                        }
                        "country" -> {
                            InviteLeaderboardRow(rank = 1, name = "Tech Maven (NYC)", count = 9800, label = "pts")
                            InviteLeaderboardRow(rank = 2, name = "Alex Mercer", count = 8900, label = "pts")
                            InviteLeaderboardRow(rank = 3, name = "Sarah Jenkins (LA)", count = 7400, label = "pts")
                        }
                        "city" -> {
                            InviteLeaderboardRow(rank = 1, name = "Alex Mercer", count = 8900, label = "pts")
                            InviteLeaderboardRow(rank = 2, name = "Chloe Dubois", count = 4120, label = "pts")
                            InviteLeaderboardRow(rank = 3, name = "Sora Hayashi", count = 2800, label = "pts")
                        }
                        else -> {
                            InviteLeaderboardRow(rank = 1, name = "Chloe Dubois", count = 4120, label = "pts")
                            InviteLeaderboardRow(rank = 2, name = "Sora Hayashi", count = 2800, label = "pts")
                            InviteLeaderboardRow(rank = 3, name = "You", count = 1240, label = "pts", isMe = true)
                        }
                    }
                }
            }
        }

        // Daily / Weekly challenges
        item {
            Text("Active Tasks & Challenges", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ChallengeTaskItem(title = "Daily Challenge: Joint Stream", desc = "Start or join a joint collaboration stream with another active creator.", xpReward = 50, pointsReward = 10, progress = 0f)
                ChallengeTaskItem(title = "Weekly Challenge: Voice Room Hosting", desc = "Host an active room with more than 5 participants for 20 minutes.", xpReward = 200, pointsReward = 50, progress = 0.6f)
                ChallengeTaskItem(title = "Seasonal Event: Neon Summer Waves", desc = "Earn 5000 coins in stream gifts during our Neon Summer Event.", xpReward = 1000, pointsReward = 250, progress = 0.1f)
            }
        }

        // Creator / Community / Live stream competitions
        item {
            Text("Active Platform Competitions", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PlatformCompetitionItem(title = "Global Creator Soundclash", prize = "50,000 Coins Prize Pool", desc = "Streamers compete in real-time acoustic matches.", joined = true)
                    Divider(color = Color.White.copy(0.06f))
                    PlatformCompetitionItem(title = "EDM Producers Community Clash", prize = "VIP Enclave Badge", desc = "Communities assemble the most active streaming logs.", joined = false)
                    Divider(color = Color.White.copy(0.06f))
                    PlatformCompetitionItem(title = "Global Live Stream Speedrun", prize = "$5,000 Cash sponsorship", desc = "Highest continuous user retention in a 60-min slot.", joined = false)
                }
            }
        }

        // Personalized AI recommendations list (Section AG and AA compliance)
        item {
            Text("AI Personalized Recommendations", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonalizedRecommendationItem(title = "EDM Synth Lab Stream", author = "DJ Aura", desc = "Based on your interest in: EDM, Music Production", matchScore = 98)
                PersonalizedRecommendationItem(title = "Decentralized AI Enclaves Chat", author = "Alex Mercer", desc = "Based on your interest in: Cybersecurity, Web3", matchScore = 94)
            }
        }
    }

    // Gift claim overlays
    if (showWelcomeDialog) {
        AlertDialog(
            onDismissRequest = { showWelcomeDialog = false },
            icon = { Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(40.dp)) },
            title = { Text("Welcome Loyalty Gift Claimed!", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Thanks for joining Lumexa! You have been awarded 50 Reward Points and 100 XP to kickstart your journey.", color = Color.White.copy(0.7f), fontSize = 12.sp, textAlign = TextAlign.Center) },
            containerColor = Color(0xFF140D2F),
            confirmButton = {
                Button(onClick = { showWelcomeDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2))) {
                    Text("Fantastic!", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showBirthdayDialog) {
        AlertDialog(
            onDismissRequest = { showBirthdayDialog = false },
            icon = { Icon(Icons.Default.Cake, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(40.dp)) },
            title = { Text("Happy Birthday Creator! 🎂", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Have a blast! We deposited 100 Golden VIP Points and a virtual Birthday Badge into your wallet.", color = Color.White.copy(0.7f), fontSize = 12.sp, textAlign = TextAlign.Center) },
            containerColor = Color(0xFF140D2F),
            confirmButton = {
                Button(onClick = { showBirthdayDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))) {
                    Text("Thank you!", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showAnniversaryDialog) {
        AlertDialog(
            onDismissRequest = { showAnniversaryDialog = false },
            icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(40.dp)) },
            title = { Text("Anniversary Reward!", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("We appreciate your continuous support! Your annual contribution of streams has qualified you for 200 Golden Coins.", color = Color.White.copy(0.7f), fontSize = 12.sp, textAlign = TextAlign.Center) },
            containerColor = Color(0xFF140D2F),
            confirmButton = {
                Button(onClick = { showAnniversaryDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))) {
                    Text("Claim Bonus", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun InviteLeaderboardRow(rank: Int, name: String, count: Int, label: String = "invites", isMe: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isMe) Color(0xFF8E2DE2).copy(0.15f) else Color.Transparent)
            .border(1.dp, if (isMe) Color(0xFF8E2DE2).copy(0.4f) else Color.Transparent, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    when (rank) {
                        1 -> Color(0xFFFFD700)
                        2 -> Color(0xFFC0C0C0)
                        3 -> Color(0xFFCD7F32)
                        else -> Color.White.copy(0.08f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rank",
                color = if (rank in 1..3) Color.Black else Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(name, color = if (isMe) Color(0xFF00E5FF) else Color.White, fontSize = 12.sp, fontWeight = if (isMe) FontWeight.Bold else FontWeight.Normal)
        Spacer(modifier = Modifier.weight(1f))
        Text("$count $label", color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ChallengeTaskItem(title: String, desc: String, xpReward: Int, pointsReward: Int, progress: Float) {
    var progressVal by remember { mutableStateOf(progress) }
    val context = LocalContext.current

    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF00E5FF).copy(0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("+$xpReward XP • +$pointsReward PTS", color = Color(0xFF00E5FF), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = Color.White.copy(0.45f), fontSize = 9.sp, lineHeight = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = progressVal,
                    color = Color(0xFF8E2DE2),
                    trackColor = Color.White.copy(0.08f),
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                if (progressVal >= 1f) {
                    Button(
                        onClick = { progressVal = 0f; Toast.makeText(context, "Challenge reward claimed! +$xpReward XP!", Toast.LENGTH_SHORT).show() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        modifier = Modifier.height(24.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text("Claim", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { progressVal = (progressVal + 0.4f).coerceAtMost(1f) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.06f)),
                        modifier = Modifier.height(24.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text("Practice", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PlatformCompetitionItem(title: String, prize: String, desc: String, joined: Boolean) {
    var isJoined by remember { mutableStateOf(joined) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(prize, color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = Color.White.copy(0.4f), fontSize = 9.sp)
        }
        Button(
            onClick = { isJoined = !isJoined },
            colors = ButtonDefaults.buttonColors(containerColor = if (isJoined) Color.White.copy(0.1f) else Color(0xFF8E2DE2)),
            modifier = Modifier.height(26.dp),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
        ) {
            Text(if (isJoined) "Quit" else "Join", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PersonalizedRecommendationItem(title: String, author: String, desc: String, matchScore: Int) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("by $author", color = Color.White.copy(0.4f), fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, color = Color.White.copy(0.5f), fontSize = 10.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF00E676).copy(0.15f))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("$matchScore% AI Match", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- HELP & SAFETY CENTER SUB SCREEN ---
@Composable
fun HelpCenterScreen() {
    var subjectInput by remember { mutableStateOf("") }
    var messageInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Text("Frequently Asked Questions", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FAQExpandableItem(
                        question = "How do I cash out virtual stream diamonds?",
                        answer = "Navigate to the 'Wallet' tab on the bottom menu, tap on the 'Withdraw Payout' section, input your desired payout value, and confirm. Diamond payouts are synchronized with Spanner ledger systems instantly."
                    )
                    Divider(color = Color.White.copy(0.06f))
                    FAQExpandableItem(
                        question = "How are harassment & spam cases handled?",
                        answer = "Our AI Shield Cybersecurity engine scans and moderates spam triggers automatically in real-time. If you encounter harassment, you can instantly block, mute, or restrict profiles via context menus."
                    )
                    Divider(color = Color.White.copy(0.06f))
                    FAQExpandableItem(
                        question = "Can I start an acoustic joint stream?",
                        answer = "Yes! Tap on the 'Creator Hub' sub-module in the main Feed screen. Navigate to 'Pending Joint Collaboration Requests' to request brand sponsorships or trigger real-time joint-streams."
                    )
                }
            }
        }

        item {
            Text("Contact Human Support Desk", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Open a Help Ticket", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = subjectInput,
                        onValueChange = { subjectInput = it },
                        placeholder = { Text("Subject (e.g., Payment issue, Appeal)", color = Color.White.copy(0.2f), fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF8E2DE2)),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Tell us details about the problem...", color = Color.White.copy(0.2f), fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = {
                            if (subjectInput.isNotBlank() && messageInput.isNotBlank()) {
                                Toast.makeText(context, "Support ticket submitted successfully!", Toast.LENGTH_LONG).show()
                                subjectInput = ""
                                messageInput = ""
                            } else {
                                Toast.makeText(context, "Please enter both subject and message details.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text("Submit Ticket", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Legal Agreements & Policies", color = Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PolicyDocumentItem(title = "Community Guidelines", content = "Lumexa aims to preserve decentralized social harmony. Bullying, spamming, and toxic behavior trigger immediate AI moderation bans. Respect other creators.")
                    Divider(color = Color.White.copy(0.06f))
                    PolicyDocumentItem(title = "Privacy Policy & GDPR", content = "We respect your digital sovereignty. All chat history, location records, and video streams are end-to-end encrypted and completely erased upon deactivation.")
                    Divider(color = Color.White.copy(0.06f))
                    PolicyDocumentItem(title = "Terms of Service", content = "By accessing our system configuration, you agree to secure account credentials. Unauthorized bot automation or coin manipulation is strictly forbidden.")
                    Divider(color = Color.White.copy(0.06f))
                    PolicyDocumentItem(title = "Copyright Policy & DMCA", content = "Lumexa protects intellectual property. Unlicensed audio streaming, copyrighted EDM loops, or visual theft is subject to instant DMCA removal.")
                }
            }
        }
    }
}

@Composable
fun FAQExpandableItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(question, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White.copy(0.3f)
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(answer, color = Color.White.copy(0.6f), fontSize = 11.sp, lineHeight = 14.sp)
        }
    }
}

@Composable
fun PolicyDocumentItem(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White.copy(0.3f)
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(content, color = Color.White.copy(0.6f), fontSize = 11.sp, lineHeight = 14.sp)
        }
    }
}

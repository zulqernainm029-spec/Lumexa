package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.CosmosBackgroundBrush
import com.example.ui.components.PremiumGradientBrush
import com.example.ui.components.VoiceVideoRoomComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiLibraryScreen(viewModel: ConnectLiveViewModel) {
    var activeCategory by remember { mutableStateOf("Auth Flows") }
    var selectedScreenId by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "Auth Flows",
        "Feeds",
        "Profiles",
        "Messaging",
        "Voice/Video",
        "Content/Reels",
        "Live Stream",
        "Marketplace",
        "Wallet",
        "Creator/Biz",
        "Community",
        "Settings",
        "Admin Controls",
        "Empty States",
        "Error Screens",
        "Success Screens"
    )

    val screensByCategory = mapOf(
        "Auth Flows" to listOf(
            ScreenItem("splash_preview", "Splash Screen", Icons.Default.FlashOn, "Simulates startup logo reveal & spinner."),
            ScreenItem("welcome_flow", "Welcome & Carousel", Icons.Default.ViewCarousel, "Intro slides with beautiful branding."),
            ScreenItem("sign_in_up", "Sign In / Sign Up Form", Icons.Default.LockOpen, "Fully validated fields & guest toggle."),
            ScreenItem("verification_otp", "OTP & Phone Verify", Icons.Default.Sms, "Enter secure SMS verification codes."),
            ScreenItem("forgot_reset_pwd", "Forgot / Reset Password", Icons.Default.VpnKey, "Initiate account recovery workflow."),
            ScreenItem("two_factor_bio", "2FA & Biometric Login", Icons.Default.Fingerprint, "Simulates secure hardware validation."),
            ScreenItem("recovery_lock_expired", "Account Lock & Expiry", Icons.Default.Block, "Warns about locked accounts or session timeouts.")
        ),
        "Feeds" to listOf(
            ScreenItem("home_following", "Home & Following Feeds", Icons.Default.Home, "Explore dynamic posts with dynamic likes & comments."),
            ScreenItem("trending_local", "Trending & Local Hubs", Icons.Default.Whatshot, "Check out hot posts with location telemetry."),
            ScreenItem("global_community", "Global & Community Feeds", Icons.Default.Public, "Unified social feeds from world creators."),
            ScreenItem("creator_biz_favs", "Creator & Business Feeds", Icons.Default.Star, "Specialized feeds optimized for products/services.")
        ),
        "Profiles" to listOf(
            ScreenItem("my_public_profile", "My Profile & Public Views", Icons.Default.Person, "Check reputation metrics, verification badges, and visitor views."),
            ScreenItem("friend_biz_creator", "Friend, Creator & Business Profiles", Icons.Default.Badge, "Social profile with achievements, rewards & public metrics."),
            ScreenItem("profile_analytics_settings", "Profile Analytics & Settings", Icons.Default.Analytics, "Visually tracks profile visits, charts, and visibility settings.")
        ),
        "Messaging" to listOf(
            ScreenItem("chat_lists", "Conversations Hub", Icons.Default.QuestionAnswer, "Displays pinned, secret, group, and archived chats."),
            ScreenItem("chat_details", "Rich Private & Group Chats", Icons.Default.Chat, "Supports location sharing, voice notes, doc attachments, and polls."),
            ScreenItem("msg_requests", "Message Requests Hub", Icons.Default.MarkunreadMailbox, "Allows filtering incoming strangers or channel broadcasts.")
        ),
        "Voice/Video" to listOf(
            ScreenItem("calling_outgoing", "Incoming & Outgoing Signals", Icons.Default.Call, "Simulate calling visualizer with dynamic ringers."),
            ScreenItem("active_voice_video", "Active Voice & Video Call", Icons.Default.Videocam, "Call room with screen-share indicator & camera toggle."),
            ScreenItem("call_history_settings", "Call History & Quality Logs", Icons.Default.History, "Shows missed calls, network latency, and call settings.")
        ),
        "Content/Reels" to listOf(
            ScreenItem("story_viewer_archive", "Story Viewer & Archive", Icons.Default.HistoryToggleOff, "Progressive bar, custom reactions, and history tracker."),
            ScreenItem("reels_player_editor", "Reel Player & Audio Track", Icons.Default.MovieFilter, "Vertical immersive video simulation with music indicator.")
        ),
        "Live Stream" to listOf(
            ScreenItem("stream_hub_setup", "Live Hub & Broadcast Setup", Icons.Default.Podcasts, "Configure camera, title, tags, and category beforehand."),
            ScreenItem("live_view_broadcast", "Immersive Broadcast & PK Battle", Icons.Default.SportsMartialArts, "Features live chat, gift combo explosions, and PK versus battles."),
            ScreenItem("stream_analytics_replays", "Live Analytics & Replays", Icons.Default.Assessment, "Tracks hourly viewers, total diamonds, and schedules.")
        ),
        "Marketplace" to listOf(
            ScreenItem("marketplace_home_search", "Marketplace Hub & Search", Icons.Default.Storefront, "Explore listings, filter tags, and view seller reviews."),
            ScreenItem("product_details_cart", "Product Details & Cart checkout", Icons.Default.ShoppingCart, "Enables quantity selectors, address checkout, and payment success.")
        ),
        "Wallet" to listOf(
            ScreenItem("wallet_hub_balance", "Wallet & Coin/Diamond Exchange", Icons.Default.AccountBalanceWallet, "Convert diamonds to cash, manage payment cards, and promo codes.")
        ),
        "Creator/Biz" to listOf(
            ScreenItem("creator_dash_insights", "Creator Dashboard & Content Manager", Icons.Default.AutoAwesome, "Track subscriptions, sponsorship offers, and schedule posts."),
            ScreenItem("biz_analytics_sales", "Business Console & Ad Platform", Icons.Default.BusinessCenter, "Create advertisement campaigns, check sales charts, and view inventory.")
        ),
        "Community" to listOf(
            ScreenItem("community_home_feed", "Community Hub & Events", Icons.Default.Groups, "Moderate members, set rules, check events, and join requests.")
        ),
        "Settings" to listOf(
            ScreenItem("settings_menu", "General & Account Settings", Icons.Default.Settings, "Configure notifications, dark appearance, languages, and backup.")
        ),
        "Admin Controls" to listOf(
            ScreenItem("admin_cmd_logs", "Super Admin Command Panel", Icons.Default.Dashboard, "Rollout feature flags, inspect system health logs, and appeals queue.")
        ),
        "Empty States" to listOf(
            ScreenItem("empty_messages", "No Messages Layout", Icons.Default.SpeakerNotesOff, "Visually appealing zero state with primary CTA."),
            ScreenItem("empty_friends", "No Friends Layout", Icons.Default.PersonOff, "Friendly onboarding illustration for social discovery."),
            ScreenItem("empty_notifications", "No Notifications Layout", Icons.Default.NotificationsNone, "Aesthetic bell indicator when updates are empty."),
            ScreenItem("empty_products", "No Products/Orders Layout", Icons.Default.Inbox, "Encourages marketplace sellers to list items.")
        ),
        "Error Screens" to listOf(
            ScreenItem("err_network", "Network Error / Offline", Icons.Default.SignalWifiOff, "Prompts with interactive retry controls."),
            ScreenItem("err_server_unauthorized", "Server Error / Unauthorized", Icons.Default.ErrorOutline, "Alerts users about access permissions."),
            ScreenItem("err_maintenance", "Maintenance Mode", Icons.Default.Build, "Aesthetic scheduling indicator during system work.")
        ),
        "Success Screens" to listOf(
            ScreenItem("success_payment", "Payment / Purchase Success", Icons.Default.CheckCircleOutline, "Shows invoice invoice and transaction ID."),
            ScreenItem("success_general", "Registration / Action Done", Icons.Default.Verified, "Confirms settings saved or profile updated.")
        )
    )

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
                    .background(Color(0xFF0C071A))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("main") },
                    modifier = Modifier.testTag("ui_library_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(26.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Lumexa UI System Hub",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Interactive Screen Library & Spec Previews",
                        fontSize = 11.sp,
                        color = Color.White.copy(0.5f)
                    )
                }
            }

            // Categories horizontal tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0C21))
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = activeCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF8E2DE2).copy(0.15f) else Color.White.copy(0.04f))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF8E2DE2) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { activeCategory = category }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("ui_lib_tab_$category")
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color(0xFFD0BCFF) else Color.White.copy(0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Screen Cards List
            val screensList = screensByCategory[activeCategory] ?: emptyList()
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("ui_library_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(screensList) { screen ->
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedScreenId = screen.id }
                            .testTag("ui_lib_screen_card_${screen.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.White.copy(0.05f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(screen.icon, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = screen.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = screen.desc,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(0.5f),
                                    lineHeight = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View",
                                tint = Color.White.copy(0.3f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Expanded screen state previewer dialog
        selectedScreenId?.let { screenId ->
            AlertDialog(
                onDismissRequest = { selectedScreenId = null },
                containerColor = Color(0xFF090514),
                titleContentColor = Color.White,
                textContentColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Live Rendering Screen Spec", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        IconButton(onClick = { selectedScreenId = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0C071A), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(12.dp))
                    ) {
                        RenderInteractiveScreen(screenId)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedScreenId = null }) {
                        Text("Exit Preview", color = Color(0xFFD0BCFF))
                    }
                }
            )
        }
    }
}

@Composable
fun RenderInteractiveScreen(screenId: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when {
                screenId.startsWith("splash_") || screenId.startsWith("welcome_") || screenId.startsWith("sign_") || screenId.startsWith("verification_") || screenId.startsWith("forgot_") || screenId.startsWith("two_") || screenId.startsWith("recovery_") -> {
                    RenderAuthScreens(screenId)
                }
                screenId.startsWith("home_") || screenId.startsWith("trending_") || screenId.startsWith("global_") || screenId.startsWith("creator_biz_") -> {
                    RenderFeedScreens(screenId)
                }
                screenId.startsWith("my_") || screenId.startsWith("friend_") || screenId.startsWith("profile_") || screenId.startsWith("chat_") || screenId.startsWith("msg_") -> {
                    RenderProfileAndMsgScreens(screenId)
                }
                screenId.startsWith("calling_") || screenId.startsWith("active_") || screenId.startsWith("call_") || screenId.startsWith("story_") || screenId.startsWith("reels_") -> {
                    RenderVoiceVideoContentScreens(screenId)
                }
                screenId.startsWith("stream_") || screenId.startsWith("live_") || screenId.startsWith("marketplace_") || screenId.startsWith("product_") || screenId.startsWith("wallet_") -> {
                    RenderStreamMarketWalletScreens(screenId)
                }
                screenId.startsWith("creator_dash_") || screenId.startsWith("biz_") || screenId.startsWith("community_") || screenId.startsWith("settings_") || screenId.startsWith("admin_") -> {
                    RenderCreatorBizCommunitySettingsScreens(screenId)
                }
                screenId.startsWith("empty_") || screenId.startsWith("err_") -> {
                    RenderEmptyAndErrorScreens(screenId)
                }
                screenId.startsWith("success_") -> {
                    RenderSuccessScreens(screenId)
                }
                else -> {
                    Text("Unknown screen: $screenId", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RenderAuthScreens(screenId: String) {
    var textInput1 by remember { mutableStateOf("") }
    var textInput2 by remember { mutableStateOf("") }
    var actionDone by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (screenId) {
            "splash_preview" -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(PremiumGradientBrush),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Lumexa Live Platform", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.White)
                    Text("Where connections are built live", color = Color.White.copy(0.5f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator(color = Color(0xFF00E5FF), modifier = Modifier.size(24.dp))
                }
            }
            "welcome_flow" -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Explore Lumexa Stories", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("The ultimate creative hub for interactive rooms, live streaming, PK battles, and secure dynamic chats.", color = Color.White.copy(0.6f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(0.04f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=600&q=80",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.4f)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ViewCarousel, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Text("Intro Slide 1 of 3", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2))
                    ) {
                        Text("Create Account", color = Color.White)
                    }
                }
            }
            "sign_in_up" -> {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Secure Portal Gate", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.White)
                    OutlinedTextField(
                        value = textInput1,
                        onValueChange = { textInput1 = it },
                        label = { Text("Email/Username") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = textInput2,
                        onValueChange = { textInput2 = it },
                        label = { Text("Password") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { actionDone = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                    ) {
                        Text("Sign In", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    if (actionDone) {
                        Text("🔐 Validating security credentials...", color = Color(0xFF00E676), fontSize = 11.sp)
                    }
                }
            }
            "verification_otp" -> {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("OTP Shield Verification", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    Text("SMS code dispatched to +1 (555) 019-XXXX.", color = Color.White.copy(0.5f), fontSize = 11.sp, textAlign = TextAlign.Center)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("5", "1", "9", " ", " ", " ").forEach { char ->
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color.White.copy(0.06f), RoundedCornerShape(6.dp))
                                    .border(1.dp, if (char.isNotBlank()) Color(0xFF8E2DE2) else Color.White.copy(0.1f), RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(char, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            "forgot_reset_pwd" -> {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Reset Secure Credentials", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    OutlinedTextField(
                        value = textInput1,
                        onValueChange = { textInput1 = it },
                        label = { Text("Registered Email") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(onClick = { actionDone = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)), modifier = Modifier.fillMaxWidth()) {
                        Text("Send Link")
                    }
                    if (actionDone) {
                        Text("✉️ Account reset instructions sent!", color = Color(0xFF00E676), fontSize = 11.sp)
                    }
                }
            }
            "two_factor_bio" -> {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(44.dp))
                    Text("Biometric Security Gate", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    Text("Scan fingerprint to authorize credentials.", color = Color.White.copy(0.6f), fontSize = 10.sp)
                }
            }
            "recovery_lock_expired" -> {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(40.dp))
                    Text("Device Blocked / Lockout", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    Text("Access temporarily restricted due to failing OTP. Retrying in 14m 58s.", color = Color.White.copy(0.6f), fontSize = 11.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun RenderFeedScreens(screenId: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        when (screenId) {
            "home_following" -> {
                Text("Unified Activity Hub", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(20.dp).background(Color.White.copy(0.2f), CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sarah Connor • Seattle", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Streaming the newly released Synthwave album in Voice Room #12 starting in 10 minutes! Join the countdown.", color = Color.White.copy(0.8f), fontSize = 11.sp)
                    }
                }
            }
            "trending_local" -> {
                Text("Hot Dynamic Hashtags", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                listOf("#PKBattles", "#SeattleVoiceClub", "#LumiArtistry").forEach { tag ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(tag, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("4.5K entries", color = Color.White.copy(0.4f), fontSize = 10.sp)
                    }
                }
            }
            "global_community" -> {
                Text("Popular Global Circles", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                listOf("Virtual Reality Guild", "Design Tokens Assembly").forEach { name ->
                    Box(modifier = Modifier.fillMaxWidth().background(Color.White.copy(0.04f), RoundedCornerShape(6.dp)).padding(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Join Circle", color = Color(0xFF8E2DE2), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            "creator_biz_favs" -> {
                Text("Sponsored Brand Campaign", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Box(modifier = Modifier.fillMaxWidth().background(PremiumGradientBrush, RoundedCornerShape(8.dp)).padding(10.dp)) {
                    Column {
                        Text("🚀 Lumexa Creator Boost Pro", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Gain 500 free bonus diamonds on your first live broadcast.", color = Color.White.copy(0.9f), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RenderProfileAndMsgScreens(screenId: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (screenId) {
            "my_public_profile" -> {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(48.dp).background(Color.White.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Text("AS", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Agent Storm • Verified ✅", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Rank Level: 48 • Elite Broadcaster", color = Color.White.copy(0.5f), fontSize = 10.sp)
                }
            }
            "friend_biz_creator" -> {
                Text("Friend Relationship Status", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Box(modifier = Modifier.fillMaxWidth().background(Color.White.copy(0.04f), RoundedCornerShape(6.dp)).padding(8.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("User_9240 (Active Room #2)", color = Color.White, fontSize = 11.sp)
                        Text("Mutual Friends: 14", color = Color(0xFF00E5FF), fontSize = 10.sp)
                    }
                }
            }
            "profile_analytics_settings" -> {
                Text("Profile Visitors Counter", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("👥 420 unique visits in the past 24 hours.", color = Color(0xFF00E676), fontSize = 11.sp)
            }
            "chat_lists" -> {
                Text("Secure Conversations", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                listOf("🔒 Pinned: Secret Agent Guild", "💬 Chat room #12").forEach { room ->
                    Box(modifier = Modifier.fillMaxWidth().background(Color.White.copy(0.04f), RoundedCornerShape(6.dp)).padding(8.dp)) {
                        Text(room, color = Color.White, fontSize = 11.sp)
                    }
                }
            }
            "chat_details" -> {
                Text("Shared Poll Options", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Poll: Schedule next group voice call?", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("- Tonight 8 PM (70%)", color = Color(0xFF00E5FF), fontSize = 10.sp)
                        Text("- Saturday 2 PM (30%)", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    }
                }
            }
            "msg_requests" -> {
                Text("Stranger Inbox Requests", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("alex_m (12 mutual connections) requests chat.", color = Color.White.copy(0.7f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun RenderVoiceVideoContentScreens(screenId: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        when (screenId) {
            "calling_outgoing" -> {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Dialing Outbound Secure Voip...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Sarah Connor • Seattle", color = Color.White.copy(0.4f), fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                            Text("Hang Up", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
            "active_voice_video" -> {
                Text("Voice and Video Rooms spec preview:", color = Color.White.copy(0.6f), fontSize = 11.sp)
                VoiceVideoRoomComponent(
                    roomTitle = "Design & Tech Lounge",
                    category = "Tech & Creative",
                    onLeaveRoomTriggered = {
                        // Clear selection or handle simulated exit
                    }
                )
            }
            "call_history_settings" -> {
                Text("Missed Logs", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("❌ Missed group call on Friday, 4:10 PM", color = Color(0xFFFF5252).copy(0.9f), fontSize = 11.sp)
            }
            "story_viewer_archive" -> {
                Text("Aesthetic Story Stream", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Box(modifier = Modifier.fillMaxWidth().height(80.dp).background(Color.White.copy(0.04f)), contentAlignment = Alignment.Center) {
                    Text("🖼️ Unsplash Image Asset Placeholder", color = Color.White.copy(0.5f), fontSize = 10.sp)
                }
            }
            "reels_player_editor" -> {
                Text("Vertical Reels Overlay", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("🎵 Playing track: Golden Sun Sunset Mix", color = Color(0xFFD0BCFF), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun RenderStreamMarketWalletScreens(screenId: String) {
    var actionDone by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        when (screenId) {
            "stream_hub_setup" -> {
                Text("Broadcast Setup", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)), modifier = Modifier.fillMaxWidth()) {
                    Text("Go Live on Camera", color = Color.Black)
                }
            }
            "live_view_broadcast" -> {
                Text("Live Match & PK Battle Arena", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Text("🥊 Player Alpha vs Player Beta", color = Color(0xFFFFB300), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth().height(8.dp)) {
                    Box(modifier = Modifier.weight(0.6f).fillMaxHeight().background(Color.Blue))
                    Box(modifier = Modifier.weight(0.4f).fillMaxHeight().background(Color.Red))
                }
            }
            "stream_analytics_replays" -> {
                Text("Diamond Revenue History", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("💎 450 total diamonds received this calendar month.", color = Color(0xFF00E5FF), fontSize = 11.sp)
            }
            "marketplace_home_search" -> {
                Text("Marketplace Spotlight Items", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("🎨 Custom Profile Frame Avatar V2 ($2.99)", color = Color.White, fontSize = 11.sp)
            }
            "product_details_cart" -> {
                Text("Stripe Secured Basket", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Button(onClick = { actionDone = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)), modifier = Modifier.fillMaxWidth()) {
                    Text("Pay $2.99 Now")
                }
                if (actionDone) {
                    Text("✓ Order placed successfully!", color = Color(0xFF00E676), fontSize = 11.sp)
                }
            }
            "wallet_hub_balance" -> {
                Text("Virtual Coin Wallet balance", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("💰 Balance: 1,420 Coins ($14.20)", color = Color(0xFFFFB300), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RenderCreatorBizCommunitySettingsScreens(screenId: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (screenId) {
            "creator_dash_insights" -> {
                Text("Creator Subscribers Log", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("📈 You gained +12 new premium tier subs today.", color = Color(0xFF00E676), fontSize = 11.sp)
            }
            "biz_analytics_sales" -> {
                Text("Ad Campaign Metrics", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("📊 CTR rate: 4.8% on Banner Banner AD #102.", color = Color.White.copy(0.8f), fontSize = 11.sp)
            }
            "community_home_feed" -> {
                Text("Circle Members Rules", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("Moderators online: 4 • Spam filter active.", color = Color.White.copy(0.6f), fontSize = 11.sp)
            }
            "settings_menu" -> {
                Text("Language Selection Settings", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("English (US)", "Español", "日本語").forEach { lang ->
                        Box(modifier = Modifier.background(Color.White.copy(0.04f), RoundedCornerShape(4.dp)).padding(6.dp)) {
                            Text(lang, color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
            "admin_cmd_logs" -> {
                Text("Emergency Global System Shutdown", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Trigger Shutdown", color = Color.White, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun RenderEmptyAndErrorScreens(screenId: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (screenId) {
            "empty_messages" -> {
                Icon(Icons.Default.SpeakerNotesOff, contentDescription = null, tint = Color.White.copy(0.2f), modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("No Message Threads Found", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("Start private chats with verified friends.", color = Color.White.copy(0.5f), fontSize = 10.sp)
            }
            "empty_friends" -> {
                Icon(Icons.Default.PersonOff, contentDescription = null, tint = Color.White.copy(0.2f), modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("No Mutual Friends Yet", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("Add your interests in profile settings.", color = Color.White.copy(0.5f), fontSize = 10.sp)
            }
            "empty_notifications" -> {
                Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color.White.copy(0.2f), modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("No Unread Alerts", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
            }
            "empty_products" -> {
                Icon(Icons.Default.Inbox, contentDescription = null, tint = Color.White.copy(0.2f), modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("No active store listings.", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
            }
            "err_network" -> {
                Icon(Icons.Default.SignalWifiOff, contentDescription = null, tint = Color(0xFFFF5252).copy(0.8f), modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Network Connection Lost", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("Please check cellular data or dynamic wifi.", color = Color.White.copy(0.5f), fontSize = 10.sp, textAlign = TextAlign.Center)
            }
            "err_server_unauthorized" -> {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFF5252).copy(0.8f), modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Access Permission Denied", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("Your profile does not hold admin security authorization.", color = Color.White.copy(0.5f), fontSize = 10.sp, textAlign = TextAlign.Center)
            }
            "err_maintenance" -> {
                Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("System Upgrades Active", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("Upgrading Spanner DB cluster. Active in 10 minutes.", color = Color.White.copy(0.5f), fontSize = 10.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun RenderSuccessScreens(screenId: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (screenId) {
            "success_payment" -> {
                Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Purchase Complete!", fontWeight = FontWeight.Black, color = Color.White, fontSize = 14.sp)
                Text("Secure invoice dispatched to email.", color = Color.White.copy(0.5f), fontSize = 10.sp)
            }
            "success_general" -> {
                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Modifications Saved Successfully!", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

private data class ScreenItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val desc: String
)

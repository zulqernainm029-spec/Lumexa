package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.AuthUiState
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.CosmosBackgroundBrush
import com.example.ui.components.PremiumGradientBrush
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private var onNewIntentCallback: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenFromIntent = getReferralTokenFromIntent(intent)

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val app = context.applicationContext as Application
                val viewModel = remember { ConnectLiveViewModel(app) }

                LaunchedEffect(tokenFromIntent) {
                    if (tokenFromIntent != null) {
                        viewModel.setPendingReferralCode(tokenFromIntent)
                        android.widget.Toast.makeText(context, "Captured Referral Code: $tokenFromIntent via deep-link!", android.widget.Toast.LENGTH_LONG).show()
                    }
                }

                LaunchedEffect(viewModel) {
                    onNewIntentCallback = { token ->
                        viewModel.setPendingReferralCode(token)
                        android.widget.Toast.makeText(this@MainActivity, "Captured Referral Code: $token via deep-link!", android.widget.Toast.LENGTH_LONG).show()
                    }
                }

                ConnectLiveApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val token = getReferralTokenFromIntent(intent)
        if (token != null) {
            onNewIntentCallback?.invoke(token)
        }
    }

    private fun getReferralTokenFromIntent(intent: android.content.Intent?): String? {
        val data: android.net.Uri? = intent?.data
        if (data != null) {
            var token: String? = null
            if (data.pathSegments.contains("invite")) {
                val idx = data.pathSegments.indexOf("invite")
                if (idx >= 0 && idx < data.pathSegments.size - 1) {
                    token = data.pathSegments[idx + 1]
                }
            } else if (data.path == "/get") {
                token = data.getQueryParameter("token")
            }
            if (!token.isNullOrBlank()) {
                return token.trim().uppercase()
            }
        }
        return null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectLiveApp(viewModel: ConnectLiveViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val pendingReferralCode by viewModel.pendingReferralCode.collectAsState()

    if (pendingReferralCode != null) {
        AlertDialog(
            onDismissRequest = { /* Force user choice or skip button */ },
            icon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(32.dp)) },
            title = { Text("Welcome to Lumexa!", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    Text(
                        text = "We detected an invitation from partner recruiter:",
                        color = Color.White.copy(0.7f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(0.08f))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pendingReferralCode ?: "",
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Claim your exclusive welcome bonus of 🪙 50 Coins and 🌟 150 XP to boost your connection score instantly!",
                        color = Color.White.copy(0.7f),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val code = pendingReferralCode
                        if (code != null) {
                            viewModel.applyReferralCode(code)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text("Claim Bonus 🎉", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.setPendingReferralCode(null)
                    }
                ) {
                    Text("Skip", color = Color.White.copy(0.5f))
                }
            },
            containerColor = Color(0xFF140D26),
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Screen Router
    Crossfade(targetState = currentScreen, label = "screen_routing") { screen ->
        when (screen) {
            "splash" -> SplashScreen {
                if (authState is AuthUiState.Success) {
                    viewModel.navigateTo("main")
                } else {
                    viewModel.navigateTo("auth")
                }
            }

            "auth" -> {
                if (authState is AuthUiState.Success) {
                    LaunchedEffect(Unit) {
                        viewModel.navigateTo("main")
                    }
                }
                AuthScreen(viewModel = viewModel)
            }

            "onboarding" -> OnboardingScreen(viewModel = viewModel)

            "user_dashboard" -> UserDashboardScreen(viewModel = viewModel)

            "creator_dashboard" -> CreatorDashboardScreen(viewModel = viewModel)

            "business_dashboard" -> BusinessDashboardScreen(viewModel = viewModel)

            "main" -> MainScreenScaffold(viewModel = viewModel)

            "chat_detail" -> ChatDetailScreen(viewModel = viewModel)

            "voice_room" -> JoinedVoiceRoomScreen(viewModel = viewModel)

            "calling" -> CallScreen(viewModel = viewModel)

            "edit_profile" -> EditProfileScreen(viewModel = viewModel)

            "admin" -> AdminPanelScreen(viewModel = viewModel)

            "settings" -> SettingsScreen(viewModel = viewModel)

            "lumi_assistant" -> LumiAssistantScreen(viewModel = viewModel)

            "event_calendar" -> EventCalendarScreen(viewModel = viewModel)

            "ui_library" -> UiLibraryScreen(viewModel = viewModel)

            "operations_hub" -> OperationsHubScreen(viewModel = viewModel)

            "referral_download" -> ReferralDownloadScreen(viewModel = viewModel)

            "family_system" -> FamilySystemScreen(viewModel = viewModel)

            "vip_system" -> VipSystemScreen(viewModel = viewModel)

            "economy_system" -> EconomySystemScreen(viewModel = viewModel)

            "host_recruitment" -> HostRecruitmentScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBackgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.connectlive_logo_1784430926307),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Lumexa",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = "Where connections are built live",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Color(0xFF8E2DE2),
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenScaffold(viewModel: ConnectLiveViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val meProfile by viewModel.meProfile.collectAsState()
    val currentUserRole by viewModel.currentUserRole.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBackgroundBrush)
    ) {
        Scaffold(
            topBar = {
                if (!isSearchActive) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.connectlive_logo_1784430926307),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Lumexa",
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            }
                        },
                        actions = {
                            // Search Trigger
                            IconButton(
                                onClick = { isSearchActive = true },
                                modifier = Modifier.testTag("app_search_trigger")
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                            }

                            // Central Command Admin Trigger - Hidden from Normal Users
                            if (currentUserRole != "User") {
                                IconButton(
                                    onClick = { viewModel.navigateTo("admin") },
                                    modifier = Modifier.testTag("admin_command_trigger")
                                ) {
                                    Icon(Icons.Default.Dashboard, contentDescription = "Admin command", tint = Color.White)
                                }
                            }

                            // Settings Trigger
                            IconButton(
                                onClick = { viewModel.navigateTo("settings") },
                                modifier = Modifier.testTag("settings_cog_trigger")
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF0C071A),
                            titleContentColor = Color.White
                        )
                    )
                } else {
                    // Search layout active, intercept search bar
                    TopAppBar(
                        title = { Text("Search Lumexa", color = Color.White, fontSize = 16.sp) },
                        navigationIcon = {
                            IconButton(onClick = {
                                isSearchActive = false
                                viewModel.updateSearchQuery("")
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF0C071A),
                            titleContentColor = Color.White
                        )
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF0C071A),
                    contentColor = Color.White,
                    modifier = Modifier.height(72.dp)
                ) {
                    val items = listOf(
                        TabItem("feed", "Feed", Icons.Default.Home),
                        TabItem("voice_rooms", "Voice Rooms", Icons.Default.Mic),
                        TabItem("chats", "Chats", Icons.AutoMirrored.Filled.Chat),
                        TabItem("wallet", "Wallet", Icons.Default.Wallet),
                        TabItem("profile", "Profile", Icons.Default.Person)
                    )

                    items.forEach { item ->
                        val isSelected = currentTab == item.id
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                isSearchActive = false
                                viewModel.setTab(item.id)
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) Color(0xFF8E2DE2) else Color.White.copy(0.4f)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.White.copy(0.4f)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF8E2DE2).copy(0.1f)
                            ),
                            modifier = Modifier.testTag("nav_tab_${item.id}")
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isSearchActive) {
                    SearchScreen(viewModel = viewModel)
                } else {
                    Crossfade(targetState = currentTab, label = "tab_routing") { tab ->
                        when (tab) {
                            "feed" -> HomeScreen(viewModel = viewModel)
                            "voice_rooms" -> VoiceRoomsTab(viewModel = viewModel)
                            "chats" -> ChatsTab(viewModel = viewModel)
                            "wallet" -> WalletScreen(viewModel = viewModel)
                            "profile" -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

private data class TabItem(
    val id: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

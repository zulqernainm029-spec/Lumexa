package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.CosmosBackgroundBrush
import com.example.ui.components.PremiumGradientBrush
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralDownloadScreen(viewModel: ConnectLiveViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Access ViewModel State
    val apkVersion by viewModel.apkVersionName.collectAsState()
    val apkSize by viewModel.apkSizeMb.collectAsState()
    val apkRelease by viewModel.apkReleaseDate.collectAsState()
    val apkMinSdk by viewModel.apkMinSdk.collectAsState()
    val isDownloadEnabled by viewModel.isApkDownloadEnabled.collectAsState()

    // Referral state
    var referralTokenInput by remember { mutableStateOf("100258741") }
    var currentReferrerName by remember { mutableStateOf("Danish_Elite") }

    // Simulated download steps state
    var installStep by remember { mutableStateOf(0) } // 0: Idle, 1: Downloading, 2: Prompt Install, 3: Installing, 4: Done
    var downloadProgress by remember { mutableStateOf(0f) }
    var installProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(installStep) {
        if (installStep == 1) {
            downloadProgress = 0f
            while (downloadProgress < 1.0f) {
                delay(100)
                downloadProgress += 0.05f
            }
            installStep = 2 // Move to Prompt Install
        } else if (installStep == 3) {
            installProgress = 0f
            while (installProgress < 1.0f) {
                delay(100)
                installProgress += 0.08f
            }
            installStep = 4 // Installation Complete
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lumexa Portal", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo("operations_hub") },
                        modifier = Modifier.testTag("referral_download_back")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF090514))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CosmosBackgroundBrush)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo and Visual Banner
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFB300), Color(0xFFFF5252))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Lumexa App Icon",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "LUMEXA",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Secure Global Audio Streams & Creator Network",
                    fontSize = 12.sp,
                    color = Color.White.copy(0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Welcome / Invitation Card
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EXCLUSIVE INVITATION",
                                color = Color(0xFFFFE082),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "You have been personally invited by",
                            color = Color.White.copy(0.7f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "$currentReferrerName (ID: $referralTokenInput)",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Live simulator input of referrer ID so users can verify custom deep-linking
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(0.03f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Simulate Inviter ID:",
                                color = Color.White.copy(0.5f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = referralTokenInput,
                                onValueChange = {
                                    referralTokenInput = it
                                    currentReferrerName = when (it) {
                                        "100258741" -> "Danish_Elite"
                                        "100123456" -> "Zulq_Ambassador"
                                        "100998877" -> "Coin_Reseller_Leader"
                                        else -> "Invited_Guest_${it.takeLast(4)}"
                                    }
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color(0xFFFFB300),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("referral_token_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "A 50 Coin welcome reward will automatically be credited upon secure registration.",
                            color = Color(0xFF4CAF50),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // App Package Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF130E26)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Release Package Integrity Check",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val details = listOf(
                            "Official Version" to apkVersion,
                            "File Size (APK)" to apkSize,
                            "Release Date" to apkRelease,
                            "Compatibility" to apkMinSdk,
                            "HTTPS Secure URL" to "https://join.lumexa.app/dl/lumexa-latest.apk",
                            "SHA-256 Checksum" to "7e2a9b4c73f829f0c2a86899b11cfec55648508f77342df3f48a"
                        )

                        details.forEach { (label, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, color = Color.White.copy(0.5f), fontSize = 11.sp)
                                Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Divider(color = Color.White.copy(0.04f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Main Download/Install Interactive Area
                if (!isDownloadEnabled) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0x33FF5252))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFFF5252))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Downloads are currently disabled by the Platform Owner for server maintenance.",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    AnimatedContent(targetState = installStep, label = "install_state") { step ->
                        when (step) {
                            0 -> { // Idle / Download Button
                                Button(
                                    onClick = {
                                        viewModel.simulateApkDownload(referralTokenInput)
                                        installStep = 1
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .testTag("download_lumexa_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Download Lumexa APK",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            1 -> { // Downloading Progress
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1738))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                CircularProgressIndicator(
                                                    progress = downloadProgress,
                                                    color = Color(0xFFFFB300),
                                                    modifier = Modifier.size(20.dp),
                                                    strokeWidth = 2.dp
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text("Downloading latest APK package...", color = Color.White, fontSize = 12.sp)
                                            }
                                            Text("${(downloadProgress * 100).toInt()}%", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        LinearProgressIndicator(
                                            progress = downloadProgress,
                                            color = Color(0xFFFFB300),
                                            trackColor = Color.White.copy(0.1f),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                        )
                                    }
                                }
                            }
                            2 -> { // Prompt Install (Simulated system alert)
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF281C4F)),
                                    border = BorderStroke(1.dp, Color(0xFFFFB300).copy(0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "System Installer Prompt",
                                            color = Color(0xFFFFB300),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            "Do you want to install Lumexa? It does not require any special access.",
                                            color = Color.White.copy(0.8f),
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(onClick = { installStep = 0 }) {
                                                Text("Cancel", color = Color.White.copy(0.6f))
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = { installStep = 3 },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Install", color = Color.Black, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                            3 -> { // Installing Progress
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1D173A))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Installing Lumexa App to Android package layer...", color = Color.White, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        LinearProgressIndicator(
                                            progress = installProgress,
                                            color = Color(0xFF4CAF50),
                                            trackColor = Color.White.copy(0.1f),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Applying referral hook token context: ID-$referralTokenInput", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                    }
                                }
                            }
                            4 -> { // Installation Complete -> Open
                                Button(
                                    onClick = {
                                        // Save referral code and link automatically
                                        viewModel.setPendingReferralCode(referralTokenInput)
                                        viewModel.navigateTo("auth")
                                        android.widget.Toast.makeText(
                                            context,
                                            "Lumexa Launched! Referral ID: $referralTokenInput successfully verified & bound.",
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                        installStep = 0
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .testTag("launch_lumexa_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.Launch, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Open Lumexa & Bind Referral",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Installation Instructions Guide
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.02f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Help, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Step-by-Step Installation Guide", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val steps = listOf(
                            "1. Tap 'Download Lumexa' above to fetch the certified APK bundle.",
                            "2. Allow browser installation from unknown sources if prompted by Android settings.",
                            "3. Click on the completed download notification to invoke the Android System Package Installer.",
                            "4. Tap 'Install' and launch the app to automatically claim your Welcome Coin Reward!"
                        )

                        steps.forEach { stepText ->
                            Text(
                                text = stepText,
                                color = Color.White.copy(0.7f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 4.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Footer Links
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Privacy Policy",
                        color = Color.White.copy(0.4f),
                        fontSize = 10.sp,
                        modifier = Modifier
                            .clickable { }
                            .padding(8.dp)
                    )
                    Text(
                        "•",
                        color = Color.White.copy(0.2f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        "Terms of Service",
                        color = Color.White.copy(0.4f),
                        fontSize = 10.sp,
                        modifier = Modifier
                            .clickable { }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

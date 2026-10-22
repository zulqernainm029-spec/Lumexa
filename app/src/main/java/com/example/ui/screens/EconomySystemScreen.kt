package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.ui.components.CosmosBackgroundBrush
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
data class RechargePack(
    val id: String,
    val name: String,
    val coins: Int,
    val bonus: Int,
    val costDollars: Double,
    val promoBadge: String = ""
)

data class EcoGift(
    val id: String,
    val name: String,
    val emoji: String,
    val coinPrice: Int,
    val diamondValue: Int,
    val category: String,
    val isLucky: Boolean = false
)

data class TransactionRecord(
    val txId: String,
    val type: String,
    val description: String,
    val coinsOffset: Int,
    val diamondsOffset: Int,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EconomySystemScreen(viewModel: ConnectLiveViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // ViewModel integrations
    val liveCoins by viewModel.coinBalance.collectAsState(initial = 1000)
    val liveDiamonds by viewModel.diamondBalance.collectAsState(initial = 3250)

    // --- Tab state ---
    var selectedTab by remember { mutableStateOf("wallet") } // wallet, store, lucky_box, rankings, analytics, owner_panel

    // Coins packages
    val packages = listOf(
        RechargePack("rec_01", "Bronze Pack", 150, 15, 0.99),
        RechargePack("rec_02", "Silver Starter", 500, 75, 2.99, "Popular"),
        RechargePack("rec_03", "Golden Vault", 1200, 250, 5.99, "Best Deal"),
        RechargePack("rec_04", "Cosmic Chest", 3000, 750, 14.99),
        RechargePack("rec_05", "Sovereign Crown Pack", 7500, 2200, 34.99, "Elite")
    )

    // Gift categories & items
    val giftCategories = listOf("Basic", "Premium", "Luxury", "Animated", "Lucky", "Family", "PK Exclusive")
    var activeGiftCategory by remember { mutableStateOf("Basic") }

    val gifts = listOf(
        EcoGift("g_01", "Heartbeat Love", "❤️", 5, 2, "Basic"),
        EcoGift("g_02", "Cosmos Flower", "🌸", 15, 7, "Basic"),
        EcoGift("g_03", "Lucky Clover", "🍀", 30, 15, "Lucky", true),
        EcoGift("g_04", "Ecosystem Crystal", "💎", 100, 50, "Premium"),
        EcoGift("g_05", "Gold Trophy", "🏆", 250, 120, "Premium"),
        EcoGift("g_06", "Neon Sports Car", "🏎️", 800, 400, "Luxury"),
        EcoGift("g_07", "Luxury Yacht", "🚢", 1500, 750, "Luxury"),
        EcoGift("g_08", "Firework Spectacular", "🎆", 500, 250, "Animated"),
        EcoGift("g_09", "Golden Phoenix Banner", "🔥", 1200, 600, "Animated"),
        EcoGift("g_10", "Syndicate Dragon", "🐉", 2000, 1000, "Family"),
        EcoGift("g_11", "Championship Shield", "🛡️", 600, 300, "PK Exclusive")
    )

    // Transaction histories
    val transactionHistory = remember {
        mutableStateListOf(
            TransactionRecord("TX_09884", "RECHARGE", "Purchased Golden Vault Package.", 1450, 0, "2026-07-19 14:22"),
            TransactionRecord("TX_09885", "GIFT_SENT", "Sent Neon Sports Car to Host Elena Rostova.", -800, 0, "2026-07-20 18:15"),
            TransactionRecord("TX_09886", "GIFT_RECV", "Received Firework Spectacular in voice room.", 0, 250, "2026-07-20 21:30")
        )
    }

    // Interactive Combo multipliers state
    var selectedCombo by remember { mutableStateOf(1) }
    val comboOptions = listOf(1, 5, 10, 20, 50, 99, 520, 999)

    // Diamond Withdraw Payout simulation State
    var showWithdrawDialog by remember { mutableStateOf(false) }

    // Lucky Gifts results
    var luckyGifResultsMsg by remember { mutableStateOf<String?>(null) }

    // Treasure box items
    var treasureBoxPoints by remember { mutableStateOf(45) } // Earn points when sending gifts

    // Economy Analytics summary (for transparency)
    var totalCoinsSold by remember { mutableStateOf(854000) }
    var totalGiftsSent by remember { mutableStateOf(65200) }
    var platformRevenueDollars by remember { mutableStateOf(12840.50) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Lumexa Economy & Gift Center", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo("profile") },
                        modifier = Modifier.testTag("economy_system_back")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0C071A))
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CosmosBackgroundBrush)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Category Tabs selector
                ScrollableTabRow(
                    selectedTabIndex = listOf("wallet", "store", "merchants", "lucky_box", "rankings", "analytics", "owner_panel").indexOf(selectedTab).coerceAtLeast(0),
                    containerColor = Color(0xFF0C071A),
                    contentColor = Color.White,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        val index = listOf("wallet", "store", "merchants", "lucky_box", "rankings", "analytics", "owner_panel").indexOf(selectedTab).coerceAtLeast(0)
                        if (index in tabPositions.indices) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                ) {
                    val tabNames = listOf(
                        "wallet" to "Wallet",
                        "store" to "Gift Store",
                        "merchants" to "Merchant Resellers",
                        "lucky_box" to "Treasure Box",
                        "rankings" to "Rankings",
                        "analytics" to "Analytics",
                        "owner_panel" to "Admin Rules"
                    )
                    tabNames.forEach { (id, label) ->
                        Tab(
                            selected = selectedTab == id,
                            onClick = { selectedTab = id },
                            text = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("economy_tab_$id")
                        )
                    }
                }

                // Inner contents
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(14.dp)
                ) {
                    when (selectedTab) {
                        "wallet" -> WalletSegment(
                            coins = liveCoins ?: 1000,
                            diamonds = liveDiamonds,
                            packages = packages,
                            history = transactionHistory,
                            onRecharge = { pack ->
                                viewModel.buyCoins(pack.coins + pack.bonus, pack.costDollars) // Directly invoke VM buying handler!
                                transactionHistory.add(0, TransactionRecord("TX_${(10000..99999).random()}", "RECHARGE", "Purchased ${pack.name} (+${pack.coins + pack.bonus} Coins).", pack.coins + pack.bonus, 0, "Just Now"))
                                totalCoinsSold += pack.coins
                                platformRevenueDollars += pack.costDollars
                                Toast.makeText(context, "Recharged successfully! Added +${pack.coins + pack.bonus} Coins to your wallet.", Toast.LENGTH_LONG).show()
                            },
                            onWithdrawClick = { showWithdrawDialog = true }
                        )
                        "store" -> GiftStoreSegment(
                            activeCategory = activeGiftCategory,
                            categories = giftCategories,
                            allGifts = gifts,
                            userCoins = liveCoins ?: 1000,
                            selectedCombo = selectedCombo,
                            comboOptions = comboOptions,
                            onCategoryChange = { activeGiftCategory = it },
                            onComboChange = { selectedCombo = it },
                            onSendGift = { gift ->
                                val totalCost = gift.coinPrice * selectedCombo
                                val currentCoins = liveCoins ?: 1000
                                if (currentCoins >= totalCost) {
                                    viewModel.buyCoins(-totalCost, 0.0)
                                    val diamondsReceived = gift.diamondValue * selectedCombo
                                    transactionHistory.add(0, TransactionRecord("TX_${(10000..99999).random()}", "GIFT_SENT", "Sent ${gift.name} x$selectedCombo to Host Elena.", -totalCost, 0, "Just Now"))
                                    totalGiftsSent += selectedCombo

                                    if (gift.isLucky) {
                                        val winChance = (1..100).random()
                                        if (winChance <= 20) {
                                            val rewardCoins = totalCost * 2
                                            viewModel.buyCoins(rewardCoins, 0.0)
                                            luckyGifResultsMsg = "JACKPOT! Lucky Clover gave you a x2 Coin Return (+${rewardCoins} Coins)!"
                                            transactionHistory.add(0, TransactionRecord("TX_${(10000..99999).random()}", "LUCKY_WIN", "Jackpot clover award.", rewardCoins, 0, "Just Now"))
                                        } else {
                                            luckyGifResultsMsg = "No jackpot this time! Keep sending to unlock clover luck."
                                        }
                                    } else {
                                        luckyGifResultsMsg = null
                                    }

                                    treasureBoxPoints += (totalCost / 10)
                                    Toast.makeText(context, "Gift sent! Recipient received 💎 $diamondsReceived!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Insufficient Coins! Recharge your wallet to send this.", Toast.LENGTH_LONG).show()
                                }
                            },
                            luckyMsg = luckyGifResultsMsg
                        )
                        "merchants" -> MerchantResellerHubSegment(viewModel = viewModel)
                        "lucky_box" -> LuckyBoxSegment(
                            boxPoints = treasureBoxPoints,
                            onOpenBox = { cost ->
                                if (treasureBoxPoints >= cost) {
                                    treasureBoxPoints -= cost
                                    val rewards = listOf(
                                        Pair("🪙 250 Bonus Coins", { viewModel.buyCoins(250, 0.0) }),
                                        Pair("💎 100 Diamonds", { /* simulated */ }),
                                        Pair("7-Day Cosmic Frame", { /* simulated */ }),
                                        Pair("3-Day VIP Days Pass", { /* simulated */ })
                                    )
                                    val selected = rewards.random()
                                    selected.second()
                                    Toast.makeText(context, "Opened Box! Received: ${selected.first}", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Insufficient points! Send more gifts to earn treasure box energy.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        "rankings" -> RankingsSegment()
                        "analytics" -> AnalyticsSegment(
                            totalCoinsSold = totalCoinsSold,
                            totalGiftsSent = totalGiftsSent,
                            platformRevenue = platformRevenueDollars
                        )
                        "owner_panel" -> AdminRulesSegment()
                    }
                }
            }
        }
    }

    // Withdrawal payout dialog
    if (showWithdrawDialog) {
        var withdrawAmount by remember { mutableStateOf("") }
        var paymentMethod by remember { mutableStateOf("Internal Coin Wallet") }
        val payoutMethods = listOf("Internal Coin Wallet", "Easypaisa Mobile", "NayaPay Digital", "Global Bank Transfer")

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Request Diamond Withdrawal Payout", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            containerColor = Color(0xFF160E2A),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Current Balance: 💎 $liveDiamonds", color = Color.White.copy(0.6f), fontSize = 12.sp)
                    Text("Platform Rule: 10 Diamonds = $0.10 USD (Min withdraw 1000 Diamonds)", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        label = { Text("Diamonds to withdraw", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select Payout Channel", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(payoutMethods.size) { index ->
                            val method = payoutMethods[index]
                            val sel = paymentMethod == method
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (sel) Color(0xFFFFD700) else Color.White.copy(0.04f))
                                    .clickable { paymentMethod = method }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(method, color = if (sel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawAmount.toIntOrNull()
                        if (amount != null && amount >= 1000 && amount <= liveDiamonds) {
                            // Deduct from wallet using viewmodel withdrawal simulated rules if possible, or local mock
                            viewModel.submitWithdrawalRequest(amount)
                            transactionHistory.add(0, TransactionRecord("TX_${(10000..99999).random()}", "WITHDRAWAL", "Requested withdrawal of $amount Diamonds to $paymentMethod.", 0, -amount, "Just Now"))
                            Toast.makeText(context, "Withdrawal Request Received! Payout under review.", Toast.LENGTH_LONG).show()
                            showWithdrawDialog = false
                        } else {
                            Toast.makeText(context, "Enter a valid amount (Minimum 1000 Diamonds & within balance)!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("Withdraw", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = Color.White.copy(0.6f))
                }
            }
        )
    }
}

// =================== SEGMENT LAYOUTS ===================

@Composable
fun WalletSegment(
    coins: Int,
    diamonds: Int,
    packages: List<RechargePack>,
    history: List<TransactionRecord>,
    onRecharge: (RechargePack) -> Unit,
    onWithdrawClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Balances Row
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("COIN BALANCE", color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("🪙 $coins", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DIAMONDS EARNED", color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💎 $diamonds", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                            Text("Withdraw", color = Color(0xFFFF5252), fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.clickable { onWithdrawClick() })
                        }
                    }
                }
            }
        }

        item {
            Text("In-App Coin Recharge Packages", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        // Packages List
        items(packages) { pack ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD700).copy(0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${pack.coins} Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (pack.promoBadge.isNotBlank()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFFF5252))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(pack.promoBadge, color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                            Text("Bonus: +${pack.bonus} Coins", color = Color(0xFF00E676), fontSize = 10.sp)
                        }
                    }

                    Button(
                        onClick = { onRecharge(pack) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        modifier = Modifier.testTag("recharge_pack_${pack.id}")
                    ) {
                        Text("$${pack.costDollars}", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Recent Virtual Economy Ledger", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        // Recent Transaction lists
        items(history) { tx ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.02f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(0.08f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(tx.type, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                        Text(tx.date, color = Color.White.copy(0.4f), fontSize = 9.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(tx.description, color = Color.White.copy(0.8f), fontSize = 11.sp, modifier = Modifier.weight(1f))
                        if (tx.coinsOffset != 0) {
                            Text(
                                text = if (tx.coinsOffset > 0) "+${tx.coinsOffset} Coins" else "${tx.coinsOffset} Coins",
                                color = if (tx.coinsOffset > 0) Color(0xFF00E676) else Color(0xFFFF5252),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        if (tx.diamondsOffset != 0) {
                            Text(
                                text = if (tx.diamondsOffset > 0) "+${tx.diamondsOffset} Diamonds" else "${tx.diamondsOffset} Diamonds",
                                color = if (tx.diamondsOffset > 0) Color(0xFF00E5FF) else Color(0xFFFF5252),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GiftStoreSegment(
    activeCategory: String,
    categories: List<String>,
    allGifts: List<EcoGift>,
    userCoins: Int,
    selectedCombo: Int,
    comboOptions: List<Int>,
    onCategoryChange: (String) -> Unit,
    onComboChange: (Int) -> Unit,
    onSendGift: (EcoGift) -> Unit,
    luckyMsg: String?
) {
    val filtered = allGifts.filter { it.category == activeCategory }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Lucky Jackpot banner popup
        if (luckyMsg != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1F2B)),
                border = BorderStroke(1.dp, Color(0xFF00E5FF))
            ) {
                Text(luckyMsg, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
            }
        }

        // Row of category filters
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(categories.size) { index ->
                val cat = categories[index]
                val active = cat == activeCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) Color(0xFFFFD700) else Color.White.copy(0.04f))
                        .clickable { onCategoryChange(cat) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(cat, color = if (active) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Grid of gifts
        Box(modifier = Modifier.weight(1f)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                gridItems(filtered) { gift ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSendGift(gift) }
                            .testTag("gift_item_${gift.id}"),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                        border = if (gift.isLucky) BorderStroke(1.dp, Color(0xFF00E676).copy(0.3f)) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(gift.emoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(gift.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙 ${gift.coinPrice}", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("💎 ${gift.diamondValue}", color = Color(0xFFFF5252), fontSize = 8.sp)
                            }
                            if (gift.isLucky) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFF00E676).copy(0.2f))
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Text("LUCKY x2", color = Color(0xFF00E676), fontSize = 7.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Combo select footer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1338))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("Select Gift Combo Multiplier", color = Color.White.copy(0.6f), fontSize = 10.sp)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(comboOptions.size) { index ->
                        val combo = comboOptions[index]
                        val active = combo == selectedCombo
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (active) Color(0xFFFFD700) else Color.White.copy(0.06f))
                                .clickable { onComboChange(combo) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("x$combo", color = if (active) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LuckyBoxSegment(boxPoints: Int, onOpenBox: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Syndicate Treasure Box", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text("Earn Treasure Energy by sending gifts inside any room. Open box to win random prizes!", color = Color.White.copy(0.5f), textAlign = TextAlign.Center, fontSize = 11.sp)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
        ) {
            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Treasure Box Energy", color = Color.White.copy(0.5f), fontSize = 11.sp)
                Text("$boxPoints pts", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 28.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = (boxPoints.toFloat() / 100f).coerceAtMost(1.0f),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF00E5FF),
                    trackColor = Color.White.copy(0.08f)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Standard Box
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenBox(50) },
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", fontSize = 48.sp)
                    Text("Standard Box", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Cost: 50 Energy", color = Color.White.copy(0.4f), fontSize = 9.sp)
                }
            }

            // Mega Box
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenBox(100) },
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👑", fontSize = 48.sp)
                    Text("Sovereign Box", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Cost: 100 Energy", color = Color.White.copy(0.4f), fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun RankingsSegment() {
    val senders = listOf(
        Triple("Alpha_One", "🪙 85,400 sent", "1"),
        Triple("Ares", "🪙 54,200 sent", "2"),
        Triple("Alex Mercer", "🪙 42,100 sent", "3")
    )
    val receivers = listOf(
        Triple("Elena Rostova", "💎 25,400 received", "1"),
        Triple("Diana", "💎 18,900 received", "2"),
        Triple("Saito", "💎 14,200 received", "3")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Daily Gifting Leaderboards", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Top Senders
            Column(modifier = Modifier.weight(1f)) {
                Text("Top Gift Senders", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                senders.forEach { (name, pts, rank) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(rank, color = Color(0xFFFFD700), fontWeight = FontWeight.Black, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(pts, color = Color.White.copy(0.5f), fontSize = 9.sp)
                            }
                        }
                    }
                }
            }

            // Top Receivers
            Column(modifier = Modifier.weight(1f)) {
                Text("Top Receivers", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                receivers.forEach { (name, pts, rank) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(rank, color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(pts, color = Color.White.copy(0.5f), fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsSegment(
    totalCoinsSold: Int,
    totalGiftsSent: Int,
    platformRevenue: Double
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Ecosystem Virtual Revenue Analytics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Ecosystem Total Revenue", color = Color.White.copy(0.5f), fontSize = 11.sp)
                Text("$${String.format("%.2f", platformRevenue)} USD", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 28.sp)

                Divider(color = Color.White.copy(0.1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TOTAL COINS MINTED", color = Color.White.copy(0.4f), fontSize = 9.sp)
                        Text("$totalCoinsSold Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Column {
                        Text("TOTAL GIFTS PROCESSED", color = Color.White.copy(0.4f), fontSize = 9.sp)
                        Text("$totalGiftsSent Gifts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminRulesSegment() {
    var conversionRatio by remember { mutableStateOf("10 Diamonds = $0.10 USD") }
    var transferMinCoins by remember { mutableStateOf("500 Coins") }
    var transferMaxCoins by remember { mutableStateOf("100000 Coins") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Ecosystem Coins & Payout Rules", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = conversionRatio,
                        onValueChange = { conversionRatio = it },
                        label = { Text("Diamond to USD conversion ratio", color = Color.White.copy(0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = transferMinCoins,
                            onValueChange = { transferMinCoins = it },
                            label = { Text("Min Transfer Limit", color = Color.White.copy(0.6f)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = transferMaxCoins,
                            onValueChange = { transferMaxCoins = it },
                            label = { Text("Max Transfer Limit", color = Color.White.copy(0.6f)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }
                    Button(
                        onClick = { /* simulated save */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Economy Rules Configuration", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MerchantResellerHubSegment(viewModel: ConnectLiveViewModel) {
    val context = LocalContext.current
    var merchantStockCoins by remember { mutableIntStateOf(100000) }
    var selectedWholesalePack by remember { mutableStateOf("100,000 Coins ($450.00 USD)") }
    var targetCustomerUserId by remember { mutableStateOf("") }
    var transferCoinAmount by remember { mutableStateOf("") }
    var selectedPaymentGateway by remember { mutableStateOf("Easypaisa / JazzCash") }
    var resellerCommissionEarned by remember { mutableDoubleStateOf(225.00) }
    var freezeTransactionsActive by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Coin Reseller & Merchant Network", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Multi-Tier Wholesale Coin Distribution & Local Reseller Hub", color = Color.White.copy(0.5f), fontSize = 10.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFD700).copy(0.15f))
                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("OFFICIAL MERCHANT", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // Multi-tier Hierarchy Visualizer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Reseller Network Hierarchy", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            "Platform Owner" to Color(0xFFFF1744),
                            "Super Manager" to Color(0xFFE040FB),
                            "Merchant Leader" to Color(0xFF00E5FF),
                            "Merchant" to Color(0xFFFFD700),
                            "Reseller" to Color(0xFF00E676)
                        ).forEach { (role, color) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(role, color = Color.White.copy(0.8f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Merchant Inventory & Commission Stat Card
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF8E2DE2).copy(0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Available Wholesale Stock", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("🪙 $merchantStockCoins", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Updated Live", color = Color(0xFF00E676), fontSize = 9.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00E5FF).copy(0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Earned 5% Commission", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$${String.format("%.2f", resellerCommissionEarned)} USD", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Auto-Credited", color = Color(0xFFFFD700), fontSize = 9.sp)
                    }
                }
            }
        }

        // Wholesale Bulk Inventory Purchase
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Purchase Bulk Coin Inventory (Wholesale)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    val wholesaleOptions = listOf(
                        "50,000 Coins ($225.00 USD)",
                        "100,000 Coins ($450.00 USD)",
                        "500,000 Coins ($2,100.00 USD)"
                    )

                    wholesaleOptions.forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedWholesalePack == opt) Color(0xFF8E2DE2).copy(0.3f) else Color.White.copy(0.02f))
                                .clickable { selectedWholesalePack = opt }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(opt, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            RadioButton(
                                selected = selectedWholesalePack == opt,
                                onClick = { selectedWholesalePack = opt },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E5FF))
                            )
                        }
                    }

                    Text("Supported Payment Gateways:", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Easypaisa / JazzCash", "Stripe / Card", "PayPal", "Bank Transfer").forEach { gateway ->
                            FilterChip(
                                selected = selectedPaymentGateway == gateway,
                                onClick = { selectedPaymentGateway = gateway },
                                label = { Text(gateway, fontSize = 9.sp) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val coinsToAdd = if (selectedWholesalePack.contains("50,000")) 50000 else if (selectedWholesalePack.contains("100,000")) 100000 else 500000
                            val comm = coinsToAdd * 0.00225
                            merchantStockCoins += coinsToAdd
                            resellerCommissionEarned += comm
                            Toast.makeText(context, "Wholesale order confirmed via $selectedPaymentGateway! Added +$coinsToAdd Coins to Merchant Inventory.", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Confirm Wholesale Stock Purchase", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Direct Customer Coin Reselling / Transfer Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Direct Customer Transfer (Instant Deposit)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = targetCustomerUserId,
                        onValueChange = { targetCustomerUserId = it },
                        label = { Text("Customer User ID or Username", color = Color.White.copy(0.6f)) },
                        placeholder = { Text("e.g. user_9210 or Sofia", color = Color.White.copy(0.3f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = transferCoinAmount,
                        onValueChange = { transferCoinAmount = it },
                        label = { Text("Coin Amount to Transfer", color = Color.White.copy(0.6f)) },
                        placeholder = { Text("e.g. 5000", color = Color.White.copy(0.3f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Freeze Fraud Shield Active", color = Color.White.copy(0.7f), fontSize = 11.sp)
                        Switch(
                            checked = freezeTransactionsActive,
                            onCheckedChange = { freezeTransactionsActive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF1744))
                        )
                    }

                    Button(
                        onClick = {
                            val amount = transferCoinAmount.toIntOrNull() ?: 0
                            if (freezeTransactionsActive) {
                                Toast.makeText(context, "Cannot transfer: Merchant Fraud Freeze is enabled!", Toast.LENGTH_LONG).show()
                            } else if (amount > 0 && amount <= merchantStockCoins && targetCustomerUserId.isNotEmpty()) {
                                merchantStockCoins -= amount
                                viewModel.buyCoins(amount, 0.0)
                                Toast.makeText(context, "Transferred $amount Coins to user '$targetCustomerUserId'! Stock updated.", Toast.LENGTH_LONG).show()
                                targetCustomerUserId = ""
                                transferCoinAmount = ""
                            } else {
                                Toast.makeText(context, "Invalid input or insufficient merchant stock!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Transfer Coins to Customer", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

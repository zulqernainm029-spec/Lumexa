package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoinTransaction
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GoldenVipBrush
import com.example.ui.components.PremiumGradientBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(viewModel: ConnectLiveViewModel) {
    val coinBalance by viewModel.coinBalance.collectAsState()
    val transactions by viewModel.transactionHistory.collectAsState()
    val meProfile by viewModel.meProfile.collectAsState()
    
    // Extended Wallet States
    val diamondBalance by viewModel.diamondBalance.collectAsState()
    val xpCount by viewModel.xpCount.collectAsState()
    val rewardPoints by viewModel.rewardPoints.collectAsState()
    val dailyRewardClaimed by viewModel.dailyRewardClaimed.collectAsState()
    val weeklyRewardClaimed by viewModel.weeklyRewardClaimed.collectAsState()
    val referralApplied by viewModel.referralApplied.collectAsState()

    // PayPal Account Integration States
    val paypalEmail by viewModel.paypalEmail.collectAsState()
    val paypalCurrency by viewModel.paypalCurrency.collectAsState()
    val paypalAccountLinked by viewModel.paypalAccountLinked.collectAsState()
    val paypalClientId by viewModel.paypalClientId.collectAsState()

    var showBuyCoinsDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showManageCardsDialog by remember { mutableStateOf(false) }
    var showPaypalSetupDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successDialogMessage by remember { mutableStateOf("") }
    var selectedPayoutMethod by remember { mutableStateOf("PayPal") } // PayPal, Stripe, Bank Wire
    
    var savedCreditCards by remember {
        mutableStateOf(
            listOf(
                SavedCreditCard("c1", "Visa", "4532", "12/28", "Zulqernain M.", true),
                SavedCreditCard("c2", "Mastercard", "8821", "09/27", "Zulqernain M.", false)
            )
        )
    }
    
    var referralInput by remember { mutableStateOf("") }
    var withdrawalAmountInput by remember { mutableStateOf("") }
    
    var activeSubTab by remember { mutableStateOf("logs") } // logs, gifts, subscriptions

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .testTag("wallet_viewport"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Multi-Asset Dashboard Card
        item {
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PremiumGradientBrush, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "LUMEXA MULTI-ASSET VAULT",
                        fontSize = 10.sp,
                        color = Color.White.copy(0.6f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // 2x2 Assets Grid
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Coins
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Circle, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Coins (Gold)", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = coinBalance?.toString() ?: "0", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                        
                        // Diamonds
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Diamonds", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = diamondBalance.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.White.copy(0.1f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        // XP Points
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFE040FB), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("XP Level Score", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "$xpCount XP", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                        
                        // Reward points
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reward Points", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = rewardPoints.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Recharge & VIP & Manage Cards Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showBuyCoinsDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(40.dp)
                                .testTag("buy_coins_button_trigger")
                        ) {
                            Text("Recharge", color = Color(0xFF4A00E0), fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                        }

                        Button(
                            onClick = { showManageCardsDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2).copy(0.3f)),
                            border = ButtonDefaults.outlinedButtonBorder,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("manage_cards_trigger")
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cards", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                        }

                        val isVip = meProfile?.isVip ?: false
                        if (isVip) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .background(GoldenVipBrush, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("VIP ACTIVE", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 10.sp)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.purchaseVipMembership() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.15f)),
                                border = ButtonDefaults.outlinedButtonBorder,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("vip_purchase_trigger")
                            ) {
                                Text("Join VIP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                            }
                        }
                    }
                }
            }
        }

        // Daily Attendance Rewards (Gamification & Retention - Section Q)
        item {
            Text("Claim Free Daily & Weekly Attendance Rewards", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Daily Claim Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = !dailyRewardClaimed) {
                            viewModel.claimDailyReward()
                            successDialogMessage = "Daily Attendance claimed! Added +25 XP and +10 Reward Points."
                            showSuccessDialog = true
                        },
                    colors = CardDefaults.cardColors(containerColor = if (dailyRewardClaimed) Color.White.copy(0.04f) else Color(0xFF140D2F))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (dailyRewardClaimed) Color(0xFF00E676) else Color.White.copy(0.3f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Daily Login", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text("+25 XP & 10 Points", color = Color.White.copy(0.5f), fontSize = 9.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (dailyRewardClaimed) Color.White.copy(0.06f) else Color(0xFF00E676))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(if (dailyRewardClaimed) "Claimed ✔" else "CLAIM", color = if (dailyRewardClaimed) Color.White.copy(0.5f) else Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Weekly Claim Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = !weeklyRewardClaimed) {
                            viewModel.claimWeeklyReward()
                            successDialogMessage = "Weekly Loyalty Bonus claimed! Added +100 XP and +50 Reward Points."
                            showSuccessDialog = true
                        },
                    colors = CardDefaults.cardColors(containerColor = if (weeklyRewardClaimed) Color.White.copy(0.04f) else Color(0xFF140D2F))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = if (weeklyRewardClaimed) Color(0xFFFFD700) else Color.White.copy(0.3f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Weekly Streak", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text("+100 XP & 50 Points", color = Color.White.copy(0.5f), fontSize = 9.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (weeklyRewardClaimed) Color.White.copy(0.06f) else Color(0xFF8E2DE2))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(if (weeklyRewardClaimed) "Claimed ✔" else "CLAIM", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Creator diamond payouts portal (Section W: Creator Monetization)
        item {
            Text("Creator Revenue Cash-out Portal", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
        }
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Exchangeable Diamond Balance", color = Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(diamondBalance.toString(), fontWeight = FontWeight.Black, color = Color.White, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("≈ $${"%.2f".format(diamondBalance * 0.01)} USD", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { showWithdrawDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                            shape = RoundedCornerShape(8.dp),
                            enabled = diamondBalance >= 100,
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Cash out", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Minimum withdrawal limit is 100 Diamonds ($1.00 USD). Direct instant payout to Stripe or PayPal.", color = Color.White.copy(0.4f), fontSize = 8.sp)
                }
            }
        }

        // PayPal Direct Incoming Dollars & Payout Integration
        item {
            Text("PayPal Incoming USD Dollar Account", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
        }
        item {
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF003087).copy(0.6f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF003087)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("P", color = Color(0xFF0079C1), fontSize = 22.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("PayPal Receiving Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    if (paypalAccountLinked) paypalEmail else "No PayPal Account Linked",
                                    color = Color.White.copy(0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (paypalAccountLinked) Color(0xFF00E676).copy(0.15f) else Color(0xFFFF1744).copy(0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                if (paypalAccountLinked) "READY FOR DOLLARS ($paypalCurrency)" else "NOT CONFIGURED",
                                color = if (paypalAccountLinked) Color(0xFF00E676) else Color(0xFFFF5252),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Incoming Dollar Transfers & Creator Earnings", color = Color.White.copy(0.5f), fontSize = 9.sp)
                            Text("Automated USD Clearing via PayPal API", color = Color.White.copy(0.9f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showPaypalSetupDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0079C1)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (paypalAccountLinked) "Set Up PayPal" else "Connect PayPal", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Referral System (Section Q & V)
        item {
            Text("Enter Ambassador Referral Code", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
        }
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = referralInput,
                        onValueChange = { referralInput = it },
                        placeholder = { Text("e.g., CODESHOW7", color = Color.White.copy(0.3f), fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8E2DE2)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !referralApplied,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (referralInput.isNotBlank()) {
                                val success = viewModel.applyReferralCode(referralInput)
                                if (success) {
                                    successDialogMessage = "Referral code applied! Added +150 XP and +50 Reward Points to your wallet!"
                                    showSuccessDialog = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (referralApplied) Color.White.copy(0.08f) else Color(0xFF00E676)),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !referralApplied && referralInput.isNotBlank(),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(if (referralApplied) "Applied ✔" else "Validate", color = if (referralApplied) Color.White.copy(0.5f) else Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Transaction Logs Filter Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.03f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("logs" to "Transaction Logs", "gifts" to "Gift History", "subscriptions" to "Subpasses").forEach { (tabId, label) ->
                    val isSelected = activeSubTab == tabId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF8E2DE2) else Color.Transparent)
                            .clickable { activeSubTab = tabId }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = if (isSelected) Color.White else Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Logs render lists
        if (activeSubTab == "logs") {
            if (transactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("No transactional items registered.", color = Color.White.copy(0.4f), fontSize = 11.sp)
                    }
                }
            } else {
                items(transactions) { tx ->
                    TransactionRow(transaction = tx)
                }
            }
        } else if (activeSubTab == "gifts") {
            val giftsLog = transactions.filter { it.type == "spend_gift" }
            if (giftsLog.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("No virtual gifts sent yet. Join active voice/live spaces to send support!", color = Color.White.copy(0.4f), fontSize = 11.sp)
                    }
                }
            } else {
                items(giftsLog) { tx ->
                    TransactionRow(transaction = tx)
                }
            }
        } else {
            // subpasses
            val isVip = meProfile?.isVip ?: false
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.03f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Monthly VIP Premium Access", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isVip) "Status: ACTIVE • Next renewal August 18, 2026." 
                                   else "Status: NOT SUBSCRIBED. Buy VIP to activate dynamic golden profile effects.",
                            color = Color.White.copy(0.6f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }

    // --- MANAGE CREDIT CARDS DIALOG ---
    if (showManageCardsDialog) {
        CreditCardManagementDialog(
            cards = savedCreditCards,
            onDismiss = { showManageCardsDialog = false },
            onAddCard = { newCard ->
                savedCreditCards = savedCreditCards + newCard
                successDialogMessage = "New Credit/Debit Card (${newCard.brand} •••• ${newCard.last4}) saved securely."
                showSuccessDialog = true
            },
            onDeleteCard = { cardId ->
                savedCreditCards = savedCreditCards.filter { it.id != cardId }
            }
        )
    }

    // --- BUY COINS SELECTOR WITH CREDIT CARD CHECKOUT ---
    if (showBuyCoinsDialog) {
        BuyCoinsSelectorDialog(
            cards = savedCreditCards,
            onDismiss = { showBuyCoinsDialog = false },
            onBuy = { amount, cost, cardLabel ->
                viewModel.buyCoins(amount, cost)
                showBuyCoinsDialog = false
                successDialogMessage = "Successfully purchased $amount Coins for $$cost using $cardLabel! 256-bit SSL Payment Authorized."
                showSuccessDialog = true
            }
        )
    }

    // --- CASH OUT WITHDRAWAL DIALOG ---
    if (showWithdrawDialog) {
        var payoutEmailInput by remember { mutableStateOf(paypalEmail) }

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Creator Dollar ($) Payout", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            containerColor = Color(0xFF140D2F),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Exchange your Diamonds for real USD ($) dollars directly into your PayPal or Bank account.",
                        color = Color.White.copy(0.7f),
                        fontSize = 11.sp
                    )

                    Text("1. Select Payout Method:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("PayPal", "Stripe", "Bank Wire").forEach { method ->
                            val isSelected = selectedPayoutMethod == method
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF0079C1) else Color.White.copy(0.04f))
                                    .border(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { selectedPayoutMethod = method }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(method, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (selectedPayoutMethod == "PayPal") {
                        Text("PayPal Receiver Email ($):", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = payoutEmailInput,
                            onValueChange = { payoutEmailInput = it },
                            placeholder = { Text("e.g. user@gmail.com", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF0079C1)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    Text("2. Amount of Diamonds to Redeem (100 = $1.00 USD):", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = withdrawalAmountInput,
                        onValueChange = { withdrawalAmountInput = it },
                        placeholder = { Text("e.g. 500 (=$5.00 USD)", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    val reqAmt = withdrawalAmountInput.toIntOrNull() ?: 0
                    if (reqAmt > 0) {
                        Text(
                            "Payout Value: ≈ $${"%.2f".format(reqAmt * 0.01)} USD sent to $payoutEmailInput",
                            color = Color(0xFF00E676),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawalAmountInput.toIntOrNull() ?: 0
                        if (amt > 0 && amt <= diamondBalance) {
                            val success = viewModel.submitWithdrawalRequest(amt, selectedPayoutMethod, payoutEmailInput)
                            showWithdrawDialog = false
                            if (success) {
                                val dollars = "%.2f".format(amt * 0.01)
                                successDialogMessage = "Payout request submitted! $$dollars USD ($amt Diamonds) successfully routed via $selectedPayoutMethod to $payoutEmailInput."
                                showSuccessDialog = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    enabled = (withdrawalAmountInput.toIntOrNull() ?: 0) > 0 && (withdrawalAmountInput.toIntOrNull() ?: 0) <= diamondBalance
                ) {
                    Text("Send Payout ($ USD)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = Color.White.copy(0.6f))
                }
            }
        )
    }

    // --- PAYPAL SETUP DIALOG ---
    if (showPaypalSetupDialog) {
        PayPalConfigDialog(
            currentEmail = paypalEmail,
            currentCurrency = paypalCurrency,
            currentClientId = paypalClientId,
            onDismiss = { showPaypalSetupDialog = false },
            onSave = { email, currency, clientId ->
                viewModel.savePaypalAccount(email, currency, clientId)
                showPaypalSetupDialog = false
                successDialogMessage = "PayPal account ($email) successfully configured for receiving USD ($) payments and creator payouts!"
                showSuccessDialog = true
            }
        )
    }

    // --- SUCCESS OVERLAY DIALOG ---
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(48.dp)) },
            title = { Text("Operation Complete", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(successDialogMessage, color = Color.White.copy(0.8f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2))
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF140D2F)
        )
    }
}

@Composable
fun VipBenefitRow(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(title, color = Color.White.copy(0.8f), fontSize = 12.sp)
    }
}

@Composable
fun TransactionRow(transaction: CoinTransaction) {
    val isDebit = transaction.type == "spend_gift" || transaction.type == "withdraw"
    val color = if (isDebit) Color(0xFFFF4D4D) else Color(0xFF00E676)
    val sign = if (isDebit) "-" else "+"

    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = transaction.description,
                    color = Color.White.copy(0.9f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = transaction.type.uppercase(),
                    color = Color.White.copy(0.4f),
                    fontSize = 9.sp
                )
            }

            Text(
                text = "$sign${transaction.coinAmount}",
                color = color,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyCoinsSelectorDialog(
    cards: List<SavedCreditCard>,
    onDismiss: () -> Unit,
    onBuy: (Int, Double, String) -> Unit
) {
    val coinPacks = listOf(
        CoinPack(100, 0.99),
        CoinPack(500, 4.99),
        CoinPack(1200, 9.99),
        CoinPack(3000, 24.99)
    )
    var selectedPack by remember { mutableStateOf(coinPacks[1]) }
    var selectedCardId by remember { mutableStateOf(cards.firstOrNull { it.isPrimary }?.id ?: cards.firstOrNull()?.id ?: "new_card") }
    
    // New Card Inputs
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvc by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var saveCardChecked by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFF00E5FF))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Credit Card & Coin Checkout", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        containerColor = Color(0xFF140D2F),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("1. Select Coin Package:", color = Color.White.copy(0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    coinPacks.forEach { pack ->
                        val isSelected = selectedPack == pack
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF8E2DE2).copy(0.4f) else Color.White.copy(0.04f))
                                .border(1.dp, if (isSelected) Color(0xFFFFD700) else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { selectedPack = pack }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${pack.coinsCount}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("$${pack.costDollars}", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Text("2. Payment Method:", color = Color.White.copy(0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                cards.forEach { card ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedCardId == card.id) Color(0xFF00E5FF).copy(0.15f) else Color.White.copy(0.03f))
                            .border(1.dp, if (selectedCardId == card.id) Color(0xFF00E5FF) else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { selectedCardId = card.id }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${card.brand} •••• ${card.last4} (${card.expiry})", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        RadioButton(
                            selected = selectedCardId == card.id,
                            onClick = { selectedCardId = card.id },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E5FF))
                        )
                    }
                }

                // New Card Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedCardId == "new_card") Color(0xFF8E2DE2).copy(0.2f) else Color.White.copy(0.03f))
                        .border(1.dp, if (selectedCardId == "new_card") Color(0xFF8E2DE2) else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { selectedCardId = "new_card" }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddCard, contentDescription = null, tint = Color.White.copy(0.7f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use New Credit or Debit Card", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    RadioButton(
                        selected = selectedCardId == "new_card",
                        onClick = { selectedCardId = "new_card" },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF8E2DE2))
                    )
                }

                if (selectedCardId == "new_card") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { if (it.length <= 16) cardNumber = it },
                            placeholder = { Text("Card Number (16 Digits)", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cardExpiry,
                                onValueChange = { if (it.length <= 5) cardExpiry = it },
                                placeholder = { Text("MM/YY", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            OutlinedTextField(
                                value = cardCvc,
                                onValueChange = { if (it.length <= 4) cardCvc = it },
                                placeholder = { Text("CVC", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                        }

                        OutlinedTextField(
                            value = cardName,
                            onValueChange = { cardName = it },
                            placeholder = { Text("Cardholder Name", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PCI-DSS Level 1 Encrypted • Stripe & Visa Shield Secured", color = Color.White.copy(0.5f), fontSize = 8.5.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val label = if (selectedCardId == "new_card") {
                        val last = if (cardNumber.length >= 4) cardNumber.takeLast(4) else "4401"
                        "Card •••• $last"
                    } else {
                        val c = cards.firstOrNull { it.id == selectedCardId }
                        "${c?.brand ?: "Card"} •••• ${c?.last4 ?: "4532"}"
                    }
                    onBuy(selectedPack.coinsCount, selectedPack.costDollars, label)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
            ) {
                Text("Pay $${selectedPack.costDollars} USD", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(0.6f))
            }
        }
    )
}

@Composable
fun CreditCardManagementDialog(
    cards: List<SavedCreditCard>,
    onDismiss: () -> Unit,
    onAddCard: (SavedCreditCard) -> Unit,
    onDeleteCard: (String) -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    var numberInput by remember { mutableStateOf("") }
    var expiryInput by remember { mutableStateOf("") }
    var cvcInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFFFFD700))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saved Credit & Debit Cards", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        containerColor = Color(0xFF140D2F),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (cards.isEmpty()) {
                    Text("No saved payment cards attached to your Lumexa Vault.", color = Color.White.copy(0.5f), fontSize = 11.sp)
                } else {
                    cards.forEach { card ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(0.04f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${card.brand} •••• ${card.last4}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    if (card.isPrimary) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF00E5FF).copy(0.2f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("DEFAULT", color = Color(0xFF00E5FF), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text("Expires: ${card.expiry} • ${card.holderName}", color = Color.White.copy(0.5f), fontSize = 9.sp)
                            }

                            IconButton(onClick = { onDeleteCard(card.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Card", tint = Color(0xFFFF1744).copy(0.7f), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                if (!showAddForm) {
                    Button(
                        onClick = { showAddForm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add New Credit / Debit Card", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.03f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Enter Card Details:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = numberInput,
                                onValueChange = { if (it.length <= 16) numberInput = it },
                                placeholder = { Text("16-digit Card Number", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = expiryInput,
                                    onValueChange = { if (it.length <= 5) expiryInput = it },
                                    placeholder = { Text("MM/YY", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = cvcInput,
                                    onValueChange = { if (it.length <= 4) cvcInput = it },
                                    placeholder = { Text("CVC", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                placeholder = { Text("Name on Card", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        val brand = if (numberInput.startsWith("4")) "Visa" else if (numberInput.startsWith("5")) "Mastercard" else "Amex"
                                        val last4 = if (numberInput.length >= 4) numberInput.takeLast(4) else "9901"
                                        onAddCard(
                                            SavedCreditCard(
                                                id = "card_${System.currentTimeMillis()}",
                                                brand = brand,
                                                last4 = last4,
                                                expiry = if (expiryInput.isNotEmpty()) expiryInput else "12/29",
                                                holderName = if (nameInput.isNotEmpty()) nameInput else "Cardholder",
                                                isPrimary = cards.isEmpty()
                                            )
                                        )
                                        showAddForm = false
                                        numberInput = ""
                                        expiryInput = ""
                                        cvcInput = ""
                                        nameInput = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Save Card", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                TextButton(onClick = { showAddForm = false }, modifier = Modifier.weight(0.8f)) {
                                    Text("Cancel", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.White.copy(0.6f))
            }
        }
    )
}

data class SavedCreditCard(
    val id: String,
    val brand: String,
    val last4: String,
    val expiry: String,
    val holderName: String,
    val isPrimary: Boolean
)

data class CoinPack(
    val coinsCount: Int,
    val costDollars: Double
)

@Composable
fun PayPalConfigDialog(
    currentEmail: String,
    currentCurrency: String,
    currentClientId: String,
    onDismiss: () -> Unit,
    onSave: (email: String, currency: String, clientId: String) -> Unit
) {
    var emailInput by remember { mutableStateOf(currentEmail) }
    var selectedCurrency by remember { mutableStateOf(currentCurrency) }
    var clientIdInput by remember { mutableStateOf(currentClientId) }
    val currencies = listOf("USD ($)", "EUR (€)", "GBP (£)", "CAD ($)")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF003087)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("P", color = Color(0xFF0079C1), fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Configure PayPal Dollar Receiving", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        },
        containerColor = Color(0xFF140D2F),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Set your PayPal email account to receive direct payouts, user gifts, and dollar ($) sales from Lumexa Vault.",
                    color = Color.White.copy(0.7f),
                    fontSize = 11.sp
                )

                Text("PayPal Email Address (Incoming Dollars):", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    placeholder = { Text("e.g. user@gmail.com", color = Color.White.copy(0.3f), fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF0079C1)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Text("Receiving Currency:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    currencies.forEach { curr ->
                        val isSelected = selectedCurrency == curr
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF0079C1) else Color.White.copy(0.05f))
                                .clickable { selectedCurrency = curr }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(curr, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text("PayPal Client ID / Merchant ID (Optional API Key):", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = clientIdInput,
                    onValueChange = { clientIdInput = it },
                    placeholder = { Text("Client ID or PayPal.me username", color = Color.White.copy(0.3f), fontSize = 10.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF0079C1)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Instant Payouts & Automated USD Clearing Authorized", color = Color.White.copy(0.6f), fontSize = 9.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(emailInput, selectedCurrency, clientIdInput)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0079C1)),
                enabled = emailInput.isNotBlank() && emailInput.contains("@")
            ) {
                Text("Save & Connect PayPal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(0.6f), fontSize = 11.sp)
            }
        }
    )
}

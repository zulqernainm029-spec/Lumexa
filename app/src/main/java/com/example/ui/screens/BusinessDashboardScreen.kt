package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.CosmosBackgroundBrush
import com.example.ui.components.GlassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDashboardScreen(viewModel: ConnectLiveViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("hierarchy_bd") }

    // Merchant Hub State
    var stockCoins by remember { mutableIntStateOf(250000) }
    var targetUser by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var customerNotes by remember { mutableStateOf("") }

    // Orders State
    var ordersList by remember {
        mutableStateOf(
            listOf(
                MerchantOrder("ORD_9901", "Sofia_VIP", "100,000 Coins", "$450.00", "Easypaisa", "Pending Payment"),
                MerchantOrder("ORD_9902", "Alex_Host", "50,000 Coins", "$225.00", "Stripe Card", "Paid"),
                MerchantOrder("ORD_9903", "Elena_Streamer", "500,000 Coins", "$2,100.00", "Bank Wire", "Completed"),
                MerchantOrder("ORD_9904", "David_Reseller", "20,000 Coins", "$90.00", "JazzCash", "Processing")
            )
        )
    }

    // Security State
    var mfaActive by remember { mutableStateOf(true) }
    var fraudShieldActive by remember { mutableStateOf(true) }

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
                    onClick = { viewModel.navigateTo("main") },
                    modifier = Modifier.testTag("business_dashboard_back")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Enterprise Merchant & BD Control", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Text("Multi-Tier Reseller, Commission & Regional Hub", color = Color.White.copy(0.5f), fontSize = 10.sp)
                }
            }

            // Quick Switcher Scrollable Tabs
            val tabs = listOf(
                "hierarchy_bd" to "Hierarchy & BD",
                "merchant_hub" to "Merchant Stock",
                "orders" to "Orders Center",
                "commissions" to "Commissions & Bonuses",
                "targets" to "Targets & Growth",
                "reports_sec" to "Financials & Security",
                "conversion" to "Funnels & Campaigns"
            )

            ScrollableTabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0),
                containerColor = Color.White.copy(0.04f),
                contentColor = Color.White,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    val index = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)
                    if (index in tabPositions.indices) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = Color(0xFF00E5FF)
                        )
                    }
                }
            ) {
                tabs.forEach { (tabId, label) ->
                    val isSelected = selectedTab == tabId
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = tabId },
                        text = {
                            Text(
                                label,
                                color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(0.7f),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    "hierarchy_bd" -> {
                        item {
                            Text("Organizational Hierarchy & Regional Territories", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // Hierarchy Flow Chart Visualizer
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("9-Level Business Operations Pipeline", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    val hierarchyList = listOf(
                                        "Platform Owner" to Pair("Full System Authority", Color(0xFFFF1744)),
                                        "Super Manager Leader" to Pair("Global Network Oversight", Color(0xFFE040FB)),
                                        "Super Manager" to Pair("Regional Operations Command", Color(0xFF9C27B0)),
                                        "BD Leader" to Pair("Territory Expansion Head", Color(0xFF00E5FF)),
                                        "BD Official" to Pair("Merchant Recruitment Manager", Color(0xFF0288D1)),
                                        "Merchant Leader" to Pair("Master Wholesale Coin Dealer", Color(0xFFFFD700)),
                                        "Merchant" to Pair("Verified Local Reseller", Color(0xFFFF9800)),
                                        "Coin Reseller" to Pair("Sub-Agent Coin Provider", Color(0xFF00E676)),
                                        "Customer / End User" to Pair("Platform Coin Consumer", Color(0xFFB0BEC5))
                                    )

                                    hierarchyList.forEachIndexed { index, (role, info) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White.copy(0.03f))
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .clip(CircleShape)
                                                        .background(info.second.copy(0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("${index + 1}", color = info.second, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(role, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text(info.first, color = Color.White.copy(0.5f), fontSize = 9.sp)
                                                }
                                            }
                                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        // Territory Management
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Regional Territory Assignments", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    val territories = listOf(
                                        Triple("North America (NA)", "Lead: Marcus Vance (BD Leader)", "42 Merchants Active"),
                                        Triple("South Asia & Middle East", "Lead: Sofia Patel (Super Manager)", "128 Merchants Active"),
                                        Triple("Europe & UK", "Lead: Alex Rostova (BD Official)", "35 Merchants Active")
                                    )

                                    territories.forEach { (region, lead, stats) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White.copy(0.02f))
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(region, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text("$lead • $stats", color = Color.White.copy(0.6f), fontSize = 9.5.sp)
                                            }
                                            Button(
                                                onClick = { Toast.makeText(context, "Configuring territory bounds for $region...", Toast.LENGTH_SHORT).show() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                modifier = Modifier.height(26.dp)
                                            ) {
                                                Text("Manage", color = Color.White, fontSize = 9.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "merchant_hub" -> {
                        item {
                            Text("Merchant Inventory & Customer Coin Reseller Console", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    MerchantStatCard("Merchant Stock", "🪙 $stockCoins", "Ready for resale", Icons.Default.Inventory2)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    MerchantStatCard("Coins Sold Today", "🪙 48,500", "+18% vs Yesterday", Icons.Default.Sell)
                                }
                            }
                        }

                        // Direct Customer Sale & Note Form
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Direct Coin Transfer to Customer", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    OutlinedTextField(
                                        value = targetUser,
                                        onValueChange = { targetUser = it },
                                        label = { Text("Customer ID / Username", color = Color.White.copy(0.6f)) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = amountText,
                                        onValueChange = { amountText = it },
                                        label = { Text("Coin Amount", color = Color.White.copy(0.6f)) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = customerNotes,
                                        onValueChange = { customerNotes = it },
                                        label = { Text("Customer Notes (e.g., Paid via Easypaisa / Stripe)", color = Color.White.copy(0.6f)) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Button(
                                        onClick = {
                                            val qty = amountText.toIntOrNull() ?: 0
                                            if (qty in 1..stockCoins && targetUser.isNotBlank()) {
                                                stockCoins -= qty
                                                viewModel.buyCoins(qty, 0.0)
                                                Toast.makeText(context, "SUCCESS: Transferred $qty Coins to $targetUser. Note saved.", Toast.LENGTH_LONG).show()
                                                targetUser = ""
                                                amountText = ""
                                                customerNotes = ""
                                            } else {
                                                Toast.makeText(context, "Invalid quantity or empty target user!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                    ) {
                                        Text("Complete Customer Transfer", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    "orders" -> {
                        item {
                            Text("Merchant Order Center & Transaction Status", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                ordersList.forEach { order ->
                                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("${order.id} • ${order.buyer}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(
                                                    order.status,
                                                    color = when(order.status) {
                                                        "Completed" -> Color(0xFF00E676)
                                                        "Paid" -> Color(0xFF00E5FF)
                                                        "Pending Payment" -> Color(0xFFFFD700)
                                                        else -> Color.White
                                                    },
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Text("Amount: ${order.amount} (${order.price}) via ${order.gateway}", color = Color.White.copy(0.6f), fontSize = 10.sp)

                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = {
                                                        ordersList = ordersList.map { if (it.id == order.id) it.copy(status = "Completed") else it }
                                                        Toast.makeText(context, "Order ${order.id} marked as COMPLETED!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                                    modifier = Modifier.weight(1f).height(28.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Approve & Deliver", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Button(
                                                    onClick = {
                                                        ordersList = ordersList.map { if (it.id == order.id) it.copy(status = "Cancelled") else it }
                                                        Toast.makeText(context, "Order ${order.id} CANCELLED.", Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                                                    modifier = Modifier.weight(1f).height(28.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Cancel Order", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "commissions" -> {
                        item {
                            Text("Multi-Tier Commission Structure & Bonus Hub", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Configurable Tier Commission Rates", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    val tiers = listOf(
                                        "Merchant Direct Sale" to "5.0%",
                                        "Merchant Leader Override" to "2.0%",
                                        "BD Official Recruitment" to "1.0%",
                                        "BD Leader Territory Bonus" to "1.0%",
                                        "Super Manager Regional Cut" to "0.5%",
                                        "Super Manager Leader Cut" to "0.5%"
                                    )

                                    tiers.forEach { (title, rate) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(title, color = Color.White.copy(0.8f), fontSize = 11.sp)
                                            Text(rate, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Bonus Hub
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Available Reseller & BD Bonuses", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    val bonuses = listOf(
                                        "Daily Reseller Streak Bonus" to "+$50.00 USD (Requirement: 100K Coins Sold)",
                                        "Weekly BD Recruitment Reward" to "+$200.00 USD (Requirement: 5 New Merchants)",
                                        "Festival Super Sales Bonus" to "+$500.00 USD (Requirement: 1M Coins Volume)"
                                    )

                                    bonuses.forEach { (bonus, desc) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White.copy(0.02f))
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(bonus, color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text(desc, color = Color.White.copy(0.5f), fontSize = 9.sp)
                                            }
                                            Button(
                                                onClick = { Toast.makeText(context, "Bonus claimed! Credited to your Merchant Wallet.", Toast.LENGTH_SHORT).show() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                                modifier = Modifier.height(28.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp)
                                            ) {
                                                Text("Claim", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "targets" -> {
                        item {
                            Text("Monthly Sales Targets & Performance Growth", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Merchant & BD Quota Tracking", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    MerchantAnalyticsBar("Monthly Coin Quota (1M Coins Goal)", "680,000 Coins Sold (68%)", 0.68f, Color(0xFF00E5FF))
                                    MerchantAnalyticsBar("Merchant Recruitment Quota (10 Goal)", "8 Verified Merchants (80%)", 0.80f, Color(0xFFFFD700))
                                    MerchantAnalyticsBar("Customer Satisfaction Rating", "98.5% Positive Feedback", 0.985f, Color(0xFF00E676))
                                }
                            }
                        }
                    }

                    "reports_sec" -> {
                        item {
                            Text("Financial Statement Export & Security Shields", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Export Official Financial Reports", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        val formats = listOf("Export PDF", "Export Excel", "Export CSV")
                                        formats.forEach { fmt ->
                                            Button(
                                                onClick = { Toast.makeText(context, "Generated $fmt statement! Check downloads.", Toast.LENGTH_SHORT).show() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                                modifier = Modifier.weight(1f).height(34.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text(fmt, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Security & Anti-Fraud Center", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("2FA / MFA Merchant Authentication", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("Requires OTP for large coin transfers", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                        }
                                        Switch(
                                            checked = mfaActive,
                                            onCheckedChange = { mfaActive = it },
                                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("AI Fraud & Chargeback Shield", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("Blocks suspicious IP & sudden high volume transfers", color = Color.White.copy(0.5f), fontSize = 9.sp)
                                        }
                                        Switch(
                                            checked = fraudShieldActive,
                                            onCheckedChange = { fraudShieldActive = it },
                                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "conversion" -> {
                        item {
                            Text("Conversion Funnel Metrics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // Sales funnels conversion charts
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    MerchantAnalyticsBar("Product Views (Reach)", "45.2K Impressions", 0.95f, Color(0xFF00E5FF))
                                    MerchantAnalyticsBar("Added to Bag (CTR)", "8.4K Actions (18%)", 0.38f, Color(0xFFFF4081))
                                    MerchantAnalyticsBar("Completed Checkouts (Conversions)", "1.2K Sales (14%)", 0.18f, Color(0xFF00E676))
                                }
                            }
                        }

                        // Sales ledger stats
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    MerchantStatCard("Total Revenue", "$12,450", "Processing live", Icons.Default.TrendingUp)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    MerchantStatCard("Average Order", "$42.50", "Nominal checkout", Icons.Default.ShoppingCart)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MerchantAnalyticsBar(title: String, subtitle: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(0.6f), fontSize = 10.sp)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color.White.copy(0.1f)
        )
    }
}

@Composable
fun MerchantStatCard(title: String, value: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, color = Color.White.copy(0.6f), fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, color = Color(0xFF00E676), fontSize = 9.sp)
        }
    }
}

data class MerchantOrder(
    val id: String,
    val buyer: String,
    val amount: String,
    val price: String,
    val gateway: String,
    val status: String
)

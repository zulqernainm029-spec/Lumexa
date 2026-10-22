package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Post
import com.example.data.UserProfile
import com.example.data.VoiceRoom
import com.example.ui.ConnectLiveViewModel
import com.example.ui.Community
import com.example.ui.Event
import com.example.ui.components.GlassmorphicCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val SearchGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: ConnectLiveViewModel) {
    val coroutineScope = rememberCoroutineScope()
    
    // Core VM States
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchFilter by viewModel.searchFilter.collectAsState()
    
    val suggestedUsers by viewModel.suggestedFriends.collectAsState()
    val activeRooms by viewModel.voiceRooms.collectAsState()
    val posts by viewModel.allPosts.collectAsState()
    
    // Expanded Feature States
    val searchCountry by viewModel.searchCountry.collectAsState()
    val searchLanguage by viewModel.searchLanguage.collectAsState()
    val searchAgeFilter by viewModel.searchAgeFilter.collectAsState()
    val searchVerifiedOnly by viewModel.searchVerifiedOnly.collectAsState()
    val searchLocationActive by viewModel.searchLocationActive.collectAsState()
    val isVoiceRecording by viewModel.isVoiceRecording.collectAsState()
    val isScanningImage by viewModel.isScanningImage.collectAsState()
    
    val communities by viewModel.communities.collectAsState()
    val events by viewModel.events.collectAsState()

    var showAdvancedFilters by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    var showRadarDialog by remember { mutableStateOf(false) }

    val recentSearches = listOf("acoustic session", "tech talks", "Sofia", "Paris meetup", "Edm tracks")
    val trendingHashtags = listOf("Music", "Coding", "Travel", "SummerVibes", "Paris", "AI", "EDM")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Search Input Row with Mic, Camera & Filters Toggles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search users, rooms, events...", color = Color.White.copy(0.4f), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White.copy(0.6f)) },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White.copy(0.6f))
                                }
                            }
                            IconButton(onClick = {
                                showVoiceDialog = true
                                viewModel.simulateVoiceSearch { transcribed ->
                                    viewModel.updateSearchQuery(transcribed)
                                    showVoiceDialog = false
                                }
                            }) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice Search", tint = Color(0xFF00E676))
                            }
                            IconButton(onClick = {
                                showImageDialog = true
                                viewModel.simulateImageSearch { queryResult ->
                                    viewModel.updateSearchQuery(queryResult)
                                    showImageDialog = false
                                }
                            }) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = "Image Search", tint = Color(0xFF29B6F6))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8E2DE2),
                        unfocusedBorderColor = Color.White.copy(0.12f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_query_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Advanced filters button
                IconButton(
                    onClick = { showAdvancedFilters = !showAdvancedFilters },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (showAdvancedFilters) Color(0xFF8E2DE2) else Color.White.copy(0.04f))
                        .testTag("toggle_advanced_filters")
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Advanced Filters",
                        tint = if (showAdvancedFilters) Color.White else Color.White.copy(0.7f)
                    )
                }
            }

            // Expandable Advanced Filters Row
            AnimatedVisibility(
                visible = showAdvancedFilters,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .background(Color.White.copy(0.03f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Advanced Recommendation Engine Filters",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Location / Country select
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Country", color = Color.White.copy(0.5f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("All", "US", "Spain", "France", "Japan").forEach { c ->
                                    val isSel = searchCountry == c
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.05f))
                                            .clickable { viewModel.updateSearchFilters(c, searchLanguage, searchAgeFilter, searchVerifiedOnly) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(c, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Verified Toggle
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Verified Users Only", color = Color.White.copy(0.5f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Switch(
                                checked = searchVerifiedOnly,
                                onCheckedChange = { viewModel.updateSearchFilters(searchCountry, searchLanguage, searchAgeFilter, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF00E676),
                                    checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.scale(0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Radar Scan Nearby (Section R)
                    Button(
                        onClick = {
                            showRadarDialog = true
                            viewModel.simulateLocationPermission()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (searchLocationActive) Color(0xFF00E676) else Color.White.copy(0.06f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (searchLocationActive) "Nearby Location Radar Active" else "Scan Nearby Creators (Location Radar)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Scrollable Segmented Category Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                val categories = listOf("All", "Users", "Rooms", "Posts", "Communities", "Events", "Products")
                items(categories) { filter ->
                    val isSelected = searchFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF8E2DE2) else Color.White.copy(0.04f))
                            .clickable { viewModel.updateSearchFilter(filter) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("filter_tab_$filter"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(filter, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (searchQuery.isEmpty()) {
                // EMPTY STATE: Show Trends & Personalized AI Recommendations (Section S)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Recent Searches
                    item {
                        Text(
                            "Recent Searches",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(recentSearches) { term ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(0.03f))
                                        .clickable { viewModel.updateSearchQuery(term) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.History, contentDescription = null, tint = Color.White.copy(0.4f), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(term, color = Color.White.copy(0.7f), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Trending Topics & Hashtags
                    item {
                        Text(
                            "Trending Hashtags",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(trendingHashtags) { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(0.05f))
                                        .clickable { viewModel.updateSearchQuery(tag) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("#$tag", color = Color(0xFFD0BCFF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Nearby radar-matched users (Section R)
                    if (searchLocationActive) {
                        item {
                            Text(
                                "Creators Scanned Nearby (5km Radar)",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E676),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                        items(suggestedUsers.take(2)) { user ->
                            Box(modifier = Modifier.padding(bottom = 8.dp)) {
                                SearchUserItem(user = user, onConnect = { viewModel.toggleFollowUser(user.id) })
                            }
                        }
                    }

                    // --- AI PERSONALIZED RECOMMENDATIONS (Section S) ---
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Recommended for You", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("99% Accuracy", color = Color.White.copy(0.4f), fontSize = 9.sp)
                        }
                    }

                    // recommended creators card flow
                    item {
                        Text("Top Creators Matching Your Interests", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(suggestedUsers) { user ->
                                Card(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .clickable { viewModel.updateSearchQuery(user.name) },
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box {
                                            AsyncImage(
                                                model = user.avatarUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                            if (user.isVerified) {
                                                Icon(
                                                    Icons.Default.Verified,
                                                    contentDescription = "Verified",
                                                    tint = Color(0xFF00E676),
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .align(Alignment.BottomEnd)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(user.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 10.sp, maxLines = 1)
                                        Text("@${user.username}", color = Color.White.copy(0.4f), fontSize = 8.sp, maxLines = 1)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.White.copy(0.08f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("98% Match", color = Color(0xFFFFD700), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // recommended communities matching interests
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Trending Communities You Might Like", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(communities) { comm ->
                                Card(
                                    modifier = Modifier
                                        .width(200.dp)
                                        .clickable { viewModel.updateSearchQuery(comm.name) },
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                                ) {
                                    Column {
                                        AsyncImage(
                                            model = comm.bannerUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(60.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(comm.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp, maxLines = 1)
                                            Text(comm.desc, color = Color.White.copy(0.5f), fontSize = 9.sp, maxLines = 2, lineHeight = 11.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("${comm.membersCount} Members • ${comm.type}", color = Color(0xFFD0BCFF), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            } else {
                // DISPLAY SEARCH MATCHES
                val termLower = searchQuery.lowercase()

                // Filter logic matching criteria
                val matchedUsers = suggestedUsers.filter {
                    (it.name.lowercase().contains(termLower) || it.username.lowercase().contains(termLower)) &&
                    (searchCountry == "All" || it.country == searchCountry) &&
                    (!searchVerifiedOnly || it.isVerified)
                }

                val matchedRooms = activeRooms.filter {
                    it.title.lowercase().contains(termLower) || it.categories.lowercase().contains(termLower)
                }

                val matchedPosts = posts.filter {
                    it.caption.lowercase().contains(termLower) || it.hashtags.lowercase().contains(termLower)
                }

                val matchedCommunities = communities.filter {
                    it.name.lowercase().contains(termLower) || it.desc.lowercase().contains(termLower)
                }

                val matchedEvents = events.filter {
                    it.title.lowercase().contains(termLower) || it.desc.lowercase().contains(termLower)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("search_results_viewport"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // MATCHED USERS
                    if (searchFilter == "All" || searchFilter == "Users") {
                        if (matchedUsers.isNotEmpty()) {
                            item { Text("Users (${matchedUsers.size})", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            items(matchedUsers) { user ->
                                SearchUserItem(user = user, onConnect = { viewModel.toggleFollowUser(user.id) })
                            }
                        }
                    }

                    // MATCHED ROOMS
                    if (searchFilter == "All" || searchFilter == "Rooms") {
                        if (matchedRooms.isNotEmpty()) {
                            item { Text("Voice Rooms (${matchedRooms.size})", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            items(matchedRooms) { room ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { viewModel.joinVoiceRoom(room.id) },
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF8E2DE2), modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(room.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                            Text("Host: ${room.hostName} • ${room.listenersCount} listeners", color = Color.White.copy(0.4f), fontSize = 10.sp)
                                        }
                                        Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }

                    // MATCHED COMMUNITIES
                    if (searchFilter == "All" || searchFilter == "Communities") {
                        if (matchedCommunities.isNotEmpty()) {
                            item { Text("Communities (${matchedCommunities.size})", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            items(matchedCommunities) { comm ->
                                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = comm.bannerUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(comm.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                            Text("${comm.membersCount} members • ${comm.type}", color = Color.White.copy(0.4f), fontSize = 10.sp)
                                        }
                                        Button(
                                            onClick = { viewModel.joinCommunity(comm.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                            modifier = Modifier.height(26.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                        ) {
                                            Text("Join", color = Color.White, fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // MATCHED EVENTS
                    if (searchFilter == "All" || searchFilter == "Events") {
                        if (matchedEvents.isNotEmpty()) {
                            item { Text("Events Hub (${matchedEvents.size})", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            items(matchedEvents) { ev ->
                                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ev.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                            Text("${ev.date} • ${ev.location}", color = Color.White.copy(0.4f), fontSize = 10.sp)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.White.copy(0.08f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(if (ev.ticketPrice == 0.0) "FREE" else "$${ev.ticketPrice}", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // MATCHED POSTS
                    if (searchFilter == "All" || searchFilter == "Posts") {
                        if (matchedPosts.isNotEmpty()) {
                            item { Text("Moments / Posts (${matchedPosts.size})", color = Color.White.copy(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            items(matchedPosts) { post ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AsyncImage(
                                                model = post.authorAvatar,
                                                contentDescription = null,
                                                modifier = Modifier.size(28.dp).clip(CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(post.authorName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(post.caption, color = Color.White.copy(0.8f), fontSize = 11.sp)
                                        Text(post.hashtags, color = Color(0xFFD0BCFF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // FALLBACK EMPTY
                    if (matchedUsers.isEmpty() && matchedRooms.isEmpty() && matchedPosts.isEmpty() && matchedCommunities.isEmpty() && matchedEvents.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color.White.copy(0.2f), modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No matches found in any category.", color = Color.White.copy(0.4f), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- SIMULATED IMAGE/VOICE SCANNING DIALOGS (Section R) ---
        if (showVoiceDialog) {
            AlertDialog(
                onDismissRequest = { showVoiceDialog = false },
                confirmButton = {},
                dismissButton = {},
                icon = {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color(0xFF00E676), modifier = Modifier.size(36.dp))
                    }
                },
                title = { Text("AI Voice Translation Scan", color = Color.White, textAlign = TextAlign.Center) },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Acoustic spectrum analyzer scanning voice frequencies...",
                            color = Color.White.copy(0.7f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Pulse lines loading representation
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(0.4f, 0.8f, 1.0f, 0.6f, 0.2f, 0.5f, 0.9f).forEach { scale ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height((30 * scale).dp)
                                        .background(Color(0xFF00E676), RoundedCornerShape(1.5.dp))
                                )
                            }
                        }
                    }
                },
                containerColor = Color(0xFF140E2A)
            )
        }

        if (showImageDialog) {
            AlertDialog(
                onDismissRequest = { showImageDialog = false },
                confirmButton = {},
                dismissButton = {},
                icon = {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF29B6F6).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Image", tint = Color(0xFF29B6F6), modifier = Modifier.size(36.dp))
                    }
                },
                title = { Text("Gemini Vision Context Match", color = Color.White, textAlign = TextAlign.Center) },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Analyzing visual features to match user avatars & product labels...",
                            color = Color.White.copy(0.7f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Scanning bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(Color(0xFF29B6F6), RoundedCornerShape(2.dp))
                        )
                    }
                },
                containerColor = Color(0xFF140E2A)
            )
        }

        if (showRadarDialog) {
            LaunchedEffect(Unit) {
                delay(3000)
                showRadarDialog = false
            }
            AlertDialog(
                onDismissRequest = { showRadarDialog = false },
                confirmButton = {},
                dismissButton = {},
                icon = {
                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(40.dp))
                },
                title = { Text("Location Radar Scan", color = Color.White, textAlign = TextAlign.Center) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Active Sonar Ping: searching for creators with similar tech, EDM or photography interests in New York...", color = Color.White.copy(0.7f), fontSize = 11.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(color = Color(0xFF00E676))
                    }
                },
                containerColor = Color(0xFF120B24)
            )
        }
    }
}

// Reuse User item layout with verified badge and dynamic MATCH indicator
@Composable
fun SearchUserItem(user: UserProfile, onConnect: () -> Unit) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (user.isVerified) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Verified Badge",
                        tint = Color(0xFF00E676),
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    if (user.isVip) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFD700))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("VIP", color = Color.Black, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text("@${user.username} • ${user.country} • Match 95%", color = Color.White.copy(0.4f), fontSize = 10.sp)
            }

            Button(
                onClick = onConnect,
                colors = ButtonDefaults.buttonColors(containerColor = if (user.isFollowing) Color.White.copy(0.12f) else Color(0xFF8E2DE2)),
                modifier = Modifier.height(28.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Text(if (user.isFollowing) "Following" else "Connect", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

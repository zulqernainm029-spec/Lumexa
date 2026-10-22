package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Comment
import com.example.data.Post
import com.example.data.UserProfile
import com.example.ui.ConnectLiveViewModel
import com.example.ui.CollabRequest
import com.example.ui.Community
import com.example.ui.CommunityAnnouncement
import com.example.ui.Event
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GoldenVipBrush
import com.example.ui.components.PremiumGradientBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: ConnectLiveViewModel) {
    val posts by viewModel.allPosts.collectAsState()
    val suggested by viewModel.suggestedFriends.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val selectedPostIdForComments by viewModel.selectedPostIdForComments.collectAsState()
    val comments by viewModel.postComments.collectAsState()

    var showCreatePostDialog by remember { mutableStateOf(false) }

    // Bottom sheet state for commenting
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showTutorialOverlay by remember { mutableStateOf(true) }
    var commentInputText by remember { mutableStateOf("") }

    var activeSubTab by remember { mutableStateOf("feed") } // "feed", "live", "reels", "creator"

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Premium Scrollable Segmented Control for Lumexa sub-modules
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color.White.copy(0.04f), RoundedCornerShape(16.dp))
                    .padding(4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val subTabs = listOf(
                    Triple("feed", "Moments", Icons.Default.DynamicFeed),
                    Triple("live", "Live Streams", Icons.Default.LiveTv),
                    Triple("reels", "Reels", Icons.Default.Movie),
                    Triple("communities", "Communities", Icons.Default.Group),
                    Triple("events", "Events Hub", Icons.Default.Event),
                    Triple("creator", "Creator Hub", Icons.Default.Stars)
                )
                subTabs.forEach { (id, label, icon) ->
                    val isSelected = activeSubTab == id
                    val bgSelected = if (isSelected) PremiumGradientBrush else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgSelected)
                            .clickable { activeSubTab = id }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color.White else Color.White.copy(0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.White.copy(0.4f)
                            )
                        }
                    }
                }
            }

            // Crossfade content based on active sub tab
            Crossfade(targetState = activeSubTab, modifier = Modifier.weight(1f), label = "sub_tab_routing") { tab ->
                when (tab) {
                    "feed" -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("home_feed_list"),
                            contentPadding = PaddingValues(bottom = 90.dp)
                        ) {
                            // Stories Reel
                            item {
                                StoriesReel(suggested = suggested, viewModel = viewModel)
                            }

                            // Suggested Friends Horizontal
                            item {
                                SuggestedFriendsRow(suggested = suggested, viewModel = viewModel)
                            }

                            // Feed Posts Title
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Recent Moments",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    IconButton(
                                        onClick = { showCreatePostDialog = true },
                                        modifier = Modifier.testTag("open_create_post_button")
                                    ) {
                                        Icon(Icons.Default.AddCircle, contentDescription = "New Post", tint = Color(0xFF8E2DE2))
                                    }
                                }
                            }

                            // Empty state or post listings
                            if (posts.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No posts available.", color = Color.White.copy(alpha = 0.5f))
                                    }
                                }
                            } else {
                                items(posts) { post ->
                                    PostCard(post = post, viewModel = viewModel) {
                                        viewModel.selectPostForComments(post.id)
                                    }
                                }
                            }
                        }
                    }
                    "live" -> {
                        LiveStreamsTab(viewModel = viewModel)
                    }
                    "reels" -> {
                        ReelsTab(viewModel = viewModel)
                    }
                    "communities" -> {
                        CommunitiesTab(viewModel = viewModel)
                    }
                    "events" -> {
                        EventsHubTab(viewModel = viewModel)
                    }
                    "creator" -> {
                        CreatorHubTab(viewModel = viewModel)
                    }
                }
            }
        }

        // Animated Loader when refreshing
        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF8E2DE2))
            }
        }

        // Comment details Bottom Sheet
        if (selectedPostIdForComments != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.selectPostForComments(null) },
                sheetState = sheetState,
                containerColor = Color(0xFF0F0C21),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Comments (${comments.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .padding(horizontal = 20.dp)
                    ) {
                        if (comments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No comments yet. Be the first!", color = Color.White.copy(0.5f), fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(comments) { comment ->
                                CommentItem(comment = comment)
                            }
                        }
                    }

                    Divider(color = Color.White.copy(0.08f), modifier = Modifier.padding(vertical = 8.dp))

                    // Comment draft input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = commentInputText,
                            onValueChange = { commentInputText = it },
                            placeholder = { Text("Write a comment...", color = Color.White.copy(0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8E2DE2),
                                unfocusedBorderColor = Color.White.copy(0.1f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_field")
                        )

                        IconButton(
                            onClick = {
                                if (commentInputText.isNotBlank()) {
                                    selectedPostIdForComments?.let {
                                        viewModel.commentOnPost(it, commentInputText)
                                        commentInputText = ""
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(PremiumGradientBrush, CircleShape)
                                .testTag("submit_comment_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Post creation dialog
        if (showCreatePostDialog) {
            CreatePostDialog(
                onDismiss = { showCreatePostDialog = false },
                onSubmit = { caption, tags, imgUrl ->
                    viewModel.triggerCreatePost(caption, tags, imgUrl)
                    showCreatePostDialog = false
                }
            )
        }

        // --- INTERACTIVE TUTORIAL / ONBOARDING OVERLAY (Section Q) ---
        if (showTutorialOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.85f))
                    .clickable { /* Block clicks to underlay */ },
                contentAlignment = Alignment.Center
            ) {
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(PremiumGradientBrush),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Welcome to Lumexa!",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Next-Generation AI Social Network",
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Interactive gesture tips
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GestureTipItem(
                                icon = Icons.Default.TouchApp,
                                title = "Double Tap to Like Moments",
                                desc = "Double tap any photo post or reel on the feed to instantly fire a heart animation."
                            )
                            GestureTipItem(
                                icon = Icons.Default.Menu,
                                title = "Long Press for Context Menus",
                                desc = "Hold down on any chat message or user profile to trigger advanced fast actions."
                            )
                            GestureTipItem(
                                icon = Icons.Default.Swipe,
                                title = "Vertical Swipe on Reels",
                                desc = "Swipe vertically up or down for smooth, infinite scroll reel content."
                            )
                            GestureTipItem(
                                icon = Icons.Default.Campaign,
                                title = "Voice & Visual Match",
                                desc = "Use search bar microphone or camera to run real-time translation & vision scans."
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { showTutorialOverlay = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Got it, Let's Explore! 🚀", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GestureTipItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
            Text(desc, color = Color.White.copy(0.5f), fontSize = 9.sp, lineHeight = 11.sp)
        }
    }
}

@Composable
fun StoriesReel(suggested: List<UserProfile>, viewModel: ConnectLiveViewModel) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Active Live Stories",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Self add story
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { viewModel.setTab("profile") }
                ) {
                    Box(modifier = Modifier.size(64.dp)) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
                            contentDescription = "My Avatar",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White.copy(0.2f), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(Color(0xFF8E2DE2), CircleShape)
                                .border(2.dp, Color(0xFF080512), CircleShape)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("My Story", color = Color.White.copy(0.6f), fontSize = 11.sp)
                }
            }

            items(suggested) { user ->
                val hasActiveLive = user.onlineStatus == "online"
                val borderBrush = if (hasActiveLive) PremiumGradientBrush else Brush.sweepGradient(listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.1f)))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        viewModel.initiateCall(user.id, user.name, user.avatarUrl, isVoiceOnly = false)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .border(2.5.dp, borderBrush, CircleShape)
                            .padding(4.dp)
                    ) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = user.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = user.name.split(" ").firstOrNull() ?: "",
                        color = Color.White.copy(0.8f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestedFriendsRow(suggested: List<UserProfile>, viewModel: ConnectLiveViewModel) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Suggested Connections",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(suggested) { user ->
                GlassmorphicCard(
                    modifier = Modifier
                        .width(140.dp)
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(56.dp)) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = user.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            if (user.onlineStatus == "online") {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFF00E676), CircleShape)
                                        .border(1.5.dp, Color(0xFF0F0C21), CircleShape)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = user.city,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.toggleFollowUser(user.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.isFollowing) Color.White.copy(0.1f) else Color(0xFF8E2DE2)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                                .testTag("follow_toggle_${user.id}"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (user.isFollowing) "Following" else "Connect",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(post: Post, viewModel: ConnectLiveViewModel, onCommentClick: () -> Unit) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = post.authorAvatar,
                    contentDescription = post.authorName,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        if (post.authorIsVip) {
                            Spacer(modifier = Modifier.width(4.dp))
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
                        text = "Social Active • 3h ago",
                        color = Color.White.copy(0.4f),
                        fontSize = 10.sp
                    )
                }

                IconButton(onClick = { viewModel.blockUser(post.authorId) }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White.copy(0.6f))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Caption
            Text(
                text = post.caption,
                color = Color.White.copy(0.9f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Hashtags
            if (post.hashtags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    post.hashtags.split(",").forEach { hashtag ->
                        Text(
                            text = "#$hashtag",
                            color = Color(0xFFD0BCFF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Image attachment
            if (post.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post attachment",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Interactive Poll UI
            if (post.pollQuestion.isNotEmpty()) {
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    cornerRadius = 14.dp,
                    backgroundColor = Color.White.copy(0.02f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = post.pollQuestion,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White.copy(0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val options = post.pollOptions.split(",")
                        val votes = post.pollVotes.split(",").map { it.toIntOrNull() ?: 0 }
                        val totalVotes = votes.sum().coerceAtLeast(1)

                        options.forEachIndexed { index, option ->
                            val optionVotes = votes.getOrElse(index) { 0 }
                            val percentage = (optionVotes * 100) / totalVotes

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(0.04f))
                                    .clickable { viewModel.voteOnPoll(post.id, index) }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                // Background filler bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(percentage / 100f)
                                        .background(Color(0xFF8E2DE2).copy(0.15f))
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(option, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text("$percentage%", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }

            // Actions bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Like
                    Row(
                        modifier = Modifier.clickable { viewModel.toggleLikePost(post.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLikedByMe) Color(0xFFFF4D4D) else Color.White.copy(0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(post.likesCount.toString(), color = Color.White.copy(0.6f), fontSize = 12.sp)
                    }

                    // Comment
                    Row(
                        modifier = Modifier.clickable { onCommentClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Comment",
                            tint = Color.White.copy(0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(post.commentsCount.toString(), color = Color.White.copy(0.6f), fontSize = 12.sp)
                    }
                }

                // Bookmark
                IconButton(onClick = { /* Simulated */ }) {
                    Icon(
                        imageVector = if (post.isBookmarkedByMe) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (post.isBookmarkedByMe) Color(0xFFFFD700) else Color.White.copy(0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = comment.authorAvatar,
            contentDescription = comment.authorName,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.authorName,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(0.9f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.content,
                color = Color.White.copy(0.7f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun CreatePostDialog(onDismiss: () -> Unit, onSubmit: (String, String, String) -> Unit) {
    var caption by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var postType by remember { mutableStateOf("Image") } // Text, Image, Video
    var restrictAudience by remember { mutableStateOf("Everyone (Public)") }
    var disableComments by remember { mutableStateOf(false) }
    var pinPost by remember { mutableStateOf(false) }
    var schedulePost by remember { mutableStateOf(false) }
    var locationInput by remember { mutableStateOf("") }
    var productTag by remember { mutableStateOf("") }

    // Poll states
    var includePoll by remember { mutableStateOf(false) }
    var pollQuestion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PostAdd, contentDescription = null, tint = Color(0xFF00E5FF))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enterprise Creator Studio", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        containerColor = Color(0xFF140D2F),
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Post Type selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(0.04f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Text", "Image", "Video").forEach { type ->
                        val isSel = postType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFF8E2DE2) else Color.Transparent)
                                .clickable { postType = type }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(type, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    placeholder = { Text("What's on your mind? Mention friends with @ or add #tags", color = Color.White.copy(0.4f), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8E2DE2)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    placeholder = { Text("Hashtags (e.g. #Art, #Travel)", color = Color.White.copy(0.4f), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8E2DE2)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (postType != "Text") {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        placeholder = { Text(if (postType == "Image") "Attach Image URL" else "Attach Video Stream URL", color = Color.White.copy(0.4f), fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8E2DE2)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Add Location
                OutlinedTextField(
                    value = locationInput,
                    onValueChange = { locationInput = it },
                    placeholder = { Text("Add Location Tag (optional)", color = Color.White.copy(0.4f), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8E2DE2)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Tag Products
                OutlinedTextField(
                    value = productTag,
                    onValueChange = { productTag = it },
                    placeholder = { Text("Tag Store Product (e.g. Vinyl #1)", color = Color.White.copy(0.4f), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8E2DE2)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Audience Restrict dropdown preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Limit Audience", color = Color.White.copy(0.6f), fontSize = 11.sp)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(0.05f))
                            .clickable {
                                restrictAudience = when (restrictAudience) {
                                    "Everyone (Public)" -> "Friends Only"
                                    "Friends Only" -> "VIP Gold Club"
                                    else -> "Everyone (Public)"
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(restrictAudience, color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }

                // Interactive Poll Switcher
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Include Interactive Poll", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        Switch(
                            checked = includePoll,
                            onCheckedChange = { includePoll = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF8E2DE2))
                        )
                    }

                    if (includePoll) {
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = pollQuestion,
                            onValueChange = { pollQuestion = it },
                            placeholder = { Text("Poll Question (e.g. Yes/No)", color = Color.White.copy(0.4f), fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF8E2DE2)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Advanced switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Disable Public Comments", color = Color.White.copy(0.6f), fontSize = 11.sp)
                    Switch(
                        checked = disableComments,
                        onCheckedChange = { disableComments = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF8E2DE2))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pin to Profile Top", color = Color.White.copy(0.6f), fontSize = 11.sp)
                    Switch(
                        checked = pinPost,
                        onCheckedChange = { pinPost = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF8E2DE2))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Schedule for Later Publish", color = Color.White.copy(0.6f), fontSize = 11.sp)
                    Switch(
                        checked = schedulePost,
                        onCheckedChange = { schedulePost = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF8E2DE2))
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Save Draft Option
                Button(
                    onClick = {
                        onSubmit("[DRAFT] $caption", tags, imageUrl)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Draft", color = Color.White, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        val prefix = if (schedulePost) "[SCHEDULED] " else ""
                        onSubmit("$prefix$caption", tags, imageUrl)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Publish", color = Color.White, fontSize = 11.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(0.6f))
            }
        }
    )
}

// Data structures for Lumexa live arena and reels
data class LiveStreamMock(
    val id: String,
    val hostId: String,
    val hostName: String,
    val hostAvatar: String,
    val title: String,
    val category: String,
    val viewersCount: Int,
    val isPkBattle: Boolean = false,
    val challengerName: String = "",
    val challengerAvatar: String = "",
    val bgPhotoUrl: String
)

data class ReelMock(
    val id: String,
    val username: String,
    val userAvatar: String,
    val title: String,
    val songTitle: String,
    val likesCount: Int,
    val commentCount: Int,
    val bgPhotoUrl: String,
    val isLikedByMe: Boolean = false
)

// Simulated live stream feed seed data
val mockLiveStreams = listOf(
    LiveStreamMock(
        id = "live_1",
        hostId = "user_gemini",
        hostName = "Luna AI",
        hostAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=150&q=80",
        title = "Luna's Live AI Assistant Lab 🧠 Ask Me Anything!",
        category = "AI Tech & Coding",
        viewersCount = 1240,
        isPkBattle = false,
        bgPhotoUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80"
    ),
    LiveStreamMock(
        id = "live_2",
        hostId = "user_liam",
        hostName = "Liam",
        hostAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
        title = "Synth & Lofi Beats Live Performance 🎹✨",
        category = "Music & Studio",
        viewersCount = 580,
        isPkBattle = true,
        challengerName = "Zoe",
        challengerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
        bgPhotoUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800&q=80"
    ),
    LiveStreamMock(
        id = "live_3",
        hostId = "user_zoe",
        hostName = "Zoe",
        hostAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
        title = "Matcha Latte Art & Sweet Baking Masterclass 🍳🍵",
        category = "Lifestyle & Cooking",
        viewersCount = 920,
        isPkBattle = false,
        bgPhotoUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80"
    ),
    LiveStreamMock(
        id = "live_4",
        hostId = "user_sophia",
        hostName = "Sophia",
        hostAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=150&q=80",
        title = "Web-Design Showdown PK Arena! ⚔️ Let's Battle!",
        category = "Creative PK Battle",
        viewersCount = 2150,
        isPkBattle = true,
        challengerName = "Emma",
        challengerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
        bgPhotoUrl = "https://images.unsplash.com/photo-1541462608141-2758574e8b80?auto=format&fit=crop&w=800&q=80"
    )
)

// Simulated short reels list
val mockReelsList = listOf(
    ReelMock(
        id = "reel_1",
        username = "sophia_designer",
        userAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=150&q=80",
        title = "Creating glassmorphic cards in Jetpack Compose under 60 seconds! 💻✨ #JetpackCompose #Kotlin #AndroidDev",
        songTitle = "Lo-Fi Lounge Beats • Sophia Beats",
        likesCount = 1420,
        commentCount = 188,
        bgPhotoUrl = "https://images.unsplash.com/photo-1541462608141-2758574e8b80?auto=format&fit=crop&w=800&q=80"
    ),
    ReelMock(
        id = "reel_2",
        username = "liam_synths",
        userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
        title = "Testing out the new analog sub-synth in my home studio! Bass drop sounds incredible! 🎹🔊 #Synthwave #StudioVlog",
        songTitle = "Modular Jam #4 • Liam Synths",
        likesCount = 980,
        commentCount = 74,
        bgPhotoUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800&q=80"
    ),
    ReelMock(
        id = "reel_3",
        username = "zoe_matcha",
        userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
        title = "Perfecting the matcha latte art pour! Practice makes perfect 🍵💚 #Matcha #LatteArt #Aesthetic",
        songTitle = "Cozy Coffee Morning • Zoe Eats",
        likesCount = 2150,
        commentCount = 312,
        bgPhotoUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80"
    )
)

@Composable
fun LiveStreamsTab(viewModel: ConnectLiveViewModel) {
    var activeStream by remember { mutableStateOf<LiveStreamMock?>(null) }
    var showGoLiveSetup by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Live stream header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Live Arena",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Interact with streamers worldwide",
                    fontSize = 11.sp,
                    color = Color.White.copy(0.5f)
                )
            }

            Button(
                onClick = { showGoLiveSetup = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1005E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Go Live", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Horizontal Quick filters
        val filters = listOf("All Live", "⚔️ PK Battles", "🎤 Music", "🍳 Life", "🧠 AI Chat")
        var selectedFilter by remember { mutableStateOf("All Live") }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(filters) { f ->
                val isSel = selectedFilter == f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSel) Color(0xFFE1005E) else Color.White.copy(0.05f))
                        .clickable { selectedFilter = f }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(f, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Live grid list
        val filteredStreams = when (selectedFilter) {
            "⚔️ PK Battles" -> mockLiveStreams.filter { it.isPkBattle }
            "🎤 Music" -> mockLiveStreams.filter { it.category.contains("Music") }
            "🍳 Life" -> mockLiveStreams.filter { it.category.contains("Lifestyle") }
            "🧠 AI Chat" -> mockLiveStreams.filter { it.category.contains("AI") }
            else -> mockLiveStreams
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Render rows of 2 cards to form a beautiful grid
            val chunked = filteredStreams.chunked(2)
            items(chunked) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { stream ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { activeStream = stream }
                        ) {
                            // Video / background cover
                            AsyncImage(
                                model = stream.bgPhotoUrl,
                                contentDescription = stream.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Ambient dark shadow gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(0.8f))
                                        )
                                    )
                            )

                            // Status Badge "● LIVE" on top left
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .background(
                                        if (stream.isPkBattle) Color(0xFFFF4D4D) else Color(0xFFE1005E),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color.White, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (stream.isPkBattle) "VS BATTLE" else "LIVE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Viewers Count on top right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(0.6f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${stream.viewersCount} viewers",
                                    fontSize = 8.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Host Avatar & details at bottom
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = stream.hostAvatar,
                                        contentDescription = stream.hostName,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .border(1.dp, Color.White, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stream.hostName,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stream.title,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    maxLines = 2,
                                    lineHeight = 14.sp,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    // Live Stream Viewer Dialog/Screen
    if (activeStream != null) {
        LiveStreamViewer(
            stream = activeStream!!,
            onClose = { activeStream = null },
            viewModel = viewModel
        )
    }

    // Go Live Setup Sheet/Overlay
    if (showGoLiveSetup) {
        SelfLiveSetupScreen(
            onClose = { showGoLiveSetup = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamViewer(
    stream: LiveStreamMock,
    onClose: () -> Unit,
    viewModel: ConnectLiveViewModel
) {
    val coinBalance by viewModel.coinBalance.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var commentText by remember { mutableStateOf("") }
    var streamViewers by remember { mutableStateOf(stream.viewersCount) }

    // Stream points for PK battle
    var hostPkPoints by remember { mutableStateOf(1450) }
    var challengerPkPoints by remember { mutableStateOf(1320) }

    // Local scrolling live comment log
    val liveComments = remember {
        mutableStateListOf(
            Pair("System Guard", "Welcome to the Live Stream! Respect user-safety protocols. 🛡️"),
            Pair("david_k", "Wow, beautiful setup! ✨"),
            Pair("emma_blue", "So cool! Greetings from London!")
        )
    }

    // Animation trigger for sent gifts
    var activeGiftSplash by remember { mutableStateOf<String?>(null) }
    var showGiftSheet by remember { mutableStateOf(false) }
    var showInsufficientCoinsDialog by remember { mutableStateOf(false) }

    // Periodic simulation for viewers oscillation and automated comments
    LaunchedEffect(Unit) {
        val commentsPool = listOf(
            "This stream is fire! 🔥",
            "Sophia is absolutely crushing it!",
            "Can you explain the Compose code? 💻",
            "Love the background music!",
            "Best stream today! 🙌",
            "Just upgraded my VIP! ⭐",
            "Sending support diamonds! 💎",
            "Amazing! Keep it up!",
            "Who is winning the PK?"
        )
        val usersPool = listOf("sarah_p", "lucas_99", "cyber_warrior", "mila_h", "dan_retro", "vip_alex")

        while (true) {
            delay(2500)
            // Oscillate viewers
            streamViewers += (-15..20).random()
            // Randomly append a comment
            liveComments.add(Pair(usersPool.random(), commentsPool.random()))
            if (liveComments.size > 20) {
                liveComments.removeAt(0)
            }
            // Challenger score grows slightly
            if (stream.isPkBattle) {
                challengerPkPoints += (5..30).random()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        confirmButton = {},
        containerColor = Color(0xFF07040F),
        modifier = Modifier.fillMaxSize(),
        text = {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main Camera Simulation feed
                AsyncImage(
                    model = stream.bgPhotoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Shadow overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(0.6f),
                                    Color.Transparent,
                                    Color.Black.copy(0.9f)
                                )
                            )
                        )
                )

                // 1. Top Bar: Header info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Host Profile details
                    Row(
                        modifier = Modifier
                            .background(Color.Black.copy(0.6f), RoundedCornerShape(30.dp))
                            .padding(4.dp)
                            .padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = stream.hostAvatar,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(stream.hostName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("$streamViewers viewers", color = Color.White.copy(0.6f), fontSize = 9.sp)
                        }
                    }

                    // Close Button
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .background(Color.Black.copy(0.4f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                // 2. PK Battle Split Screen View Overlay
                if (stream.isPkBattle) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Display both Host & Challenger in split boxes
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Host
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.5.dp, Color(0xFF007BFF), RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = stream.hostAvatar,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .background(Color(0xFF007BFF))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Alex", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // VS Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .background(Color(0xFFFFD700), CircleShape)
                                    .size(30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("VS", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }

                            // Challenger
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.5.dp, Color(0xFFFF2D55), RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = stream.challengerAvatar,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(Color(0xFFFF2D55))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(stream.challengerName, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // PK Battle Progress Bar points comparison
                        PKBattleView(hostPoints = hostPkPoints, challengerPoints = challengerPkPoints)
                    }
                }

                // 3. Floating Scrolling Comments (Bottom left)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 80.dp, start = 12.dp, end = 12.dp)
                        .widthIn(max = 280.dp)
                        .height(180.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(liveComments) { (author, text) ->
                            val colorAuthor = if (author == "System Guard") Color(0xFFFFD700) else if (author == "Alex") Color(0xFF00E676) else Color.White.copy(0.9f)
                            Box(
                                modifier = Modifier
                                    .background(Color.Black.copy(0.45f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "$author:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorAuthor
                                    )
                                    Text(
                                        text = text,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(0.85f)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Animated Gift Overlay effect splash!
                if (activeGiftSplash != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(0.7f), RoundedCornerShape(20.dp))
                            .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(20.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "🎁 Alex sent a $activeGiftSplash!",
                                color = Color(0xFFFFD700),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Icon(
                                imageVector = when (activeGiftSplash) {
                                    "Sparkling Heart" -> Icons.Default.Favorite
                                    "Gold Rose" -> Icons.Default.Stars
                                    "Diamond Crown" -> Icons.Default.Stars
                                    else -> Icons.Default.AutoAwesome
                                },
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }

                // 5. Bottom Controls (Gift, Comment, Heart trigger)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chat comment field
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Say something...", color = Color.White.copy(0.4f), fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Black.copy(0.5f),
                            unfocusedContainerColor = Color.Black.copy(0.5f),
                            focusedBorderColor = Color.White.copy(0.3f),
                            unfocusedBorderColor = Color.White.copy(0.15f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(24.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (commentText.isNotEmpty()) {
                                    liveComments.add(Pair("Alex", commentText))
                                    commentText = ""
                                }
                            }) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                            }
                        }
                    )

                    // Gift Tray Button
                    IconButton(
                        onClick = { showGiftSheet = true },
                        modifier = Modifier
                            .background(Color(0xFFFF2D55), CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Send Gift", tint = Color.White)
                    }

                    // Heart Reaction button
                    IconButton(
                        onClick = {
                            liveComments.add(Pair("Alex", "Liked the stream! ❤️"))
                            hostPkPoints += 10
                        },
                        modifier = Modifier
                            .background(Color(0xFF00E676), CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Like", tint = Color.White)
                    }
                }
            }
        }
    )

    // Virtual Gifting Bottom Tray Modal Sheet
    if (showGiftSheet) {
        ModalBottomSheet(
            onDismissRequest = { showGiftSheet = false },
            containerColor = Color(0xFF130A29),
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Virtual Gift", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Circle, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${coinBalance ?: 0} coins", color = Color.White.copy(0.7f), fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GiftItemCard("Sparkling Heart", 10, "❤️") {
                        if ((coinBalance ?: 0) >= 10) {
                            viewModel.sendGiftToFriend(stream.hostId, stream.hostName, 10, "Sparkling Heart")
                            liveComments.add(Pair("Alex", "sent a Sparkling Heart! ❤️"))
                            hostPkPoints += 100
                            activeGiftSplash = "Sparkling Heart"
                            showGiftSheet = false
                            coroutineScope.launch {
                                delay(2200)
                                activeGiftSplash = null
                            }
                        } else {
                            showInsufficientCoinsDialog = true
                        }
                    }

                    GiftItemCard("Gold Rose", 50, "🌹") {
                        if ((coinBalance ?: 0) >= 50) {
                            viewModel.sendGiftToFriend(stream.hostId, stream.hostName, 50, "Gold Rose")
                            liveComments.add(Pair("Alex", "sent a Gold Rose! 🌹"))
                            hostPkPoints += 500
                            activeGiftSplash = "Gold Rose"
                            showGiftSheet = false
                            coroutineScope.launch {
                                delay(2200)
                                activeGiftSplash = null
                            }
                        } else {
                            showInsufficientCoinsDialog = true
                        }
                    }

                    GiftItemCard("Diamond Crown", 200, "👑") {
                        if ((coinBalance ?: 0) >= 200) {
                            viewModel.sendGiftToFriend(stream.hostId, stream.hostName, 200, "Diamond Crown")
                            liveComments.add(Pair("Alex", "sent a Diamond Crown! 👑"))
                            hostPkPoints += 2000
                            activeGiftSplash = "Diamond Crown"
                            showGiftSheet = false
                            coroutineScope.launch {
                                delay(2200)
                                activeGiftSplash = null
                            }
                        } else {
                            showInsufficientCoinsDialog = true
                        }
                    }
                }
            }
        }
    }

    // Insufficient Coins recharge prompt dialog
    if (showInsufficientCoinsDialog) {
        AlertDialog(
            onDismissRequest = { showInsufficientCoinsDialog = false },
            containerColor = Color(0xFF140D2F),
            title = { Text("Recharge Balance", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("You do not have enough coins to purchase this virtual gift. Would you like to recharge 500 coins for $4.99 instantly?", color = Color.White.copy(0.7f)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.buyCoins(500, 4.99)
                        showInsufficientCoinsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text("Recharge Now", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInsufficientCoinsDialog = false }) {
                    Text("Cancel", color = Color.White.copy(0.6f))
                }
            }
        )
    }
}

@Composable
fun PKBattleView(hostPoints: Int, challengerPoints: Int) {
    val total = hostPoints + challengerPoints
    val hostRatio = if (total > 0) hostPoints.toFloat() / total else 0.5f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Alex: $hostPoints pts", color = Color(0xFF007BFF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("Challenger: $challengerPoints pts", color = Color(0xFFFF2D55), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White.copy(0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(hostRatio)
                    .background(Color(0xFF007BFF))
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f - hostRatio)
                    .background(Color(0xFFFF2D55))
            )
        }
    }
}

@Composable
fun GiftItemCard(name: String, cost: Int, icon: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(0.04f))
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("$cost coins", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun SelfLiveSetupScreen(onClose: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("No Filter") }
    var beautyValue by remember { mutableStateOf(0.5f) }
    var isMuted by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }

    val filters = listOf("No Filter", "Cyberpunk Glow", "Midnight Retro", "Soft Velvet")

    AlertDialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        confirmButton = {},
        containerColor = Color(0xFF090412),
        modifier = Modifier.fillMaxSize(),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Go Live Setup 🎥", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (!isStarted) {
                    Text("Enter Stream Broadcast Title", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("My epic live session...", color = Color.White.copy(0.4f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFE1005E)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Select Camera Beauty Filters", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filters.forEach { f ->
                            val sel = activeFilter == f
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (sel) Color(0xFFE1005E) else Color.White.copy(0.05f))
                                    .clickable { activeFilter = f }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(f, color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Auto Beauty Enhancement: ${(beautyValue * 100).toInt()}%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = beautyValue,
                        onValueChange = { beautyValue = it },
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color(0xFFE1005E),
                            thumbColor = Color(0xFFE1005E)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { isStarted = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1005E)),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start Live Broadcast Now", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Running stream simulation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Radio, contentDescription = null, tint = Color(0xFFE1005E), modifier = Modifier.size(50.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("STREAM ACTIVE • BROADCASTING LIVE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            Text("Title: \"$title\"", color = Color.White.copy(0.6f), fontSize = 11.sp)
                            Text("Active Filter: $activeFilter", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { isStarted = false; onClose() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Stop Broadcast", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsTab(viewModel: ConnectLiveViewModel) {
    var activeReelIndex by remember { mutableStateOf(0) }
    val activeReel = mockReelsList[activeReelIndex]

    // Local likes count and state
    var isLiked by remember(activeReelIndex) { mutableStateOf(activeReel.isLikedByMe) }
    var likesCount by remember(activeReelIndex) { mutableStateOf(activeReel.likesCount) }

    var showCommentsSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        // Full height video cover simulation
        AsyncImage(
            model = activeReel.bgPhotoUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(0.5f), Color.Transparent, Color.Black.copy(0.85f))
                    )
                )
        )

        // Title and description overlay at bottom left
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .widthIn(max = 280.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = activeReel.userAvatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "@${activeReel.username}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = activeReel.title,
                color = Color.White.copy(0.9f),
                fontSize = 12.sp,
                lineHeight = 15.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(activeReel.songTitle, color = Color.White.copy(0.7f), fontSize = 10.sp)
            }
        }

        // Overlay Action buttons on the right side
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Swipe Nav instructions
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        if (activeReelIndex > 0) activeReelIndex--
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(0.5f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Prev", tint = Color.White)
                }
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(
                    onClick = {
                        if (activeReelIndex < mockReelsList.size - 1) activeReelIndex++
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(0.5f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next", tint = Color.White)
                }
            }

            // Like action
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        likesCount += if (isLiked) 1 else -1
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(0.5f), CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.White
                    )
                }
                Text("$likesCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            // Comments trigger
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { showCommentsSheet = true },
                    modifier = Modifier
                        .background(Color.Black.copy(0.5f), CircleShape)
                        .size(44.dp)
                ) {
                    Icon(Icons.Default.Comment, contentDescription = "Comments", tint = Color.White)
                }
                Text("${activeReel.commentCount}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            // Spinning Vinyl disc decoration
            Box(
                modifier = Modifier
                    .background(Color.Black, CircleShape)
                    .border(2.dp, Color.White.copy(0.3f), CircleShape)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Album, contentDescription = null, tint = Color.White.copy(0.8f), modifier = Modifier.size(24.dp))
            }
        }
    }

    // Modal sheet for Comments on Reels
    if (showCommentsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCommentsSheet = false },
            containerColor = Color(0xFF130926),
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Reel Comments", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    item {
                        Row {
                            Text("emma_design: ", color = Color(0xFF8E2DE2), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Unbelievably useful trick, thanks Sophia!", color = Color.White, fontSize = 11.sp)
                        }
                    }
                    item {
                        Row {
                            Text("code_master: ", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("The spring animations in compose are the best.", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatorHubTab(viewModel: ConnectLiveViewModel) {
    val coinBalance by viewModel.coinBalance.collectAsState()
    val collabs by viewModel.collabRequests.collectAsState()

    var diamondBalance by remember { mutableStateOf(2850) }
    var isSpamGuardEnabled by remember { mutableStateOf(true) }
    var isToxicFilterEnabled by remember { mutableStateOf(true) }

    val blockLogs = remember {
        mutableStateListOf(
            "Spam bot 'cheap_coins_99' blocked by AI Guard • 20:51",
            "Offensive message from anonymous flagged & deleted • 18:34"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creator Hub Header Banner
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text("Creator Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Manage your content monetization and cybersecurity logs", fontSize = 11.sp, color = Color.White.copy(0.5f))
            }
        }

        // Diamond Vault Earnings Card (MONETIZATION & PAYMENTS)
        item {
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PremiumGradientBrush, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("DIAMOND EARNINGS VAULT", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$diamondBalance Diamonds", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text("Current Exchange Rate: 10 Diamonds = 1 Spendable Coin", color = Color.White.copy(0.5f), fontSize = 10.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (diamondBalance >= 1000) {
                                    diamondBalance -= 1000
                                    // Redeem 1000 Diamonds for 100 Coins inside VM
                                    viewModel.buyCoins(100, 0.0)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            enabled = diamondBalance >= 1000,
                            modifier = Modifier.weight(1.0f)
                        ) {
                            Text("Convert 1000 Dia to 100 Coins", color = Color(0xFF8E2DE2), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (diamondBalance > 0) {
                                    val coinsToAdd = diamondBalance / 10
                                    viewModel.buyCoins(coinsToAdd, 0.0)
                                    diamondBalance = 0
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.15f)),
                            shape = RoundedCornerShape(10.dp),
                            enabled = diamondBalance > 0,
                            modifier = Modifier.weight(1.0f)
                        ) {
                            Text("Convert All Earnings", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Growth Statistics (ANALYTICS)
        item {
            Text("Growth & Earnings Metrics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Followers Gained Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(0.04f))
                        .padding(14.dp)
                ) {
                    Column {
                        Text("Follower Growth", color = Color.White.copy(0.5f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("+240 users", color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("Last 7 days", color = Color.White.copy(0.3f), fontSize = 8.sp)
                    }
                }

                // Broadcast Minutes Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(0.04f))
                        .padding(14.dp)
                ) {
                    Column {
                        Text("Live Duration", color = Color.White.copy(0.5f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("420 minutes", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("Active streak: 4 days", color = Color.White.copy(0.3f), fontSize = 8.sp)
                    }
                }
            }
        }

        // --- CREATOR COLLABORATION REQUESTS (Section X & Z) ---
        item {
            Text("Pending Joint Collaboration Requests", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (collabs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(0.03f))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No pending joint-stream requests.", color = Color.White.copy(0.4f), fontSize = 11.sp)
                }
            }
        } else {
            items(collabs) { request ->
                val statusStr = if (request.isAccepted) "Accepted" else if (request.isDeclined) "Declined" else "Pending"
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Joint Call Request • BRAND SPONSOR", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (statusStr == "Pending") Color(0xFFFFD700).copy(0.15f) else Color(0xFF00E676).copy(0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(statusStr.uppercase(), color = if (statusStr == "Pending") Color(0xFFFFD700) else Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Requested by: ${request.brandName}", fontWeight = FontWeight.Bold, color = Color.White.copy(0.9f), fontSize = 12.sp)
                        Text(request.description, color = Color.White.copy(0.6f), fontSize = 10.sp, lineHeight = 12.sp)
                        Text("Sponsorship Budget: ${request.offerAmount} coins", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        if (statusStr == "Pending") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.handleCollabRequest(request.id, true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    modifier = Modifier.height(28.dp).weight(1f),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Accept Call", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { viewModel.handleCollabRequest(request.id, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                    modifier = Modifier.height(28.dp).weight(1f),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Dismiss", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // AI Cybersecurity Control Panel (SECURITY & SPAM BLOCK)
        item {
            Text("AI Shield Cybersecurity Panel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // AI Real-time Spam filter toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Real-Time Spam Blocker", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Intercept and suppress duplicate spam links inside stream chats instantly", color = Color.White.copy(0.5f), fontSize = 9.sp)
                        }
                        Switch(
                            checked = isSpamGuardEnabled,
                            onCheckedChange = { isSpamGuardEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                        )
                    }

                    Divider(color = Color.White.copy(0.05f))

                    // AI Toxic words blocker toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Toxicity Filtering Guard", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Auto-moderate and mask aggressive, abusive words with * stars", color = Color.White.copy(0.5f), fontSize = 9.sp)
                        }
                        Switch(
                            checked = isToxicFilterEnabled,
                            onCheckedChange = { isToxicFilterEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                        )
                    }
                }
            }
        }

        // Real-time block audit logging entries
        item {
            Text("Real-Time Cybersecurity Block Audit Logs", fontSize = 11.sp, color = Color.White.copy(0.5f), fontWeight = FontWeight.Bold)
        }

        items(blockLogs) { log ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(0.03f))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(log, color = Color.White.copy(0.8f), fontSize = 10.sp)
                }
            }
        }
    }
}

// --- COMMUNITIES MANAGEMENT HUB (Section T: Communities & Announcements) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitiesTab(viewModel: ConnectLiveViewModel) {
    val communities by viewModel.communities.collectAsState()
    val communityAnnouncements by viewModel.communityAnnouncements.collectAsState()
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var newCommName by remember { mutableStateOf("") }
    var newCommDesc by remember { mutableStateOf("") }
    var newCommType by remember { mutableStateOf("Public") } // Public, Private

    var activeSubView by remember { mutableStateOf("all") } // all, announcements

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tab header & Sub menu
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Communities Lounge", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Join specialized interest clusters & discuss ideas", fontSize = 11.sp, color = Color.White.copy(0.5f))
                }
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Subtabs: All Communities vs Announcements
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(0.03f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("all" to "All Guilds", "announcements" to "Guild Announcements").forEach { (viewId, label) ->
                    val isSel = activeSubView == viewId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) Color(0xFF8E2DE2) else Color.Transparent)
                            .clickable { activeSubView = viewId }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = if (isSel) Color.White else Color.White.copy(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (activeSubView == "all") {
            if (communities.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("No communities available. Create the very first one!", color = Color.White.copy(0.4f), fontSize = 12.sp)
                    }
                }
            } else {
                items(communities) { comm ->
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            AsyncImage(
                                model = comm.bannerUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(comm.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(0.08f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(comm.type.uppercase(), color = Color(0xFF00E5FF), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(comm.desc, color = Color.White.copy(0.6f), fontSize = 11.sp, lineHeight = 14.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${comm.membersCount} active guilds members", color = Color.White.copy(0.4f), fontSize = 10.sp)
                                    
                                    Button(
                                        onClick = { viewModel.joinCommunity(comm.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                        modifier = Modifier.height(28.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                    ) {
                                        Text("Join Club", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Announcements
            if (communityAnnouncements.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("No announcements posted yet.", color = Color.White.copy(0.4f), fontSize = 12.sp)
                    }
                }
            } else {
                items(communityAnnouncements) { announce ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.03f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(announce.author, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700), fontSize = 11.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("Acoustic Alert", color = Color.White.copy(0.3f), fontSize = 8.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(announce.content, color = Color.White.copy(0.7f), fontSize = 11.sp, lineHeight = 14.sp)
                        }
                    }
                }
            }
        }
    }

    // Community Create Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Assemble New Guild Community", color = Color.White, fontWeight = FontWeight.Bold) },
            containerColor = Color(0xFF140D2F),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCommName,
                        onValueChange = { newCommName = it },
                        placeholder = { Text("Guild Name, e.g., EDM Lovers", color = Color.White.copy(0.3f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = newCommDesc,
                        onValueChange = { newCommDesc = it },
                        placeholder = { Text("What are the cluster rules and topics?", color = Color.White.copy(0.3f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF8E2DE2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Public", "Private").forEach { t ->
                            val isSel = newCommType == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.05f))
                                    .clickable { newCommType = t }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(t, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCommName.isNotBlank() && newCommDesc.isNotBlank()) {
                            viewModel.createCommunity(
                                name = newCommName,
                                desc = newCommDesc,
                                rules = listOf("Be kind & supportive", "No toxic speech or spamming"),
                                type = newCommType
                            )
                            showCreateDialog = false
                            newCommName = ""
                            newCommDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text("Initialize Club", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = Color.White.copy(0.6f))
                }
            }
        )
    }
}

// --- EVENTS & TICKETING HUB (Section U: Events Booking & RSVPs) ---
@Composable
fun EventsHubTab(viewModel: ConnectLiveViewModel) {
    val events by viewModel.events.collectAsState()
    val selectedRsvpReceipt = remember { mutableStateOf<Event?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text("Events & Live Ticketing", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Reserve entry tickets to virtual stream gigs & physical gatherings", fontSize = 11.sp, color = Color.White.copy(0.5f))
            }
        }

        if (events.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    Text("No upcoming events listed.", color = Color.White.copy(0.4f), fontSize = 12.sp)
                }
            }
        } else {
            items(events) { event ->
                val eventType = if (event.isOnline) "ONLINE STREAM" else "PHYSICAL EVENT"
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(eventType, color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(0.08f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (event.ticketPrice == 0.0) "FREE" else "$${event.ticketPrice}",
                                    color = Color(0xFF00E676),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(event.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(event.desc, color = Color.White.copy(0.6f), fontSize = 11.sp, lineHeight = 14.sp)
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color.White.copy(0.06f))
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.White.copy(0.4f), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${event.date} • ${event.location}", color = Color.White.copy(0.5f), fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${event.attendeesCount} attendees booked", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            
                            Button(
                                onClick = {
                                    viewModel.rsvpEvent(event.id, "attending")
                                    selectedRsvpReceipt.value = event.copy(attendeesCount = event.attendeesCount + 1)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                            ) {
                                Text("Reserve Seat", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Ticket Receipt Overlay Dialog (Ticketing Compliance - Section U)
    if (selectedRsvpReceipt.value != null) {
        val ticket = selectedRsvpReceipt.value!!
        AlertDialog(
            onDismissRequest = { selectedRsvpReceipt.value = null },
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(48.dp)) },
            title = { Text("RSVP Booking Confirmed", color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
            containerColor = Color(0xFF140D2F),
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Your entry pass to ${ticket.title} is secured!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Ticket Code: LMX-${System.currentTimeMillis().toString().takeLast(6)}", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Please arrive in the stream room or physical venue 10 minutes early. A digital calendar invite was auto-synchronized.", color = Color.White.copy(0.5f), fontSize = 9.sp, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedRsvpReceipt.value = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2))
                ) {
                    Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}


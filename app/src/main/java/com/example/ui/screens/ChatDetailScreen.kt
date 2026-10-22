package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ChatMessage
import com.example.data.Conversation
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.PremiumGradientBrush
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ChatsTab(viewModel: ConnectLiveViewModel) {
    val conversations by viewModel.conversations.collectAsState()
    val pinnedConversationIds by viewModel.pinnedConversationIds.collectAsState()
    val favoriteConversationIds by viewModel.favoriteConversationIds.collectAsState()

    var activeFolderTab by remember { mutableStateOf("All") }
    val folders = listOf("All", "Favorites", "Groups", "Archived")

    // Filter conversations based on selected folder
    val filteredConversations = conversations.filter { conv ->
        when (activeFolderTab) {
            "Favorites" -> favoriteConversationIds.contains(conv.id)
            "Archived" -> conv.isArchived
            "Groups" -> conv.peerName.contains("Group") || conv.peerName.contains("Club") || conv.peerName.contains("Room")
            else -> !conv.isArchived // "All" folder displays unarchived chats
        }
    }

    // Sort pinned conversations first
    val sortedConversations = filteredConversations.sortedWith(
        compareByDescending<Conversation> { pinnedConversationIds.contains(it.id) }
            .thenByDescending { it.lastMessageTime }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        // Chat list header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Lumexa Messaging",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            IconButton(
                onClick = { viewModel.navigateTo("lumi_assistant") },
                modifier = Modifier.testTag("lumi_workspace_chat_trigger")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Lumi AI", tint = Color(0xFF00E5FF))
            }
        }

        // Folders row (Horizontal Scroll)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(folders) { folder ->
                val isSelected = activeFolderTab == folder
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0xFF8E2DE2).copy(0.15f) else Color.White.copy(0.04f))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFF8E2DE2) else Color.Transparent,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { activeFolderTab = folder }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("chat_folder_tab_$folder")
                ) {
                    Text(
                        text = folder,
                        color = if (isSelected) Color(0xFFD0BCFF) else Color.White.copy(0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (sortedConversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No conversations in $activeFolderTab.", color = Color.White.copy(0.4f), fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("conversations_list"),
                contentPadding = PaddingValues(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedConversations, key = { it.id }) { conv ->
                    ConversationItem(
                        conversation = conv,
                        isPinned = pinnedConversationIds.contains(conv.id),
                        isFavorite = favoriteConversationIds.contains(conv.id),
                        onPinToggle = { viewModel.togglePinConversation(conv.id) },
                        onFavoriteToggle = { viewModel.toggleFavoriteConversation(conv.id) },
                        onArchiveToggle = { viewModel.archiveConversation(conv.id, !conv.isArchived) },
                        onDelete = { /* empty placeholder */ },
                        onClick = { viewModel.enterConversation(conv.id) }
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ConversationItem(
    conversation: Conversation,
    isPinned: Boolean,
    isFavorite: Boolean,
    onPinToggle: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onArchiveToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    Box {
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { showDropdownMenu = true }
                )
                .testTag("conversation_item_${conversation.id}")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(48.dp)) {
                    AsyncImage(
                        model = conversation.peerAvatar,
                        contentDescription = conversation.peerName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    if (conversation.peerOnlineStatus == "online") {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFF00E676), CircleShape)
                                .border(1.5.dp, Color(0xFF0F0C21), CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = conversation.peerName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        if (isPinned) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = Color(0xFF00E5FF), modifier = Modifier.size(11.dp))
                        }
                        if (isFavorite) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = Color(0xFFFF4081), modifier = Modifier.size(11.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = conversation.lastMessage,
                        color = Color.White.copy(0.5f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "30m ago",
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.3f)
                    )
                    if (conversation.unreadCount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF8E2DE2), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = conversation.unreadCount.toString(),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        DropdownMenu(
            expanded = showDropdownMenu,
            onDismissRequest = { showDropdownMenu = false },
            modifier = Modifier.background(Color(0xFF0F0C21))
        ) {
            DropdownMenuItem(
                text = { Text(if (isPinned) "📌 Unpin Chat" else "📌 Pin Chat", color = Color.White, fontSize = 12.sp) },
                onClick = {
                    onPinToggle()
                    showDropdownMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(if (isFavorite) "❤️ Remove Favorite" else "❤️ Add to Favorites", color = Color.White, fontSize = 12.sp) },
                onClick = {
                    onFavoriteToggle()
                    showDropdownMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(if (conversation.isArchived) "📥 Unarchive Chat" else "📥 Archive Chat", color = Color.White, fontSize = 12.sp) },
                onClick = {
                    onArchiveToggle()
                    showDropdownMenu = false
                }
            )
        }
    }
}

// Active detailed chat log screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(viewModel: ConnectLiveViewModel) {
    val activeConvId by viewModel.activeConversationId.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val messages by viewModel.activeChatMessages.collectAsState(initial = emptyList())
    val isTyping by viewModel.isPeerTyping.collectAsState()

    val currentConv = conversations.find { it.id == activeConvId } ?: return

    var inputText by remember { mutableStateOf("") }
    var showMorePanel by remember { mutableStateOf(false) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var voiceTimerSeconds by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    // Simulated voice message recording timer
    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            voiceTimerSeconds = 0
            while (isRecordingVoice) {
                delay(1000)
                voiceTimerSeconds++
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080415))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0C21))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.leaveConversation() },
                    modifier = Modifier.testTag("chat_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(modifier = Modifier.size(40.dp)) {
                    AsyncImage(
                        model = currentConv.peerAvatar,
                        contentDescription = currentConv.peerName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    if (currentConv.peerOnlineStatus == "online") {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFF00E676), CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentConv.peerName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (isTyping) "typing..." else currentConv.peerOnlineStatus,
                        color = if (isTyping) Color(0xFF00E676) else Color.White.copy(0.4f),
                        fontSize = 11.sp,
                        fontWeight = if (isTyping) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Video Call icon
                IconButton(
                    onClick = { viewModel.initiateCall(currentConv.peerId, currentConv.peerName, currentConv.peerAvatar, isVoiceOnly = false) },
                    modifier = Modifier.testTag("video_call_icon")
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = Color.White)
                }

                // Voice Call icon
                IconButton(
                    onClick = { viewModel.initiateCall(currentConv.peerId, currentConv.peerName, currentConv.peerAvatar, isVoiceOnly = true) },
                    modifier = Modifier.testTag("voice_call_icon")
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = Color.White)
                }
            }

            // Message logs viewport
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("chat_messages_viewport"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    val isMe = msg.senderId == "me"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        MessageBubble(message = msg, isMe = isMe, viewModel = viewModel)
                    }
                }
            }

            // Bottom drawer panel
            AnimatedVisibility(
                visible = showMorePanel,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                MoreSharingPanel(onSelect = { type, label ->
                    viewModel.sendSharedMedia(type, label)
                    showMorePanel = false
                })
            }

            val aiLoading by viewModel.aiOperationLoading.collectAsState()
            val aiResult by viewModel.aiOperationResult.collectAsState()
            var showAiDialog by remember { mutableStateOf(false) }

            // If an AI operation completes and it's a summary, show a dialog
            LaunchedEffect(aiResult) {
                if (aiResult != null && !aiResult!!.startsWith("Hi!") && !aiResult!!.contains("Rewrite") && !aiResult!!.contains("Translate") && aiResult!!.length > 80) {
                    showAiDialog = true
                }
            }

            if (showAiDialog && aiResult != null) {
                AlertDialog(
                    onDismissRequest = {
                        showAiDialog = false
                        viewModel.clearAiOperationResult()
                    },
                    containerColor = Color(0xFF0F0C21),
                    titleContentColor = Color.White,
                    textContentColor = Color.White.copy(0.8f),
                    title = { Text("Luna AI Chat Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                    text = { Text(aiResult!!) },
                    confirmButton = {
                        TextButton(onClick = {
                            showAiDialog = false
                            viewModel.clearAiOperationResult()
                        }) {
                            Text("Awesome", color = Color(0xFFD0BCFF))
                        }
                    }
                )
            }

            if (!isRecordingVoice) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A071A))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Features",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "LUNA AI:",
                        color = Color(0xFF00E5FF),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    if (aiLoading) {
                        CircularProgressIndicator(
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Thinking...", color = Color.White.copy(0.5f), fontSize = 10.sp)
                    } else {
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (inputText.isNotBlank()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF8E2DE2).copy(0.15f))
                                            .clickable {
                                                viewModel.aiRewriteMessage(inputText) { rewritten ->
                                                    inputText = rewritten
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                            .testTag("ai_rewrite_btn")
                                    ) {
                                        Text("✨ Optimize Style", color = Color(0xFFD0BCFF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                item {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF00E676).copy(0.12f))
                                            .clickable {
                                                viewModel.aiTranslateMessage(inputText, "Spanish") { translated ->
                                                    inputText = translated
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                            .testTag("ai_translate_es_btn")
                                    ) {
                                        Text("🇪🇸 To Spanish", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                item {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF00E5FF).copy(0.12f))
                                            .clickable {
                                                viewModel.aiTranslateMessage(inputText, "French") { translated ->
                                                    inputText = translated
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                            .testTag("ai_translate_fr_btn")
                                    ) {
                                        Text("🇫🇷 To French", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.White.copy(0.04f))
                                            .clickable {
                                                viewModel.aiSummarizeActiveChat(currentConv.id)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                            .testTag("ai_summarize_btn")
                                    ) {
                                        Text("📝 Summarize Chat History", color = Color.White.copy(0.8f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                item {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.White.copy(0.04f))
                                            .clickable {
                                                inputText = "Hi! What kind of future AI AMA rooms are active today? 🧠"
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("💡 Suggest Question", color = Color.White.copy(0.8f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Chat input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0C21))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // More sharing menu
                IconButton(
                    onClick = { showMorePanel = !showMorePanel },
                    modifier = Modifier.testTag("chat_sharing_trigger")
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = Color.White.copy(alpha = 0.7f))
                }

                if (isRecordingVoice) {
                    // Voice notes recording simulator state
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.Red.copy(0.15f))
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.Red, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Recording Voice Memo: ${voiceTimerSeconds}s", color = Color.White, fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = {
                                isRecordingVoice = false
                                viewModel.sendSharedMedia("voice", "🎙️ [Voice Memo - ${voiceTimerSeconds}s]")
                            },
                            modifier = Modifier.testTag("send_voice_memo_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Send Memo", tint = Color.White)
                        }
                    }
                } else {
                    // Regular text input
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Write a message...", color = Color.White.copy(0.4f), fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8E2DE2)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp)
                    )
                }

                if (inputText.isBlank() && !isRecordingVoice) {
                    // Microphone record trigger
                    IconButton(
                        onClick = { isRecordingVoice = true },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(0.05f), CircleShape)
                            .testTag("record_mic_trigger")
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice note", tint = Color.White)
                    }
                } else if (inputText.isNotBlank()) {
                    // Send text button
                    IconButton(
                        onClick = {
                            viewModel.sendTextMessage(inputText)
                            inputText = ""
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(PremiumGradientBrush, CircleShape)
                            .testTag("chat_send_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(message: ChatMessage, isMe: Boolean, viewModel: ConnectLiveViewModel) {
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editInputText by remember { mutableStateOf(message.content) }

    val bubbleShape = if (isMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
    }

    val bubbleBackground = if (isMe) PremiumGradientBrush else Brush.linearGradient(listOf(Color(0xFF1E1938), Color(0xFF1E1938)))

    Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
        Box(
            modifier = Modifier
                .clip(bubbleShape)
                .background(bubbleBackground)
                .combinedClickable(
                    onClick = { /* normal click */ },
                    onLongClick = { showMenu = true }
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .widthIn(max = 280.dp)
        ) {
            Column {
                if (message.replyToContent != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(0.08f), RoundedCornerShape(4.dp))
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "Replying to: ${message.replyToContent}",
                            color = Color.White.copy(0.6f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                when (message.messageType) {
                    "voice" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Waves, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(message.content, color = Color.White, fontSize = 12.sp)
                        }
                    }

                    "location" -> {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Map, contentDescription = "Map", tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Shared Live Location", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFF5252))
                                    Text("Lat: 37.77, Lng: -122.41", color = Color.White.copy(0.6f), fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    "contact" -> {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = "Contact", tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Contact Shared", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Sarah Connor", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("+1 (555) 019-2834", color = Color.White.copy(0.5f), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    "document" -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(0.05f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.Description, contentDescription = "Doc", tint = Color(0xFF00E5FF), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("specification_doc.pdf", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("4.2 MB • PDF", color = Color.White.copy(0.4f), fontSize = 10.sp)
                            }
                            Icon(Icons.Default.Download, contentDescription = "Download", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    "poll" -> {
                        Column {
                            Text("📊 Interactive Poll", fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Which room feature is best?", color = Color.White, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            listOf("1. AI Chat Summaries (42%)", "2. Animated Gift Effects (30%)", "3. Video Call Filters (28%)").forEach { opt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .background(Color.White.copy(0.05f), RoundedCornerShape(4.dp))
                                        .padding(6.dp)
                                ) {
                                    Text(opt, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    "invitation" -> {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, contentDescription = "Invite", tint = Color(0xFFE040FB), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Calendar Invite: Synth Wave", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("August 28, 2026 at 7:00 PM", color = Color.White.copy(0.6f), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    contentPadding = PaddingValues(horizontal = 10.dp),
                                    modifier = Modifier.weight(1f).height(28.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Accept", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                    contentPadding = PaddingValues(horizontal = 10.dp),
                                    modifier = Modifier.weight(1f).height(28.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Ignore", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = message.content,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        if (message.reactions.isNotBlank()) {
            Row(
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.reactions.split(",").forEach { reaction ->
                    val emoji = reaction.substringBefore("_")
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(0.12f), CircleShape)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(emoji, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Just now",
                fontSize = 9.sp,
                color = Color.White.copy(0.3f)
            )
            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Delivered",
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(Color(0xFF0F0C21))
        ) {
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("❤️", "👍", "🔥", "😂", "😮", "🙏").forEach { emoji ->
                            Text(
                                text = emoji,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.addMessageReaction(message, emoji)
                                        showMenu = false
                                    }
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }
                },
                onClick = {}
            )

            Divider(color = Color.White.copy(0.1f))

            if (isMe && message.messageType == "text") {
                DropdownMenuItem(
                    text = { Text("✍️ Edit Message", color = Color.White, fontSize = 12.sp) },
                    onClick = {
                        showEditDialog = true
                        showMenu = false
                    }
                )
            }

            DropdownMenuItem(
                text = { Text("🗑️ Delete Message", color = Color(0xFFFF5252), fontSize = 12.sp) },
                onClick = {
                    viewModel.deleteMessage(message.id)
                    showMenu = false
                }
            )

            DropdownMenuItem(
                text = { Text("⭐ Star Message", color = Color.White, fontSize = 12.sp) },
                onClick = {
                    viewModel.toggleStarMessage(message)
                    showMenu = false
                }
            )
        }

        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                containerColor = Color(0xFF0F0C21),
                titleContentColor = Color.White,
                textContentColor = Color.White,
                title = { Text("Edit Message", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                text = {
                    OutlinedTextField(
                        value = editInputText,
                        onValueChange = { editInputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editInputText.isNotBlank()) {
                                viewModel.editMessage(message, editInputText)
                                showEditDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2))
                    ) {
                        Text("Save Changes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel", color = Color.White.copy(0.6f))
                    }
                }
            )
        }
    }
}

@Composable
fun MoreSharingPanel(onSelect: (String, String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F0C21))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf(
            ShareItem("image", "Image", Icons.Default.Image, Color(0xFFE040FB)),
            ShareItem("document", "Doc", Icons.Default.Description, Color(0xFF00E5FF)),
            ShareItem("location", "Location", Icons.Default.LocationOn, Color(0xFF00E676)),
            ShareItem("contact", "Contact", Icons.Default.Contacts, Color(0xFFFFB300))
        ).forEach { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelect(item.id, "📍 Shared [${item.label}]") }
                    .testTag("share_${item.id}")
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(item.color.copy(0.1f), CircleShape)
                        .border(1.dp, item.color.copy(0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(item.icon, contentDescription = item.label, tint = item.color)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(item.label, color = Color.White.copy(0.6f), fontSize = 11.sp)
            }
        }
    }
}

private data class ShareItem(
    val id: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

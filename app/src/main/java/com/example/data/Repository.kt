package com.example.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Repository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userProfileDao = db.userProfileDao()
    private val postDao = db.postDao()
    private val chatMessageDao = db.chatMessageDao()
    private val conversationDao = db.conversationDao()
    private val voiceRoomDao = db.voiceRoomDao()
    private val callHistoryDao = db.callHistoryDao()
    private val coinTransactionDao = db.coinTransactionDao()
    private val systemLogDao = db.systemLogDao()

    // Flow streams for UI
    val meProfileFlow: Flow<UserProfile?> = userProfileDao.getUserByIdFlow("me")
    val suggestedFriendsFlow: Flow<List<UserProfile>> = userProfileDao.getSuggestedFriendsFlow()
    val blockedUsersFlow: Flow<List<UserProfile>> = userProfileDao.getBlockedUsersFlow()
    val allPostsFlow: Flow<List<Post>> = postDao.getAllPostsFlow()
    val activeConversationsFlow: Flow<List<Conversation>> = conversationDao.getActiveConversationsFlow()
    val archivedConversationsFlow: Flow<List<Conversation>> = conversationDao.getArchivedConversationsFlow()
    val voiceRoomsFlow: Flow<List<VoiceRoom>> = voiceRoomDao.getAllVoiceRoomsFlow()
    val callLogsFlow: Flow<List<CallHistoryEntry>> = callHistoryDao.getAllCallLogsFlow()
    val transactionHistoryFlow: Flow<List<CoinTransaction>> = coinTransactionDao.getAllTransactionsFlow()
    val coinBalanceFlow: Flow<Int?> = coinTransactionDao.getCoinBalanceFlow()
    val systemLogsFlow: Flow<List<SystemLogEntry>> = systemLogDao.getAllLogsFlow()

    init {
        // Seed initial data asynchronously on first startup
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialData()
        }
    }

    private suspend fun seedInitialData() {
        val existingMe = userProfileDao.getUserById("me")
        if (existingMe != null) return // Already seeded

        // 1. Seed self profile ("me")
        val me = UserProfile(
            id = "me",
            name = "Alex Mercer",
            username = "alex_mercer",
            bio = "Digital explorer & creator. Connecting live from New York! 🚀 Let's talk tech, art, and music.",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
            coverUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
            gender = "Non-binary",
            birthday = "1998-05-15",
            country = "United States",
            city = "New York",
            interests = "Technology, Photography, Travel, EDM, Cooking",
            languages = "English, Spanish",
            isVip = false,
            isVerified = true,
            onlineStatus = "online",
            joinDate = "July 2026"
        )
        userProfileDao.insertOrUpdate(me)

        // 2. Seed Suggested Friends (Lively social community)
        val users = listOf(
            UserProfile(
                id = "user_sofia",
                name = "Sofia Rodriguez",
                username = "sofia_r",
                bio = "Singer, songwriter, and world traveler. Join my voice room for acoustic sessions! 🎸🎙️",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
                coverUrl = "https://images.unsplash.com/photo-1557683316-973673baf926?auto=format&fit=crop&w=800&q=80",
                gender = "Female",
                birthday = "2000-11-20",
                country = "Spain",
                city = "Barcelona",
                interests = "Music, Guitar, Hiking, Tapas, Spanish",
                languages = "Spanish, English, French",
                isVip = true,
                isVerified = true,
                isFriend = true,
                isFollowing = true,
                onlineStatus = "online",
                lastSeen = "Active now",
                joinDate = "March 2025"
            ),
            UserProfile(
                id = "user_liam",
                name = "Liam Vance",
                username = "liam_tech",
                bio = "AI Research Engineer. Builder of cool widgets. Always down for chat about future tech. 🧠🤖",
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
                coverUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80",
                gender = "Male",
                birthday = "1995-03-12",
                country = "Canada",
                city = "Toronto",
                interests = "AI, Cyberpunk, Gaming, Espresso, Sci-Fi",
                languages = "English, Japanese",
                isVip = false,
                isVerified = false,
                isFriend = true,
                isFollowing = false,
                onlineStatus = "busy",
                lastSeen = "Active now",
                joinDate = "January 2026"
            ),
            UserProfile(
                id = "user_chloe",
                name = "Chloe Dubois",
                username = "chloe_fashion",
                bio = "Creative director & digital artist. Fashion is my playground. 🎨👗 Curating dreams.",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
                coverUrl = "https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=800&q=80",
                gender = "Female",
                birthday = "1997-09-02",
                country = "France",
                city = "Paris",
                interests = "Design, Styling, Cinema, Photography, Wine",
                languages = "French, English",
                isVip = true,
                isVerified = true,
                isFriend = false,
                isFollowing = true,
                onlineStatus = "offline",
                lastSeen = "2 hours ago",
                joinDate = "September 2024"
            ),
            UserProfile(
                id = "user_hiroshi",
                name = "Hiroshi Tanaka",
                username = "hiro_tokyo",
                bio = "Chef & Restaurateur. Let's talk Ramen, Sushi, and culinary journeys! 🍜🍣 Tokyo vibe.",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
                coverUrl = "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?auto=format&fit=crop&w=800&q=80",
                gender = "Male",
                birthday = "1992-07-25",
                country = "Japan",
                city = "Tokyo",
                interests = "Cooking, Foodie, Travel, Jazz, Sake",
                languages = "Japanese, English",
                isVip = false,
                isVerified = true,
                isFriend = false,
                isFollowing = false,
                onlineStatus = "online",
                lastSeen = "Active now",
                joinDate = "December 2025"
            ),
            UserProfile(
                id = "user_emma",
                name = "Emma Watson",
                username = "emma_wander",
                bio = "Adventure seeker & nature photographer. Off-grid explorer but staying connected. 🎒🌲",
                avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=150&q=80",
                coverUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80",
                gender = "Female",
                birthday = "1999-01-30",
                country = "United Kingdom",
                city = "London",
                interests = "Hiking, Camping, Wildlife, Tea, Yoga",
                languages = "English",
                isVip = true,
                isVerified = false,
                isFriend = false,
                isFollowing = false,
                onlineStatus = "offline",
                lastSeen = "Yesterday",
                joinDate = "February 2026"
            ),
            UserProfile(
                id = "user_gemini",
                name = "Luna AI",
                username = "luna_ai",
                bio = "Your official Gemini-powered smart co-pilot on Lumexa. Ask me anything about translation, coding, or profile recommendations! 🧠✨",
                avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=150&q=80",
                coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80",
                gender = "AI",
                birthday = "2026-01-01",
                country = "Cloud",
                city = "AI Studio",
                interests = "AI, Translation, Coding, Creativity, Music",
                languages = "All Languages",
                isVip = true,
                isVerified = true,
                isFriend = true,
                isFollowing = true,
                onlineStatus = "online",
                lastSeen = "Active now",
                joinDate = "July 2026"
            )
        )
        for (u in users) {
            userProfileDao.insertOrUpdate(u)
        }

        // 3. Seed Voice Rooms (Active rooms)
        val rooms = listOf(
            VoiceRoom(
                id = "room_1",
                title = "🎵 Sofia's Late Night Acoustic Jam",
                hostId = "user_sofia",
                hostName = "Sofia Rodriguez",
                hostAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
                isPrivate = false,
                activeSpeakersCount = 4,
                listenersCount = 87,
                categories = "Music, Chat",
                rankingValue = 950
            ),
            VoiceRoom(
                id = "room_2",
                title = "🌎 Speak English/Spanish - Chill Conversation",
                hostId = "user_hiroshi",
                hostName = "Hiroshi Tanaka",
                hostAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
                isPrivate = false,
                activeSpeakersCount = 6,
                listenersCount = 142,
                categories = "Language, Education",
                rankingValue = 1200
            ),
            VoiceRoom(
                id = "room_3",
                title = "💡 Future of Artificial Intelligence AMA",
                hostId = "user_liam",
                hostName = "Liam Vance",
                hostAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
                isPrivate = false,
                activeSpeakersCount = 3,
                listenersCount = 58,
                categories = "Tech, Startups",
                rankingValue = 820
            ),
            VoiceRoom(
                id = "room_4",
                title = "🔒 VIP Founders Confidential Lounge",
                hostId = "user_chloe",
                hostName = "Chloe Dubois",
                hostAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
                isPrivate = true,
                password = "123",
                activeSpeakersCount = 2,
                listenersCount = 12,
                categories = "Private, VIP Only",
                rankingValue = 310
            )
        )
        for (r in rooms) {
            voiceRoomDao.insertOrUpdate(r)
        }

        // 4. Seed Posts / Social Feed
        val posts = listOf(
            Post(
                id = "post_1",
                authorId = "user_sofia",
                authorName = "Sofia Rodriguez",
                authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
                authorIsVip = true,
                caption = "Sunset on the beach in Barcelona. Writing some new acoustic tracks today. What is your favorite live music instrument? 🌅🎸",
                hashtags = "Music, Acoustic, Travel, SummerVibes",
                imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
                videoUrl = "",
                timestamp = System.currentTimeMillis() - 4 * 3600_000, // 4 hours ago
                likesCount = 342,
                commentsCount = 24,
                isLikedByMe = true,
                isBookmarkedByMe = false,
                pollQuestion = "Which acoustic sound do you prefer?",
                pollOptions = "Classic Guitar,Piano,Violin,Acoustic Drums",
                pollVotes = "182,120,40,24"
            ),
            Post(
                id = "post_2",
                authorId = "user_liam",
                authorName = "Liam Vance",
                authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
                authorIsVip = false,
                caption = "Just finished setting up my local deep learning model. The response times are down to 120ms. Let me know if you want to test it out! 💻🤖📈",
                hashtags = "Tech, Coding, MachineLearning, OpenSource",
                imageUrl = "",
                videoUrl = "",
                timestamp = System.currentTimeMillis() - 8 * 3600_000, // 8 hours ago
                likesCount = 98,
                commentsCount = 7,
                isLikedByMe = false,
                isBookmarkedByMe = true
            ),
            Post(
                id = "post_3",
                authorId = "user_chloe",
                authorName = "Chloe Dubois",
                authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
                authorIsVip = true,
                caption = "Paris Fashion Week prep has been insane, but so worth it. Sneak peek of our cyberpunk-inspired ready-to-wear collection. 🇫🇷🖤👓 Let me know your thoughts!",
                hashtags = "Fashion, Cyberpunk, ArtDirection, Paris",
                imageUrl = "https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=800&q=80",
                videoUrl = "",
                timestamp = System.currentTimeMillis() - 1 * 86400_000, // 1 day ago
                likesCount = 1240,
                commentsCount = 89,
                isLikedByMe = false,
                isBookmarkedByMe = false
            )
        )
        for (p in posts) {
            postDao.insertOrUpdate(p)
        }

        // 5. Seed Post Comments
        val comments = listOf(
            Comment(
                id = "comment_1",
                postId = "post_1",
                authorId = "me",
                authorName = "Alex Mercer",
                authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
                content = "This view is incredible! Can't wait to hear the new tracks, Sofia! Classic guitar definitely wins for me.",
                timestamp = System.currentTimeMillis() - 3 * 3600_000
            ),
            Comment(
                id = "comment_2",
                postId = "post_1",
                authorId = "user_liam",
                authorName = "Liam Vance",
                authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
                content = "Sounds super clean. Have you tried recording with dynamic spatial processing?",
                timestamp = System.currentTimeMillis() - 2 * 3600_000
            ),
            Comment(
                id = "comment_3",
                postId = "post_2",
                authorId = "me",
                authorName = "Alex Mercer",
                authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
                content = "120ms locally is huge! What card are you running this on?",
                timestamp = System.currentTimeMillis() - 6 * 3600_000
            )
        )
        for (c in comments) {
            postDao.insertComment(c)
        }

        // 6. Seed Chats / Conversations & Messages
        val conversations = listOf(
            Conversation(
                id = "chat_sofia",
                peerId = "user_sofia",
                peerName = "Sofia Rodriguez",
                peerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
                peerOnlineStatus = "online",
                lastMessage = "Thanks for joining the acoustic live stream earlier! ✨ Keep rocking.",
                lastMessageTime = System.currentTimeMillis() - 30 * 60_000,
                unreadCount = 2,
                isArchived = false,
                isMuted = false
            ),
            Conversation(
                id = "chat_liam",
                peerId = "user_liam",
                peerName = "Liam Vance",
                peerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
                peerOnlineStatus = "busy",
                lastMessage = "Sent you the code snippet on GitHub. Let me know if you run into syntax errors.",
                lastMessageTime = System.currentTimeMillis() - 1 * 3600_000,
                unreadCount = 0,
                isArchived = false,
                isMuted = false
            ),
            Conversation(
                id = "chat_chloe",
                peerId = "user_chloe",
                peerName = "Chloe Dubois",
                peerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
                peerOnlineStatus = "offline",
                lastMessage = "Are you available for a design review call tomorrow at 10 AM Paris time?",
                lastMessageTime = System.currentTimeMillis() - 5 * 3600_000,
                unreadCount = 0,
                isArchived = false,
                isMuted = true
            ),
            Conversation(
                id = "chat_gemini",
                peerId = "user_gemini",
                peerName = "Luna AI",
                peerAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=150&q=80",
                peerOnlineStatus = "online",
                lastMessage = "Hi! I am Luna, your Gemini-powered personal assistant on Lumexa. How can I assist you today? 🧠✨",
                lastMessageTime = System.currentTimeMillis() - 5 * 60_000,
                unreadCount = 1,
                isArchived = false,
                isMuted = false
            )
        )
        for (cv in conversations) {
            conversationDao.insertOrUpdate(cv)
        }

        val messages = listOf(
            ChatMessage(
                id = "msg_s1",
                conversationId = "chat_sofia",
                senderId = "me",
                content = "Hey Sofia! Your stream earlier was fantastic. Really loved the acoustic guitar cover.",
                messageType = "text",
                timestamp = System.currentTimeMillis() - 40 * 60_000,
                isSeenByRecipient = true
            ),
            ChatMessage(
                id = "msg_s2",
                conversationId = "chat_sofia",
                senderId = "user_sofia",
                content = "Thanks for joining the acoustic live stream earlier! ✨ Keep rocking.",
                messageType = "text",
                timestamp = System.currentTimeMillis() - 30 * 60_000,
                isSeenByRecipient = false,
                isDelivered = true
            ),
            ChatMessage(
                id = "msg_l1",
                conversationId = "chat_liam",
                senderId = "user_liam",
                content = "Hi Alex, here is the neural network setup.",
                messageType = "text",
                timestamp = System.currentTimeMillis() - 70 * 60_000,
                isSeenByRecipient = true
            ),
            ChatMessage(
                id = "msg_l2",
                conversationId = "chat_liam",
                senderId = "me",
                content = "Perfect. I am checking the memory usage right now.",
                messageType = "text",
                timestamp = System.currentTimeMillis() - 65 * 60_000,
                isSeenByRecipient = true
            ),
            ChatMessage(
                id = "msg_l3",
                conversationId = "chat_liam",
                senderId = "user_liam",
                content = "Sent you the code snippet on GitHub. Let me know if you run into syntax errors.",
                messageType = "text",
                timestamp = System.currentTimeMillis() - 60 * 60_000,
                isSeenByRecipient = true
            ),
            ChatMessage(
                id = "msg_g1",
                conversationId = "chat_gemini",
                senderId = "user_gemini",
                content = "Hi! I am Luna, your Gemini-powered personal assistant on Lumexa. How can I assist you today? 🧠✨",
                messageType = "text",
                timestamp = System.currentTimeMillis() - 5 * 60_000,
                isSeenByRecipient = false,
                isDelivered = true
            )
        )
        for (m in messages) {
            chatMessageDao.insertMessage(m)
        }

        // 7. Seed Call Logs
        val callLogs = listOf(
            CallHistoryEntry(
                peerId = "user_sofia",
                peerName = "Sofia Rodriguez",
                peerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
                isVoiceOnly = false,
                isIncoming = true,
                callStatus = "connected",
                durationSeconds = 485,
                timestamp = System.currentTimeMillis() - 2 * 3600_000
            ),
            CallHistoryEntry(
                peerId = "user_liam",
                peerName = "Liam Vance",
                peerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
                isVoiceOnly = true,
                isIncoming = false,
                callStatus = "connected",
                durationSeconds = 120,
                timestamp = System.currentTimeMillis() - 5 * 3600_000
            ),
            CallHistoryEntry(
                peerId = "user_chloe",
                peerName = "Chloe Dubois",
                peerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
                isVoiceOnly = false,
                isIncoming = true,
                callStatus = "missed",
                durationSeconds = 0,
                timestamp = System.currentTimeMillis() - 1 * 86400_000
            )
        )
        for (cl in callLogs) {
            callHistoryDao.insertCallLog(cl)
        }

        // 8. Seed Coin Balance / Wallet Transactions
        val transactions = listOf(
            CoinTransaction(
                type = "purchase",
                coinAmount = 1500,
                description = "Purchased Gold Coin Bundle (Premium Booster)",
                timestamp = System.currentTimeMillis() - 3 * 86400_000
            ),
            CoinTransaction(
                type = "spend_gift",
                coinAmount = 500,
                description = "Sent 'Animated Yacht' gift to Sofia Rodriguez",
                timestamp = System.currentTimeMillis() - 2 * 3600_000
            ),
            CoinTransaction(
                type = "reward_receive",
                coinAmount = 50,
                description = "Daily active login bonus",
                timestamp = System.currentTimeMillis() - 4 * 3600_000
            )
        )
        for (tx in transactions) {
            coinTransactionDao.insertTransaction(tx)
        }

        // 9. Seed System logs (Admin audit)
        val logs = listOf(
            SystemLogEntry(
                action = "SECURITY_SCAN",
                details = "Anti-spam validation completed successfully. 0 threats detected.",
                timestamp = System.currentTimeMillis() - 24 * 3600_000
            ),
            SystemLogEntry(
                action = "USER_VERIFICATION",
                details = "Verified user profile Hiroshi Tanaka (hiro_tokyo) after manual review.",
                timestamp = System.currentTimeMillis() - 12 * 3600_000
            ),
            SystemLogEntry(
                action = "PAYMENT_SYNC",
                details = "VIP subscription plan database synchronized.",
                timestamp = System.currentTimeMillis() - 2 * 3600_000
            )
        )
        for (lg in logs) {
            systemLogDao.insertLog(lg)
        }
    }

    // Interactive updates
    suspend fun updateMeProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        userProfileDao.insertOrUpdate(profile)
    }

    suspend fun sendChatMessage(conversationId: String, content: String, messageType: String = "text", mediaUrl: String = ""): ChatMessage = withContext(Dispatchers.IO) {
        val conv = conversationDao.getConversationById(conversationId)
        val msgId = "msg_${System.currentTimeMillis()}"
        val message = ChatMessage(
            id = msgId,
            conversationId = conversationId,
            senderId = "me",
            content = content,
            messageType = messageType,
            mediaUrl = mediaUrl,
            timestamp = System.currentTimeMillis(),
            isSeenByRecipient = false,
            isDelivered = true
        )
        chatMessageDao.insertMessage(message)

        if (conv != null) {
            val updatedConv = conv.copy(
                lastMessage = if (messageType == "text") content else "[$messageType shared]",
                lastMessageTime = message.timestamp
            )
            conversationDao.insertOrUpdate(updatedConv)
        }
        message
    }

    suspend fun clearUnread(conversationId: String) = withContext(Dispatchers.IO) {
        conversationDao.clearUnreadCount(conversationId)
    }

    suspend fun editChatMessage(messageId: String, newContent: String) = withContext(Dispatchers.IO) {
        val messages = chatMessageDao.getMessagesForConversationFlow("").firstOrNull() ?: emptyList()
        // Note: we can queries messages directly or if we have the messageId, we can just load the list of messages in ViewModel and re-insert the updated ChatMessage.
    }

    suspend fun saveChatMessage(message: ChatMessage) = withContext(Dispatchers.IO) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun deleteChatMessage(messageId: String) = withContext(Dispatchers.IO) {
        chatMessageDao.deleteMessage(messageId)
    }

    suspend fun updateConversation(conv: Conversation) = withContext(Dispatchers.IO) {
        conversationDao.insertOrUpdate(conv)
    }

    suspend fun insertPost(caption: String, hashtags: String, imageUrl: String, pollQ: String = "", pollO: String = "") = withContext(Dispatchers.IO) {
        val pId = "post_${System.currentTimeMillis()}"
        val post = Post(
            id = pId,
            authorId = "me",
            authorName = "Alex Mercer",
            authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
            authorIsVip = false,
            caption = caption,
            hashtags = hashtags,
            imageUrl = imageUrl,
            videoUrl = "",
            timestamp = System.currentTimeMillis(),
            likesCount = 0,
            commentsCount = 0,
            isLikedByMe = false,
            isBookmarkedByMe = false,
            pollQuestion = pollQ,
            pollOptions = pollO,
            pollVotes = if (pollO.isNotEmpty()) pollO.split(",").joinToString(",") { "0" } else ""
        )
        postDao.insertOrUpdate(post)
    }

    suspend fun toggleLikePost(postId: String) = withContext(Dispatchers.IO) {
        val post = postDao.getPostById(postId)
        if (post != null) {
            val isLikedNow = !post.isLikedByMe
            val newCount = post.likesCount + (if (isLikedNow) 1 else -1)
            val updated = post.copy(
                isLikedByMe = isLikedNow,
                likesCount = newCount
            )
            postDao.insertOrUpdate(updated)
        }
    }

    suspend fun addComment(postId: String, text: String) = withContext(Dispatchers.IO) {
        val commentId = "comment_${System.currentTimeMillis()}"
        val comment = Comment(
            id = commentId,
            postId = postId,
            authorId = "me",
            authorName = "Alex Mercer",
            authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
            content = text,
            timestamp = System.currentTimeMillis()
        )
        postDao.insertComment(comment)

        val post = postDao.getPostById(postId)
        if (post != null) {
            postDao.insertOrUpdate(post.copy(commentsCount = post.commentsCount + 1))
        }
    }

    suspend fun addTransaction(type: String, amount: Int, desc: String) = withContext(Dispatchers.IO) {
        val tx = CoinTransaction(
            type = type,
            coinAmount = amount,
            description = desc,
            timestamp = System.currentTimeMillis()
        )
        coinTransactionDao.insertTransaction(tx)
    }

    suspend fun buyCoins(amount: Int, costDollars: Double) = withContext(Dispatchers.IO) {
        val tx = CoinTransaction(
            type = "purchase",
            coinAmount = amount,
            description = "Purchased $amount Coins Bundle ($$costDollars)",
            timestamp = System.currentTimeMillis()
        )
        coinTransactionDao.insertTransaction(tx)

        systemLogDao.insertLog(SystemLogEntry(
            action = "COIN_PURCHASE",
            details = "User Alex purchased $amount coins for $$costDollars.",
            timestamp = System.currentTimeMillis()
        ))
    }

    suspend fun sendGift(peerId: String, peerName: String, coinCost: Int, giftName: String) = withContext(Dispatchers.IO) {
        val tx = CoinTransaction(
            type = "spend_gift",
            coinAmount = coinCost,
            description = "Sent '$giftName' gift to $peerName",
            timestamp = System.currentTimeMillis()
        )
        coinTransactionDao.insertTransaction(tx)

        systemLogDao.insertLog(SystemLogEntry(
            action = "VIRTUAL_GIFT",
            details = "Sent $giftName ($coinCost coins) to $peerName.",
            timestamp = System.currentTimeMillis()
        ))
    }

    suspend fun toggleFollowUser(userId: String) = withContext(Dispatchers.IO) {
        val u = userProfileDao.getUserById(userId)
        if (u != null) {
            val updated = u.copy(isFollowing = !u.isFollowing)
            userProfileDao.insertOrUpdate(updated)
        }
    }

    suspend fun toggleBlockUser(userId: String) = withContext(Dispatchers.IO) {
        val u = userProfileDao.getUserById(userId)
        if (u != null) {
            val updated = u.copy(isBlocked = !u.isBlocked, isFriend = false, isFollowing = false)
            userProfileDao.insertOrUpdate(updated)

            systemLogDao.insertLog(SystemLogEntry(
                action = if (updated.isBlocked) "USER_BLOCKED" else "USER_UNBLOCKED",
                details = "User ${updated.name} updated block status.",
                timestamp = System.currentTimeMillis()
            ))
        }
    }

    suspend fun deletePost(postId: String) = withContext(Dispatchers.IO) {
        postDao.deletePostById(postId)
    }

    suspend fun logCall(peerId: String, name: String, avatar: String, isVoice: Boolean, isIncoming: Boolean, status: String, duration: Int) = withContext(Dispatchers.IO) {
        val call = CallHistoryEntry(
            peerId = peerId,
            peerName = name,
            peerAvatar = avatar,
            isVoiceOnly = isVoice,
            isIncoming = isIncoming,
            callStatus = status,
            durationSeconds = duration,
            timestamp = System.currentTimeMillis()
        )
        callHistoryDao.insertCallLog(call)
    }

    suspend fun registerSystemLog(action: String, details: String) = withContext(Dispatchers.IO) {
        systemLogDao.insertLog(SystemLogEntry(
            action = action,
            details = details,
            timestamp = System.currentTimeMillis()
        ))
    }

    suspend fun voteOnPoll(postId: String, optionIndex: Int) = withContext(Dispatchers.IO) {
        val post = postDao.getPostById(postId)
        if (post != null) {
            val votesList = post.pollVotes.split(",").map { it.toIntOrNull() ?: 0 }.toMutableList()
            if (optionIndex in 0 until votesList.size) {
                votesList[optionIndex] = votesList[optionIndex] + 1
                val updated = post.copy(pollVotes = votesList.joinToString(","))
                postDao.insertOrUpdate(updated)
            }
        }
    }

    suspend fun upgradeToVip() = withContext(Dispatchers.IO) {
        val me = userProfileDao.getUserById("me")
        if (me != null) {
            userProfileDao.insertOrUpdate(me.copy(isVip = true))

            systemLogDao.insertLog(SystemLogEntry(
                action = "VIP_SUBSCRIBE",
                details = "User Alex Mercer upgraded to VIP Membership.",
                timestamp = System.currentTimeMillis()
            ))
        }
    }

    suspend fun updateUserProfile(name: String, bio: String, avatarUrl: String) = withContext(Dispatchers.IO) {
        val me = userProfileDao.getUserById("me")
        if (me != null) {
            val updated = me.copy(
                name = name,
                bio = bio,
                avatarUrl = if (avatarUrl.isNotBlank()) avatarUrl else me.avatarUrl
            )
            userProfileDao.insertOrUpdate(updated)
        }
    }

    suspend fun deleteMyAccount() = withContext(Dispatchers.IO) {
        userProfileDao.deleteUserById("me")
    }

    suspend fun getMessagesForConversation(conversationId: String): List<ChatMessage> = withContext(Dispatchers.IO) {
        chatMessageDao.getMessagesForConversation(conversationId)
    }

    // Chat bot/auto reply simulator for dynamic chat feel
    suspend fun triggerBotReply(conversationId: String, peerName: String) = withContext(Dispatchers.IO) {
        val replyContent = if (conversationId == "chat_gemini") {
            try {
                // Get chat history for Gemini context
                val history = chatMessageDao.getMessagesForConversation(conversationId)
                    .sortedBy { it.timestamp }
                    .takeLast(10)

                val promptBuilder = java.lang.StringBuilder()
                promptBuilder.append("You are Luna, a highly intelligent and friendly Gemini AI co-pilot built inside Lumexa. ")
                promptBuilder.append("The user chatting with you is named Alex. Help them explore Lumexa features, translate messages, summarize things, or just chat pleasantly. ")
                promptBuilder.append("Here is the chat history:\n")
                for (msg in history) {
                    val sender = if (msg.senderId == "me") "Alex" else "Luna AI"
                    promptBuilder.append("$sender: ${msg.content}\n")
                }
                promptBuilder.append("Generate Luna's next helpful response. Keep it friendly, premium, and concise (under 3 sentences):")
                GeminiClient.generateContent(promptBuilder.toString())
            } catch (e: java.lang.Exception) {
                "Hi! I'm here to assist. Ask me anything about translation, profile optimization, or system features! 🧠✨"
            }
        } else {
            // Simple automated replies to make messaging feel incredibly live and functional!
            val replies = listOf(
                "That sounds amazing! Let's definitely catch up on a video call later today.",
                "I absolutely agree. Let's start a voice room tonight to discuss this with Liam as well!",
                "Haha that's hilarious! 😹 Did you see my latest post about the design mockups?",
                "Can you call me? I am active and free right now!",
                "Sending you lots of positive vibes! ✨ See you on Lumexa."
            )
            replies.random()
        }

        val msgId = "msg_reply_${System.currentTimeMillis()}"

        val message = ChatMessage(
            id = msgId,
            conversationId = conversationId,
            senderId = conversationId.replace("chat_", "user_"),
            content = replyContent,
            messageType = "text",
            timestamp = System.currentTimeMillis(),
            isSeenByRecipient = false,
            isDelivered = true
        )
        chatMessageDao.insertMessage(message)

        val conv = conversationDao.getConversationById(conversationId)
        if (conv != null) {
            val updatedConv = conv.copy(
                lastMessage = replyContent,
                lastMessageTime = message.timestamp,
                unreadCount = conv.unreadCount + 1
            )
            conversationDao.insertOrUpdate(updatedConv)
        }
    }

    fun getCommentsForPostFlow(postId: String): Flow<List<Comment>> {
        return postDao.getCommentsForPostFlow(postId)
    }

    fun getMessagesForConversationFlow(conversationId: String): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessagesForConversationFlow(conversationId)
    }
}

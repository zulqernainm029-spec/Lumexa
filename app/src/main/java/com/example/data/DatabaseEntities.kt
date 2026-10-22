package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val bio: String,
    val avatarUrl: String,
    val coverUrl: String,
    val gender: String,
    val birthday: String,
    val country: String,
    val city: String,
    val interests: String, // Comma separated
    val languages: String,
    val isVip: Boolean = false,
    val isVerified: Boolean = false,
    val isFriend: Boolean = false,
    val isFollowing: Boolean = false,
    val isBlocked: Boolean = false,
    val isMuted: Boolean = false,
    val isFavorite: Boolean = false,
    val onlineStatus: String = "offline", // online, offline, busy
    val lastSeen: String = "",
    val joinDate: String = ""
)

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val authorIsVip: Boolean,
    val caption: String,
    val hashtags: String, // Comma separated
    val imageUrl: String, // Empty if no image
    val videoUrl: String, // Empty if no video
    val timestamp: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val isLikedByMe: Boolean = false,
    val isBookmarkedByMe: Boolean = false,
    val pollQuestion: String = "",
    val pollOptions: String = "", // Comma separated, e.g., "Yes,No"
    val pollVotes: String = "" // Comma separated vote counts, e.g., "12,4"
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: Long
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val messageType: String, // text, image, video, voice, document, location, contact
    val mediaUrl: String = "",
    val timestamp: Long,
    val isSeenByRecipient: Boolean = false,
    val isDelivered: Boolean = true,
    val replyToId: String? = null,
    val replyToContent: String? = null,
    val isStarred: Boolean = false,
    val reactions: String = "" // Comma separated list of reactions: "emoji_senderId"
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String,
    val peerId: String,
    val peerName: String,
    val peerAvatar: String,
    val peerOnlineStatus: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val wallpaper: String? = null
)

@Entity(tableName = "voice_rooms")
data class VoiceRoom(
    @PrimaryKey val id: String,
    val title: String,
    val hostId: String,
    val hostName: String,
    val hostAvatar: String,
    val isPrivate: Boolean = false,
    val password: String? = null,
    val activeSpeakersCount: Int = 1,
    val listenersCount: Int = 1,
    val categories: String = "",
    val rankingValue: Int = 0
)

@Entity(tableName = "call_history")
data class CallHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val peerId: String,
    val peerName: String,
    val peerAvatar: String,
    val isVoiceOnly: Boolean,
    val isIncoming: Boolean,
    val callStatus: String, // connected, missed, rejected, no_answer
    val durationSeconds: Int,
    val timestamp: Long
)

@Entity(tableName = "coin_transactions")
data class CoinTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // purchase, spend_gift, reward_receive, withdraw
    val coinAmount: Int,
    val description: String,
    val timestamp: Long
)

@Entity(tableName = "system_logs")
data class SystemLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val action: String,
    val details: String,
    val timestamp: Long
)

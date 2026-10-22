package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    suspend fun getUserById(userId: String): UserProfile?

    @Query("SELECT * FROM user_profiles WHERE id != 'me' AND isBlocked = 0")
    fun getSuggestedFriendsFlow(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE isBlocked = 1")
    fun getBlockedUsersFlow(): Flow<List<UserProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)

    @Update
    suspend fun update(profile: UserProfile)

    @Query("DELETE FROM user_profiles WHERE id = :userId")
    suspend fun deleteUserById(userId: String)
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPostsFlow(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): Post?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(post: Post)

    @Update
    suspend fun update(post: Post)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPostFlow(postId: String): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversationFlow(conversationId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    suspend fun getMessagesForConversation(conversationId: String): List<ChatMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("UPDATE chat_messages SET isSeenByRecipient = 1 WHERE conversationId = :conversationId AND senderId != 'me'")
    suspend fun markConversationMessagesAsSeen(conversationId: String)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE isArchived = 0 ORDER BY lastMessageTime DESC")
    fun getActiveConversationsFlow(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE isArchived = 1 ORDER BY lastMessageTime DESC")
    fun getArchivedConversationsFlow(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: String): Conversation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(conversation: Conversation)

    @Update
    suspend fun update(conversation: Conversation)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :conversationId")
    suspend fun clearUnreadCount(conversationId: String)

    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversation(conversationId: String)
}

@Dao
interface VoiceRoomDao {
    @Query("SELECT * FROM voice_rooms ORDER BY rankingValue DESC")
    fun getAllVoiceRoomsFlow(): Flow<List<VoiceRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(room: VoiceRoom)

    @Query("DELETE FROM voice_rooms WHERE id = :roomId")
    suspend fun deleteRoom(roomId: String)
}

@Dao
interface CallHistoryDao {
    @Query("SELECT * FROM call_history ORDER BY timestamp DESC")
    fun getAllCallLogsFlow(): Flow<List<CallHistoryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(entry: CallHistoryEntry)

    @Query("DELETE FROM call_history")
    suspend fun clearAllCallLogs()
}

@Dao
interface CoinTransactionDao {
    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<CoinTransaction>>

    @Query("SELECT SUM(CASE WHEN type = 'purchase' OR type = 'reward_receive' THEN coinAmount ELSE -coinAmount END) FROM coin_transactions")
    fun getCoinBalanceFlow(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CoinTransaction)
}

@Dao
interface SystemLogDao {
    @Query("SELECT * FROM system_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<SystemLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(entry: SystemLogEntry)

    @Query("DELETE FROM system_logs")
    suspend fun clearAllLogs()
}

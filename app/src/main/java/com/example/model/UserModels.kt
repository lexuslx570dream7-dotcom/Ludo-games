package com.example.model

data class UserProfile(
  val id: String = "LK-84920",
  val username: String = "Ayan KN",
  val avatarResId: Int = 0,
  val level: Int = 12,
  val currentXp: Int = 1420,
  val maxXp: Int = 2000,
  val coins: Long = 2000L,
  val gems: Int = 4,
  val totalGames: Int = 184,
  val wins: Int = 118,
  val losses: Int = 66,
  val currentStreak: Int = 4,
  val bestStreak: Int = 11,
  val totalCaptures: Int = 342,
  val frameType: AvatarFrame = AvatarFrame.ROYAL_GOLD,
  val musicEnabled: Boolean = true,
  val soundEnabled: Boolean = true,
  val vibrationEnabled: Boolean = true,
) {
  val winRate: Int
    get() = if (totalGames > 0) ((wins.toDouble() / totalGames) * 100).toInt() else 0
}

enum class AvatarFrame {
  GREEN_GLOW,
  ROYAL_GOLD,
  DRAGON_MYSTIC,
  RACCOON_NATURE,
  RUBY_CROWN
}

data class Friend(
  val id: String,
  val username: String,
  val level: Int,
  val isOnline: Boolean,
  val inGame: Boolean = false,
  val avatarIndex: Int = 0,
  val frame: AvatarFrame = AvatarFrame.GREEN_GLOW
)

data class FriendRequest(
  val id: String,
  val senderId: String,
  val senderName: String,
  val senderLevel: Int,
  val timestamp: String = "Just now"
)

enum class MessageType {
  TEXT,
  VOICE,
  IMAGE
}

data class ChatMessage(
  val id: String,
  val senderId: String,
  val senderName: String,
  val text: String,
  val timestamp: String,
  val isMine: Boolean,
  val type: MessageType = MessageType.TEXT,
  val voiceDurationSec: Int = 0,
  val imageUrl: String? = null
)

data class Tournament(
  val id: String,
  val title: String,
  val entryFeeCoins: Long,
  val prizeCoins: Long,
  val prizeGems: Int,
  val participantsCount: Int,
  val maxParticipants: Int = 16,
  val startTimeFormatted: String,
  val status: TournamentStatus = TournamentStatus.REGISTRATION_OPEN,
  val isJoined: Boolean = false
)

enum class TournamentStatus {
  LIVE,
  REGISTRATION_OPEN,
  STARTING_SOON,
  COMPLETED
}

data class Achievement(
  val id: String,
  val title: String,
  val description: String,
  val currentProgress: Int,
  val maxProgress: Int,
  val rewardCoins: Long,
  val rewardGems: Int,
  val isClaimed: Boolean = false
) {
  val isUnlocked: Boolean
    get() = currentProgress >= maxProgress
}

data class NotificationItem(
  val id: String,
  val title: String,
  val message: String,
  val timeAgo: String,
  val isRead: Boolean = false
)

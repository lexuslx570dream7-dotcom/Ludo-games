package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Achievement
import com.example.model.AvatarFrame
import com.example.model.ChatMessage
import com.example.model.Friend
import com.example.model.FriendRequest
import com.example.model.MessageType
import com.example.model.NotificationItem
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class GameRepository(context: Context) {

  private val prefs: SharedPreferences = context.getSharedPreferences("ludo_game_prefs", Context.MODE_PRIVATE)

  private val _userProfile = MutableStateFlow(
    UserProfile(
      id = "LK-84920",
      username = "Ayan KN",
      level = 12,
      currentXp = 1420,
      maxXp = 2000,
      coins = prefs.getLong("coins", 2000L),
      gems = prefs.getInt("gems", 4),
      totalGames = prefs.getInt("totalGames", 184),
      wins = prefs.getInt("wins", 118),
      losses = prefs.getInt("losses", 66),
      currentStreak = prefs.getInt("currentStreak", 4),
      bestStreak = prefs.getInt("bestStreak", 11),
      totalCaptures = prefs.getInt("totalCaptures", 342),
      frameType = AvatarFrame.ROYAL_GOLD,
      musicEnabled = prefs.getBoolean("musicEnabled", true),
      soundEnabled = prefs.getBoolean("soundEnabled", true),
      vibrationEnabled = prefs.getBoolean("vibrationEnabled", true)
    )
  )
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _friends = MutableStateFlow(
    listOf(
      Friend("FR-01", "Ali", 14, isOnline = true, inGame = true, frame = AvatarFrame.RACCOON_NATURE),
      Friend("FR-02", "Ayan", 19, isOnline = true, inGame = false, frame = AvatarFrame.DRAGON_MYSTIC),
      Friend("FR-03", "PabLou D Z", 9, isOnline = false, inGame = false, frame = AvatarFrame.RUBY_CROWN),
      Friend("FR-04", "Zara Queen", 22, isOnline = true, inGame = false, frame = AvatarFrame.ROYAL_GOLD),
      Friend("FR-05", "Rashid 007", 11, isOnline = false, inGame = false, frame = AvatarFrame.GREEN_GLOW)
    )
  )
  val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

  private val _friendRequests = MutableStateFlow(
    listOf(
      FriendRequest("REQ-1", "USR-5501", "KingArthur_99", 15, "5m ago"),
      FriendRequest("REQ-2", "USR-9923", "Princess_Mia", 8, "2h ago")
    )
  )
  val friendRequests: StateFlow<List<FriendRequest>> = _friendRequests.asStateFlow()

  private val _chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(
    mapOf(
      "FR-01" to listOf(
        ChatMessage("M1", "FR-01", "Ali", "Hey! Ready for a 4-player match?", "12:30 PM", isMine = false),
        ChatMessage("M2", "LK-84920", "Ayan KN", "Yeah, joining the room now!", "12:31 PM", isMine = true),
        ChatMessage("M3", "FR-01", "Ali", "Great, let's win this!", "12:32 PM", isMine = false, type = MessageType.VOICE, voiceDurationSec = 4)
      ),
      "FR-02" to listOf(
        ChatMessage("M4", "FR-02", "Ayan", "Bro check out my new Dragon frame!", "11:15 AM", isMine = false),
        ChatMessage("M5", "LK-84920", "Ayan KN", "Looks super sick! 🔥", "11:18 AM", isMine = true)
      )
    )
  )
  val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()

  private val _tournaments = MutableStateFlow(
    listOf(
      Tournament(
        id = "TOURN-1",
        title = "Grand Master Cup",
        entryFeeCoins = 1000L,
        prizeCoins = 15000L,
        prizeGems = 20,
        participantsCount = 12,
        maxParticipants = 16,
        startTimeFormatted = "Starts in 14m",
        status = TournamentStatus.REGISTRATION_OPEN,
        isJoined = false
      ),
      Tournament(
        id = "TOURN-2",
        title = "Royal Diamond Arena",
        entryFeeCoins = 5000L,
        prizeCoins = 80000L,
        prizeGems = 100,
        participantsCount = 16,
        maxParticipants = 16,
        startTimeFormatted = "LIVE NOW",
        status = TournamentStatus.LIVE,
        isJoined = true
      ),
      Tournament(
        id = "TOURN-3",
        title = "Weekend Warriors Clash",
        entryFeeCoins = 500L,
        prizeCoins = 7500L,
        prizeGems = 10,
        participantsCount = 6,
        maxParticipants = 16,
        startTimeFormatted = "Starts at 6:00 PM",
        status = TournamentStatus.REGISTRATION_OPEN,
        isJoined = false
      )
    )
  )
  val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

  private val _achievements = MutableStateFlow(
    listOf(
      Achievement("ACH-1", "First Win", "Win your first Ludo match", 1, 1, 500L, 2, isClaimed = true),
      Achievement("ACH-2", "10 Wins", "Achieve 10 total victories", 10, 10, 1500L, 5, isClaimed = true),
      Achievement("ACH-3", "50 Wins", "Achieve 50 total victories", 50, 50, 5000L, 15, isClaimed = false),
      Achievement("ACH-4", "100 Wins", "Achieve 100 total victories", 118, 100, 10000L, 30, isClaimed = false),
      Achievement("ACH-5", "Tournament Champion", "Win any official tournament", 1, 1, 8000L, 25, isClaimed = false),
      Achievement("ACH-6", "Team Master", "Win 10 matches in 2v2 Team mode", 7, 10, 3000L, 10, isClaimed = false),
      Achievement("ACH-7", "VIP High Roller", "Play 3 matches in VIP Room", 1, 3, 15000L, 50, isClaimed = false),
      Achievement("ACH-8", "Capture King", "Capture 200 opponent tokens", 342, 200, 4000L, 15, isClaimed = false)
    )
  )
  val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

  private val _notifications = MutableStateFlow(
    listOf(
      NotificationItem("NOTIF-1", "Daily Reward Ready!", "Claim your 1,000 free coins and 2 gems today.", "10m ago", isRead = false),
      NotificationItem("NOTIF-2", "Tournament Starting", "Grand Master Cup registration closes in 14 minutes.", "30m ago", isRead = false),
      NotificationItem("NOTIF-3", "Friend Request", "KingArthur_99 sent you a friend request.", "1h ago", isRead = true),
      NotificationItem("NOTIF-4", "Victory Bonus", "You earned 1,000 coins from your last 4-player win!", "2h ago", isRead = true)
    )
  )
  val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

  // Room Codes storage
  private val activePrivateRooms = mutableMapOf<String, Long>()

  fun addCoins(amount: Long) {
    val newCoins = _userProfile.value.coins + amount
    prefs.edit().putLong("coins", newCoins).apply()
    _userProfile.value = _userProfile.value.copy(coins = newCoins)
  }

  fun spendCoins(amount: Long): Boolean {
    if (_userProfile.value.coins >= amount) {
      val newCoins = _userProfile.value.coins - amount
      prefs.edit().putLong("coins", newCoins).apply()
      _userProfile.value = _userProfile.value.copy(coins = newCoins)
      return true
    }
    return false
  }

  fun addGems(amount: Int) {
    val newGems = _userProfile.value.gems + amount
    prefs.edit().putInt("gems", newGems).apply()
    _userProfile.value = _userProfile.value.copy(gems = newGems)
  }

  fun spendGems(amount: Int): Boolean {
    if (_userProfile.value.gems >= amount) {
      val newGems = _userProfile.value.gems - amount
      prefs.edit().putInt("gems", newGems).apply()
      _userProfile.value = _userProfile.value.copy(gems = newGems)
      return true
    }
    return false
  }

  fun toggleMusic(enabled: Boolean) {
    prefs.edit().putBoolean("musicEnabled", enabled).apply()
    _userProfile.value = _userProfile.value.copy(musicEnabled = enabled)
  }

  fun toggleSound(enabled: Boolean) {
    prefs.edit().putBoolean("soundEnabled", enabled).apply()
    _userProfile.value = _userProfile.value.copy(soundEnabled = enabled)
  }

  fun toggleVibration(enabled: Boolean) {
    prefs.edit().putBoolean("vibrationEnabled", enabled).apply()
    _userProfile.value = _userProfile.value.copy(vibrationEnabled = enabled)
  }

  fun updateUsername(newName: String) {
    if (newName.isNotBlank()) {
      prefs.edit().putString("username", newName).apply()
      _userProfile.value = _userProfile.value.copy(username = newName)
    }
  }

  fun recordMatchResult(won: Boolean, captures: Int) {
    val prev = _userProfile.value
    val newGames = prev.totalGames + 1
    val newWins = if (won) prev.wins + 1 else prev.wins
    val newLosses = if (!won) prev.losses + 1 else prev.losses
    val newStreak = if (won) prev.currentStreak + 1 else 0
    val newBestStreak = maxOf(prev.bestStreak, newStreak)
    val newCaptures = prev.totalCaptures + captures
    val addedXp = if (won) 100 else 30
    var xp = prev.currentXp + addedXp
    var lvl = prev.level
    if (xp >= prev.maxXp) {
      lvl += 1
      xp -= prev.maxXp
    }

    prefs.edit()
      .putInt("totalGames", newGames)
      .putInt("wins", newWins)
      .putInt("losses", newLosses)
      .putInt("currentStreak", newStreak)
      .putInt("bestStreak", newBestStreak)
      .putInt("totalCaptures", newCaptures)
      .apply()

    _userProfile.value = prev.copy(
      totalGames = newGames,
      wins = newWins,
      losses = newLosses,
      currentStreak = newStreak,
      bestStreak = newBestStreak,
      totalCaptures = newCaptures,
      level = lvl,
      currentXp = xp
    )
  }

  fun claimAchievement(id: String) {
    val list = _achievements.value.map {
      if (it.id == id && it.isUnlocked && !it.isClaimed) {
        addCoins(it.rewardCoins)
        addGems(it.rewardGems)
        it.copy(isClaimed = true)
      } else {
        it
      }
    }
    _achievements.value = list
  }

  fun sendChatMessage(friendId: String, text: String, type: MessageType = MessageType.TEXT, voiceDurationSec: Int = 0) {
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    val newMsg = ChatMessage(
      id = "MSG-${System.currentTimeMillis()}",
      senderId = _userProfile.value.id,
      senderName = _userProfile.value.username,
      text = text,
      timestamp = time,
      isMine = true,
      type = type,
      voiceDurationSec = voiceDurationSec
    )
    val map = _chatMessages.value.toMutableMap()
    val list = (map[friendId] ?: emptyList()) + newMsg
    map[friendId] = list
    _chatMessages.value = map
  }

  fun acceptFriendRequest(requestId: String) {
    val req = _friendRequests.value.firstOrNull { it.id == requestId } ?: return
    _friendRequests.value = _friendRequests.value.filter { it.id != requestId }
    val newFriend = Friend(
      id = req.senderId,
      username = req.senderName,
      level = req.senderLevel,
      isOnline = true,
      inGame = false
    )
    _friends.value = _friends.value + newFriend
  }

  fun rejectFriendRequest(requestId: String) {
    _friendRequests.value = _friendRequests.value.filter { it.id != requestId }
  }

  fun removeFriend(friendId: String) {
    _friends.value = _friends.value.filter { it.id != friendId }
  }

  fun sendFriendRequest(usernameOrId: String): Boolean {
    if (usernameOrId.isBlank()) return false
    // Simulate creating a sent friend request notification
    val notif = NotificationItem(
      id = "NOTIF-${System.currentTimeMillis()}",
      title = "Friend Request Sent",
      message = "Request sent to $usernameOrId.",
      timeAgo = "Just now",
      isRead = false
    )
    _notifications.value = listOf(notif) + _notifications.value
    return true
  }

  fun joinTournament(tournamentId: String): Boolean {
    val tournament = _tournaments.value.firstOrNull { it.id == tournamentId } ?: return false
    if (tournament.isJoined) return true
    if (spendCoins(tournament.entryFeeCoins)) {
      _tournaments.value = _tournaments.value.map {
        if (it.id == tournamentId) it.copy(isJoined = true, participantsCount = it.participantsCount + 1) else it
      }
      return true
    }
    return false
  }

  fun createAdminTournament(title: String, fee: Long, prize: Long, gems: Int) {
    val newTournament = Tournament(
      id = "TOURN-${System.currentTimeMillis()}",
      title = title,
      entryFeeCoins = fee,
      prizeCoins = prize,
      prizeGems = gems,
      participantsCount = 1,
      maxParticipants = 16,
      startTimeFormatted = "Starts in 30m",
      status = TournamentStatus.REGISTRATION_OPEN,
      isJoined = true
    )
    _tournaments.value = listOf(newTournament) + _tournaments.value
  }

  fun createPrivateRoom(betAmount: Long): String {
    val code = (100000..999999).random().toString()
    activePrivateRooms[code] = betAmount
    return code
  }

  fun validatePrivateRoomCode(code: String): Long? {
    return activePrivateRooms[code] ?: 500L // returns bet amount or standard 500
  }
}

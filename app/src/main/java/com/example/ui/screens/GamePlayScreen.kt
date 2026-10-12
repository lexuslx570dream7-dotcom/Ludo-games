package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.SoundManager
import com.example.logic.LudoGameEngine
import com.example.model.GamePlayer
import com.example.model.LudoGameState
import com.example.model.PlayerColor
import com.example.model.UserProfile
import com.example.ui.components.AndroidStatusBar
import com.example.ui.components.LudoBoardView
import com.example.ui.theme.CoinGold
import com.example.ui.theme.GemCyan
import com.example.ui.theme.LudoBlue
import com.example.ui.theme.LudoGreen
import com.example.ui.theme.LudoRed
import com.example.ui.theme.LudoYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun GamePlayScreen(
  initialState: LudoGameState,
  userProfile: UserProfile,
  soundManager: SoundManager,
  onExitGame: () -> Unit,
  onGameWon: (won: Boolean, prizeCoins: Long) -> Unit,
  onSpendGems: (amount: Int) -> Boolean,
  onToggleMusic: (Boolean) -> Unit,
  onToggleSound: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var gameState by remember { mutableStateOf(initialState) }
  var showSettingsDialog by remember { mutableStateOf(false) }
  var showEmojiPicker by remember { mutableStateOf(false) }
  var showChatPicker by remember { mutableStateOf(false) }
  var activeUserSpeechBubble by remember { mutableStateOf<String?>("👑") }
  var isMicMuted by remember { mutableStateOf(false) }
  var statusBannerMessage by remember { mutableStateOf<String?>(null) }

  val coroutineScope = rememberCoroutineScope()
  val diceRotation = remember { Animatable(0f) }

  // Turn handling & AI loop
  LaunchedEffect(gameState.currentTurnIndex, gameState.canRollDice, gameState.waitingForTokenSelection) {
    if (gameState.isGameOver) return@LaunchedEffect

    val current = gameState.currentTurnPlayer
    if (current.isBot) {
      delay(700)
      if (gameState.canRollDice) {
        // Roll dice for AI
        val rolledDice = Random.nextInt(1, 7)
        soundManager.playDiceRollSound(userProfile.soundEnabled)
        soundManager.vibrate(userProfile.vibrationEnabled, 30)

        val selectable = LudoGameEngine.getSelectableTokenIds(current, rolledDice)
        val consecutive = if (rolledDice == 6) gameState.consecutiveSixes + 1 else 0

        if (consecutive >= 3) {
          // 3 sixes - turn ends
          statusBannerMessage = "${current.name} rolled 3 sixes! Turn passed."
          delay(800)
          val nextIndex = (gameState.currentTurnIndex + 1) % gameState.players.size
          gameState = gameState.copy(
            currentTurnIndex = nextIndex,
            canRollDice = true,
            waitingForTokenSelection = false,
            selectableTokenIds = emptySet(),
            consecutiveSixes = 0
          )
        } else if (selectable.isEmpty()) {
          statusBannerMessage = "${current.name} rolled $rolledDice - no valid moves."
          delay(800)
          val nextIndex = (gameState.currentTurnIndex + 1) % gameState.players.size
          gameState = gameState.copy(
            diceValue = rolledDice,
            currentTurnIndex = nextIndex,
            canRollDice = true,
            waitingForTokenSelection = false,
            selectableTokenIds = emptySet(),
            consecutiveSixes = 0
          )
        } else {
          gameState = gameState.copy(
            diceValue = rolledDice,
            canRollDice = false,
            waitingForTokenSelection = true,
            selectableTokenIds = selectable,
            consecutiveSixes = consecutive
          )
          delay(650)
          // Pick best move
          val bestTokenId = LudoGameEngine.pickBestAiMove(current, selectable, rolledDice, gameState) ?: selectable.first()
          soundManager.playMoveSound(userProfile.soundEnabled)
          gameState = LudoGameEngine.applyMove(gameState, bestTokenId)
          if (gameState.lastRollWasBonus) {
            statusBannerMessage = gameState.lastCaptureMessage ?: "${current.name} rolled 6! Extra roll!"
          }
        }
      }
    } else {
      // User's turn
      if (gameState.lastRollWasBonus && gameState.canRollDice) {
        statusBannerMessage = gameState.lastCaptureMessage ?: "Bonus roll! Roll again!"
      }
    }
  }

  // Check victory
  LaunchedEffect(gameState.isGameOver) {
    if (gameState.isGameOver) {
      val userWon = gameState.winnerName == userProfile.username
      if (userWon) {
        soundManager.playWinSound(userProfile.soundEnabled)
        onGameWon(true, gameState.betAmount * 2)
      } else {
        onGameWon(false, 0)
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF332047),
            Color(0xFF28183B),
            Color(0xFF1E1030)
          )
        )
      )
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Android Status Bar
      AndroidStatusBar()

      // 2. Top Game Header (Settings, Trophy, Spectator, Gift)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Settings Gear
        IconButton(
          onClick = { showSettingsDialog = true },
          modifier = Modifier.size(36.dp).testTag("game_settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = Color(0xFFCFD8DC),
            modifier = Modifier.size(26.dp)
          )
        }

        // Center Trophy + Spectator counter
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CardGiftcard,
            contentDescription = "Trophy",
            tint = CoinGold,
            modifier = Modifier.size(24.dp)
          )

          // Spectator Pill
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF150A24).copy(alpha = 0.7f))
              .border(1.dp, Color(0xFF512DA8), RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Visibility,
              contentDescription = null,
              tint = Color(0xFFB39DDB),
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Spectator ${gameState.spectatorsCount}",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Gift bag
        IconButton(
          onClick = { statusBannerMessage = "Lucky Gift sent to all players!" },
          modifier = Modifier.size(36.dp).testTag("gift_button")
        ) {
          Icon(
            imageVector = Icons.Default.CardGiftcard,
            contentDescription = "Gift",
            tint = Color(0xFF81C784),
            modifier = Modifier.size(26.dp)
          )
        }
      }

      // 3. Top Players Row (Top-Left: Ali, Top-Right: Ayan)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        val playerAli = gameState.players.firstOrNull { it.color == PlayerColor.YELLOW }
        val playerAyan = gameState.players.firstOrNull { it.color == PlayerColor.BLUE }

        if (playerAli != null) {
          PlayerCardSlot(
            player = playerAli,
            isCurrentTurn = gameState.currentTurnPlayer.id == playerAli.id,
            isTop = true,
            isLeft = true
          )
        }

        if (playerAyan != null) {
          PlayerCardSlot(
            player = playerAyan,
            isCurrentTurn = gameState.currentTurnPlayer.id == playerAyan.id,
            isTop = true,
            isLeft = false
          )
        }
      }

      // Status Notification Banner
      AnimatedVisibility(visible = statusBannerMessage != null) {
        statusBannerMessage?.let { msg ->
          Box(
            modifier = Modifier
              .padding(horizontal = 24.dp, vertical = 2.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF00C853).copy(alpha = 0.9f))
              .padding(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Text(msg, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }

      // 4. Central Ludo Board
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
      ) {
        LudoBoardView(
          gameState = gameState,
          onTokenClick = { tokenId ->
            soundManager.playMoveSound(userProfile.soundEnabled)
            soundManager.vibrate(userProfile.vibrationEnabled, 40)
            gameState = LudoGameEngine.applyMove(gameState, tokenId)
            if (gameState.lastRollWasBonus) {
              statusBannerMessage = gameState.lastCaptureMessage ?: "Bonus roll! Roll again!"
            }
          }
        )
      }

      // 5. Bottom Players Row (Bottom-Left: User "Ayan KN", Bottom-Right: PabLou D Z...)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        val userPlayer = gameState.players.firstOrNull { it.isHumanUser }
        val redPlayer = gameState.players.firstOrNull { it.color == PlayerColor.RED }

        if (userPlayer != null) {
          UserPlayerSlot(
            player = userPlayer,
            isCurrentTurn = gameState.currentTurnPlayer.id == userPlayer.id,
            canRoll = gameState.canRollDice,
            diceValue = gameState.diceValue,
            isRolling = gameState.isRolling,
            waitingSelection = gameState.waitingForTokenSelection,
            speechBubble = activeUserSpeechBubble,
            onRollDice = {
              if (gameState.canRollDice && !gameState.isRolling) {
                coroutineScope.launch {
                  gameState = gameState.copy(isRolling = true)
                  soundManager.playDiceRollSound(userProfile.soundEnabled)
                  soundManager.vibrate(userProfile.vibrationEnabled, 50)
                  diceRotation.animateTo(diceRotation.value + 360f, tween(350))
                  val rolled = Random.nextInt(1, 7)
                  val consecutive = if (rolled == 6) gameState.consecutiveSixes + 1 else 0
                  val selectable = LudoGameEngine.getSelectableTokenIds(userPlayer, rolled)

                  if (consecutive >= 3) {
                    statusBannerMessage = "3 consecutive sixes! Turn lost."
                    delay(700)
                    val nextIndex = (gameState.currentTurnIndex + 1) % gameState.players.size
                    gameState = gameState.copy(
                      diceValue = rolled,
                      isRolling = false,
                      canRollDice = true,
                      waitingForTokenSelection = false,
                      selectableTokenIds = emptySet(),
                      consecutiveSixes = 0,
                      currentTurnIndex = nextIndex
                    )
                  } else if (selectable.isEmpty()) {
                    statusBannerMessage = "Rolled $rolled - no valid moves."
                    delay(700)
                    val nextIndex = (gameState.currentTurnIndex + 1) % gameState.players.size
                    gameState = gameState.copy(
                      diceValue = rolled,
                      isRolling = false,
                      canRollDice = true,
                      waitingForTokenSelection = false,
                      selectableTokenIds = emptySet(),
                      consecutiveSixes = 0,
                      currentTurnIndex = nextIndex
                    )
                  } else if (selectable.size == 1) {
                    // Auto move if single token
                    gameState = gameState.copy(
                      diceValue = rolled,
                      isRolling = false,
                      canRollDice = false,
                      waitingForTokenSelection = false,
                      selectableTokenIds = emptySet(),
                      consecutiveSixes = consecutive
                    )
                    delay(300)
                    soundManager.playMoveSound(userProfile.soundEnabled)
                    gameState = LudoGameEngine.applyMove(gameState, selectable.first())
                  } else {
                    gameState = gameState.copy(
                      diceValue = rolled,
                      isRolling = false,
                      canRollDice = false,
                      waitingForTokenSelection = true,
                      selectableTokenIds = selectable,
                      consecutiveSixes = consecutive
                    )
                    statusBannerMessage = "Tap a pulsing token to move!"
                  }
                }
              }
            },
            onGemReroll = {
              if (userProfile.gems >= 2 && !gameState.canRollDice && gameState.waitingForTokenSelection) {
                if (onSpendGems(2)) {
                  coroutineScope.launch {
                    soundManager.playDiceRollSound(userProfile.soundEnabled)
                    val rerolled = Random.nextInt(1, 7)
                    val selectable = LudoGameEngine.getSelectableTokenIds(userPlayer, rerolled)
                    gameState = gameState.copy(
                      diceValue = rerolled,
                      selectableTokenIds = selectable
                    )
                    statusBannerMessage = "Gem Reroll! Rolled $rerolled!"
                  }
                }
              }
            }
          )
        }

        if (redPlayer != null) {
          PlayerCardSlot(
            player = redPlayer,
            isCurrentTurn = gameState.currentTurnPlayer.id == redPlayer.id,
            isTop = false,
            isLeft = false
          )
        }
      }

      // 6. Bottom Dock Controls: Mic, Emoji, Chat
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Grey Mic toggle
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF546E7A))
            .border(1.5.dp, Color(0xFF90A4AE), RoundedCornerShape(16.dp))
            .clickable { isMicMuted = !isMicMuted }
            .testTag("in_game_mic_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = "Microphone",
            tint = if (isMicMuted) Color(0xFFFF8A80) else Color.White,
            modifier = Modifier.size(28.dp)
          )
        }

        // Yellow Emoji reaction button
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFFFFEE58), Color(0xFFFFA000))
              )
            )
            .border(1.5.dp, Color(0xFFFFF9C4), RoundedCornerShape(16.dp))
            .clickable { showEmojiPicker = !showEmojiPicker }
            .testTag("in_game_emoji_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEmotions,
            contentDescription = "Emojis",
            tint = Color(0xFF3E2723),
            modifier = Modifier.size(28.dp)
          )
        }

        // Yellow Chat button
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFFFFEE58), Color(0xFFFFA000))
              )
            )
            .border(1.5.dp, Color(0xFFFFF9C4), RoundedCornerShape(16.dp))
            .clickable { showChatPicker = !showChatPicker }
            .testTag("in_game_chat_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = "Quick Chat",
            tint = Color(0xFF3E2723),
            modifier = Modifier.size(28.dp)
          )
        }
      }
    }

    // Emoji Picker Floating Bar
    AnimatedVisibility(
      visible = showEmojiPicker,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = (-70).dp)
    ) {
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(24.dp))
          .background(Color(0xFF1B0C2E))
          .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(24.dp))
          .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        listOf("👑", "🦉", "🔥", "❤️", "😂", "🏆", "😭", "👏").forEach { emoji ->
          Text(
            text = emoji,
            fontSize = 24.sp,
            modifier = Modifier
              .clickable {
                activeUserSpeechBubble = emoji
                showEmojiPicker = false
              }
              .padding(4.dp)
          )
        }
      }
    }

    // Chat Quick Phrases Bar
    AnimatedVisibility(
      visible = showChatPicker,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = (-70).dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth(0.9f)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF1B0C2E))
          .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
          .padding(12.dp)
      ) {
        Text("Quick Chat", color = CoinGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(listOf("Good Luck! 🍀", "Hurry Up! ⏰", "Nice Move! 👏", "Thanks! 🙏", "Well Played! 👑")) { phrase ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF311B92))
                .clickable {
                  activeUserSpeechBubble = phrase
                  showChatPicker = false
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(phrase, color = Color.White, fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Victory / Game Over Dialog
    if (gameState.isGameOver) {
      Dialog(onDismissRequest = onExitGame) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFF28114B), Color(0xFF140826))
              )
            )
            .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(24.dp))
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎉 MATCH FINISHED! 🎉", color = CoinGold, fontWeight = FontWeight.Black, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = if (gameState.winnerName == userProfile.username) "VICTORY! YOU WON!" else "${gameState.winnerName} WON!",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (gameState.winnerName == userProfile.username) {
              Text("+${gameState.betAmount * 2} Coins Won!", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
              onClick = onExitGame,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.testTag("exit_game_victory_button")
            ) {
              Text("Back to Lobby", color = Color(0xFF3E2723), fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }

    // In-Game Settings Dialog (Independent Music & Sound toggles)
    if (showSettingsDialog) {
      Dialog(onDismissRequest = { showSettingsDialog = false }) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF150A24))
            .border(2.dp, Color(0xFF673AB7), RoundedCornerShape(20.dp))
            .padding(20.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Game Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
              IconButton(onClick = { showSettingsDialog = false }) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
              }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Ludo Music", color = Color.White, fontSize = 15.sp)
              Switch(
                checked = userProfile.musicEnabled,
                onCheckedChange = onToggleMusic,
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD54F))
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Game Sound", color = Color.White, fontSize = 15.sp)
              Switch(
                checked = userProfile.soundEnabled,
                onCheckedChange = onToggleSound,
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD54F))
              )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(
              onClick = onExitGame,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
              modifier = Modifier.fillMaxWidth().testTag("surrender_match_button")
            ) {
              Text("Leave / Surrender Match", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun PlayerCardSlot(
  player: GamePlayer,
  isCurrentTurn: Boolean,
  isTop: Boolean,
  isLeft: Boolean
) {
  val primaryColor = Color(player.color.hex)

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End
  ) {
    if (!isLeft && player.speechBubble != null) {
      SpeechBubbleBadge(text = player.speechBubble)
      Spacer(modifier = Modifier.width(6.dp))
    }

    Column(horizontalAlignment = if (isLeft) Alignment.Start else Alignment.End) {
      // Avatar with frame and mic
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (isLeft && player.micOn) {
          Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(3.dp))
        }

        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              if (isCurrentTurn) Brush.sweepGradient(listOf(primaryColor, Color.White, primaryColor))
              else Brush.linearGradient(listOf(primaryColor, Color.Black))
            )
            .border(if (isCurrentTurn) 2.5.dp else 1.dp, if (isCurrentTurn) Color.White else primaryColor, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = player.name.take(1),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        }

        if (!isLeft && player.micOn) {
          Spacer(modifier = Modifier.width(3.dp))
          Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
        }
      }

      Text(
        text = player.name,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    if (isLeft && player.speechBubble != null) {
      Spacer(modifier = Modifier.width(6.dp))
      SpeechBubbleBadge(text = player.speechBubble)
    }
  }
}

@Composable
fun UserPlayerSlot(
  player: GamePlayer,
  isCurrentTurn: Boolean,
  canRoll: Boolean,
  diceValue: Int,
  isRolling: Boolean,
  waitingSelection: Boolean,
  speechBubble: String?,
  onRollDice: () -> Unit,
  onGemReroll: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "turnBounce")
  val bounceScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "diceBounce"
  )

  Row(
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Avatar
    Column(horizontalAlignment = Alignment.Start) {
      Text(
        text = player.name,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(2.dp))
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(Color(0xFF2E7D32))
          .border(
            if (isCurrentTurn) 2.5.dp else 1.dp,
            if (isCurrentTurn) Color(0xFF00E676) else Color.White,
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    // Interactive Dice / Speech Bubble
    Box(
      modifier = Modifier
        .size(54.dp)
        .scale(if (isCurrentTurn && canRoll) bounceScale else 1f)
        .clip(RoundedCornerShape(12.dp))
        .background(
          Brush.verticalGradient(
            listOf(Color.White, Color(0xFFECEFF1))
          )
        )
        .border(
          2.dp,
          if (isCurrentTurn) Color(0xFFFFD54F) else Color(0xFFCFD8DC),
          RoundedCornerShape(12.dp)
        )
        .clickable { onRollDice() }
        .testTag("interactive_dice_box"),
      contentAlignment = Alignment.Center
    ) {
      if (isCurrentTurn && canRoll) {
        // Roll prompt with crown or dots
        Text(
          text = "ROLL",
          color = Color(0xFF0D47A1),
          fontWeight = FontWeight.Black,
          fontSize = 13.sp
        )
      } else {
        // Draw 3D dice dots
        DiceFaceView(value = diceValue)
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    // Gem Reroll button (matching Screenshot 4: counter-clockwise arrow + 2 gems)
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF1F103A))
        .border(1.dp, Color(0xFF512DA8), RoundedCornerShape(8.dp))
        .clickable { onGemReroll() }
        .padding(horizontal = 6.dp, vertical = 3.dp)
        .testTag("gem_reroll_button")
    ) {
      Icon(Icons.Default.Refresh, contentDescription = "Reroll", tint = Color.White, modifier = Modifier.size(16.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("2", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.Diamond, contentDescription = null, tint = GemCyan, modifier = Modifier.size(12.dp))
      }
    }
  }
}

@Composable
fun DiceFaceView(value: Int) {
  Canvas(modifier = Modifier.size(36.dp)) {
    val r = size.width * 0.12f
    val c = size.width / 2f
    val l = size.width * 0.25f
    val h = size.width * 0.75f

    val pipColor = Color(0xFF212121)

    when (value) {
      1 -> drawCircle(pipColor, r, Offset(c, c))
      2 -> {
        drawCircle(pipColor, r, Offset(l, l))
        drawCircle(pipColor, r, Offset(h, h))
      }
      3 -> {
        drawCircle(pipColor, r, Offset(l, l))
        drawCircle(pipColor, r, Offset(c, c))
        drawCircle(pipColor, r, Offset(h, h))
      }
      4 -> {
        drawCircle(pipColor, r, Offset(l, l))
        drawCircle(pipColor, r, Offset(h, l))
        drawCircle(pipColor, r, Offset(l, h))
        drawCircle(pipColor, r, Offset(h, h))
      }
      5 -> {
        drawCircle(pipColor, r, Offset(l, l))
        drawCircle(pipColor, r, Offset(h, l))
        drawCircle(pipColor, r, Offset(c, c))
        drawCircle(pipColor, r, Offset(l, h))
        drawCircle(pipColor, r, Offset(h, h))
      }
      6 -> {
        drawCircle(pipColor, r, Offset(l, l))
        drawCircle(pipColor, r, Offset(h, l))
        drawCircle(pipColor, r, Offset(l, c))
        drawCircle(pipColor, r, Offset(h, c))
        drawCircle(pipColor, r, Offset(l, h))
        drawCircle(pipColor, r, Offset(h, h))
      }
    }
  }
}

@Composable
fun SpeechBubbleBadge(text: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color.White)
      .border(1.dp, Color(0xFFB0BEC5), RoundedCornerShape(8.dp))
      .padding(horizontal = 6.dp, vertical = 3.dp)
  ) {
    Text(text, fontSize = 14.sp)
  }
}

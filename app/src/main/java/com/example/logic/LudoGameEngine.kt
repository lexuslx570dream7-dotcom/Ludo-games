package com.example.logic

import com.example.model.GameMode
import com.example.model.GamePlayer
import com.example.model.LudoGameState
import com.example.model.LudoToken
import com.example.model.PlayerColor
import com.example.model.RuleVariant
import kotlin.random.Random

object LudoGameEngine {

  val SAFE_GLOBAL_CELLS = setOf(0, 8, 13, 21, 26, 34, 39, 47)

  fun getStartOffset(color: PlayerColor): Int {
    return when (color) {
      PlayerColor.GREEN -> 0
      PlayerColor.YELLOW -> 13
      PlayerColor.BLUE -> 26
      PlayerColor.RED -> 39
    }
  }

  fun getGlobalCell(color: PlayerColor, step: Int): Int? {
    if (step !in 1..51) return null
    return (getStartOffset(color) + (step - 1)) % 52
  }

  fun getSelectableTokenIds(player: GamePlayer, dice: Int): Set<Int> {
    val result = mutableSetOf<Int>()
    for (token in player.tokens) {
      if (canMoveToken(token, dice)) {
        result.add(token.id)
      }
    }
    return result
  }

  fun canMoveToken(token: LudoToken, dice: Int): Boolean {
    if (token.isFinished) return false
    if (token.isInYard) {
      return dice == 6
    }
    // Track or home path
    return (token.step + dice) <= 57
  }

  fun applyMove(
    state: LudoGameState,
    tokenId: Int
  ): LudoGameState {
    val currentPlayer = state.currentTurnPlayer
    val token = currentPlayer.tokens.firstOrNull { it.id == tokenId } ?: return state
    val dice = state.diceValue

    val newStep = if (token.isInYard) 1 else token.step + dice
    val updatedToken = token.copy(step = newStep)
    val updatedTokens = currentPlayer.tokens.map { if (it.id == tokenId) updatedToken else it }
    var updatedCurrentPlayer = currentPlayer.copy(tokens = updatedTokens)

    var bonusTurn = false
    var captureMsg: String? = null

    // Check if entered home
    if (newStep == 57) {
      bonusTurn = true
      captureMsg = "${currentPlayer.name} reached Home! Bonus roll!"
    }

    // Check capture if on track
    val landingGlobalCell = getGlobalCell(currentPlayer.color, newStep)
    var updatedPlayers = state.players.map { if (it.id == currentPlayer.id) updatedCurrentPlayer else it }.toMutableList()

    if (landingGlobalCell != null && landingGlobalCell !in SAFE_GLOBAL_CELLS) {
      // Check opponents on the same cell
      var capturedAny = false
      updatedPlayers = updatedPlayers.map { opponent ->
        if (opponent.id != currentPlayer.id) {
          // If team mode, do not capture teammate
          val isTeammate = state.mode == GameMode.TEAM_2V2 &&
            ((currentPlayer.color == PlayerColor.GREEN && opponent.color == PlayerColor.BLUE) ||
             (currentPlayer.color == PlayerColor.YELLOW && opponent.color == PlayerColor.RED))

          if (!isTeammate) {
            val oppTokens = opponent.tokens.map { oppToken ->
              val oppGlobal = getGlobalCell(opponent.color, oppToken.step)
              if (oppGlobal == landingGlobalCell) {
                capturedAny = true
                captureMsg = "${currentPlayer.name} captured ${opponent.name}'s token!"
                oppToken.copy(step = 0) // Sent back to yard
              } else {
                oppToken
              }
            }
            opponent.copy(tokens = oppTokens)
          } else {
            opponent
          }
        } else {
          opponent
        }
      }.toMutableList()

      if (capturedAny) {
        bonusTurn = true
      }
    }

    // Rolling 6 gives bonus turn (if not already 3 sixes)
    if (dice == 6 && state.consecutiveSixes < 3) {
      bonusTurn = true
    }

    // Check victory condition
    val playerWon = updatedTokens.all { it.isFinished }
    val isGameOver = playerWon || (state.mode == GameMode.TWO_PLAYERS && playerWon)

    val nextTurnIndex = if (bonusTurn && !isGameOver) {
      state.currentTurnIndex
    } else {
      (state.currentTurnIndex + 1) % updatedPlayers.size
    }

    val nextConsecutiveSixes = if (bonusTurn && dice == 6) state.consecutiveSixes else 0

    return state.copy(
      players = updatedPlayers,
      currentTurnIndex = nextTurnIndex,
      waitingForTokenSelection = false,
      selectableTokenIds = emptySet(),
      canRollDice = true,
      consecutiveSixes = nextConsecutiveSixes,
      lastRollWasBonus = bonusTurn,
      lastCaptureMessage = captureMsg,
      isGameOver = isGameOver,
      winnerName = if (playerWon) currentPlayer.name else null,
      turnTimeRemaining = 15
    )
  }

  fun pickBestAiMove(player: GamePlayer, selectableIds: Set<Int>, dice: Int, state: LudoGameState): Int? {
    if (selectableIds.isEmpty()) return null
    if (selectableIds.size == 1) return selectableIds.first()

    // Score candidate moves
    var bestScore = -100
    var bestTokenId = selectableIds.first()

    for (id in selectableIds) {
      val token = player.tokens.first { it.id == id }
      val targetStep = if (token.isInYard) 1 else token.step + dice
      var score = 10

      // Reaching home is highest
      if (targetStep == 57) {
        score += 100
      }

      // Safe spot
      val globalCell = getGlobalCell(player.color, targetStep)
      if (globalCell in SAFE_GLOBAL_CELLS) {
        score += 35
      }

      // Capture opponent
      if (globalCell != null && globalCell !in SAFE_GLOBAL_CELLS) {
        val wouldCapture = state.players.any { opp ->
          opp.id != player.id && opp.tokens.any { oppToken ->
            getGlobalCell(opp.color, oppToken.step) == globalCell
          }
        }
        if (wouldCapture) score += 80
      }

      // Exit yard
      if (token.isInYard && dice == 6) {
        score += 40
      }

      // Progress towards home
      score += (targetStep / 3)

      if (score > bestScore) {
        bestScore = score
        bestTokenId = id
      }
    }
    return bestTokenId
  }

  fun createInitialGame(
    mode: GameMode,
    variant: RuleVariant,
    betAmount: Long,
    userProfile: com.example.model.UserProfile
  ): LudoGameState {
    val players = when (mode) {
      GameMode.TWO_PLAYERS -> listOf(
        GamePlayer(
          id = userProfile.id,
          name = userProfile.username,
          color = PlayerColor.GREEN,
          isHumanUser = true,
          micOn = true,
          frame = userProfile.frameType
        ),
        GamePlayer(
          id = "AI-1",
          name = "Ali",
          color = PlayerColor.YELLOW,
          isBot = true,
          micOn = true,
          frame = com.example.model.AvatarFrame.RACCOON_NATURE,
          speechBubble = "🦉"
        )
      )
      GameMode.TEAM_2V2 -> listOf(
        GamePlayer(
          id = userProfile.id,
          name = userProfile.username,
          color = PlayerColor.GREEN,
          isHumanUser = true,
          micOn = true,
          frame = userProfile.frameType
        ),
        GamePlayer(
          id = "P-ALI",
          name = "Ali",
          color = PlayerColor.YELLOW,
          isBot = true,
          micOn = true,
          frame = com.example.model.AvatarFrame.RACCOON_NATURE,
          speechBubble = "🦉"
        ),
        GamePlayer(
          id = "P-AYAN",
          name = "Ayan",
          color = PlayerColor.BLUE,
          isBot = true,
          micOn = true,
          frame = com.example.model.AvatarFrame.DRAGON_MYSTIC
        ),
        GamePlayer(
          id = "P-PABLOU",
          name = "PabLou D Z...",
          color = PlayerColor.RED,
          isBot = true,
          micOn = false,
          frame = com.example.model.AvatarFrame.RUBY_CROWN
        )
      )
      else -> listOf(
        // Four players (Matching Screenshot 4)
        GamePlayer(
          id = "P-ALI",
          name = "Ali",
          color = PlayerColor.YELLOW,
          isBot = true,
          micOn = true,
          frame = com.example.model.AvatarFrame.RACCOON_NATURE,
          speechBubble = "🦉"
        ),
        GamePlayer(
          id = "P-AYAN",
          name = "Ayan",
          color = PlayerColor.BLUE,
          isBot = true,
          micOn = true,
          frame = com.example.model.AvatarFrame.DRAGON_MYSTIC
        ),
        GamePlayer(
          id = userProfile.id,
          name = userProfile.username,
          color = PlayerColor.GREEN,
          isHumanUser = true,
          micOn = false,
          frame = userProfile.frameType,
          speechBubble = "👑"
        ),
        GamePlayer(
          id = "P-PABLOU",
          name = "PabLou D Z...",
          color = PlayerColor.RED,
          isBot = true,
          micOn = true,
          frame = com.example.model.AvatarFrame.RUBY_CROWN
        )
      )
    }

    val startIndex = if (mode == GameMode.FOUR_PLAYERS) 2 else 0 // Start on user for instant playability!

    return LudoGameState(
      gameId = "ROOM-${Random.nextInt(100000, 999999)}",
      mode = mode,
      variant = variant,
      betAmount = betAmount,
      players = players,
      currentTurnIndex = startIndex,
      diceValue = 6,
      canRollDice = true,
      waitingForTokenSelection = false,
      turnTimeRemaining = 15,
      spectatorsCount = Random.nextInt(0, 3)
    )
  }
}

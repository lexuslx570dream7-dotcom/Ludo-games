package com.example.model

enum class PlayerColor(val hex: Long, val displayName: String) {
  YELLOW(0xFFFFC107, "Yellow"),
  BLUE(0xFF1E88E5, "Blue"),
  RED(0xFFE53935, "Red"),
  GREEN(0xFF43A047, "Green")
}

data class LudoToken(
  val id: Int, // 0..3
  val color: PlayerColor,
  val step: Int = 0 // 0 = Yard, 1..51 = track steps, 52..56 = home path, 57 = home finished
) {
  val isInYard: Boolean get() = step == 0
  val isFinished: Boolean get() = step == 57
  val isInHomePath: Boolean get() = step in 52..56
  val isOnBoard: Boolean get() = step in 1..51
}

data class GamePlayer(
  val id: String,
  val name: String,
  val color: PlayerColor,
  val tokens: List<LudoToken> = (0..3).map { LudoToken(it, color) },
  val isBot: Boolean = false,
  val isHumanUser: Boolean = false,
  val micOn: Boolean = false,
  val frame: AvatarFrame = AvatarFrame.GREEN_GLOW,
  val speechBubble: String? = null,
  val hasLeft: Boolean = false,
  val rank: Int = 0 // 1st, 2nd, 3rd, 4th
) {
  val finishedTokensCount: Int get() = tokens.count { it.isFinished }
  val hasWon: Boolean get() = finishedTokensCount == 4
}

enum class GameMode(val title: String) {
  TWO_PLAYERS("2 Players"),
  FOUR_PLAYERS("4 Players"),
  TEAM_2V2("Team 2v2"),
  PRIVATE_ROOM("Private Room"),
  LOCAL_PASS_PLAY("Local Game"),
  VIP_ROOM("VIP Room")
}

enum class RuleVariant(val title: String) {
  CLASSIC("Classic"),
  ARROW("Arrow"),
  QUICK("Quick"),
  MASTER("Master")
}

data class LudoGameState(
  val gameId: String,
  val mode: GameMode,
  val variant: RuleVariant = RuleVariant.CLASSIC,
  val betAmount: Long = 500L,
  val players: List<GamePlayer>,
  val currentTurnIndex: Int = 0,
  val diceValue: Int = 1,
  val isRolling: Boolean = false,
  val canRollDice: Boolean = true,
  val consecutiveSixes: Int = 0,
  val waitingForTokenSelection: Boolean = false,
  val selectableTokenIds: Set<Int> = emptySet(),
  val isGameOver: Boolean = false,
  val winnerName: String? = null,
  val turnTimeRemaining: Int = 15,
  val spectatorsCount: Int = 0,
  val lastRollWasBonus: Boolean = false,
  val lastCaptureMessage: String? = null
) {
  val currentTurnPlayer: GamePlayer
    get() = players[currentTurnIndex.coerceIn(0, players.lastIndex)]
}

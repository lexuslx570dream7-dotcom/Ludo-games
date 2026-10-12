package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.GameRepository
import com.example.data.SoundManager
import com.example.logic.LudoGameEngine
import com.example.model.GameMode
import com.example.model.LudoGameState
import com.example.model.RuleVariant
import com.example.ui.components.CoinGemStoreDialog
import com.example.ui.components.SelectModeDialog
import com.example.ui.components.VipRoomDialog
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrivateLocalScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SocialScreen
import com.example.ui.screens.TournamentScreen
import com.example.ui.theme.MyApplicationTheme

enum class AppDestination {
  MAIN_TABS,
  GAME_PLAY,
  TOURNAMENT,
  PRIVATE_LOCAL
}

class MainActivity : ComponentActivity() {

  private lateinit var repository: GameRepository
  private lateinit var soundManager: SoundManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    repository = GameRepository(applicationContext)
    soundManager = SoundManager(applicationContext)

    setContent {
      MyApplicationTheme(dynamicColor = false) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color(0xFF071120)
        ) {
          LudoAppRoot(repository = repository, soundManager = soundManager)
        }
      }
    }
  }
}

@Composable
fun LudoAppRoot(
  repository: GameRepository,
  soundManager: SoundManager
) {
  val userProfile by repository.userProfile.collectAsState()

  var currentDestination by remember { mutableStateOf(AppDestination.MAIN_TABS) }
  var currentTabIndex by remember { mutableIntStateOf(0) } // 0 = Home, 1 = Social, 2 = Profile

  // Active game state
  var activeGameState by remember { mutableStateOf<LudoGameState?>(null) }

  // Modals & Dialogs
  var showSelectModeDialog by remember { mutableStateOf(false) }
  var selectModeTarget by remember { mutableStateOf(GameMode.FOUR_PLAYERS) }
  var showStoreDialog by remember { mutableStateOf(false) }
  var storeInitialTab by remember { mutableIntStateOf(0) }
  var showVipRoomDialog by remember { mutableStateOf(false) }

  // Handle hardware back press
  BackHandler(enabled = currentDestination != AppDestination.MAIN_TABS || currentTabIndex != 0) {
    if (currentDestination != AppDestination.MAIN_TABS) {
      currentDestination = AppDestination.MAIN_TABS
    } else if (currentTabIndex != 0) {
      currentTabIndex = 0
    }
  }

  AnimatedContent(
    targetState = currentDestination,
    transitionSpec = { fadeIn() togetherWith fadeOut() },
    label = "screen_transition"
  ) { destination ->
    when (destination) {
      AppDestination.MAIN_TABS -> {
        when (currentTabIndex) {
          0 -> {
            HomeScreen(
              userProfile = userProfile,
              onOpenStore = { tab ->
                storeInitialTab = tab
                showStoreDialog = true
              },
              onSelectGameMode = { mode ->
                selectModeTarget = mode
                showSelectModeDialog = true
              },
              onOpenTournament = {
                currentDestination = AppDestination.TOURNAMENT
              },
              onOpenPrivateLocal = {
                currentDestination = AppDestination.PRIVATE_LOCAL
              },
              onOpenVipRoom = {
                showVipRoomDialog = true
              },
              onNavigateTab = { tab ->
                currentTabIndex = tab
              }
            )
          }
          1 -> {
            SocialScreen(
              repository = repository,
              onNavigateTab = { tab ->
                currentTabIndex = tab
              },
              onChallengeFriend = { friend ->
                activeGameState = LudoGameEngine.createInitialGame(
                  mode = GameMode.TWO_PLAYERS,
                  variant = RuleVariant.CLASSIC,
                  betAmount = 500L,
                  userProfile = userProfile
                )
                currentDestination = AppDestination.GAME_PLAY
              }
            )
          }
          2 -> {
            ProfileScreen(
              repository = repository,
              onNavigateTab = { tab ->
                currentTabIndex = tab
              }
            )
          }
        }
      }

      AppDestination.GAME_PLAY -> {
        activeGameState?.let { gameState ->
          GamePlayScreen(
            initialState = gameState,
            userProfile = userProfile,
            soundManager = soundManager,
            onExitGame = {
              currentDestination = AppDestination.MAIN_TABS
            },
            onGameWon = { won, prize ->
              repository.recordMatchResult(won = won, captures = 2)
              if (won && prize > 0) {
                repository.addCoins(prize)
              }
            },
            onSpendGems = { count ->
              repository.spendGems(count)
            },
            onToggleMusic = { repository.toggleMusic(it) },
            onToggleSound = { repository.toggleSound(it) }
          )
        }
      }

      AppDestination.TOURNAMENT -> {
        TournamentScreen(
          repository = repository,
          onBack = { currentDestination = AppDestination.MAIN_TABS },
          onStartTournamentMatch = { tourney ->
            activeGameState = LudoGameEngine.createInitialGame(
              mode = GameMode.FOUR_PLAYERS,
              variant = RuleVariant.MASTER,
              betAmount = tourney.entryFeeCoins,
              userProfile = userProfile
            )
            currentDestination = AppDestination.GAME_PLAY
          }
        )
      }

      AppDestination.PRIVATE_LOCAL -> {
        PrivateLocalScreen(
          repository = repository,
          onBack = { currentDestination = AppDestination.MAIN_TABS },
          onStartPrivateGame = { code, bet ->
            activeGameState = LudoGameEngine.createInitialGame(
              mode = GameMode.PRIVATE_ROOM,
              variant = RuleVariant.CLASSIC,
              betAmount = bet,
              userProfile = userProfile
            )
            currentDestination = AppDestination.GAME_PLAY
          },
          onStartLocalGame = { playersCount ->
            val mode = if (playersCount == 2) GameMode.TWO_PLAYERS else GameMode.FOUR_PLAYERS
            activeGameState = LudoGameEngine.createInitialGame(
              mode = mode,
              variant = RuleVariant.CLASSIC,
              betAmount = 0L,
              userProfile = userProfile
            )
            currentDestination = AppDestination.GAME_PLAY
          }
        )
      }
    }
  }

  // Dialogs & Modals
  if (showSelectModeDialog) {
    SelectModeDialog(
      modeType = selectModeTarget,
      onDismiss = { showSelectModeDialog = false },
      onStartGame = { mode, variant, bet ->
        showSelectModeDialog = false
        activeGameState = LudoGameEngine.createInitialGame(
          mode = mode,
          variant = variant,
          betAmount = bet,
          userProfile = userProfile
        )
        currentDestination = AppDestination.GAME_PLAY
      },
      onJoinCode = { code ->
        showSelectModeDialog = false
        activeGameState = LudoGameEngine.createInitialGame(
          mode = GameMode.FOUR_PLAYERS,
          variant = RuleVariant.CLASSIC,
          betAmount = 500L,
          userProfile = userProfile
        )
        currentDestination = AppDestination.GAME_PLAY
      }
    )
  }

  if (showStoreDialog) {
    CoinGemStoreDialog(
      initialTab = storeInitialTab,
      onDismiss = { showStoreDialog = false },
      onRewardClaimed = { coins, gems ->
        if (coins > 0) repository.addCoins(coins)
        if (gems > 0) repository.addGems(gems)
      }
    )
  }

  if (showVipRoomDialog) {
    VipRoomDialog(
      userCoins = userProfile.coins,
      onDismiss = { showVipRoomDialog = false },
      onEnterVipRoom = { stake ->
        showVipRoomDialog = false
        if (repository.spendCoins(stake)) {
          activeGameState = LudoGameEngine.createInitialGame(
            mode = GameMode.VIP_ROOM,
            variant = RuleVariant.MASTER,
            betAmount = stake,
            userProfile = userProfile
          )
          currentDestination = AppDestination.GAME_PLAY
        }
      }
    )
  }
}

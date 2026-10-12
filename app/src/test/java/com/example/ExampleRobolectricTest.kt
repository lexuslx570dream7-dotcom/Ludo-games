package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.logic.LudoGameEngine
import com.example.model.GameMode
import com.example.model.GamePlayer
import com.example.model.LudoGameState
import com.example.model.LudoToken
import com.example.model.PlayerColor
import com.example.model.RuleVariant
import com.example.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ludo Master", appName)
  }

  @Test
  fun `token in yard requires 6 to exit`() {
    val tokenInYard = LudoToken(id = 0, color = PlayerColor.GREEN, step = 0)
    assertFalse(LudoGameEngine.canMoveToken(tokenInYard, dice = 5))
    assertFalse(LudoGameEngine.canMoveToken(tokenInYard, dice = 1))
    assertTrue(LudoGameEngine.canMoveToken(tokenInYard, dice = 6))
  }

  @Test
  fun `token on track cannot overshoot beyond 57`() {
    val tokenNearHome = LudoToken(id = 0, color = PlayerColor.GREEN, step = 55)
    assertTrue(LudoGameEngine.canMoveToken(tokenNearHome, dice = 2)) // 55 + 2 = 57 (exact)
    assertFalse(LudoGameEngine.canMoveToken(tokenNearHome, dice = 3)) // 55 + 3 = 58 (overshoot)
  }

  @Test
  fun `safe global cells are protected from capture`() {
    val safeCells = LudoGameEngine.SAFE_GLOBAL_CELLS
    assertTrue(safeCells.contains(0))
    assertTrue(safeCells.contains(8))
    assertTrue(safeCells.contains(13))
    assertTrue(safeCells.contains(21))
    assertTrue(safeCells.contains(26))
    assertTrue(safeCells.contains(34))
    assertTrue(safeCells.contains(39))
    assertTrue(safeCells.contains(47))
  }

  @Test
  fun `initial game creation sets up players correctly`() {
    val dummyUser = UserProfile(username = "Tester")
    val game = LudoGameEngine.createInitialGame(
      mode = GameMode.FOUR_PLAYERS,
      variant = RuleVariant.CLASSIC,
      betAmount = 1000L,
      userProfile = dummyUser
    )
    assertEquals(4, game.players.size)
    assertNotNull(game.gameId)
    assertTrue(game.canRollDice)
  }
}

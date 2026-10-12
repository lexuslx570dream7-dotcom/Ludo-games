package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.logic.LudoGameEngine
import com.example.model.GamePlayer
import com.example.model.LudoGameState
import com.example.model.LudoToken
import com.example.model.PlayerColor
import com.example.ui.theme.BoardTile
import com.example.ui.theme.BoardTileDark
import com.example.ui.theme.BoardWood
import com.example.ui.theme.BoardWoodLight
import com.example.ui.theme.LudoBlue
import com.example.ui.theme.LudoGreen
import com.example.ui.theme.LudoRed
import com.example.ui.theme.LudoYellow
import com.example.ui.theme.StarGold
import kotlin.math.hypot

@Composable
fun LudoBoardView(
  gameState: LudoGameState,
  onTokenClick: (tokenId: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val pulseTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by pulseTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(1f)
      .padding(8.dp)
      .shadow(16.dp, RoundedCornerShape(16.dp))
      .clip(RoundedCornerShape(16.dp))
      .background(BoardWood)
      .border(5.dp, Brush.radialGradient(listOf(BoardWoodLight, BoardWood)), RoundedCornerShape(16.dp))
      .padding(4.dp)
      .testTag("ludo_board")
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(gameState) {
          detectTapGestures { tapOffset ->
            val cellSize = size.width / 15f
            val currentPlayer = gameState.currentTurnPlayer
            if (gameState.waitingForTokenSelection && currentPlayer.isHumanUser) {
              // Find which selectable token was clicked
              var clickedTokenId: Int? = null
              var minDistance = Float.MAX_VALUE

              for (tokenId in gameState.selectableTokenIds) {
                val token = currentPlayer.tokens.firstOrNull { it.id == tokenId } ?: continue
                val tokenPos = getTokenBoardPosition(token, currentPlayer.color)
                val tokenPixelX = tokenPos.x * cellSize + cellSize / 2f
                val tokenPixelY = tokenPos.y * cellSize + cellSize / 2f
                val dist = hypot(tapOffset.x - tokenPixelX, tapOffset.y - tokenPixelY)

                if (dist < cellSize * 1.3f && dist < minDistance) {
                  minDistance = dist
                  clickedTokenId = tokenId
                }
              }

              clickedTokenId?.let { onTokenClick(it) }
            }
          }
        }
    ) {
      val w = size.width
      val h = size.height
      val cellSize = w / 15f

      // 1. Draw base white track grid background
      drawRect(color = Color(0xFFF9FBE7), size = Size(w, h))

      // 2. Draw 4 Yards (6x6 cells each)
      // Yellow (Top-Left): cols 0..5, rows 0..5
      drawYard(0f, 0f, 6 * cellSize, LudoYellow, Color(0xFFFFF9C4), "Yellow")
      // Blue (Top-Right): cols 9..14, rows 0..5
      drawYard(9 * cellSize, 0f, 6 * cellSize, LudoBlue, Color(0xFFE1F5FE), "Blue")
      // Green (Bottom-Left): cols 0..5, rows 9..14
      drawYard(0f, 9 * cellSize, 6 * cellSize, LudoGreen, Color(0xFFE8F5E9), "Green")
      // Red (Bottom-Right): cols 9..14, rows 9..14
      drawYard(9 * cellSize, 9 * cellSize, 6 * cellSize, LudoRed, Color(0xFFFFEBEE), "Red")

      // 3. Draw Track Grid & Safe Cells
      drawTrackGrid(cellSize)

      // 4. Draw Center Finish Triangles (cols 6..8, rows 6..8)
      drawCenterFinish(6 * cellSize, 6 * cellSize, 3 * cellSize)

      // 5. Draw Tokens
      val selectableIds = if (gameState.waitingForTokenSelection && gameState.currentTurnPlayer.isHumanUser) {
        gameState.selectableTokenIds
      } else {
        emptySet()
      }

      for (player in gameState.players) {
        for (token in player.tokens) {
          if (token.isFinished) continue // in center finish
          val pos = getTokenBoardPosition(token, player.color)
          val cx = pos.x * cellSize + cellSize / 2f
          val cy = pos.y * cellSize + cellSize / 2f
          val isSelectable = player.id == gameState.currentTurnPlayer.id && selectableIds.contains(token.id)

          drawLudoToken(
            cx = cx,
            cy = cy,
            radius = cellSize * 0.42f,
            color = player.color,
            isSelectable = isSelectable,
            pulseScale = if (isSelectable) pulseScale else 1f,
            tokenId = token.id
          )
        }
      }
    }
  }
}

private fun DrawScope.drawYard(
  x: Float,
  y: Float,
  size: Float,
  primaryColor: Color,
  innerColor: Color,
  label: String
) {
  // Outer colored yard rectangle
  drawRoundRect(
    color = primaryColor,
    topLeft = Offset(x, y),
    size = Size(size, size),
    cornerRadius = CornerRadius(8f, 8f)
  )

  // Inner yard base (lighter rounded square)
  val innerMargin = size * 0.15f
  val innerSize = size - (innerMargin * 2f)
  drawRoundRect(
    color = innerColor,
    topLeft = Offset(x + innerMargin, y + innerMargin),
    size = Size(innerSize, innerSize),
    cornerRadius = CornerRadius(14f, 14f)
  )

  // 4 circular pedestals for tokens
  val pedestalRadius = size * 0.11f
  val step = innerSize * 0.5f
  val startX = x + innerMargin + (step * 0.5f)
  val startY = y + innerMargin + (step * 0.5f)

  val offsets = listOf(
    Offset(startX, startY),
    Offset(startX + step, startY),
    Offset(startX, startY + step),
    Offset(startX + step, startY + step)
  )

  for (off in offsets) {
    drawCircle(
      color = primaryColor.copy(alpha = 0.35f),
      radius = pedestalRadius + 3f,
      center = off
    )
    drawCircle(
      color = Color.White,
      radius = pedestalRadius,
      center = off
    )
    drawCircle(
      color = primaryColor,
      radius = pedestalRadius * 0.7f,
      center = off
    )
  }
}

private fun DrawScope.drawTrackGrid(cellSize: Float) {
  // Cells borders
  val strokePaint = Color(0xFFB0BEC5)
  val strokeWidth = 1.2f

  for (col in 0..14) {
    for (row in 0..14) {
      // Yard areas & center are already custom drawn
      val inYard = (col < 6 && row < 6) || (col > 8 && row < 6) ||
        (col < 6 && row > 8) || (col > 8 && row > 8)
      val inCenter = (col in 6..8 && row in 6..8)

      if (!inYard && !inCenter) {
        val x = col * cellSize
        val y = row * cellSize

        // Color specific home columns & start cells
        val cellColor = when {
          // Yellow Home Path: col 7, rows 1..5
          col == 7 && row in 1..5 -> Color(LudoYellow.value)
          // Blue Home Path: col in 9..13, row 7
          col in 9..13 && row == 7 -> Color(LudoBlue.value)
          // Red Home Path: col 7, rows 9..13
          col == 7 && row in 9..13 -> Color(LudoRed.value)
          // Green Home Path: col in 1..5, row 7
          col in 1..5 && row == 7 -> Color(LudoGreen.value)

          // Start cells
          col == 6 && row == 1 -> Color(LudoYellow.value) // Yellow Start
          col == 13 && row == 6 -> Color(LudoBlue.value) // Blue Start
          col == 8 && row == 13 -> Color(LudoRed.value) // Red Start
          col == 1 && row == 8 -> Color(LudoGreen.value) // Green Start

          else -> BoardTile
        }

        drawRect(
          color = cellColor,
          topLeft = Offset(x, y),
          size = Size(cellSize, cellSize)
        )
        drawRect(
          color = strokePaint,
          topLeft = Offset(x, y),
          size = Size(cellSize, cellSize),
          style = Stroke(width = strokeWidth)
        )

        // Draw star / safe markers
        val isStarCell = (col == 6 && row == 2) || (col == 12 && row == 6) ||
          (col == 8 && row == 12) || (col == 2 && row == 8) ||
          (col == 6 && row == 1) || (col == 13 && row == 6) ||
          (col == 8 && row == 13) || (col == 1 && row == 8)

        if (isStarCell) {
          drawSafeStar(x + cellSize / 2f, y + cellSize / 2f, cellSize * 0.28f)
        }
      }
    }
  }
}

private fun DrawScope.drawSafeStar(cx: Float, cy: Float, radius: Float) {
  val path = Path()
  val points = 5
  val innerRadius = radius * 0.45f
  for (i in 0 until points * 2) {
    val r = if (i % 2 == 0) radius else innerRadius
    val angle = (i * Math.PI / points) - (Math.PI / 2)
    val px = (cx + r * Math.cos(angle)).toFloat()
    val py = (cy + r * Math.sin(angle)).toFloat()
    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
  }
  path.close()
  drawPath(path, color = StarGold, style = Fill)
  drawPath(path, color = Color(0xFFF57F17), style = Stroke(1.5f))
}

private fun DrawScope.drawCenterFinish(x: Float, y: Float, size: Float) {
  val cx = x + size / 2f
  val cy = y + size / 2f

  // Yellow top triangle
  drawPath(
    Path().apply {
      moveTo(x, y)
      lineTo(x + size, y)
      lineTo(cx, cy)
      close()
    },
    color = Color(LudoYellow.value)
  )

  // Blue right triangle
  drawPath(
    Path().apply {
      moveTo(x + size, y)
      lineTo(x + size, y + size)
      lineTo(cx, cy)
      close()
    },
    color = Color(LudoBlue.value)
  )

  // Red bottom triangle
  drawPath(
    Path().apply {
      moveTo(x, y + size)
      lineTo(x + size, y + size)
      lineTo(cx, cy)
      close()
    },
    color = Color(LudoRed.value)
  )

  // Green left triangle
  drawPath(
    Path().apply {
      moveTo(x, y)
      lineTo(x, y + size)
      lineTo(cx, cy)
      close()
    },
    color = Color(LudoGreen.value)
  )

  // Center golden star
  drawSafeStar(cx, cy, size * 0.22f)
}

private fun DrawScope.drawLudoToken(
  cx: Float,
  cy: Float,
  radius: Float,
  color: PlayerColor,
  isSelectable: Boolean,
  pulseScale: Float,
  tokenId: Int
) {
  val baseRadius = radius * pulseScale
  val primaryColor = Color(color.hex)

  // Selectable pulsing glow ring
  if (isSelectable) {
    drawCircle(
      color = Color(0xFFFFD700).copy(alpha = 0.5f),
      radius = baseRadius * 1.55f,
      center = Offset(cx, cy)
    )
    drawCircle(
      color = Color(0xFFFFEB3B),
      radius = baseRadius * 1.35f,
      center = Offset(cx, cy),
      style = Stroke(3.dp.toPx())
    )
  }

  // Token drop shadow
  drawCircle(
    color = Color.Black.copy(alpha = 0.45f),
    radius = baseRadius,
    center = Offset(cx + 2.dp.toPx(), cy + 3.dp.toPx())
  )

  // Token base (outer ring)
  drawCircle(
    color = Color.White,
    radius = baseRadius,
    center = Offset(cx, cy)
  )

  // Token main metallic body
  drawCircle(
    brush = Brush.radialGradient(
      listOf(
        primaryColor.copy(alpha = 0.9f),
        primaryColor,
        Color.Black.copy(alpha = 0.3f)
      ),
      center = Offset(cx - baseRadius * 0.3f, cy - baseRadius * 0.3f),
      radius = baseRadius * 1.2f
    ),
    radius = baseRadius * 0.88f,
    center = Offset(cx, cy)
  )

  // Inner emblem: golden crown / star / number
  drawCircle(
    color = Color.White.copy(alpha = 0.35f),
    radius = baseRadius * 0.45f,
    center = Offset(cx, cy)
  )
}

data class GridCoord(val x: Float, val y: Float)

fun getTokenBoardPosition(token: LudoToken, color: PlayerColor): GridCoord {
  // If in yard
  if (token.isInYard) {
    return when (color) {
      PlayerColor.YELLOW -> when (token.id) {
        0 -> GridCoord(1.6f, 1.6f)
        1 -> GridCoord(3.4f, 1.6f)
        2 -> GridCoord(1.6f, 3.4f)
        else -> GridCoord(3.4f, 3.4f)
      }
      PlayerColor.BLUE -> when (token.id) {
        0 -> GridCoord(10.6f, 1.6f)
        1 -> GridCoord(12.4f, 1.6f)
        2 -> GridCoord(10.6f, 3.4f)
        else -> GridCoord(12.4f, 3.4f)
      }
      PlayerColor.GREEN -> when (token.id) {
        0 -> GridCoord(1.6f, 10.6f)
        1 -> GridCoord(3.4f, 10.6f)
        2 -> GridCoord(1.6f, 12.4f)
        else -> GridCoord(3.4f, 12.4f)
      }
      PlayerColor.RED -> when (token.id) {
        0 -> GridCoord(10.6f, 10.6f)
        1 -> GridCoord(12.4f, 10.6f)
        2 -> GridCoord(10.6f, 12.4f)
        else -> GridCoord(12.4f, 12.4f)
      }
    }
  }

  // If in home corridor (steps 52..56)
  if (token.isInHomePath) {
    val homeIndex = token.step - 51 // 1..5
    return when (color) {
      PlayerColor.YELLOW -> GridCoord(7f, homeIndex.toFloat())
      PlayerColor.BLUE -> GridCoord((14 - homeIndex).toFloat(), 7f)
      PlayerColor.RED -> GridCoord(7f, (14 - homeIndex).toFloat())
      PlayerColor.GREEN -> GridCoord(homeIndex.toFloat(), 7f)
    }
  }

  // Ring tracks (steps 1..51)
  val globalCell = LudoGameEngine.getGlobalCell(color, token.step) ?: 0
  return getRingCoordinates(globalCell)
}

fun getRingCoordinates(globalCell: Int): GridCoord {
  // Standard 52 cells ring coordinate map
  return when (globalCell) {
    0 -> GridCoord(1f, 8f) // Green Start
    1 -> GridCoord(2f, 8f)
    2 -> GridCoord(3f, 8f)
    3 -> GridCoord(4f, 8f)
    4 -> GridCoord(5f, 8f)
    5 -> GridCoord(6f, 5f)
    6 -> GridCoord(6f, 4f)
    7 -> GridCoord(6f, 3f)
    8 -> GridCoord(6f, 2f) // Star
    9 -> GridCoord(6f, 1f)
    10 -> GridCoord(6f, 0f)
    11 -> GridCoord(7f, 0f)
    12 -> GridCoord(8f, 0f)
    13 -> GridCoord(8f, 1f) // Yellow/Blue start
    14 -> GridCoord(8f, 2f)
    15 -> GridCoord(8f, 3f)
    16 -> GridCoord(8f, 4f)
    17 -> GridCoord(8f, 5f)
    18 -> GridCoord(9f, 6f)
    19 -> GridCoord(10f, 6f)
    20 -> GridCoord(11f, 6f)
    21 -> GridCoord(12f, 6f) // Star
    22 -> GridCoord(13f, 6f)
    23 -> GridCoord(14f, 6f)
    24 -> GridCoord(14f, 7f)
    25 -> GridCoord(14f, 8f)
    26 -> GridCoord(13f, 8f) // Blue start
    27 -> GridCoord(12f, 8f)
    28 -> GridCoord(11f, 8f)
    29 -> GridCoord(10f, 8f)
    30 -> GridCoord(9f, 8f)
    31 -> GridCoord(8f, 9f)
    32 -> GridCoord(8f, 10f)
    33 -> GridCoord(8f, 11f)
    34 -> GridCoord(8f, 12f) // Star
    35 -> GridCoord(8f, 13f)
    36 -> GridCoord(8f, 14f)
    37 -> GridCoord(7f, 14f)
    38 -> GridCoord(6f, 14f)
    39 -> GridCoord(6f, 13f) // Red start
    40 -> GridCoord(6f, 12f)
    41 -> GridCoord(6f, 11f)
    42 -> GridCoord(6f, 10f)
    43 -> GridCoord(6f, 9f)
    44 -> GridCoord(5f, 6f)
    45 -> GridCoord(4f, 6f)
    46 -> GridCoord(3f, 6f)
    47 -> GridCoord(2f, 6f) // Star
    48 -> GridCoord(1f, 6f)
    49 -> GridCoord(0f, 6f)
    50 -> GridCoord(0f, 7f)
    51 -> GridCoord(0f, 8f)
    else -> GridCoord(7f, 7f)
  }
}

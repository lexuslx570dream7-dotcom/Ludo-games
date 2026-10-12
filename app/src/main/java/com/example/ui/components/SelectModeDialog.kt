package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.GameMode
import com.example.model.RuleVariant
import com.example.ui.theme.CoinGold

@Composable
fun SelectModeDialog(
  modeType: GameMode = GameMode.FOUR_PLAYERS,
  onDismiss: () -> Unit,
  onStartGame: (mode: GameMode, variant: RuleVariant, betAmount: Long) -> Unit,
  onJoinCode: (code: String) -> Unit = {}
) {
  var selectedVariant by remember { mutableStateOf(RuleVariant.CLASSIC) }
  var magicDiceEnabled by remember { mutableStateOf(false) }

  val betOptions = remember { listOf(500L, 1000L, 2000L, 5000L, 10000L, 50000L) }
  var betIndex by remember { mutableIntStateOf(0) }
  val currentBet = betOptions[betIndex]

  var showTeamCodeDialog by remember { mutableStateOf(false) }
  var enteredTeamCode by remember { mutableStateOf("") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF140D36),
              Color(0xFF10284D),
              Color(0xFF00796B),
              Color(0xFF004D40)
            )
          )
        )
        .padding(horizontal = 16.dp, vertical = 24.dp)
        .testTag("select_mode_screen")
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // 1. Top Bar with (?) on left and red close (X) on right
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Info button
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(Color(0xFF1976D2))
              .border(1.5.dp, Color(0xFF64B5F6), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.HelpOutline,
              contentDescription = "Help",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          // Close button
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(
                Brush.verticalGradient(
                  listOf(Color(0xFFE53935), Color(0xFFB71C1C))
                )
              )
              .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(8.dp))
              .clickable { onDismiss() }
              .testTag("close_select_mode_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Banner illustration with pawns and VS badge
        Box(
          modifier = Modifier
            .size(130.dp),
          contentAlignment = Alignment.Center
        ) {
          // Glow and pawns
          Box(
            modifier = Modifier
              .size(110.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.08f))
          )

          // 3D Pawns around fiery VS
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Left pawns
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFB300))
                .border(2.dp, Color.White, CircleShape)
            )

            // Center fiery VS
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                  Brush.radialGradient(
                    listOf(Color(0xFFFFD54F), Color(0xFFFF6D00), Color(0xFFD50000))
                  )
                )
                .border(2.dp, Color(0xFFFFEB3B), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "VS",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
              )
            }

            // Right pawns
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF00E676))
                .border(2.dp, Color.White, CircleShape)
            )
          }
        }

        // 3. Main Center Selection Card (Ocean Blue Container)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0xFF1A477A),
                  Color(0xFF0D325E),
                  Color(0xFF09294F)
                )
              )
            )
            .border(
              2.dp,
              Brush.verticalGradient(
                listOf(Color(0xFF42A5F5), Color(0xFF1565C0))
              ),
              RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // "Select Mode" Title
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = "Select Mode",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = null,
                tint = Color(0xFF90CAF9),
                modifier = Modifier.size(16.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Mode Buttons in 2x2 grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              ModeButton(
                title = "Classic",
                isSelected = selectedVariant == RuleVariant.CLASSIC,
                modifier = Modifier.weight(1f),
                onClick = { selectedVariant = RuleVariant.CLASSIC }
              )
              ModeButton(
                title = "Arrow",
                isSelected = selectedVariant == RuleVariant.ARROW,
                badge = "HOT",
                modifier = Modifier.weight(1f),
                onClick = { selectedVariant = RuleVariant.ARROW }
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              ModeButton(
                title = "Quick",
                isSelected = selectedVariant == RuleVariant.QUICK,
                modifier = Modifier.weight(1f),
                onClick = { selectedVariant = RuleVariant.QUICK }
              )
              ModeButton(
                title = "Master",
                isSelected = selectedVariant == RuleVariant.MASTER,
                modifier = Modifier.weight(1f),
                onClick = { selectedVariant = RuleVariant.MASTER }
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Magic Dice Option Card
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0C2442))
                .border(1.dp, Color(0xFF1E4D82), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = magicDiceEnabled,
                  onCheckedChange = { magicDiceEnabled = it },
                  colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFFFFD54F),
                    checkmarkColor = Color(0xFF3E2723),
                    uncheckedColor = Color(0xFF90CAF9)
                  )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.Casino,
                  contentDescription = null,
                  tint = CoinGold,
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Magic",
                  color = Color.White,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = "Magic Info",
                tint = Color(0xFF90CAF9),
                modifier = Modifier.size(18.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bet Selector: [-] 500 [+]
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0B2544))
                .border(1.dp, Color(0xFF1D5088), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              // Minus button
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF78909C))
                  .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                  .clickable {
                    if (betIndex > 0) betIndex--
                  }
                  .testTag("bet_minus_button"),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease Bet", tint = Color.White)
              }

              // Coin + Bet Amount
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.MonetizationOn,
                  contentDescription = null,
                  tint = CoinGold,
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "$currentBet",
                  color = Color.White,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Black
                )
              }

              // Plus button
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(
                    Brush.verticalGradient(
                      listOf(Color(0xFFFFD54F), Color(0xFFFF9800))
                    )
                  )
                  .border(1.dp, Color(0xFFFFF9C4), RoundedCornerShape(8.dp))
                  .clickable {
                    if (betIndex < betOptions.lastIndex) betIndex++
                  }
                  .testTag("bet_plus_button"),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Add, contentDescription = "Increase Bet", tint = Color(0xFF3E2723))
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: "Single" and "Create"
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Button(
                onClick = {
                  onStartGame(modeType, selectedVariant, currentBet)
                },
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .shadow(6.dp, RoundedCornerShape(24.dp))
                  .testTag("play_single_button"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFFFFD54F)
                ),
                shape = RoundedCornerShape(24.dp)
              ) {
                Text(
                  text = "Single",
                  color = Color(0xFF3E2723),
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black
                )
              }

              Button(
                onClick = {
                  onStartGame(modeType, selectedVariant, currentBet)
                },
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .shadow(6.dp, RoundedCornerShape(24.dp))
                  .testTag("create_room_button"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFFFFD54F)
                ),
                shape = RoundedCornerShape(24.dp)
              ) {
                Text(
                  text = "Create",
                  color = Color(0xFF3E2723),
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Bottom bar: "Have a team code? [Join]"
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF004D40).copy(alpha = 0.6f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Have a team code?",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )

          Button(
            onClick = { showTeamCodeDialog = true },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF26A69A)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(34.dp).testTag("join_team_code_button")
          ) {
            Text(
              text = "Join",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    if (showTeamCodeDialog) {
      Dialog(onDismissRequest = { showTeamCodeDialog = false }) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0A2540))
            .border(2.dp, Color(0xFF26A69A), RoundedCornerShape(16.dp))
            .padding(16.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Enter Room / Team Code", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = enteredTeamCode,
              onValueChange = { enteredTeamCode = it },
              placeholder = { Text("e.g. 849201", color = Color.Gray) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("team_code_input")
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              Button(
                onClick = {
                  if (enteredTeamCode.isNotBlank()) {
                    showTeamCodeDialog = false
                    onJoinCode(enteredTeamCode)
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A))
              ) {
                Text("Join Match", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun ModeButton(
  title: String,
  isSelected: Boolean,
  badge: String? = null,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(
          if (isSelected) Brush.horizontalGradient(
            listOf(Color(0xFF29B6F6), Color(0xFF0288D1))
          ) else Brush.horizontalGradient(
            listOf(Color(0xFF1E4975), Color(0xFF14375D))
          )
        )
        .border(
          if (isSelected) 2.dp else 1.dp,
          if (isSelected) Color(0xFFFFD54F) else Color(0xFF2E659E),
          RoundedCornerShape(12.dp)
        )
        .clickable { onClick() }
        .testTag("mode_option_$title"),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        if (isSelected) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
          text = title,
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Flame HOT badge
    badge?.let {
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .offset(x = 6.dp, y = (-8).dp)
          .clip(RoundedCornerShape(8.dp))
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFFFF3D00), Color(0xFFFF9100))
            )
          )
          .padding(horizontal = 5.dp, vertical = 2.dp)
      ) {
        Text(
          text = "🔥 $it",
          color = Color.White,
          fontSize = 9.sp,
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}

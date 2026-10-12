package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameRepository
import com.example.model.GameMode
import com.example.model.RuleVariant
import com.example.ui.components.AndroidStatusBar
import com.example.ui.theme.ActiveGreenGlow
import com.example.ui.theme.CoinGold

@Composable
fun PrivateLocalScreen(
  repository: GameRepository,
  onBack: () -> Unit,
  onStartPrivateGame: (code: String, bet: Long) -> Unit,
  onStartLocalGame: (playersCount: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Private Room, 1 = Local Pass & Play

  // Private room states
  var generatedCode by remember { mutableStateOf<String?>(null) }
  var joinCodeInput by remember { mutableStateOf("") }
  var selectedBet by remember { mutableStateOf(500L) }
  var statusMessage by remember { mutableStateOf<String?>(null) }

  // Local game states
  var localPlayersCount by remember { mutableIntStateOf(4) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF072124),
            Color(0xFF093139),
            Color(0xFF061B21),
            Color(0xFF021013)
          )
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp)
    ) {
      AndroidStatusBar()

      // Header row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("private_local_back_button")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
          text = "Private & Local Game",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 20.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFF073035),
        contentColor = Color.White,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = Color(0xFF29B6F6)
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Private Room", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Local Pass & Play", fontWeight = FontWeight.Bold) }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      statusMessage?.let { msg ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF00C853))
            .padding(10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(msg, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
      }

      when (selectedTab) {
        0 -> {
          // Private Room: Create & Join
          Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Card 1: Create Private Room
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF082C31))
                .border(1.5.dp, Color(0xFF0288D1), RoundedCornerShape(16.dp))
                .padding(16.dp)
            ) {
              Column {
                Text("Create Private Room", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Invite friends to a private real-time multiplayer match with custom bet.", color = Color(0xFF80CBC4), fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))

                // Bet chips
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  listOf(500L, 1000L, 2000L, 5000L).forEach { bet ->
                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedBet == bet) Color(0xFF0288D1) else Color(0xFF062327))
                        .border(1.dp, if (selectedBet == bet) Color.White else Color(0xFF00695C), RoundedCornerShape(8.dp))
                        .clickable { selectedBet = bet }
                        .padding(vertical = 8.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text("$bet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (generatedCode == null) {
                  Button(
                    onClick = {
                      val code = repository.createPrivateRoom(selectedBet)
                      generatedCode = code
                      statusMessage = "Room created with code $code! Share with friends."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29B6F6)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("generate_room_code_button")
                  ) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF003840))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate Invite Code", color = Color(0xFF003840), fontWeight = FontWeight.Black)
                  }
                } else {
                  // Show Code with Copy and Start Game button
                  Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.Center
                    ) {
                      Text(
                        text = "ROOM CODE: $generatedCode",
                        color = CoinGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                      )
                      IconButton(
                        onClick = {
                          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                          clipboard?.setPrimaryClip(ClipData.newPlainText("Ludo Code", generatedCode))
                          statusMessage = "Code copied to clipboard!"
                        }
                      ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White)
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                      onClick = {
                        onStartPrivateGame(generatedCode!!, selectedBet)
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                      shape = RoundedCornerShape(14.dp),
                      modifier = Modifier.fillMaxWidth().testTag("start_private_game_button")
                    ) {
                      Text("Start Private Match", color = Color(0xFF003810), fontWeight = FontWeight.Black)
                    }
                  }
                }
              }
            }

            // Card 2: Join Private Room
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF082C31))
                .border(1.dp, Color(0xFF00897B), RoundedCornerShape(16.dp))
                .padding(16.dp)
            ) {
              Column {
                Text("Join with Invite Code", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                  value = joinCodeInput,
                  onValueChange = { joinCodeInput = it },
                  placeholder = { Text("Enter 6-digit code", color = Color.Gray) },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth().testTag("join_code_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                  onClick = {
                    if (joinCodeInput.isNotBlank()) {
                      val bet = repository.validatePrivateRoomCode(joinCodeInput) ?: 500L
                      onStartPrivateGame(joinCodeInput, bet)
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier.fillMaxWidth().testTag("join_private_room_button")
                ) {
                  Text("Join Match Now", color = Color(0xFF3E2723), fontWeight = FontWeight.Black)
                }
              }
            }
          }
        }
        1 -> {
          // Local Pass & Play Game on Single Device
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF082C31))
              .border(1.5.dp, Color(0xFF00E676), RoundedCornerShape(16.dp))
              .padding(16.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.Smartphone, contentDescription = null, tint = ActiveGreenGlow, modifier = Modifier.size(48.dp))
              Spacer(modifier = Modifier.height(6.dp))
              Text("Pass & Play on 1 Device", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text("Play locally with friends or family on this single phone without internet.", color = Color(0xFF80CBC4), fontSize = 12.sp)

              Spacer(modifier = Modifier.height(18.dp))

              Text("Select Number of Players", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                listOf(2, 3, 4).forEach { count ->
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(12.dp))
                      .background(if (localPlayersCount == count) Color(0xFF00E676) else Color(0xFF062327))
                      .border(1.dp, if (localPlayersCount == count) Color.White else Color(0xFF004D40), RoundedCornerShape(12.dp))
                      .clickable { localPlayersCount = count }
                      .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "$count Players",
                      color = if (localPlayersCount == count) Color(0xFF003810) else Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.sp
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(20.dp))

              Button(
                onClick = { onStartLocalGame(localPlayersCount) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("start_local_pass_play_button")
              ) {
                Text("Start Local Game", color = Color(0xFF003810), fontWeight = FontWeight.Black, fontSize = 15.sp)
              }
            }
          }
        }
      }
    }
  }
}

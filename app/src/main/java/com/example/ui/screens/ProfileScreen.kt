package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GameRepository
import com.example.model.Achievement
import com.example.model.UserProfile
import com.example.ui.components.AndroidStatusBar
import com.example.ui.components.formatNumber
import com.example.ui.theme.ActiveGreenGlow
import com.example.ui.theme.CoinGold
import com.example.ui.theme.GemCyan

@Composable
fun ProfileScreen(
  repository: GameRepository,
  onNavigateTab: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val userProfile by repository.userProfile.collectAsState()
  val achievements by repository.achievements.collectAsState()
  val scrollState = rememberScrollState()

  var showEditNameDialog by remember { mutableStateOf(false) }
  var editedNameInput by remember { mutableStateOf(userProfile.username) }

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
        .verticalScroll(scrollState)
        .padding(bottom = 76.dp)
    ) {
      AndroidStatusBar()

      // Header title
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Player Profile", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text("ID: ${userProfile.id}", color = Color(0xFF80CBC4), fontSize = 12.sp, fontWeight = FontWeight.Medium)
      }

      // Profile Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF0A3C42), Color(0xFF06282D))
            )
          )
          .border(1.5.dp, ActiveGreenGlow, RoundedCornerShape(20.dp))
          .padding(16.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          // Large Avatar with green glowing border
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(
                Brush.sweepGradient(
                  listOf(Color(0xFF00E676), Color(0xFF00B0FF), Color(0xFF00E676))
                )
              )
              .padding(4.dp),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFF0D47A1)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Username + Edit button
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = userProfile.username,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
              onClick = {
                editedNameInput = userProfile.username
                showEditNameDialog = true
              },
              modifier = Modifier.size(28.dp).testTag("edit_username_button")
            ) {
              Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = ActiveGreenGlow, modifier = Modifier.size(18.dp))
            }
          }

          Text("Level ${userProfile.level} Champion", color = CoinGold, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

          Spacer(modifier = Modifier.height(10.dp))

          // XP Progress Bar
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Experience", color = Color(0xFFB0BEC5), fontSize = 11.sp)
              Text("${userProfile.currentXp} / ${userProfile.maxXp} XP", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
              progress = { (userProfile.currentXp.toFloat() / userProfile.maxXp).coerceIn(0f, 1f) },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = ActiveGreenGlow,
              trackColor = Color(0xFF031C1F)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Currencies Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            // Coins
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF031E22))
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CoinGold, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(formatNumber(userProfile.coins), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            // Gems
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF031E22))
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.Diamond, contentDescription = null, tint = GemCyan, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("${userProfile.gems}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Statistics Section
      Text(
        text = "Match Statistics",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatCard(title = "Games", value = "${userProfile.totalGames}", modifier = Modifier.weight(1f))
        StatCard(title = "Wins", value = "${userProfile.wins}", valueColor = ActiveGreenGlow, modifier = Modifier.weight(1f))
        StatCard(title = "Win Rate", value = "${userProfile.winRate}%", valueColor = CoinGold, modifier = Modifier.weight(1f))
        StatCard(title = "Streak", value = "${userProfile.currentStreak}", modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Achievements Section
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Achievements", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(
          "${achievements.count { it.isUnlocked }} / ${achievements.size} Unlocked",
          color = CoinGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.height(8.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        achievements.forEach { ach ->
          AchievementItemRow(
            achievement = ach,
            onClaim = { repository.claimAchievement(ach.id) }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Independent Settings Section
      Text(
        text = "Audio & Settings",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF072A2E))
          .border(1.dp, Color(0xFF0A4F56), RoundedCornerShape(16.dp))
          .padding(14.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Ludo Music toggle (independent!)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Ludo Music", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
              Text("Theme background soundtrack", color = Color(0xFF80CBC4), fontSize = 11.sp)
            }
            Switch(
              checked = userProfile.musicEnabled,
              onCheckedChange = { repository.toggleMusic(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = ActiveGreenGlow),
              modifier = Modifier.testTag("toggle_music_switch")
            )
          }

          // Game Sound toggle (independent!)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Game Sound", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
              Text("Dice roll and pawn capture audio", color = Color(0xFF80CBC4), fontSize = 11.sp)
            }
            Switch(
              checked = userProfile.soundEnabled,
              onCheckedChange = { repository.toggleSound(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = ActiveGreenGlow),
              modifier = Modifier.testTag("toggle_sound_switch")
            )
          }

          // Vibration toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Haptic Vibration", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
              Text("Tactile feedback on dice roll", color = Color(0xFF80CBC4), fontSize = 11.sp)
            }
            Switch(
              checked = userProfile.vibrationEnabled,
              onCheckedChange = { repository.toggleVibration(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = ActiveGreenGlow),
              modifier = Modifier.testTag("toggle_vibration_switch")
            )
          }
        }
      }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
      Dialog(onDismissRequest = { showEditNameDialog = false }) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF093139))
            .border(2.dp, ActiveGreenGlow, RoundedCornerShape(16.dp))
            .padding(16.dp)
        ) {
          Column {
            Text("Edit Username", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = editedNameInput,
              onValueChange = { editedNameInput = it },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("username_input")
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              Button(
                onClick = {
                  repository.updateUsername(editedNameInput)
                  showEditNameDialog = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActiveGreenGlow)
              ) {
                Text("Save", color = Color(0xFF003810), fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // Permanent Bottom Navigation Bar
    BottomNavigationBar(
      currentTabIndex = 2,
      onTabSelected = onNavigateTab,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
    )
  }
}

@Composable
fun StatCard(
  title: String,
  value: String,
  valueColor: Color = Color.White,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF072A2E))
      .border(1.dp, Color(0xFF0A4F56), RoundedCornerShape(12.dp))
      .padding(vertical = 10.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(value, color = valueColor, fontWeight = FontWeight.Black, fontSize = 15.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(title, color = Color(0xFF80CBC4), fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
  }
}

@Composable
fun AchievementItemRow(
  achievement: Achievement,
  onClaim: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF06292D))
      .border(1.dp, if (achievement.isUnlocked) CoinGold.copy(alpha = 0.5f) else Color(0xFF0B464C), RoundedCornerShape(12.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(if (achievement.isUnlocked) Color(0xFFFFB300) else Color(0xFF004D40)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.EmojiEvents,
          contentDescription = null,
          tint = if (achievement.isUnlocked) Color(0xFF3E2723) else Color.Gray,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(achievement.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(achievement.description, color = Color(0xFF80CBC4), fontSize = 10.sp)
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
          progress = { (achievement.currentProgress.toFloat() / achievement.maxProgress).coerceIn(0f, 1f) },
          modifier = Modifier
            .width(110.dp)
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = CoinGold,
          trackColor = Color(0xFF00383D)
        )
      }
    }

    if (achievement.isClaimed) {
      Text("Claimed", color = Color(0xFF80CBC4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    } else if (achievement.isUnlocked) {
      Button(
        onClick = onClaim,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(32.dp).testTag("claim_ach_${achievement.id}")
      ) {
        Text("Claim", color = Color(0xFF3E2723), fontWeight = FontWeight.Black, fontSize = 11.sp)
      }
    } else {
      Text("${achievement.currentProgress}/${achievement.maxProgress}", color = Color.Gray, fontSize = 11.sp)
    }
  }
}

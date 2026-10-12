package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.ui.components.AndroidStatusBar
import com.example.ui.components.formatNumber
import com.example.ui.theme.ActiveGreenGlow
import com.example.ui.theme.CoinGold
import com.example.ui.theme.GemCyan

@Composable
fun TournamentScreen(
  repository: GameRepository,
  onBack: () -> Unit,
  onStartTournamentMatch: (Tournament) -> Unit,
  modifier: Modifier = Modifier
) {
  val tournaments by repository.tournaments.collectAsState()
  var showCreateAdminDialog by remember { mutableStateOf(false) }
  var tournamentNotice by remember { mutableStateOf<String?>(null) }

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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBack, modifier = Modifier.testTag("tournament_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Text(
            text = "Tournaments",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
          )
        }

        // Admin Tournament Creator Button
        Button(
          onClick = { showCreateAdminDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.height(34.dp).testTag("admin_create_tournament_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Host", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }

      tournamentNotice?.let { notice ->
        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF00C853))
            .padding(8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(notice, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(tournaments) { tourney ->
          TournamentCardItem(
            tournament = tourney,
            onJoin = {
              if (tourney.isJoined) {
                onStartTournamentMatch(tourney)
              } else {
                val success = repository.joinTournament(tourney.id)
                if (success) {
                  tournamentNotice = "Registered for ${tourney.title}! Match starting!"
                  onStartTournamentMatch(tourney)
                } else {
                  tournamentNotice = "Insufficient coins to register (${tourney.entryFeeCoins} needed)."
                }
              }
            }
          )
        }
      }
    }

    // Admin Host Tournament Dialog
    if (showCreateAdminDialog) {
      HostTournamentDialog(
        onDismiss = { showCreateAdminDialog = false },
        onCreate = { title, fee, prize, gems ->
          repository.createAdminTournament(title, fee, prize, gems)
          showCreateAdminDialog = false
          tournamentNotice = "Created new tournament '$title'!"
        }
      )
    }
  }
}

@Composable
fun TournamentCardItem(
  tournament: Tournament,
  onJoin: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFF083338), Color(0xFF041F23))
        )
      )
      .border(
        1.5.dp,
        if (tournament.status == TournamentStatus.LIVE) Color(0xFFFF5252) else Color(0xFF00E676),
        RoundedCornerShape(16.dp)
      )
      .padding(14.dp)
  ) {
    Column {
      // Top row: Title + Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(tournament.title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
              if (tournament.status == TournamentStatus.LIVE) Color(0xFFFF5252) else Color(0xFF00E676)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (tournament.status == TournamentStatus.LIVE) "LIVE" else tournament.startTimeFormatted,
            color = if (tournament.status == TournamentStatus.LIVE) Color.White else Color(0xFF003810),
            fontWeight = FontWeight.Black,
            fontSize = 10.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Prize and Entry details
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Grand Prize", color = Color(0xFF80CBC4), fontSize = 11.sp)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CoinGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(formatNumber(tournament.prizeCoins), color = CoinGold, fontWeight = FontWeight.Black, fontSize = 15.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.Default.Diamond, contentDescription = null, tint = GemCyan, modifier = Modifier.size(14.dp))
            Text("${tournament.prizeGems}", color = GemCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text("Entry Fee", color = Color(0xFF80CBC4), fontSize = 11.sp)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CoinGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("${tournament.entryFeeCoins}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Players count and Join Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Players: ${tournament.participantsCount}/${tournament.maxParticipants}",
          color = Color(0xFFB0BEC5),
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )

        Button(
          onClick = onJoin,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (tournament.isJoined) Color(0xFF00E676) else Color(0xFFFFB300)
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.height(36.dp).testTag("join_tournament_${tournament.id}")
        ) {
          Text(
            text = if (tournament.isJoined) "PLAY MATCH" else "JOIN TOURNAMENT",
            color = if (tournament.isJoined) Color(0xFF003810) else Color(0xFF3E2723),
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
fun HostTournamentDialog(
  onDismiss: () -> Unit,
  onCreate: (title: String, fee: Long, prize: Long, gems: Int) -> Unit
) {
  var title by remember { mutableStateOf("Championship Clash") }
  var fee by remember { mutableStateOf("1000") }
  var prize by remember { mutableStateOf("15000") }
  var gems by remember { mutableStateOf("25") }

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFF072A2E))
        .border(2.dp, ActiveGreenGlow, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column {
        Text("Host New Tournament", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Tournament Title", color = Color(0xFF80CBC4)) },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = fee,
          onValueChange = { fee = it },
          label = { Text("Entry Fee Coins", color = Color(0xFF80CBC4)) },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = prize,
          onValueChange = { prize = it },
          label = { Text("Prize Pool Coins", color = Color(0xFF80CBC4)) },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Button(
            onClick = {
              onCreate(title, fee.toLongOrNull() ?: 1000L, prize.toLongOrNull() ?: 15000L, gems.toIntOrNull() ?: 25)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
          ) {
            Text("Create Tournament", color = Color(0xFF003810), fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

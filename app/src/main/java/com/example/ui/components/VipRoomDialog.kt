package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CoinGold
import com.example.ui.theme.GemCyan

@Composable
fun VipRoomDialog(
  userCoins: Long,
  onDismiss: () -> Unit,
  onEnterVipRoom: (stakeCoins: Long) -> Unit
) {
  var selectedStake by remember { mutableStateOf(10000L) }
  val stakes = remember { listOf(10000L, 50000L, 100000L) }

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF3E0A12),
              Color(0xFF20050A),
              Color(0xFF100204)
            )
          )
        )
        .border(
          2.5.dp,
          Brush.linearGradient(
            listOf(Color(0xFFFFD700), Color(0xFFFFA000), Color(0xFFFFD700))
          ),
          RoundedCornerShape(24.dp)
        )
        .padding(18.dp)
        .testTag("vip_room_dialog")
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD700)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF3E0A12), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "VIP ROYAL LOUNGE",
              color = CoinGold,
              fontWeight = FontWeight.Black,
              fontSize = 17.sp,
              letterSpacing = 0.5.sp
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "High-Roller Championship Stakes",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
        Text(
          text = "Exclusive gold-trimmed board, VIP Crown animation, 2x XP bonus!",
          color = Color(0xFFFFCDD2),
          fontSize = 11.sp,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Select Stakes
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          stakes.forEach { stake ->
            val isSelected = selectedStake == stake
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) Color(0xFF5E131E) else Color(0xFF28070C))
                .border(
                  if (isSelected) 2.dp else 1.dp,
                  if (isSelected) CoinGold else Color(0xFF4A1017),
                  RoundedCornerShape(14.dp)
                )
                .clickable { selectedStake = stake }
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CoinGold, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(formatNumber(stake), color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
              }

              Text(
                text = "Prize: ${formatNumber(stake * 2)}",
                color = CoinGold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            onEnterVipRoom(selectedStake)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFD700)
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("enter_vip_room_button")
        ) {
          Text(
            text = "ENTER VIP ROOM",
            color = Color(0xFF3E0A12),
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
          )
        }
      }
    }
  }
}

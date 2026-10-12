package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AndroidStatusBar(
  modifier: Modifier = Modifier
) {
  var currentTime by remember {
    mutableStateOf(SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()))
  }

  LaunchedEffect(Unit) {
    while (true) {
      delay(30000)
      currentTime = SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())
    }
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(34.dp)
      .padding(horizontal = 16.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    // Current time on left
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = currentTime,
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.2.sp
      )

      // Right indicators: Signal, Wi-Fi, Battery 78%
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.NetworkCell,
          contentDescription = "Signal",
          tint = Color.White,
          modifier = Modifier.size(15.dp)
        )
        Icon(
          imageVector = Icons.Default.Wifi,
          contentDescription = "Wi-Fi",
          tint = Color.White,
          modifier = Modifier.size(15.dp)
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.BatteryFull,
            contentDescription = "Battery",
            tint = Color.White,
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = "78%",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Camera punch hole in exact center (like modern smartphones in screenshots)
    Box(
      modifier = Modifier
        .size(11.dp)
        .clip(CircleShape)
        .background(Color.Black)
    )
  }
}

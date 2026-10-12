package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.model.UserProfile
import com.example.ui.theme.CoinGold
import com.example.ui.theme.CoinOrange
import com.example.ui.theme.GemCyan

@Composable
fun TopHeaderBar(
  userProfile: UserProfile,
  onAvatarClick: () -> Unit = {},
  onAddCoinsClick: () -> Unit = {},
  onAddGemsClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // 1. Avatar with green glowing border & level badge
    Box(
      modifier = Modifier
        .size(54.dp)
        .clickable(interactionSource = interactionSource, indication = null) { onAvatarClick() }
        .testTag("avatar_button"),
      contentAlignment = Alignment.Center
    ) {
      // Glow border
      Box(
        modifier = Modifier
          .size(52.dp)
          .clip(CircleShape)
          .background(
            Brush.sweepGradient(
              listOf(
                Color(0xFF00E676),
                Color(0xFF00C853),
                Color(0xFFB9F6CA),
                Color(0xFF00E676)
              )
            )
          )
          .padding(3.dp),
        contentAlignment = Alignment.Center
      ) {
        // Inner avatar photo
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(Color(0xFF1E88E5), Color(0xFF0D47A1))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User Avatar",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
          )
        }
      }

      // Level Badge Hexagon/Tag at bottom-right of avatar
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .offset(x = 2.dp, y = 2.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
            )
          )
          .border(1.dp, Color(0xFF5D4037), RoundedCornerShape(4.dp))
          .padding(horizontal = 4.dp, vertical = 1.dp)
      ) {
        Text(
          text = "${userProfile.level}",
          color = Color(0xFF2E1C0C),
          fontSize = 11.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    // 2. Main pill bar containing Coins and Gems
    Row(
      modifier = Modifier
        .weight(1f)
        .height(44.dp)
        .clip(RoundedCornerShape(22.dp))
        .background(
          Brush.horizontalGradient(
            listOf(
              Color(0xFF053427),
              Color(0xFF0A4D3B),
              Color(0xFF042B20)
            )
          )
        )
        .border(
          1.5.dp,
          Brush.horizontalGradient(
            listOf(
              Color(0xFF00E676),
              Color(0xFF1DE9B6),
              Color(0xFF00C853)
            )
          ),
          RoundedCornerShape(22.dp)
        )
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Coins Section
      Row(
        modifier = Modifier
          .clickable(interactionSource = interactionSource, indication = null) { onAddCoinsClick() }
          .testTag("add_coins_button"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Gold Coin Icon
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(Color(0xFFFFEE58), Color(0xFFFFA000))
              )
            )
            .border(1.dp, Color(0xFFFFD54F), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.MonetizationOn,
            contentDescription = "Coins",
            tint = Color(0xFF5D4037),
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Text(
          text = formatNumber(userProfile.coins),
          color = Color.White,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(5.dp))

        // Yellow (+) button
        Box(
          modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFFFFEE58), Color(0xFFFF9800))
              )
            )
            .border(1.dp, Color(0xFFFFF9C4), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Coins",
            tint = Color(0xFF3E2723),
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Gems Section
      Row(
        modifier = Modifier
          .clickable(interactionSource = interactionSource, indication = null) { onAddGemsClick() }
          .testTag("add_gems_button"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Diamond Gem Icon
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(Color(0xFF80DEEA), Color(0xFF00ACC1))
              )
            )
            .border(1.dp, Color(0xFFE0F7FA), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Diamond,
            contentDescription = "Gems",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Text(
          text = "${userProfile.gems}",
          color = Color.White,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(5.dp))

        // Yellow (+) button
        Box(
          modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFFFFEE58), Color(0xFFFF9800))
              )
            )
            .border(1.dp, Color(0xFFFFF9C4), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Gems",
            tint = Color(0xFF3E2723),
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}

fun formatNumber(number: Long): String {
  return when {
    number >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", number / 1_000_000.0)
    number >= 10_000 -> String.format(java.util.Locale.US, "%.1fK", number / 1_000.0)
    else -> String.format(java.util.Locale.US, "%,d", number)
  }
}

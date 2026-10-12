package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CoinGold
import com.example.ui.theme.GemCyan
import kotlinx.coroutines.launch
import kotlin.random.Random

data class StorePack(
  val id: String,
  val title: String,
  val amountText: String,
  val bonusText: String?,
  val priceText: String,
  val isFree: Boolean = false,
  val isGem: Boolean = false,
  val rewardCoins: Long = 0,
  val rewardGems: Int = 0
)

@Composable
fun CoinGemStoreDialog(
  initialTab: Int = 0, // 0 = Coins, 1 = Gems, 2 = Lucky Spin
  onDismiss: () -> Unit,
  onRewardClaimed: (coins: Long, gems: Int) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(initialTab) }
  var claimedMessage by remember { mutableStateOf<String?>(null) }

  val coinPacks = remember {
    listOf(
      StorePack("C1", "Daily Free Gift", "+1,000 Coins", "Claim once daily", "FREE", isFree = true, rewardCoins = 1000L),
      StorePack("C2", "Starter Stack", "+5,000 Coins", "+10% Extra", "$0.99", rewardCoins = 5000L),
      StorePack("C3", "Pouch of Gold", "+20,000 Coins", "+25% Extra", "$2.99", rewardCoins = 20000L),
      StorePack("C4", "Treasure Chest", "+100,000 Coins", "+50% Extra", "$9.99", rewardCoins = 100000L),
      StorePack("C5", "VIP Royal Vault", "+500,000 Coins", "BEST VALUE", "$29.99", rewardCoins = 500000L)
    )
  }

  val gemPacks = remember {
    listOf(
      StorePack("G1", "Daily Gem Drop", "+3 Gems", "Daily bonus", "FREE", isFree = true, isGem = true, rewardGems = 3),
      StorePack("G2", "Gem Bag", "+15 Gems", "+2 Bonus", "$0.99", isGem = true, rewardGems = 15),
      StorePack("G3", "Crystal Cache", "+50 Gems", "+10 Bonus", "$2.99", isGem = true, rewardGems = 50),
      StorePack("G4", "Royal Diamond Stash", "+200 Gems", "+50 Bonus", "$9.99", isGem = true, rewardGems = 200)
    )
  }

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF0F2027),
              Color(0xFF203A43),
              Color(0xFF2C5364)
            )
          )
        )
        .border(
          2.dp,
          Brush.linearGradient(
            listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
          ),
          RoundedCornerShape(24.dp)
        )
        .padding(16.dp)
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Header row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Game Store & Rewards",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("close_store_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color(0xFF0A1826),
          contentColor = Color.White,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = Color(0xFFFFD54F)
            )
          }
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("Coins", fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("Gems", fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text("Lucky Spin", fontWeight = FontWeight.Bold) }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        claimedMessage?.let { msg ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF00E676))
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(msg, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.height(8.dp))
        }

        when (selectedTab) {
          0 -> {
            LazyColumn(
              modifier = Modifier.height(280.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(coinPacks) { pack ->
                StoreItemRow(
                  pack = pack,
                  onClaim = {
                    onRewardClaimed(pack.rewardCoins, pack.rewardGems)
                    claimedMessage = "Success! Added ${pack.amountText} to balance!"
                  }
                )
              }
            }
          }
          1 -> {
            LazyColumn(
              modifier = Modifier.height(280.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(gemPacks) { pack ->
                StoreItemRow(
                  pack = pack,
                  onClaim = {
                    onRewardClaimed(pack.rewardCoins, pack.rewardGems)
                    claimedMessage = "Success! Added ${pack.amountText} to balance!"
                  }
                )
              }
            }
          }
          2 -> {
            LuckyWheelSpinView(
              onPrizeWon = { coins, gems ->
                onRewardClaimed(coins, gems)
                claimedMessage = "Jackpot! Won $coins Coins and $gems Gems!"
              }
            )
          }
        }
      }
    }
  }
}

@Composable
fun StoreItemRow(
  pack: StorePack,
  onClaim: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF132838))
      .border(1.dp, Color(0xFF264966), RoundedCornerShape(12.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(
            if (pack.isGem) Brush.radialGradient(listOf(Color(0xFF80DEEA), Color(0xFF00ACC1)))
            else Brush.radialGradient(listOf(Color(0xFFFFEE58), Color(0xFFFFA000)))
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (pack.isGem) Icons.Default.Diamond else Icons.Default.MonetizationOn,
          contentDescription = null,
          tint = if (pack.isGem) Color.White else Color(0xFF4E342E),
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(pack.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(pack.amountText, color = if (pack.isGem) GemCyan else CoinGold, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        pack.bonusText?.let {
          Text(it, color = Color(0xFFB0BEC5), fontSize = 10.sp)
        }
      }
    }

    Button(
      onClick = onClaim,
      colors = ButtonDefaults.buttonColors(
        containerColor = if (pack.isFree) Color(0xFF00E676) else Color(0xFFFFB300)
      ),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.height(36.dp).testTag("claim_${pack.id}")
    ) {
      Text(
        text = pack.priceText,
        color = if (pack.isFree) Color(0xFF003310) else Color(0xFF3E2723),
        fontWeight = FontWeight.Black,
        fontSize = 12.sp
      )
    }
  }
}

@Composable
fun LuckyWheelSpinView(
  onPrizeWon: (coins: Long, gems: Int) -> Unit
) {
  val rotation = remember { Animatable(0f) }
  val coroutineScope = rememberCoroutineScope()
  var isSpinning by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .height(280.dp)
      .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      "Daily Lucky Wheel",
      color = CoinGold,
      fontWeight = FontWeight.Bold,
      fontSize = 16.sp
    )
    Text(
      "Spin for a chance to win up to 50,000 Coins!",
      color = Color.White.copy(alpha = 0.8f),
      fontSize = 12.sp
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Wheel visual representation
    Box(
      modifier = Modifier
        .size(130.dp)
        .rotate(rotation.value)
        .clip(CircleShape)
        .background(
          Brush.sweepGradient(
            listOf(
              Color(0xFFFF5252),
              Color(0xFFFFD740),
              Color(0xFF69F0AE),
              Color(0xFF40C4FF),
              Color(0xFFE040FB),
              Color(0xFFFF5252)
            )
          )
        )
        .border(3.dp, Color(0xFFFFD54F), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(Color(0xFF0C1B33))
          .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Star,
          contentDescription = null,
          tint = CoinGold,
          modifier = Modifier.size(24.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = {
        if (!isSpinning) {
          isSpinning = true
          coroutineScope.launch {
            val extraRotations = 360f * 5 + Random.nextInt(0, 360)
            rotation.animateTo(
              targetValue = rotation.value + extraRotations,
              animationSpec = tween(durationMillis = 2500)
            )
            val wonCoins = listOf(1000L, 2500L, 5000L, 10000L, 25000L).random()
            val wonGems = listOf(2, 5, 10).random()
            onPrizeWon(wonCoins, wonGems)
            isSpinning = false
          }
        }
      },
      enabled = !isSpinning,
      colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFFFFB300)
      ),
      shape = RoundedCornerShape(20.dp),
      modifier = Modifier.testTag("spin_wheel_button")
    ) {
      Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF3E2723))
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = if (isSpinning) "Spinning..." else "SPIN FOR FREE!",
        color = Color(0xFF3E2723),
        fontWeight = FontWeight.Black
      )
    }
  }
}

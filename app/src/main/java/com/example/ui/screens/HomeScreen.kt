package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GameMode
import com.example.model.UserProfile
import com.example.ui.components.AndroidStatusBar
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.ActiveGreenGlow

@Composable
fun HomeScreen(
  userProfile: UserProfile,
  onOpenStore: (initialTab: Int) -> Unit,
  onSelectGameMode: (GameMode) -> Unit,
  onOpenTournament: () -> Unit,
  onOpenPrivateLocal: () -> Unit,
  onOpenVipRoom: () -> Unit,
  onNavigateTab: (tabIndex: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  // Floating ambient animation for 3D elements
  val infiniteTransition = rememberInfiniteTransition(label = "ambient")
  val floatOffset by infiniteTransition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "float"
  )

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
        .padding(bottom = 76.dp) // space for bottom navigation bar
    ) {
      // 1. Android Status Bar (top)
      AndroidStatusBar()

      // 2. Top Player Header
      TopHeaderBar(
        userProfile = userProfile,
        onAvatarClick = { onNavigateTab(2) }, // opens profile
        onAddCoinsClick = { onOpenStore(0) },
        onAddGemsClick = { onOpenStore(1) }
      )

      Spacer(modifier = Modifier.height(4.dp))

      // 3. Main Ludo Hero Section (Royal Crown + 3D Ludo Logo + 3D Board)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp)
          .height(180.dp)
          .clip(RoundedCornerShape(22.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF003840), Color(0xFF001F24))
            )
          )
          .border(
            1.5.dp,
            Brush.verticalGradient(
              listOf(Color(0xFF00E676), Color(0xFF00897B))
            ),
            RoundedCornerShape(22.dp)
          )
          .testTag("hero_ludo_banner"),
        contentAlignment = Alignment.Center
      ) {
        // High resolution generated 3D Hero Art
        Image(
          painter = painterResource(id = R.drawable.ic_ludo_hero),
          contentDescription = "Ludo Hero Art",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxSize()
            .offset(y = floatOffset.dp)
        )

        // Subtle gradient overlay for readability & glow
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.35f)
                )
              )
            )
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Mode Cards Section
      // Top Row: "2 & 4 Players" (Left) and "Team" (Right)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // 2 & 4 Players Card
        GameModeLargeCard(
          title = "2&4 Players",
          imageResId = R.drawable.ic_mode_two_four,
          topGradient = Color(0xFFFF9800),
          bottomGradient = Color(0xFFE65100),
          borderColor = Color(0xFFFFD54F),
          modifier = Modifier.weight(1f),
          onClick = { onSelectGameMode(GameMode.FOUR_PLAYERS) },
          testTag = "card_2_4_players"
        )

        // Team Card
        GameModeLargeCard(
          title = "Team",
          imageResId = R.drawable.ic_mode_team,
          topGradient = Color(0xFFAB47BC),
          bottomGradient = Color(0xFF6A1B9A),
          borderColor = Color(0xFFE1BEE7),
          modifier = Modifier.weight(1f),
          onClick = { onSelectGameMode(GameMode.TEAM_2V2) },
          testTag = "card_team"
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Row of 3 Cards: Tournament, Private & Local, VIP Room
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Tournament Card
        GameModeSmallCard(
          title = "Tournament",
          imageResId = R.drawable.ic_mode_tournament,
          topGradient = Color(0xFF43A047),
          bottomGradient = Color(0xFF1B5E20),
          borderColor = Color(0xFFA5D6A7),
          modifier = Modifier.weight(1f),
          onClick = onOpenTournament,
          testTag = "card_tournament"
        )

        // Private & Local Card
        GameModeSmallCard(
          title = "Private&Local",
          imageResId = R.drawable.ic_mode_private,
          topGradient = Color(0xFF039BE5),
          bottomGradient = Color(0xFF01579B),
          borderColor = Color(0xFF81D4FA),
          modifier = Modifier.weight(1f),
          onClick = onOpenPrivateLocal,
          testTag = "card_private_local"
        )

        // VIP Room Card
        GameModeSmallCard(
          title = "VIP Room",
          imageResId = R.drawable.ic_mode_vip,
          topGradient = Color(0xFFE53935),
          bottomGradient = Color(0xFFB71C1C),
          borderColor = Color(0xFFFFCDD2),
          modifier = Modifier.weight(1f),
          onClick = onOpenVipRoom,
          testTag = "card_vip_room"
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5. Bottom Foreground 3D Ludo Board Atmosphere (Perspective board with dice and 4 pawns)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp)
          .height(170.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF04191D))
          .border(1.dp, Color(0xFF00796B), RoundedCornerShape(20.dp))
          .clickable { onSelectGameMode(GameMode.FOUR_PLAYERS) }
          .testTag("bottom_quick_play_board"),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_ludo_hero),
          contentDescription = "Live Board",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Quick Play Floating Badge
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(y = (-12).dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF00E676), Color(0xFF00C853))
              )
            )
            .border(1.5.dp, Color.White, RoundedCornerShape(20.dp))
            .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
          Text(
            text = "TAP TO PLAY NOW",
            color = Color(0xFF003810),
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            letterSpacing = 0.5.sp
          )
        }
      }
    }

    // 6. Permanent Bottom Navigation Bar (Home, Social, Profile)
    BottomNavigationBar(
      currentTabIndex = 0,
      onTabSelected = onNavigateTab,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
    )
  }
}

@Composable
fun GameModeLargeCard(
  title: String,
  imageResId: Int,
  topGradient: Color,
  bottomGradient: Color,
  borderColor: Color,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier = modifier
      .height(130.dp)
      .clip(RoundedCornerShape(18.dp))
      .background(
        Brush.verticalGradient(
          listOf(topGradient, bottomGradient)
        )
      )
      .border(2.dp, borderColor, RoundedCornerShape(18.dp))
      .clickable(interactionSource = interactionSource, indication = null) { onClick() }
      .testTag(testTag)
  ) {
    // Dedicated 3D Artwork
    Image(
      painter = painterResource(id = imageResId),
      contentDescription = title,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(18.dp))
    )

    // Dark bottom vignette for title text
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            0.6f to Color.Transparent,
            1.0f to Color.Black.copy(alpha = 0.65f)
          )
        )
    )

    // Label with drop shadow
    Text(
      text = title,
      color = Color.White,
      fontWeight = FontWeight.Black,
      fontSize = 16.sp,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 6.dp)
    )
  }
}

@Composable
fun GameModeSmallCard(
  title: String,
  imageResId: Int,
  topGradient: Color,
  bottomGradient: Color,
  borderColor: Color,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier = modifier
      .height(105.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.verticalGradient(
          listOf(topGradient, bottomGradient)
        )
      )
      .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
      .clickable(interactionSource = interactionSource, indication = null) { onClick() }
      .testTag(testTag)
  ) {
    // Dedicated 3D Artwork
    Image(
      painter = painterResource(id = imageResId),
      contentDescription = title,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(16.dp))
    )

    // Dark bottom vignette
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            0.5f to Color.Transparent,
            1.0f to Color.Black.copy(alpha = 0.7f)
          )
        )
    )

    // Label
    Text(
      text = title,
      color = Color.White,
      fontWeight = FontWeight.Black,
      fontSize = 11.5.sp,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 4.dp)
    )
  }
}

@Composable
fun BottomNavigationBar(
  currentTabIndex: Int,
  onTabSelected: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .padding(horizontal = 14.dp, vertical = 8.dp)
      .clip(RoundedCornerShape(26.dp))
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF032624).copy(alpha = 0.95f),
            Color(0xFF011413).copy(alpha = 0.98f)
          )
        )
      )
      .border(
        1.5.dp,
        Brush.horizontalGradient(
          listOf(
            Color(0xFF00E676).copy(alpha = 0.4f),
            Color(0xFF00B0FF).copy(alpha = 0.3f),
            Color(0xFF00E676).copy(alpha = 0.4f)
          )
        ),
        RoundedCornerShape(26.dp)
      )
      .padding(vertical = 6.dp, horizontal = 12.dp)
      .testTag("bottom_nav_bar")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Home
      NavTabItem(
        title = "Home",
        icon = Icons.Default.Home,
        isSelected = currentTabIndex == 0,
        onClick = { onTabSelected(0) },
        testTag = "nav_tab_home"
      )

      // 2. Social
      NavTabItem(
        title = "Social",
        icon = Icons.Default.Group,
        isSelected = currentTabIndex == 1,
        onClick = { onTabSelected(1) },
        testTag = "nav_tab_social"
      )

      // 3. Profile
      NavTabItem(
        title = "Profile",
        icon = Icons.Default.Person,
        isSelected = currentTabIndex == 2,
        onClick = { onTabSelected(2) },
        testTag = "nav_tab_profile"
      )
    }
  }
}

@Composable
fun NavTabItem(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable { onClick() }
      .padding(horizontal = 12.dp, vertical = 2.dp)
      .testTag(testTag)
  ) {
    if (isSelected) {
      // Glowing green circular icon badge
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(ActiveGreenGlow)
          .padding(2.dp),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = Color(0xFF003810),
          modifier = Modifier.size(20.dp)
        )
      }
    } else {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = Color(0xFF80CBC4),
        modifier = Modifier.size(26.dp)
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = title,
      color = if (isSelected) ActiveGreenGlow else Color(0xFF80CBC4),
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      fontSize = 11.sp
    )

    if (isSelected) {
      Spacer(modifier = Modifier.height(2.dp))
      Box(
        modifier = Modifier
          .width(20.dp)
          .height(2.dp)
          .clip(RoundedCornerShape(1.dp))
          .background(ActiveGreenGlow)
      )
    }
  }
}

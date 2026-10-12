package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LudoDarkColorScheme =
  darkColorScheme(
    primary = ActiveGreenGlow,
    secondary = CoinGold,
    tertiary = GemCyan,
    background = LudoBackgroundDark,
    surface = LudoSurfaceNavy,
    onPrimary = LudoBackgroundDark,
    onSecondary = LudoBackgroundDark,
    onBackground = TextLight,
    onSurface = TextLight
  )

private val LudoLightColorScheme =
  lightColorScheme(
    primary = ActiveGreenGlow,
    secondary = CoinGold,
    tertiary = GemCyan,
    background = LudoBackgroundDark,
    surface = LudoSurfaceNavy,
    onPrimary = LudoBackgroundDark,
    onSecondary = LudoBackgroundDark,
    onBackground = TextLight,
    onSurface = TextLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) LudoDarkColorScheme else LudoLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

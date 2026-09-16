package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ComicDarkColorScheme =
  darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = TertiaryColor,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = OnPrimaryColor,
    onSecondary = OnSecondaryColor,
    onTertiary = Color.White,
    onBackground = ComicTextLight,
    onSurface = ComicTextLight,
  )

private val ComicLightColorScheme =
  lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = TertiaryColor,
    background = Color(0xFFFFF9EE),
    surface = Color(0xFFFFFFFF),
    onPrimary = OnPrimaryColor,
    onSecondary = OnSecondaryColor,
    onTertiary = Color.White,
    onBackground = Color(0xFF1E1B2E),
    onSurface = Color(0xFF1E1B2E),
  )

@Composable
fun RehanKoTangKaroTheme(
  darkTheme: Boolean = true, // Default to vibrant comic dark theme for mobile arcade game feel
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) ComicDarkColorScheme else ComicLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  RehanKoTangKaroTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}


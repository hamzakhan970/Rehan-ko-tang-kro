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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.audio.SoundManager
import com.example.components.AnimatedCharacterAvatar
import com.example.components.ComicButton
import com.example.model.CharacterId
import com.example.model.CharacterMood
import com.example.ui.theme.ComicBackgroundDark
import com.example.ui.theme.ComicCardDark
import com.example.ui.theme.ComicGreen
import com.example.ui.theme.ComicOrange
import com.example.ui.theme.ComicRed
import com.example.ui.theme.ComicYellow

@Composable
fun MainMenuScreen(
  soundManager: SoundManager,
  totalCoins: Int,
  highestUnlockedLevel: Int,
  onPlay: () -> Unit,
  onLevels: () -> Unit,
  onCharacters: () -> Unit,
  onSettings: () -> Unit,
  onAbout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "title_bounce")
  val titleScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "titleScale"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ComicBackgroundDark)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Status Bar: Coins & Sound Toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Coins Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF2E2412))
            .border(2.dp, ComicYellow, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🪙", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "$totalCoins",
              color = ComicYellow,
              fontWeight = FontWeight.Black,
              fontSize = 15.sp
            )
          }
        }

        // Sound Toggle
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(ComicCardDark)
            .border(2.dp, ComicYellow, CircleShape)
            .clickable {
              soundManager.soundEnabled = !soundManager.soundEnabled
              soundManager.playTap()
            }
            .testTag("btn_sound_toggle"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (soundManager.soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeMute,
            contentDescription = "Sound Toggle",
            tint = ComicYellow,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Official Game Logo (Prominent top/center, uncropped, adaptive scaling)
      Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = ComicCardDark),
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 340.dp)
          .aspectRatio(1f)
          .scale(titleScale)
          .border(3.5.dp, ComicYellow, RoundedCornerShape(26.dp))
          .shadow(12.dp, RoundedCornerShape(26.dp))
          .clickable {
            soundManager.playBoing()
          }
          .testTag("official_game_logo")
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141221)),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_game_logo),
            contentDescription = "REHAN KO TANG KARO - Official Game Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(22.dp))
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Status subtitle banner
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFF231F36))
          .border(1.5.dp, ComicOrange, RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Text(
          text = "🎯 Level $highestUnlockedLevel / 5 Unlocked • 3 Dost vs Rehan!",
          color = ComicYellow,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          textAlign = TextAlign.Center
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4 Friends Mini Interactive Avatars Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(Color(0xFF231F36))
          .border(1.5.dp, Color(0xFF3B355A), RoundedCornerShape(18.dp))
          .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        AnimatedCharacterAvatar(
          characterId = CharacterId.REHAN,
          mood = CharacterMood.SUSPICIOUS,
          size = 62.dp,
          modifier = Modifier.clickable {
            soundManager.playAlert()
          }
        )
        AnimatedCharacterAvatar(
          characterId = CharacterId.MUAVIYA,
          mood = CharacterMood.IDLE,
          size = 62.dp,
          modifier = Modifier.clickable {
            soundManager.playTap()
          }
        )
        AnimatedCharacterAvatar(
          characterId = CharacterId.HAMZA,
          mood = CharacterMood.LAUGHING,
          size = 62.dp,
          modifier = Modifier.clickable {
            soundManager.playLaugh()
          }
        )
        AnimatedCharacterAvatar(
          characterId = CharacterId.HUZAIFA,
          mood = CharacterMood.IDLE,
          size = 62.dp,
          modifier = Modifier.clickable {
            soundManager.playWhoopee()
          }
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Main Menu Action Buttons
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // ▶ PLAY BUTTON (Large Primary)
        ComicButton(
          text = "▶ PLAY (LEVEL $highestUnlockedLevel)",
          onClick = {
            soundManager.playBoing()
            onPlay()
          },
          color = ComicGreen,
          textColor = Color.White,
          modifier = Modifier.fillMaxWidth(),
          testTag = "btn_menu_play"
        )

        // 🎯 LEVELS BUTTON
        ComicButton(
          text = "🎯 LEVELS",
          onClick = {
            soundManager.playTap()
            onLevels()
          },
          color = ComicYellow,
          textColor = Color.Black,
          modifier = Modifier.fillMaxWidth(),
          testTag = "btn_menu_levels"
        )

        // 👥 CHARACTERS BUTTON
        ComicButton(
          text = "👥 CHARACTERS",
          onClick = {
            soundManager.playTap()
            onCharacters()
          },
          color = ComicOrange,
          textColor = Color.White,
          modifier = Modifier.fillMaxWidth(),
          testTag = "btn_menu_characters"
        )

        // Two small buttons row: SETTINGS & ABOUT
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ComicButton(
            text = "⚙ SETTINGS",
            onClick = {
              soundManager.playTap()
              onSettings()
            },
            color = Color(0xFF5E35B1),
            textColor = Color.White,
            modifier = Modifier.weight(1f),
            testTag = "btn_menu_settings"
          )

          ComicButton(
            text = "ℹ ABOUT",
            onClick = {
              soundManager.playTap()
              onAbout()
            },
            color = Color(0xFF00897B),
            textColor = Color.White,
            modifier = Modifier.weight(1f),
            testTag = "btn_menu_about"
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Harmless Comedy • 100% Real Friendship Fun! 🤝",
        color = Color.White.copy(alpha = 0.6f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundManager
import com.example.components.ComicButton
import com.example.data.GameRepository
import com.example.model.GameDifficulty
import com.example.ui.theme.ComicBackgroundDark
import com.example.ui.theme.ComicCardDark
import com.example.ui.theme.ComicGreen
import com.example.ui.theme.ComicOrange
import com.example.ui.theme.ComicRed
import com.example.ui.theme.ComicYellow

@Composable
fun SettingsScreen(
  repository: GameRepository,
  soundManager: SoundManager,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var soundOn by remember { mutableStateOf(repository.soundEnabled.value) }
  var hapticsOn by remember { mutableStateOf(repository.hapticsEnabled.value) }
  var difficulty by remember { mutableStateOf(repository.difficulty.value) }
  var showResetDialog by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ComicBackgroundDark)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(ComicCardDark)
            .border(2.dp, ComicYellow, CircleShape)
            .clickable {
              soundManager.playTap()
              onBack()
            }
            .testTag("btn_back_settings"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = ComicYellow,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "SETTINGS ⚙",
            color = ComicYellow,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
          )
          Text(
            text = "Game Controls & Preferences",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Audio & Vibration Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ComicCardDark),
        modifier = Modifier
          .fillMaxWidth()
          .border(2.dp, Color(0xFF3B3550), RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "AUDIO & FEEDBACK",
            color = ComicYellow,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Sound Effects Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Filled.VolumeUp, contentDescription = null, tint = ComicOrange)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Sound Effects 🔊", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Funny laughs, boings & horns", color = Color.Gray, fontSize = 11.sp)
              }
            }

            Switch(
              checked = soundOn,
              onCheckedChange = {
                soundOn = it
                repository.setSoundEnabled(it)
                soundManager.soundEnabled = it
                if (it) soundManager.playBoing()
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = ComicYellow,
                checkedTrackColor = ComicOrange
              )
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Haptics Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Filled.Vibration, contentDescription = null, tint = ComicGreen)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Haptic Vibration 📳", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Tactile prank feedback", color = Color.Gray, fontSize = 11.sp)
              }
            }

            Switch(
              checked = hapticsOn,
              onCheckedChange = {
                hapticsOn = it
                repository.setHapticsEnabled(it)
                soundManager.hapticsEnabled = it
                if (it) soundManager.vibrate(60)
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = ComicYellow,
                checkedTrackColor = ComicGreen
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Difficulty Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ComicCardDark),
        modifier = Modifier
          .fillMaxWidth()
          .border(2.dp, Color(0xFF3B3550), RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "GAME DIFFICULTY",
            color = ComicYellow,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
          Text(
            text = "Controls Rehan's vigilance and reaction speed",
            color = Color.Gray,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            GameDifficulty.entries.forEach { diff ->
              val isSelected = difficulty == diff
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) ComicYellow else Color(0xFF1D1A2C))
                  .border(
                    width = 1.5.dp,
                    color = if (isSelected) ComicOrange else Color(0xFF3B3550),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable {
                    difficulty = diff
                    repository.setDifficulty(diff)
                    soundManager.playTap()
                  }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = when (diff) {
                    GameDifficulty.EASY -> "Easy 🟢"
                    GameDifficulty.NORMAL -> "Normal 🟡"
                    GameDifficulty.HARD -> "Hard 🔴"
                  },
                  color = if (isSelected) Color.Black else Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Progress Management Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ComicCardDark),
        modifier = Modifier
          .fillMaxWidth()
          .border(2.dp, Color(0xFF3B3550), RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "PROGRESS MANAGEMENT",
            color = ComicYellow,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
          Text(
            text = "Unlocked: Level ${repository.unlockedLevels.value.maxOrNull() ?: 1} of 5",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
          )

          ComicButton(
            text = "RESET ALL PROGRESS 🔄",
            onClick = {
              soundManager.playAlert()
              showResetDialog = true
            },
            color = ComicRed,
            textColor = Color.White,
            modifier = Modifier.fillMaxWidth(),
            testTag = "btn_reset_progress"
          )
        }
      }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
      Dialog(onDismissRequest = { showResetDialog = false }) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = ComicCardDark),
          modifier = Modifier
            .fillMaxWidth()
            .border(3.dp, ComicRed, RoundedCornerShape(24.dp))
        ) {
          Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "RESET PROGRESS? ⚠️",
              color = ComicRed,
              fontWeight = FontWeight.Black,
              fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "All unlocked levels, stars, and high scores will be reset to Level 1.",
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 13.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              ComicButton(
                text = "CANCEL",
                onClick = { showResetDialog = false },
                color = Color(0xFF455A64),
                textColor = Color.White,
                modifier = Modifier.weight(1f)
              )
              ComicButton(
                text = "YES, RESET",
                onClick = {
                  repository.resetProgress()
                  soundManager.playWhoopee()
                  showResetDialog = false
                },
                color = ComicRed,
                textColor = Color.White,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }
  }
}

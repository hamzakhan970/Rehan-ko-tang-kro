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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.components.ComicButton
import com.example.data.GameRepository
import com.example.model.LevelsCatalog
import com.example.ui.theme.ComicBackgroundDark
import com.example.ui.theme.ComicCardDark
import com.example.ui.theme.ComicGreen
import com.example.ui.theme.ComicOrange
import com.example.ui.theme.ComicYellow

@Composable
fun LevelsScreen(
  repository: GameRepository,
  soundManager: SoundManager,
  onSelectLevel: (Int) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val unlockedLevels = repository.unlockedLevels.value
  val starsMap = repository.levelStars.value
  val highScoresMap = repository.levelHighScores.value

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ComicBackgroundDark)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
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
            .testTag("btn_back_levels"),
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
            text = "PRANK LEVELS 🎯",
            color = ComicYellow,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
          )
          Text(
            text = "Beat each level to unlock the next prank!",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
          )
        }
      }

      // Levels List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(LevelsCatalog.levels) { level ->
          val isUnlocked = unlockedLevels.contains(level.id)
          val stars = starsMap[level.id] ?: 0
          val highScore = highScoresMap[level.id] ?: 0

          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isUnlocked) ComicCardDark else Color(0xFF1E1B28)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .border(
                width = if (isUnlocked) 2.dp else 1.dp,
                color = if (isUnlocked) ComicYellow else Color(0xFF3B3550),
                shape = RoundedCornerShape(20.dp)
              )
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(level.iconEmoji, fontSize = 28.sp)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = level.title,
                      color = if (isUnlocked) ComicYellow else Color.Gray,
                      fontWeight = FontWeight.Black,
                      fontSize = 16.sp
                    )
                    Text(
                      text = level.subtitle,
                      color = if (isUnlocked) ComicOrange else Color.Gray,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                  }
                }

                if (isUnlocked) {
                  // Stars
                  Row {
                    for (i in 1..3) {
                      Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (i <= stars) ComicYellow else Color.DarkGray,
                        modifier = Modifier.size(18.dp)
                      )
                    }
                  }
                } else {
                  // Lock icon
                  Row(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color.Black.copy(alpha = 0.4f))
                      .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Filled.Lock,
                      contentDescription = "Locked",
                      tint = Color.Gray,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LOCKED", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = level.objective,
                color = if (isUnlocked) Color.White.copy(alpha = 0.85f) else Color.Gray,
                fontSize = 12.sp,
                lineHeight = 16.sp
              )

              if (isUnlocked && highScore > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Best Score: $highScore pts",
                  color = ComicGreen,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              ComicButton(
                text = if (isUnlocked) (if (stars > 0) "PLAY AGAIN ▶" else "PLAY LEVEL ▶") else "LOCKED 🔒",
                onClick = {
                  if (isUnlocked) {
                    soundManager.playBoing()
                    onSelectLevel(level.id)
                  }
                },
                color = if (isUnlocked) ComicGreen else Color(0xFF37474F),
                textColor = if (isUnlocked) Color.White else Color.Gray,
                enabled = isUnlocked,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_play_level_${level.id}"
              )
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}

package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CharacterId
import com.example.model.CharacterMood
import com.example.ui.theme.ComicBackgroundDark
import com.example.ui.theme.ComicCardDark
import com.example.ui.theme.ComicGreen
import com.example.ui.theme.ComicOrange
import com.example.ui.theme.ComicRed
import com.example.ui.theme.ComicYellow
import com.example.ui.theme.HamzaRed
import com.example.ui.theme.HuzaifaGreen
import com.example.ui.theme.MuaviyaGold
import com.example.ui.theme.RehanBlue

@Composable
fun ComicButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  color: Color = ComicYellow,
  textColor: Color = Color(0xFF1E1B2E),
  testTag: String = "comic_button",
  enabled: Boolean = true
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.94f else 1.0f,
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "button_scale"
  )

  val shadowOffset = if (isPressed) 2.dp else 5.dp

  Box(
    modifier = modifier
      .scale(scale)
      .testTag(testTag)
      .shadow(elevation = shadowOffset, shape = RoundedCornerShape(16.dp))
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.verticalGradient(
          listOf(
            color,
            color.copy(red = (color.red * 0.85f).coerceIn(0f, 1f), green = (color.green * 0.85f).coerceIn(0f, 1f))
          )
        )
      )
      .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClick = onClick
      )
      .padding(horizontal = 20.dp, vertical = 14.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = textColor,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        color = textColor,
        fontWeight = FontWeight.Black,
        fontSize = 17.sp,
        letterSpacing = 0.5.sp
      )
    }
  }
}

@Composable
fun PauseDialog(
  onResume: () -> Unit,
  onRestart: () -> Unit,
  onLevelSelect: () -> Unit,
  onHome: () -> Unit,
  soundEnabled: Boolean,
  onToggleSound: () -> Unit
) {
  Dialog(onDismissRequest = onResume) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = ComicCardDark),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .border(3.dp, ComicYellow, RoundedCornerShape(24.dp))
        .padding(4.dp)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "⏸ GAME PAUSED",
          color = ComicYellow,
          fontSize = 24.sp,
          fontWeight = FontWeight.Black
        )
        Text(
          text = "Rehan is pausing too... don't make a sound!",
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        Row(
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.padding(bottom = 16.dp)
        ) {
          AnimatedCharacterAvatar(characterId = CharacterId.MUAVIYA, mood = CharacterMood.IDLE, size = 60.dp)
          Spacer(modifier = Modifier.width(12.dp))
          AnimatedCharacterAvatar(characterId = CharacterId.HAMZA, mood = CharacterMood.LAUGHING, size = 60.dp)
          Spacer(modifier = Modifier.width(12.dp))
          AnimatedCharacterAvatar(characterId = CharacterId.HUZAIFA, mood = CharacterMood.IDLE, size = 60.dp)
        }

        ComicButton(
          text = "RESUME PRANK ▶",
          onClick = onResume,
          color = ComicGreen,
          textColor = Color.White,
          modifier = Modifier.fillMaxWidth(),
          testTag = "btn_resume"
        )
        Spacer(modifier = Modifier.height(10.dp))
        ComicButton(
          text = "RESTART LEVEL 🔄",
          onClick = onRestart,
          color = ComicOrange,
          textColor = Color.White,
          modifier = Modifier.fillMaxWidth(),
          testTag = "btn_restart_pause"
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          ComicButton(
            text = "LEVELS 🎯",
            onClick = onLevelSelect,
            color = ComicYellow,
            textColor = Color.Black,
            modifier = Modifier.weight(1f),
            testTag = "btn_level_select_pause"
          )
          ComicButton(
            text = if (soundEnabled) "SFX ON 🔊" else "SFX OFF 🔇",
            onClick = onToggleSound,
            color = Color(0xFF5E35B1),
            textColor = Color.White,
            modifier = Modifier.weight(1f),
            testTag = "btn_toggle_sfx_pause"
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        ComicButton(
          text = "MAIN MENU 🏠",
          onClick = onHome,
          color = Color(0xFF455A64),
          textColor = Color.White,
          modifier = Modifier.fillMaxWidth(),
          testTag = "btn_home_pause"
        )
      }
    }
  }
}

@Composable
fun VictoryDialog(
  levelId: Int,
  score: Int,
  coinsEarned: Int,
  stars: Int,
  onNextLevel: () -> Unit,
  onReplay: () -> Unit,
  onLevelSelect: () -> Unit
) {
  Dialog(onDismissRequest = {}) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = ComicCardDark),
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .border(3.dp, ComicGreen, RoundedCornerShape(26.dp))
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "🎉 PRANK COMPLETE! 😂",
          color = ComicYellow,
          fontSize = 24.sp,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        val funnySubtitle = when (levelId) {
          1 -> "Rehan heard the chicken and jumped 3 feet high! 🐔"
          2 -> "Rehan's face when tasting that spicy squeak burger was priceless! 🍔"
          3 -> "Rehan saw his marker mustache in the mirror! He roared laughing! 😴"
          4 -> "The 3 dost escaped into the clubhouse safe and sound! 🏃"
          5 -> "Rehan got his epic water balloon revenge! 4 Dost Forever! 🏆"
          else -> "Mission Accomplished!"
        }

        Text(
          text = funnySubtitle,
          color = Color.White.copy(alpha = 0.9f),
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
        )

        // Celebratory Star Display
        Row(
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.padding(vertical = 4.dp)
        ) {
          for (i in 1..3) {
            val isGold = i <= stars
            Icon(
              imageVector = Icons.Filled.Star,
              contentDescription = "Star $i",
              tint = if (isGold) ComicYellow else Color.Gray.copy(alpha = 0.4f),
              modifier = Modifier
                .size(42.dp)
                .padding(horizontal = 4.dp)
            )
          }
        }

        // Stats Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1B182B))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("SCORE", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text("$score", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("REWARD", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text("+$coinsEarned 🪙", color = ComicYellow, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
          }
        }

        // Animated character reaction
        Row(
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.padding(vertical = 8.dp)
        ) {
          AnimatedCharacterAvatar(characterId = CharacterId.HAMZA, mood = CharacterMood.LAUGHING, size = 65.dp)
          Spacer(modifier = Modifier.width(12.dp))
          AnimatedCharacterAvatar(
            characterId = if (levelId == 5) CharacterId.REHAN else CharacterId.MUAVIYA,
            mood = CharacterMood.VICTORY,
            size = 65.dp
          )
        }

        if (levelId < 5) {
          ComicButton(
            text = "NEXT LEVEL ➔",
            onClick = onNextLevel,
            color = ComicGreen,
            textColor = Color.White,
            modifier = Modifier.fillMaxWidth(),
            testTag = "btn_next_level"
          )
          Spacer(modifier = Modifier.height(10.dp))
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ComicButton(
            text = "REPLAY 🔄",
            onClick = onReplay,
            color = ComicOrange,
            textColor = Color.White,
            modifier = Modifier.weight(1f),
            testTag = "btn_replay_victory"
          )
          ComicButton(
            text = "LEVELS 🎯",
            onClick = onLevelSelect,
            color = ComicYellow,
            textColor = Color.Black,
            modifier = Modifier.weight(1f),
            testTag = "btn_levels_victory"
          )
        }
      }
    }
  }
}

@Composable
fun GameOverDialog(
  reason: String,
  onRetry: () -> Unit,
  onLevelSelect: () -> Unit
) {
  Dialog(onDismissRequest = {}) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = ComicCardDark),
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .border(3.dp, ComicRed, RoundedCornerShape(26.dp))
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "🚨 PAKDE GAYE! 😱",
          color = ComicRed,
          fontSize = 24.sp,
          fontWeight = FontWeight.Black
        )
        Text(
          text = "Rehan Caught You!",
          color = ComicYellow,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = reason,
          color = Color.White.copy(alpha = 0.85f),
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 8.dp, bottom = 14.dp)
        )

        // Comic Rehan caught you face
        AnimatedCharacterAvatar(
          characterId = CharacterId.REHAN,
          mood = CharacterMood.SUSPICIOUS,
          size = 80.dp,
          modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ComicButton(
          text = "TRY AGAIN 🔄",
          onClick = onRetry,
          color = ComicYellow,
          textColor = Color.Black,
          modifier = Modifier.fillMaxWidth(),
          testTag = "btn_retry_gameover"
        )
        Spacer(modifier = Modifier.height(10.dp))
        ComicButton(
          text = "CHOOSE LEVEL 🎯",
          onClick = onLevelSelect,
          color = Color(0xFF455A64),
          textColor = Color.White,
          modifier = Modifier.fillMaxWidth(),
          testTag = "btn_levels_gameover"
        )
      }
    }
  }
}

@Composable
fun HowToPlayDialog(
  levelNumber: Int,
  levelTitle: String,
  goal: String,
  instructions: List<Pair<String, String>>,
  onStart: () -> Unit
) {
  Dialog(onDismissRequest = onStart) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = ComicCardDark),
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .border(3.dp, ComicYellow, RoundedCornerShape(26.dp))
        .testTag("how_to_play_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ComicYellow)
            .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
          Text(
            text = "HOW TO PLAY 🎮",
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Level $levelNumber: $levelTitle",
          color = ComicOrange,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mission box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF231E38))
            .border(1.5.dp, ComicOrange.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .padding(12.dp)
        ) {
          Column {
            Text(
              text = "🎯 YOUR MISSION:",
              color = ComicYellow,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
            Text(
              text = goal,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Instruction list
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          instructions.forEach { (emoji, text) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1B172B))
                .border(1.dp, Color(0xFF383254), RoundedCornerShape(12.dp))
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(ComicYellow.copy(alpha = 0.15f))
                  .border(1.dp, ComicYellow, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(text = emoji, fontSize = 18.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = text,
                color = Color.White.copy(alpha = 0.95f),
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big action start button
        ComicButton(
          text = "GOT IT! LET'S PRANK ▶",
          onClick = onStart,
          color = ComicGreen,
          textColor = Color.White,
          modifier = Modifier.fillMaxWidth(),
          testTag = "btn_how_to_play_start"
        )
      }
    }
  }
}


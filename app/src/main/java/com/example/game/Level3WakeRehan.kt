package com.example.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.components.ControlsHelpBanner
import com.example.components.GameOverDialog
import com.example.components.GameTopBar
import com.example.components.HowToPlayDialog
import com.example.components.PauseDialog
import com.example.components.PrankProgressBar
import com.example.components.VictoryDialog
import com.example.model.CharacterId
import com.example.model.CharacterMood
import com.example.model.GameDifficulty
import com.example.ui.theme.ComicBackgroundDark
import com.example.ui.theme.ComicGreen
import com.example.ui.theme.ComicOrange
import com.example.ui.theme.ComicRed
import com.example.ui.theme.ComicYellow

@Composable
fun Level3WakeRehan(
  soundManager: SoundManager,
  difficulty: GameDifficulty,
  coins: Int,
  onComplete: (score: Int, coinsEarned: Int, stars: Int) -> Unit,
  onNextLevel: () -> Unit,
  onBack: () -> Unit,
  onLevelSelect: () -> Unit,
  modifier: Modifier = Modifier
) {
  var score by remember { mutableIntStateOf(0) }
  var currentStep by remember { mutableIntStateOf(1) } // 1: Feather, 2: Mustache, 3: Alarm

  // Step 1: Feather swipes
  var featherSwipes by remember { mutableIntStateOf(0) }
  val targetSwipes = 6

  // Step 2: Mustache dots traced
  var mustacheDotsDrawn by remember { mutableIntStateOf(0) }
  val targetDots = 4

  // Step 3: Alarm clock wound up
  var alarmWindPercent by remember { mutableFloatStateOf(0f) }

  // Rehan state
  var rehanMood by remember { mutableStateOf(CharacterMood.SLEEPING) }
  var wakeUpAnimation by remember { mutableStateOf(false) }

  // Dialogs
  var showHowToPlay by remember { mutableStateOf(true) }
  var isPaused by remember { mutableStateOf(false) }
  var isGameOver by remember { mutableStateOf(false) }
  var gameOverReason by remember { mutableStateOf("") }
  var isVictory by remember { mutableStateOf(false) }
  var finalStars by remember { mutableIntStateOf(3) }

  fun restartLevel() {
    score = 0
    currentStep = 1
    featherSwipes = 0
    mustacheDotsDrawn = 0
    alarmWindPercent = 0f
    rehanMood = CharacterMood.SLEEPING
    wakeUpAnimation = false
    isPaused = false
    isGameOver = false
    isVictory = false
  }

  fun triggerWakeUp() {
    wakeUpAnimation = true
    rehanMood = CharacterMood.SHOCKED
    soundManager.playAlarm()
    soundManager.playWhoopee()
    soundManager.playLaugh()
    soundManager.playPrankVictory()

    val stars = 3
    finalStars = stars
    onComplete(score + 750, stars * 40, stars)
    isVictory = true
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ComicBackgroundDark)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      GameTopBar(
        levelTitle = "Level 3: Wake Rehan 😂",
        score = score,
        coins = coins,
        onPause = { isPaused = true },
        onBack = onBack,
        onHelp = { showHowToPlay = true }
      )

      val progress = when (currentStep) {
        1 -> (featherSwipes.toFloat() / targetSwipes) * 0.33f
        2 -> 0.33f + (mustacheDotsDrawn.toFloat() / targetDots) * 0.33f
        else -> 0.66f + alarmWindPercent * 0.34f
      }

      PrankProgressBar(
        progressPercent = progress,
        label = when (currentStep) {
          1 -> "Step 1: Feather Tickle ($featherSwipes / $targetSwipes Swipes)"
          2 -> "Step 2: Draw Mustache ($mustacheDotsDrawn / $targetDots Points)"
          else -> "Step 3: Wind-Up Alarm Clock (${(alarmWindPercent * 100).toInt()}%)"
        },
        iconEmoji = when (currentStep) {
          1 -> "🪶"
          2 -> "🖍️"
          else -> "⏰"
        }
      )

      ControlsHelpBanner(
        text = when (currentStep) {
          1 -> "🪶 SWIPE your finger horizontally across the bed to tickle Rehan's nose!"
          2 -> "🖍️ TAP the 4 glowing points on Rehan's face to draw a funny handlebar mustache!"
          else -> "⏰ TAP 'WIND ALARM' rapidly to charge the rooster alarm clock!"
        }
      )

      // Bedroom Canvas Stage
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF1E2638), Color(0xFF121724))
            )
          )
          .border(2.dp, Color(0xFF37474F), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
      ) {
        // Room background
        Image(
          painter = painterResource(id = R.drawable.img_prank_club),
          contentDescription = "Bedroom",
          contentScale = ContentScale.Crop,
          alpha = 0.2f,
          modifier = Modifier.fillMaxSize()
        )

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxSize()
        ) {
          Spacer(modifier = Modifier.height(14.dp))

          // Sleeping speech bubble
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (wakeUpAnimation) ComicRed else Color(0xFF0277BD))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (wakeUpAnimation) "“MERI MOOCH?! HAMZA! MUAVIYA! 😂”"
              else "“Zzz... aam ras... zzz... burger... 😴”",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Bed with sleeping Rehan
          Box(
            modifier = Modifier
              .fillMaxWidth(0.9f)
              .height(160.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFF263238))
              .border(2.dp, Color(0xFF455A64), RoundedCornerShape(20.dp))
              .pointerInput(currentStep) {
                if (currentStep == 1) {
                  detectHorizontalDragGestures { _, _ ->
                    featherSwipes++
                    score += 20
                    soundManager.playSneakStep()
                    if (featherSwipes >= targetSwipes) {
                      currentStep = 2
                      soundManager.playWhoopee()
                    }
                  }
                }
              },
            contentAlignment = Alignment.Center
          ) {
            // Pillow & Rehan
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              // Pillow
              Box(
                modifier = Modifier
                  .size(90.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color(0xFFECEFF1))
                  .border(2.dp, Color(0xFFB0BEC5), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = if (currentStep >= 3) "⏰" else "🛌",
                  fontSize = 36.sp
                )
              }

              Spacer(modifier = Modifier.width(16.dp))

              // Rehan Avatar
              Box(contentAlignment = Alignment.Center) {
                AnimatedCharacterAvatar(
                  characterId = CharacterId.REHAN,
                  mood = rehanMood,
                  size = 115.dp
                )

                // Drawn Mustache on Rehan
                if (mustacheDotsDrawn > 0) {
                  Box(
                    modifier = Modifier
                      .offset(y = 10.dp)
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color.Black.copy(alpha = 0.85f))
                      .padding(horizontal = 8.dp, vertical = 2.dp)
                  ) {
                    Text("🥸", fontSize = 20.sp)
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Interactive Step 2: Mustache Tracing Dots
          if (currentStep == 2) {
            Text(
              text = "Tap all 4 points to complete the mustache!",
              color = ComicYellow,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              for (dot in 1..targetDots) {
                val isDrawn = dot <= mustacheDotsDrawn
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isDrawn) ComicGreen else ComicYellow)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable(enabled = dot == mustacheDotsDrawn + 1) {
                      mustacheDotsDrawn++
                      score += 75
                      soundManager.playTap()
                      if (mustacheDotsDrawn >= targetDots) {
                        currentStep = 3
                        soundManager.playBoing()
                      }
                    }
                    .testTag("dot_mustache_$dot"),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = if (isDrawn) "✓" else "$dot",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                  )
                }
              }
            }
          }

          // Interactive Step 3: Alarm Wind-up
          if (currentStep == 3 && !wakeUpAnimation) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "WIND UP THE ROOSTER ALARM! ⏰",
                color = ComicOrange,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              ComicButton(
                text = "TAP RAPIDLY! ⏰ (${(alarmWindPercent * 100).toInt()}%)",
                onClick = {
                  alarmWindPercent = (alarmWindPercent + 0.15f).coerceAtMost(1f)
                  score += 40
                  soundManager.playTap()
                  if (alarmWindPercent >= 1.0f) {
                    triggerWakeUp()
                  }
                },
                color = ComicYellow,
                textColor = Color.Black,
                testTag = "btn_wind_alarm"
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // 3 Friends Watching
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            AnimatedCharacterAvatar(characterId = CharacterId.HUZAIFA, mood = CharacterMood.IDLE, size = 50.dp)
            Spacer(modifier = Modifier.width(8.dp))
            AnimatedCharacterAvatar(characterId = CharacterId.HAMZA, mood = CharacterMood.LAUGHING, size = 55.dp)
            Spacer(modifier = Modifier.width(8.dp))
            AnimatedCharacterAvatar(characterId = CharacterId.MUAVIYA, mood = CharacterMood.IDLE, size = 50.dp)
          }
        }
      }

      // Step 1 helper button if user prefers tapping
      if (currentStep == 1) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          ComicButton(
            text = "TICKLE WITH FEATHER 🪶 ($featherSwipes / $targetSwipes)",
            onClick = {
              featherSwipes++
              score += 20
              soundManager.playSneakStep()
              if (featherSwipes >= targetSwipes) {
                currentStep = 2
                soundManager.playWhoopee()
              }
            },
            color = ComicYellow,
            textColor = Color.Black,
            modifier = Modifier.fillMaxWidth(0.9f),
            testTag = "btn_feather_tap"
          )
        }
      }
    }

    if (showHowToPlay) {
      HowToPlayDialog(
        levelNumber = 3,
        levelTitle = "Wake Rehan 😴",
        goal = "Wake up deep sleeper Rehan using feather tickles, silly mustache, and rooster alarm!",
        instructions = listOf(
          "😴" to "Rehan is fast asleep snoring under his blanket.",
          "🪶" to "Step 1: Tap or swipe to tickle Rehan's nose with a feather.",
          "🖍️" to "Step 2: Tap the glowing facial dots to draw a funny marker mustache.",
          "⏰" to "Step 3: Rapidly tap 'WIND ALARM' to trigger the rooster wake-up chime!",
          "😂" to "Rehan looks in the mirror and roars with laughter!"
        ),
        onStart = {
          showHowToPlay = false
          soundManager.playTap()
        }
      )
    }

    if (isPaused) {
      PauseDialog(
        onResume = { isPaused = false },
        onRestart = { restartLevel() },
        onLevelSelect = onLevelSelect,
        onHome = onBack,
        soundEnabled = soundManager.soundEnabled,
        onToggleSound = { soundManager.soundEnabled = !soundManager.soundEnabled }
      )
    }

    if (isGameOver) {
      GameOverDialog(
        reason = gameOverReason,
        onRetry = { restartLevel() },
        onLevelSelect = onLevelSelect
      )
    }

    if (isVictory) {
      VictoryDialog(
        levelId = 3,
        score = score,
        coinsEarned = finalStars * 40,
        stars = finalStars,
        onNextLevel = onNextLevel,
        onReplay = { restartLevel() },
        onLevelSelect = onLevelSelect
      )
    }
  }
}

package com.example.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.delay
import kotlin.random.Random

data class HidingSpot(
  val id: Int,
  val name: String,
  val xPercent: Float,
  val yPercent: Float
)

@Composable
fun Level5RehansRevenge(
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
  var hitsSoaked by remember { mutableIntStateOf(0) }
  val targetHits = 12
  var secondsLeft by remember { mutableIntStateOf(45) }

  // 4 possible hiding spots in the room
  val spots = remember {
    listOf(
      HidingSpot(1, "Left Door", 0.15f, 0.22f),
      HidingSpot(2, "Window", 0.50f, 0.15f),
      HidingSpot(3, "Right Door", 0.85f, 0.22f),
      HidingSpot(4, "Behind Armchair", 0.30f, 0.55f),
      HidingSpot(5, "Behind Bookshelf", 0.70f, 0.55f)
    )
  }

  // Active target popping up
  var activeSpotId by remember { mutableIntStateOf(-1) }
  var activeCharacter by remember { mutableStateOf(CharacterId.HAMZA) }
  var isHitCurrentTarget by remember { mutableStateOf(false) }

  // Dialogs
  var showHowToPlay by remember { mutableStateOf(true) }
  var isPaused by remember { mutableStateOf(false) }
  var isGameOver by remember { mutableStateOf(false) }
  var gameOverReason by remember { mutableStateOf("") }
  var isVictory by remember { mutableStateOf(false) }
  var finalStars by remember { mutableIntStateOf(3) }

  fun restartLevel() {
    score = 0
    hitsSoaked = 0
    secondsLeft = 45
    activeSpotId = -1
    isHitCurrentTarget = false
    isPaused = false
    isGameOver = false
    isVictory = false
  }

  // Countdown timer
  LaunchedEffect(isPaused, isGameOver, isVictory, showHowToPlay) {
    if (isPaused || isGameOver || isVictory || showHowToPlay) return@LaunchedEffect
    while (secondsLeft > 0) {
      delay(1000)
      secondsLeft--
      if (secondsLeft <= 0 && hitsSoaked < targetHits) {
        soundManager.playCaught()
        gameOverReason = "Time's up! The 3 dost dodged your water balloons! Try again!"
        isGameOver = true
        break
      }
    }
  }

  // Pop-up loop
  LaunchedEffect(isPaused, isGameOver, isVictory, showHowToPlay) {
    if (isPaused || isGameOver || isVictory || showHowToPlay) return@LaunchedEffect
    val speedMult = difficulty.speedMultiplier

    while (true) {
      // Pick random spot and character
      val randomSpot = spots.random()
      val friends = listOf(CharacterId.HAMZA, CharacterId.MUAVIYA, CharacterId.HUZAIFA)
      val randomFriend = friends.random()

      activeSpotId = randomSpot.id
      activeCharacter = randomFriend
      isHitCurrentTarget = false

      soundManager.playSneakStep()

      // Active pop-up window
      val showDuration = ((Random.nextInt(1200, 2000)) / speedMult).toLong()
      delay(showDuration)

      // Hide spot before next pop
      activeSpotId = -1
      delay(Random.nextLong(300, 700))
    }
  }

  fun onTargetTapped(spotId: Int) {
    if (spotId == activeSpotId && !isHitCurrentTarget) {
      isHitCurrentTarget = true
      hitsSoaked++
      score += 120
      soundManager.playSplat()
      soundManager.playLaugh()

      if (hitsSoaked >= targetHits) {
        soundManager.playPrankVictory()
        val stars = when {
          secondsLeft > 20 -> 3
          secondsLeft > 8 -> 2
          else -> 1
        }
        finalStars = stars
        onComplete(score + 1000, stars * 50, stars)
        isVictory = true
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ComicBackgroundDark)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      GameTopBar(
        levelTitle = "Level 5: Rehan's Revenge 😈",
        score = score,
        coins = coins,
        onPause = { isPaused = true },
        onBack = onBack,
        onHelp = { showHowToPlay = true }
      )

      PrankProgressBar(
        progressPercent = (hitsSoaked.toFloat() / targetHits).coerceIn(0f, 1f),
        label = "Soaked Friends: $hitsSoaked / $targetHits  |  Time: ${secondsLeft}s ⏱️",
        iconEmoji = "💦"
      )

      ControlsHelpBanner(
        text = "😈 YOU ARE REHAN! TAP the 3 friends as they pop out from doors, windows & furniture to splash them with water balloons!"
      )

      // Revenge Room Arena
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF261D36), Color(0xFF171224))
            )
          )
          .border(2.dp, ComicOrange, RoundedCornerShape(20.dp))
      ) {
        // Room background art
        Image(
          painter = painterResource(id = R.drawable.img_prank_club),
          contentDescription = "Revenge Room",
          contentScale = ContentScale.Crop,
          alpha = 0.25f,
          modifier = Modifier.fillMaxSize()
        )

        // Render Hiding Spots
        spots.forEach { spot ->
          val isActive = spot.id == activeSpotId
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .fillMaxWidth()
              .fillMaxSize()
              .offset(
                x = (spot.xPercent * 280).dp,
                y = (spot.yPercent * 340).dp
              )
          ) {
            if (isActive) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                  .clickable { onTargetTapped(spot.id) }
                  .testTag("target_friend_${spot.id}")
              ) {
                // Comic Taunt Bubble
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isHitCurrentTarget) ComicOrange else ComicYellow)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = if (isHitCurrentTarget) "SPLAT! 💦" else "“Catch me! 😜”",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                  )
                }

                Spacer(modifier = Modifier.height(2.dp))

                AnimatedCharacterAvatar(
                  characterId = activeCharacter,
                  mood = if (isHitCurrentTarget) CharacterMood.SPLATTED else CharacterMood.LAUGHING,
                  size = 72.dp
                )
              }
            } else {
              // Idle hiding marker
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color.Black.copy(alpha = 0.3f))
                  .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Text("🚪", fontSize = 18.sp)
              }
            }
          }
        }

        // Rehan in bottom center holding water balloon launcher
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 12.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(ComicYellow)
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "“Ab dekho mera prank! 💦🎈”",
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🎈", fontSize = 28.sp)
              AnimatedCharacterAvatar(
                characterId = CharacterId.REHAN,
                mood = CharacterMood.VICTORY,
                size = 85.dp
              )
              Text("💦", fontSize = 28.sp)
            }
          }
        }
      }

      // Bottom Quick Slingshot Action Button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        contentAlignment = Alignment.Center
      ) {
        ComicButton(
          text = if (activeSpotId != -1) "LAUNCH BALLOON NOW! 💦" else "AIMING SLINGSHOT... 🎯",
          onClick = {
            if (activeSpotId != -1) {
              onTargetTapped(activeSpotId)
            } else {
              soundManager.playBoing()
            }
          },
          color = if (activeSpotId != -1) ComicRed else ComicYellow,
          textColor = if (activeSpotId != -1) Color.White else Color.Black,
          modifier = Modifier.fillMaxWidth(0.9f),
          testTag = "btn_launch_balloon"
        )
      }
    }

    if (showHowToPlay) {
      HowToPlayDialog(
        levelNumber = 5,
        levelTitle = "Rehan's Revenge 😈",
        goal = "Play as Rehan and splash all 3 dost with colorful water balloons!",
        instructions = listOf(
          "😈" to "You are now playing as Rehan! It's payback time.",
          "👀" to "Watch the doors, windows, and chairs as Muaviya, Hamza, or Huzaifa pop up.",
          "🎈" to "Tap directly on the hiding spot or tap 'SPLASH WATER BALLOON 🎈'!",
          "⏱️" to "Splash 12 friends before the 45-second timer runs out!",
          "🏆" to "Complete the final level and unlock the 4 Dost Victory celebration!"
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
        levelId = 5,
        score = score,
        coinsEarned = finalStars * 50,
        stars = finalStars,
        onNextLevel = onLevelSelect,
        onReplay = { restartLevel() },
        onLevelSelect = onLevelSelect
      )
    }
  }
}

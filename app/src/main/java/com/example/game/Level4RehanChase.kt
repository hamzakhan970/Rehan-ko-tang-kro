package com.example.game

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

data class ChaseObstacle(
  val id: Long,
  var xOffset: Float, // 0.0f (left) to 1.0f (right)
  val type: ObstacleType,
  var passed: Boolean = false
)

enum class ObstacleType(val emoji: String, val requiresSlide: Boolean) {
  FOOTBALL("⚽", false),
  SKATEBOARD("🛹", false),
  LAUNDRY("🧺", true),
  PUDDLE("💧", false)
}

@Composable
fun Level4RehanChase(
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
  var distanceRan by remember { mutableIntStateOf(0) }
  val targetDistance = 300 // meters

  // Distance from Rehan behind friends (in meters: 15m to 80m)
  var rehanDistance by remember { mutableFloatStateOf(50f) }
  var bananasLeft by remember { mutableIntStateOf(3) }
  var isJumping by remember { mutableStateOf(false) }
  var isSliding by remember { mutableStateOf(false) }

  val obstacles = remember { mutableStateListOf<ChaseObstacle>() }

  // Dialogs
  var showHowToPlay by remember { mutableStateOf(true) }
  var isPaused by remember { mutableStateOf(false) }
  var isGameOver by remember { mutableStateOf(false) }
  var gameOverReason by remember { mutableStateOf("") }
  var isVictory by remember { mutableStateOf(false) }
  var finalStars by remember { mutableIntStateOf(3) }

  fun restartLevel() {
    score = 0
    distanceRan = 0
    rehanDistance = 50f
    bananasLeft = 3
    isJumping = false
    isSliding = false
    obstacles.clear()
    isPaused = false
    isGameOver = false
    isVictory = false
  }

  // Jump recovery
  LaunchedEffect(isJumping) {
    if (isJumping) {
      delay(550)
      isJumping = false
    }
  }

  // Slide recovery
  LaunchedEffect(isSliding) {
    if (isSliding) {
      delay(600)
      isSliding = false
    }
  }

  // Main Game Loop: Distance, Obstacles, Rehan closing in
  LaunchedEffect(isPaused, isGameOver, isVictory) {
    if (isPaused || isGameOver || isVictory) return@LaunchedEffect

    val speedMult = difficulty.speedMultiplier
    var nextObstacleDist = 20

    while (true) {
      delay(50)
      distanceRan += (2 * speedMult).toInt().coerceAtLeast(1)
      score += 5

      // Rehan slowly creeps closer
      rehanDistance -= 0.12f * speedMult
      if (rehanDistance <= 5f) {
        soundManager.playCaught()
        gameOverReason = "Rehan caught up and grabbed Hamza's hoodie! 😂"
        isGameOver = true
        break
      }

      // Spawn obstacle
      if (distanceRan >= nextObstacleDist) {
        val type = ObstacleType.entries.random()
        obstacles.add(
          ChaseObstacle(
            id = System.currentTimeMillis(),
            xOffset = 1.0f,
            type = type
          )
        )
        nextObstacleDist = distanceRan + Random.nextInt(35, 60)
      }

      // Move existing obstacles leftwards
      val iterator = obstacles.iterator()
      while (iterator.hasNext()) {
        val obs = iterator.next()
        obs.xOffset -= 0.045f * speedMult

        // Collision zone check (around 0.25f where the 3 friends are running)
        if (obs.xOffset in 0.20f..0.32f && !obs.passed) {
          val dodged = if (obs.type.requiresSlide) isSliding else isJumping
          if (!dodged) {
            // Hit obstacle!
            soundManager.playAlert()
            soundManager.vibrate(80)
            rehanDistance -= 15f
            score = (score - 40).coerceAtLeast(0)
            obs.passed = true
          } else {
            obs.passed = true
            score += 60
            soundManager.playCoin()
          }
        }

        // Remove offscreen
        if (obs.xOffset < -0.1f) {
          iterator.remove()
        }
      }

      // Check win condition
      if (distanceRan >= targetDistance) {
        soundManager.playPrankVictory()
        soundManager.playLaugh()
        val stars = when {
          rehanDistance > 40f -> 3
          rehanDistance > 20f -> 2
          else -> 1
        }
        finalStars = stars
        onComplete(score + 800, stars * 45, stars)
        isVictory = true
        break
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
        levelTitle = "Level 4: Rehan Chases Us 🏃",
        score = score,
        coins = coins,
        onPause = { isPaused = true },
        onBack = onBack,
        onHelp = { showHowToPlay = true }
      )

      PrankProgressBar(
        progressPercent = (distanceRan.toFloat() / targetDistance).coerceIn(0f, 1f),
        label = "Distance to Clubhouse: $distanceRan m / $targetDistance m",
        iconEmoji = "🏠"
      )

      // Rehan Distance warning
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (rehanDistance < 20f) "🚨 REHAN IS RIGHT BEHIND! DROP BANANA!"
          else "🏃 Distance ahead: ${rehanDistance.toInt()} m",
          color = if (rehanDistance < 20f) ComicRed else ComicYellow,
          fontWeight = FontWeight.Black,
          fontSize = 12.sp
        )
        Text(
          text = "🍌 $bananasLeft Bananas",
          color = ComicYellow,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      }

      ControlsHelpBanner(
        text = "⬆️ JUMP over Footballs, Skateboards & Puddles • ⬇️ SLIDE under Laundry lines • 🍌 DROP BANANA to slip Rehan!"
      )

      // Running Corridor Stage
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF1B2335), Color(0xFF131722))
            )
          )
          .border(2.dp, Color(0xFF2C3852), RoundedCornerShape(20.dp))
      ) {
        // Background track illustration
        Image(
          painter = painterResource(id = R.drawable.img_prank_club),
          contentDescription = "Street Chase",
          contentScale = ContentScale.Crop,
          alpha = 0.2f,
          modifier = Modifier.fillMaxSize()
        )

        // Ground Track Line
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .align(Alignment.BottomCenter)
            .background(Color(0xFF37474F))
            .border(2.dp, ComicYellow.copy(alpha = 0.5f))
        )

        // Rehan Chasing Behind (Left Side)
        val rehanVisualOffset = (rehanDistance * 0.6f).dp
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = 10.dp, y = (-20).dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ComicRed)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("“RUKO! 😡”", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
            }
            AnimatedCharacterAvatar(
              characterId = CharacterId.REHAN,
              mood = CharacterMood.RUNNING,
              size = 75.dp
            )
          }
        }

        // Friends Running Together (Ahead on right)
        val jumpY by animateDpAsState(
          targetValue = if (isJumping) (-70).dp else if (isSliding) 14.dp else 0.dp,
          label = "jumpY"
        )

        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = 110.dp + rehanVisualOffset.coerceAtMost(50.dp), y = (-20).dp + jumpY)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isSliding) {
              Text("SLIDING! 💨", color = ComicYellow, fontWeight = FontWeight.Black, fontSize = 10.sp)
            }
            Row(verticalAlignment = Alignment.Bottom) {
              AnimatedCharacterAvatar(
                characterId = CharacterId.MUAVIYA,
                mood = CharacterMood.RUNNING,
                size = if (isSliding) 45.dp else 65.dp
              )
              AnimatedCharacterAvatar(
                characterId = CharacterId.HAMZA,
                mood = CharacterMood.RUNNING,
                size = if (isSliding) 50.dp else 70.dp
              )
              AnimatedCharacterAvatar(
                characterId = CharacterId.HUZAIFA,
                mood = CharacterMood.RUNNING,
                size = if (isSliding) 45.dp else 65.dp
              )
            }
          }
        }

        // Moving Obstacles
        obstacles.forEach { obs ->
          val obsY = if (obs.type.requiresSlide) (-85).dp else (-20).dp
          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .fillMaxWidth()
              .offset(x = (obs.xOffset * 320).dp, y = obsY)
          ) {
            Text(
              text = obs.type.emoji,
              fontSize = if (obs.type.requiresSlide) 36.sp else 30.sp
            )
          }
        }

        // Clubhouse at the Finish Line (Appears near the end)
        if (distanceRan >= targetDistance - 60) {
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(end = 12.dp, bottom = 20.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("SAFE CLUBHOUSE!", color = ComicGreen, fontWeight = FontWeight.Black, fontSize = 11.sp)
              Text("🏠🚪", fontSize = 44.sp)
            }
          }
        }
      }

      // Arcade Touch Action Buttons
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Jump Button
        ComicButton(
          text = "JUMP ⬆️",
          onClick = {
            if (!isJumping && !isSliding) {
              isJumping = true
              soundManager.playBoing()
            }
          },
          color = ComicYellow,
          textColor = Color.Black,
          modifier = Modifier.weight(1f),
          testTag = "btn_chase_jump"
        )

        // Slide Button
        ComicButton(
          text = "SLIDE ⬇️",
          onClick = {
            if (!isJumping && !isSliding) {
              isSliding = true
              soundManager.playWhoopee()
            }
          },
          color = ComicOrange,
          textColor = Color.White,
          modifier = Modifier.weight(1f),
          testTag = "btn_chase_slide"
        )

        // Banana Trap Button
        ComicButton(
          text = "🍌 SLIP ($bananasLeft)",
          onClick = {
            if (bananasLeft > 0) {
              bananasLeft--
              rehanDistance += 30f
              soundManager.playWhoopee()
              soundManager.playLaugh()
            }
          },
          color = if (bananasLeft > 0) ComicGreen else Color.DarkGray,
          textColor = Color.White,
          enabled = bananasLeft > 0,
          modifier = Modifier.weight(1.1f),
          testTag = "btn_chase_banana"
        )
      }
    }

    if (showHowToPlay) {
      HowToPlayDialog(
        levelNumber = 4,
        levelTitle = "Rehan Chases Us 🏃",
        goal = "Help the 3 friends safely dodge obstacles and reach the clubhouse!",
        instructions = listOf(
          "🏃" to "Rehan is jogging behind the 3 friends. Don't let him catch up!",
          "⬆️" to "Tap 'JUMP ⬆' to hop over puddles, skateboards, and footballs.",
          "⬇️" to "Tap 'SLIDE ⬇' to duck under hanging laundry clotheslines.",
          "🍌" to "Tap 'DROP BANANA 🍌' if Rehan gets too close to slip him up!",
          "🏠" to "Reach 300 meters safely to escape into the prank clubhouse!"
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
        levelId = 4,
        score = score,
        coinsEarned = finalStars * 45,
        stars = finalStars,
        onNextLevel = onNextLevel,
        onReplay = { restartLevel() },
        onLevelSelect = onLevelSelect
      )
    }
  }
}

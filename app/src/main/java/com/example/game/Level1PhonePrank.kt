package com.example.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.components.GameTopBar
import com.example.components.HowToPlayDialog
import com.example.components.PauseDialog
import com.example.components.VictoryDialog
import com.example.model.CharacterId
import com.example.model.CharacterMood
import com.example.model.GameDifficulty
import com.example.ui.theme.ComicBackgroundDark
import com.example.ui.theme.ComicCardDark
import com.example.ui.theme.ComicGreen
import com.example.ui.theme.ComicOrange
import com.example.ui.theme.ComicRed
import com.example.ui.theme.ComicYellow
import kotlinx.coroutines.delay

@Composable
fun Level1PhonePrank(
  soundManager: SoundManager,
  difficulty: GameDifficulty,
  coins: Int,
  onComplete: (score: Int, coinsEarned: Int, stars: Int) -> Unit,
  onNextLevel: () -> Unit,
  onBack: () -> Unit,
  onLevelSelect: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Gameplay States
  var score by remember { mutableIntStateOf(0) }
  var prankDone by remember { mutableStateOf(false) }
  var showPointsPopup by remember { mutableStateOf(false) }
  var rehanMood by remember { mutableStateOf(CharacterMood.IDLE) }
  var friendsMood by remember { mutableStateOf(CharacterMood.SNEAKING) }

  // Dialog States
  var showHowToPlay by remember { mutableStateOf(true) }
  var isPaused by remember { mutableStateOf(false) }
  var isVictoryDialogVisible by remember { mutableStateOf(false) }

  // Pulsing animations for guide arrows and buttons
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_guide")
  val arrowBounce by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 10f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "arrow_bounce"
  )

  val buttonPulseScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "button_pulse"
  )

  // Floating points animation
  val pointsScale by animateFloatAsState(
    targetValue = if (showPointsPopup) 1.2f else 0f,
    animationSpec = spring(dampingRatio = 0.5f),
    label = "points_scale"
  )

  fun triggerPhonePrank() {
    if (prankDone) return

    prankDone = true
    score += 10
    showPointsPopup = true

    // Play funny prank sounds
    soundManager.playAlarm()
    soundManager.playWhoopee()
    soundManager.playLaugh()
    soundManager.vibrate(80)

    // Character reactions
    rehanMood = CharacterMood.SHOCKED
    friendsMood = CharacterMood.LAUGHING

    // Save completion
    onComplete(score, 30, 3)
  }

  fun restartLevel() {
    score = 0
    prankDone = false
    showPointsPopup = false
    rehanMood = CharacterMood.IDLE
    friendsMood = CharacterMood.SNEAKING
    isPaused = false
    isVictoryDialogVisible = false
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ComicBackgroundDark)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
      // Top Bar with Score, Coins, Help (?) and Pause buttons
      GameTopBar(
        levelTitle = "Level 1: Phone Prank 📱",
        score = score,
        coins = coins,
        onPause = { isPaused = true },
        onBack = onBack,
        onHelp = { showHowToPlay = true }
      )

      // Prominent Instruction Banner
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (prankDone) ComicGreen else ComicYellow),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .border(2.5.dp, if (prankDone) Color.White else ComicOrange, RoundedCornerShape(16.dp))
          .shadow(6.dp, RoundedCornerShape(16.dp))
          .testTag("instruction_banner")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = if (prankDone) "🎉 PRANK SUCCESSFUL! 😂" else "👉 Tap the phone to prank Rehan 😂",
            color = if (prankDone) Color.White else Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
          )
          if (!prankDone) {
            Text(
              text = "Watch Rehan jump when the loud chicken alarm rings! 🐔",
              color = Color.Black.copy(alpha = 0.85f),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }
      }

      // Main Living Room Area with Rehan & Friends
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ComicCardDark),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .border(2.dp, Color(0xFF3D355F), RoundedCornerShape(22.dp))
          .shadow(8.dp, RoundedCornerShape(22.dp))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFF252042), Color(0xFF161329))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          // Subtle room background art
          Image(
            painter = painterResource(id = R.drawable.img_prank_club),
            contentDescription = "Living room",
            contentScale = ContentScale.Crop,
            alpha = 0.22f,
            modifier = Modifier.fillMaxSize()
          )

          // 3 Sneaking Friends on the Left
          Column(
            modifier = Modifier
              .align(Alignment.CenterStart)
              .padding(start = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (prankDone) ComicGreen else ComicOrange)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (prankDone) "HAHAHA! 😂" else "Shh... 🤫",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
              AnimatedCharacterAvatar(
                characterId = CharacterId.MUAVIYA,
                mood = friendsMood,
                size = 58.dp
              )
              AnimatedCharacterAvatar(
                characterId = CharacterId.HAMZA,
                mood = friendsMood,
                size = 66.dp
              )
              AnimatedCharacterAvatar(
                characterId = CharacterId.HUZAIFA,
                mood = friendsMood,
                size = 56.dp
              )
            }

            Text(
              text = "Muaviya, Hamza & Huzaifa",
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(top = 4.dp)
            )
          }

          // Rehan & His Phone on the Right
          Column(
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .padding(end = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Speech bubble
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (prankDone) ComicRed else Color(0xFF1976D2))
                .border(1.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = if (prankDone) "CHICKEN ALARM?! AHH! 🐔😱" else "Watching funny memes... 📱",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Rehan Character Avatar
            Box(
              modifier = Modifier
                .scale(if (prankDone) 1.08f else 1.0f),
              contentAlignment = Alignment.Center
            ) {
              AnimatedCharacterAvatar(
                characterId = CharacterId.REHAN,
                mood = rehanMood,
                size = 115.dp
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Phone in Rehan's hands (clickable drop target with glowing border)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (prankDone) ComicGreen else Color(0xFF2C2548))
                .border(
                  width = if (!prankDone) 2.5.dp else 2.dp,
                  color = if (!prankDone) ComicYellow else Color.White,
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable { triggerPhonePrank() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("rehan_phone_target"),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📱", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (prankDone) "🐔 BAAAWK! 😂" else "Rehan's Phone",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp
                )
              }
            }
          }

          // Animated Floating Points Feedback (+10 POINTS!)
          if (showPointsPopup) {
            Box(
              modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-30).dp)
                .scale(pointsScale)
                .clip(RoundedCornerShape(20.dp))
                .background(
                  Brush.linearGradient(listOf(ComicYellow, ComicOrange))
                )
                .border(3.dp, Color.White, RoundedCornerShape(20.dp))
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .padding(horizontal = 22.dp, vertical = 12.dp)
                .testTag("popup_points_feedback")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "+10 POINTS! 🌟",
                  color = Color.Black,
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
          }
        }
      }

      // Guiding Arrow / Highlight Indicator
      if (!prankDone) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset(y = arrowBounce.dp)
          ) {
            Text("👇", fontSize = 26.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "TAP THE BUTTON BELOW",
              color = ComicYellow,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("👇", fontSize = 26.sp)
          }
        }
      }

      // Large Action Button Panel
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
      ) {
        if (!prankDone) {
          // Large, unmissable Phone Action Button with animated highlight
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .scale(buttonPulseScale)
              .clip(RoundedCornerShape(20.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFFFFA000), Color(0xFFFF5722))
                )
              )
              .border(3.5.dp, ComicYellow, RoundedCornerShape(20.dp))
              .shadow(12.dp, RoundedCornerShape(20.dp))
              .clickable { triggerPhonePrank() }
              .padding(vertical = 18.dp, horizontal = 20.dp)
              .testTag("btn_tap_phone"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text("📱", fontSize = 32.sp)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "TAP THE PHONE TO PRANK! 😂",
                  color = Color.White,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "Ring the loud chicken alarm on Rehan",
                  color = Color.White.copy(alpha = 0.9f),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        } else {
          // Prank Done: Clearly Show NEXT LEVEL Button & Celebration
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Unmissable NEXT LEVEL button (Large Green)
            ComicButton(
              text = "NEXT LEVEL ➔",
              onClick = onNextLevel,
              color = ComicGreen,
              textColor = Color.White,
              modifier = Modifier
                .fillMaxWidth()
                .scale(buttonPulseScale)
                .testTag("btn_next_level_prominent")
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              ComicButton(
                text = "REPLAY 🔄",
                onClick = { restartLevel() },
                color = ComicOrange,
                textColor = Color.White,
                modifier = Modifier.weight(1f),
                testTag = "btn_replay_level1"
              )

              ComicButton(
                text = "LEVELS 🎯",
                onClick = onLevelSelect,
                color = ComicYellow,
                textColor = Color.Black,
                modifier = Modifier.weight(1f),
                testTag = "btn_all_levels"
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // "HOW TO PLAY" Dialog at beginning of level
    if (showHowToPlay) {
      HowToPlayDialog(
        levelNumber = 1,
        levelTitle = "Phone Prank 📱",
        goal = "Prank Rehan by setting off a hilarious loud chicken alarm on his phone!",
        instructions = listOf(
          "📱" to "Rehan is busy reading funny memes on his phone.",
          "👇" to "Tap the big glowing PHONE button to activate the prank!",
          "😂" to "Watch Rehan jump in surprise and earn +10 POINTS!",
          "➔" to "Tap 'NEXT LEVEL' to proceed to Level 2 (Food Prank)!"
        ),
        onStart = {
          showHowToPlay = false
          soundManager.playTap()
        }
      )
    }

    // Pause Dialog
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

    // Optional Victory Dialog
    if (isVictoryDialogVisible) {
      VictoryDialog(
        levelId = 1,
        score = score,
        coinsEarned = 30,
        stars = 3,
        onNextLevel = onNextLevel,
        onReplay = { restartLevel() },
        onLevelSelect = onLevelSelect
      )
    }
  }
}

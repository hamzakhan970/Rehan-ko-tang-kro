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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.components.SuspicionMeter
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
import kotlin.random.Random

@Composable
fun Level2FoodPrank(
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
  var suspicion by remember { mutableFloatStateOf(0f) }

  // 3 Harmless Prank Toppings:
  var addedChili by remember { mutableStateOf(false) }
  var addedSqueakCheese by remember { mutableStateOf(false) }
  var addedMustacheFlag by remember { mutableStateOf(false) }

  // Rehan dining states: LOOKING_TABLE, SIPPING_SODA, WATCHING_TV
  var rehanEatingState by remember { mutableStateOf("LOOKING_TABLE") }
  var rehanMood by remember { mutableStateOf(CharacterMood.IDLE) }
  var rehanBiteAnimation by remember { mutableStateOf(false) }

  // Dialogs
  var showHowToPlay by remember { mutableStateOf(true) }
  var isPaused by remember { mutableStateOf(false) }
  var isGameOver by remember { mutableStateOf(false) }
  var gameOverReason by remember { mutableStateOf("") }
  var isVictory by remember { mutableStateOf(false) }
  var finalStars by remember { mutableIntStateOf(3) }

  val toppingsCount = (if (addedChili) 1 else 0) + (if (addedSqueakCheese) 1 else 0) + (if (addedMustacheFlag) 1 else 0)
  val progress = toppingsCount / 3f

  fun restartLevel() {
    score = 0
    suspicion = 0f
    addedChili = false
    addedSqueakCheese = false
    addedMustacheFlag = false
    rehanEatingState = "LOOKING_TABLE"
    rehanMood = CharacterMood.IDLE
    rehanBiteAnimation = false
    isPaused = false
    isGameOver = false
    isVictory = false
  }

  // Rehan Dining Loop
  LaunchedEffect(isPaused, isGameOver, isVictory, rehanBiteAnimation) {
    if (isPaused || isGameOver || isVictory || rehanBiteAnimation) return@LaunchedEffect

    val speedMult = difficulty.speedMultiplier
    while (true) {
      // Rehan is looking at his burger
      rehanEatingState = "LOOKING_TABLE"
      rehanMood = CharacterMood.IDLE
      delay(((Random.nextInt(2000, 3500)) / speedMult).toLong())

      // Rehan looks away to watch TV or wipes napkin
      val lookAwayChoice = if (Random.nextBoolean()) "WATCHING_TV" else "SIPPING_SODA"
      rehanEatingState = lookAwayChoice
      rehanMood = CharacterMood.SUSPICIOUS
      soundManager.playAlert()
      delay(((Random.nextInt(2400, 3800)) / speedMult).toLong())
    }
  }

  fun tryAddTopping(toppingIndex: Int) {
    if (rehanEatingState == "LOOKING_TABLE") {
      // Caught in the act!
      suspicion += 0.35f
      soundManager.playCaught()
      soundManager.vibrate(60)
      if (suspicion >= 1.0f) {
        gameOverReason = "Rehan saw Hamza dropping toppings onto his burger! 😂"
        isGameOver = true
      }
    } else {
      // Safe addition!
      soundManager.playWhoopee()
      score += 150
      when (toppingIndex) {
        1 -> addedChili = true
        2 -> addedSqueakCheese = true
        3 -> addedMustacheFlag = true
      }

      // Check if all 3 toppings placed
      if (toppingsCount + 1 >= 3) {
        rehanBiteAnimation = true
        soundManager.playBoing()
        // Final bite cutscene
        rehanMood = CharacterMood.SHOCKED
        soundManager.playLaugh()
        soundManager.playPrankVictory()

        val stars = when {
          suspicion < 0.35f -> 3
          suspicion < 0.70f -> 2
          else -> 1
        }
        finalStars = stars
        onComplete(score + 600, stars * 35, stars)
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
        levelTitle = "Level 2: Food Prank 🍔",
        score = score,
        coins = coins,
        onPause = { isPaused = true },
        onBack = onBack,
        onHelp = { showHowToPlay = true }
      )

      SuspicionMeter(
        suspicionPercent = suspicion,
        isLooking = rehanEatingState == "LOOKING_TABLE"
      )

      PrankProgressBar(
        progressPercent = progress,
        label = "Burger Toppings ($toppingsCount / 3)",
        iconEmoji = "🍔"
      )

      ControlsHelpBanner(
        text = if (rehanEatingState == "LOOKING_TABLE") {
          "⚠️ STOP! Rehan is looking directly at his burger plate!"
        } else {
          "🍔 TAP the prank toppings below to add them while Rehan is looking away (${if (rehanEatingState == "WATCHING_TV") "Watching TV 📺" else "Sipping Soda 🥤"})!"
        }
      )

      // Dining Table Stage
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF2E201B), Color(0xFF1B1626))
            )
          )
          .border(2.dp, Color(0xFF5D4037), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
      ) {
        // Background illustration
        Image(
          painter = painterResource(id = R.drawable.img_prank_club),
          contentDescription = "Dining room",
          contentScale = ContentScale.Crop,
          alpha = 0.2f,
          modifier = Modifier.fillMaxSize()
        )

        // Dining Table and Rehan
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxSize()
        ) {
          Spacer(modifier = Modifier.height(16.dp))

          // Rehan Dining Bubble
          val statusText = when (rehanEatingState) {
            "WATCHING_TV" -> "“Oho, cricket match chal raha hai! 📺”"
            "SIPPING_SODA" -> "“Slurp... yeh soda bohot thanda hai! 🥤”"
            else -> "“Mera burger kitna mazedar lag raha hai! 😋”"
          }
          val bubbleBg = if (rehanEatingState == "LOOKING_TABLE") ComicRed else ComicGreen

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(bubbleBg)
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = statusText,
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Rehan avatar
          AnimatedCharacterAvatar(
            characterId = CharacterId.REHAN,
            mood = if (rehanBiteAnimation) CharacterMood.SHOCKED else rehanMood,
            size = 110.dp
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Dining Table with Giant Burger
          Box(
            modifier = Modifier
              .fillMaxWidth(0.85f)
              .height(90.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF4E342E))
              .border(3.dp, Color(0xFF8D6E63), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              // Soda Glass
              Text("🥤", fontSize = 32.sp)
              Spacer(modifier = Modifier.width(16.dp))

              // The Burger
              Box(contentAlignment = Alignment.Center) {
                Text("🍔", fontSize = 48.sp)
                // Overlaid prank toppings
                Row(modifier = Modifier.offset(y = (-14).dp)) {
                  if (addedChili) Text("🌶️", fontSize = 20.sp)
                  if (addedSqueakCheese) Text("🧀", fontSize = 20.sp)
                  if (addedMustacheFlag) Text("🚩", fontSize = 20.sp)
                }
              }

              Spacer(modifier = Modifier.width(16.dp))
              Text("🍟", fontSize = 32.sp)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Sneaking Friends Lookout
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            AnimatedCharacterAvatar(characterId = CharacterId.MUAVIYA, mood = CharacterMood.IDLE, size = 55.dp)
            Spacer(modifier = Modifier.width(8.dp))
            AnimatedCharacterAvatar(characterId = CharacterId.HAMZA, mood = CharacterMood.LAUGHING, size = 60.dp)
            Spacer(modifier = Modifier.width(8.dp))
            AnimatedCharacterAvatar(characterId = CharacterId.HUZAIFA, mood = CharacterMood.IDLE, size = 55.dp)
          }
        }
      }

      // Topping Action Buttons
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Chili Drop
          ComicButton(
            text = if (addedChili) "CHILI ADDED ✅" else "SPICY CHILI 🌶️",
            onClick = { tryAddTopping(1) },
            color = if (addedChili) Color.DarkGray else ComicRed,
            textColor = Color.White,
            enabled = !addedChili,
            modifier = Modifier.weight(1f),
            testTag = "btn_add_chili"
          )

          // Squeak Cheese
          ComicButton(
            text = if (addedSqueakCheese) "CHEESE ADDED ✅" else "SQUEAK CHEESE 🧀",
            onClick = { tryAddTopping(2) },
            color = if (addedSqueakCheese) Color.DarkGray else ComicYellow,
            textColor = Color.Black,
            enabled = !addedSqueakCheese,
            modifier = Modifier.weight(1f),
            testTag = "btn_add_cheese"
          )
        }

        // Mustache Flag
        ComicButton(
          text = if (addedMustacheFlag) "FLAG PLACED ✅" else "HAMZA'S MUSTACHE FLAG 🚩",
          onClick = { tryAddTopping(3) },
          color = if (addedMustacheFlag) Color.DarkGray else ComicOrange,
          textColor = Color.White,
          enabled = !addedMustacheFlag,
          modifier = Modifier.fillMaxWidth(),
          testTag = "btn_add_flag"
        )
      }
    }

    if (showHowToPlay) {
      HowToPlayDialog(
        levelNumber = 2,
        levelTitle = "Food Prank 🍔",
        goal = "Add 3 hilarious prank toppings to Rehan's burger while he looks away!",
        instructions = listOf(
          "🍔" to "Rehan is enjoying his burger at the table.",
          "👀" to "Wait until Rehan looks away (watching TV 📺 or sipping soda 🥤).",
          "🌶️" to "Tap the 3 topping buttons: Chili Sauce, Squeak Cheese & Silly Mustache Flag!",
          "😂" to "Watch Rehan take a bite and hear the hilarious squeak reaction!"
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
        levelId = 2,
        score = score,
        coinsEarned = finalStars * 35,
        stars = finalStars,
        onNextLevel = onNextLevel,
        onReplay = { restartLevel() },
        onLevelSelect = onLevelSelect
      )
    }
  }
}

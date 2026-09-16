package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.audio.SoundManager
import com.example.data.GameRepository
import com.example.game.Level1PhonePrank
import com.example.game.Level2FoodPrank
import com.example.game.Level3WakeRehan
import com.example.game.Level4RehanChase
import com.example.game.Level5RehansRevenge
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.CharactersScreen
import com.example.ui.screens.LevelsScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.RehanKoTangKaroTheme

sealed interface AppScreen {
  data object MainMenu : AppScreen
  data object Levels : AppScreen
  data object Characters : AppScreen
  data object Settings : AppScreen
  data object About : AppScreen
  data class Game(val levelId: Int) : AppScreen
}

class MainActivity : ComponentActivity() {
  private lateinit var soundManager: SoundManager
  private lateinit var gameRepository: GameRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    soundManager = SoundManager(this)
    gameRepository = GameRepository(this)

    // Sync sound/haptic preferences from repository
    soundManager.soundEnabled = gameRepository.soundEnabled.value
    soundManager.hapticsEnabled = gameRepository.hapticsEnabled.value

    setContent {
      RehanKoTangKaroTheme {
        Scaffold(
          contentWindowInsets = WindowInsets.safeDrawing,
          modifier = Modifier.fillMaxSize()
        ) { _ ->
          GameApp(
            repository = gameRepository,
            soundManager = soundManager
          )
        }
      }
    }
  }
}

@Composable
fun GameApp(
  repository: GameRepository,
  soundManager: SoundManager
) {
  var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.MainMenu) }

  val totalCoins by repository.totalCoins.collectAsState()
  val difficulty by repository.difficulty.collectAsState()
  val unlockedLevels by repository.unlockedLevels.collectAsState()
  val highestUnlocked = unlockedLevels.maxOrNull() ?: 1

  // Handle system back navigation
  BackHandler(enabled = currentScreen != AppScreen.MainMenu) {
    currentScreen = when (currentScreen) {
      is AppScreen.Game -> AppScreen.Levels
      else -> AppScreen.MainMenu
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    when (val screen = currentScreen) {
      is AppScreen.MainMenu -> {
        MainMenuScreen(
          soundManager = soundManager,
          totalCoins = totalCoins,
          highestUnlockedLevel = highestUnlocked,
          onPlay = { currentScreen = AppScreen.Game(highestUnlocked) },
          onLevels = { currentScreen = AppScreen.Levels },
          onCharacters = { currentScreen = AppScreen.Characters },
          onSettings = { currentScreen = AppScreen.Settings },
          onAbout = { currentScreen = AppScreen.About }
        )
      }

      is AppScreen.Levels -> {
        LevelsScreen(
          repository = repository,
          soundManager = soundManager,
          onSelectLevel = { levelId ->
            currentScreen = AppScreen.Game(levelId)
          },
          onBack = { currentScreen = AppScreen.MainMenu }
        )
      }

      is AppScreen.Characters -> {
        CharactersScreen(
          soundManager = soundManager,
          onBack = { currentScreen = AppScreen.MainMenu }
        )
      }

      is AppScreen.Settings -> {
        SettingsScreen(
          repository = repository,
          soundManager = soundManager,
          onBack = { currentScreen = AppScreen.MainMenu }
        )
      }

      is AppScreen.About -> {
        AboutScreen(
          soundManager = soundManager,
          onBack = { currentScreen = AppScreen.MainMenu }
        )
      }

      is AppScreen.Game -> {
        val levelId = screen.levelId
        val onLevelComplete: (Int, Int, Int) -> Unit = { score, coinsEarned, stars ->
          repository.completeLevel(levelId, score, coinsEarned, stars)
        }
        val onNextLevel: () -> Unit = {
          if (levelId < 5) {
            currentScreen = AppScreen.Game(levelId + 1)
          } else {
            currentScreen = AppScreen.Levels
          }
        }
        val onBackToLevels: () -> Unit = {
          currentScreen = AppScreen.Levels
        }

        when (levelId) {
          1 -> Level1PhonePrank(
            soundManager = soundManager,
            difficulty = difficulty,
            coins = totalCoins,
            onComplete = onLevelComplete,
            onNextLevel = onNextLevel,
            onBack = onBackToLevels,
            onLevelSelect = onBackToLevels
          )

          2 -> Level2FoodPrank(
            soundManager = soundManager,
            difficulty = difficulty,
            coins = totalCoins,
            onComplete = onLevelComplete,
            onNextLevel = onNextLevel,
            onBack = onBackToLevels,
            onLevelSelect = onBackToLevels
          )

          3 -> Level3WakeRehan(
            soundManager = soundManager,
            difficulty = difficulty,
            coins = totalCoins,
            onComplete = onLevelComplete,
            onNextLevel = onNextLevel,
            onBack = onBackToLevels,
            onLevelSelect = onBackToLevels
          )

          4 -> Level4RehanChase(
            soundManager = soundManager,
            difficulty = difficulty,
            coins = totalCoins,
            onComplete = onLevelComplete,
            onNextLevel = onNextLevel,
            onBack = onBackToLevels,
            onLevelSelect = onBackToLevels
          )

          5 -> Level5RehansRevenge(
            soundManager = soundManager,
            difficulty = difficulty,
            coins = totalCoins,
            onComplete = onLevelComplete,
            onNextLevel = onNextLevel,
            onBack = onBackToLevels,
            onLevelSelect = onBackToLevels
          )

          else -> {
            currentScreen = AppScreen.Levels
          }
        }
      }
    }
  }
}

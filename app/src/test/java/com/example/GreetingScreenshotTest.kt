package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.example.audio.SoundManager
import com.example.ui.screens.MainMenuScreen
import com.example.ui.theme.RehanKoTangKaroTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun mainMenu_screenshot() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val soundManager = SoundManager(context)

    composeTestRule.setContent {
      RehanKoTangKaroTheme {
        MainMenuScreen(
          soundManager = soundManager,
          totalCoins = 250,
          highestUnlockedLevel = 1,
          onPlay = {},
          onLevels = {},
          onCharacters = {},
          onSettings = {},
          onAbout = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/main_menu.png")
  }
}

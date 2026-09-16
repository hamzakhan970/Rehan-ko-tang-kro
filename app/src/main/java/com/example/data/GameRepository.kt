package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GameDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("rehan_prank_game_prefs", Context.MODE_PRIVATE)

  private val _unlockedLevels = MutableStateFlow(loadUnlockedLevels())
  val unlockedLevels: StateFlow<Set<Int>> = _unlockedLevels.asStateFlow()

  private val _totalCoins = MutableStateFlow(prefs.getInt("total_coins", 150))
  val totalCoins: StateFlow<Int> = _totalCoins.asStateFlow()

  private val _levelStars = MutableStateFlow(loadLevelStars())
  val levelStars: StateFlow<Map<Int, Int>> = _levelStars.asStateFlow()

  private val _levelHighScores = MutableStateFlow(loadLevelHighScores())
  val levelHighScores: StateFlow<Map<Int, Int>> = _levelHighScores.asStateFlow()

  private val _soundEnabled = MutableStateFlow(prefs.getBoolean("sound_enabled", true))
  val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

  private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean("haptics_enabled", true))
  val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

  private val _difficulty = MutableStateFlow(loadDifficulty())
  val difficulty: StateFlow<GameDifficulty> = _difficulty.asStateFlow()

  private fun loadUnlockedLevels(): Set<Int> {
    val stringSet = prefs.getStringSet("unlocked_levels", setOf("1")) ?: setOf("1")
    val set = stringSet.mapNotNull { it.toIntOrNull() }.toMutableSet()
    if (!set.contains(1)) set.add(1)
    return set
  }

  private fun loadLevelStars(): Map<Int, Int> {
    val map = mutableMapOf<Int, Int>()
    for (i in 1..5) {
      val stars = prefs.getInt("level_${i}_stars", 0)
      if (stars > 0) map[i] = stars
    }
    return map
  }

  private fun loadLevelHighScores(): Map<Int, Int> {
    val map = mutableMapOf<Int, Int>()
    for (i in 1..5) {
      val score = prefs.getInt("level_${i}_high_score", 0)
      if (score > 0) map[i] = score
    }
    return map
  }

  private fun loadDifficulty(): GameDifficulty {
    val name = prefs.getString("game_difficulty", GameDifficulty.NORMAL.name)
    return try {
      GameDifficulty.valueOf(name ?: GameDifficulty.NORMAL.name)
    } catch (_: Exception) {
      GameDifficulty.NORMAL
    }
  }

  fun isLevelUnlocked(levelId: Int): Boolean {
    return _unlockedLevels.value.contains(levelId)
  }

  fun getStarsForLevel(levelId: Int): Int {
    return _levelStars.value[levelId] ?: 0
  }

  fun getHighScoreForLevel(levelId: Int): Int {
    return _levelHighScores.value[levelId] ?: 0
  }

  fun completeLevel(levelId: Int, score: Int, coinsEarned: Int, stars: Int) {
    // 1. Update stars
    val currentStars = _levelStars.value[levelId] ?: 0
    val newStars = maxOf(currentStars, stars)
    val updatedStars = _levelStars.value.toMutableMap()
    updatedStars[levelId] = newStars
    _levelStars.value = updatedStars
    prefs.edit().putInt("level_${levelId}_stars", newStars).apply()

    // 2. Update high score
    val currentScore = _levelHighScores.value[levelId] ?: 0
    val newScore = maxOf(currentScore, score)
    val updatedScores = _levelHighScores.value.toMutableMap()
    updatedScores[levelId] = newScore
    _levelHighScores.value = updatedScores
    prefs.edit().putInt("level_${levelId}_high_score", newScore).apply()

    // 3. Update coins
    val newTotalCoins = _totalCoins.value + coinsEarned
    _totalCoins.value = newTotalCoins
    prefs.edit().putInt("total_coins", newTotalCoins).apply()

    // 4. Unlock next level if exists
    if (levelId < 5) {
      val nextLevel = levelId + 1
      val updatedUnlocked = _unlockedLevels.value.toMutableSet()
      updatedUnlocked.add(nextLevel)
      _unlockedLevels.value = updatedUnlocked
      prefs.edit().putStringSet("unlocked_levels", updatedUnlocked.map { it.toString() }.toSet()).apply()
    }
  }

  fun addCoins(amount: Int) {
    val updated = _totalCoins.value + amount
    _totalCoins.value = updated
    prefs.edit().putInt("total_coins", updated).apply()
  }

  fun setSoundEnabled(enabled: Boolean) {
    _soundEnabled.value = enabled
    prefs.edit().putBoolean("sound_enabled", enabled).apply()
  }

  fun setHapticsEnabled(enabled: Boolean) {
    _hapticsEnabled.value = enabled
    prefs.edit().putBoolean("haptics_enabled", enabled).apply()
  }

  fun setDifficulty(difficulty: GameDifficulty) {
    _difficulty.value = difficulty
    prefs.edit().putString("game_difficulty", difficulty.name).apply()
  }

  fun resetProgress() {
    prefs.edit()
      .clear()
      .putStringSet("unlocked_levels", setOf("1"))
      .putInt("total_coins", 100)
      .apply()

    _unlockedLevels.value = setOf(1)
    _levelStars.value = emptyMap()
    _levelHighScores.value = emptyMap()
    _totalCoins.value = 100
    _soundEnabled.value = true
    _hapticsEnabled.value = true
    _difficulty.value = GameDifficulty.NORMAL
  }
}

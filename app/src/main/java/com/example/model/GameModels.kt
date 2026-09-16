package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.HamzaRed
import com.example.ui.theme.HuzaifaGreen
import com.example.ui.theme.MuaviyaGold
import com.example.ui.theme.RehanBlue

enum class CharacterId {
  REHAN,
  MUAVIYA,
  HAMZA,
  HUZAIFA
}

enum class CharacterMood {
  IDLE,
  SNEAKING,
  SUSPICIOUS,
  LAUGHING,
  SHOCKED,
  SLEEPING,
  RUNNING,
  VICTORY,
  SPLATTED
}

data class CharacterProfile(
  val id: CharacterId,
  val name: String,
  val nickname: String,
  val role: String,
  val color: Color,
  val badge: String,
  val description: String,
  val funnyQuote: String,
  val favoriteProp: String,
  val traits: List<String>
)

object CharactersData {
  val list = listOf(
    CharacterProfile(
      id = CharacterId.REHAN,
      name = "Rehan",
      nickname = "The Innocent Target 🎯",
      role = "Main Character & Comic Target",
      color = RehanBlue,
      badge = "Target #1",
      description = "Rehan loves his phone, yummy burgers, and peaceful naps. But with his three best friends around, peace is never an option! His comic reactions and dramatic chases are legendary.",
      funnyQuote = "“Yaar tum teeno kabhi to chain se jeene do! 😂”",
      favoriteProp = "Smartphone & Nap Pillow",
      traits = listOf(
        "Gets lost in phone memes",
        "Deep sleeper (snoring champion)",
        "Sprints like a rocket when pranked",
        "Master of comic revenge"
      )
    ),
    CharacterProfile(
      id = CharacterId.MUAVIYA,
      name = "Muaviya",
      nickname = "The Mastermind 🧠",
      role = "Prank Planner & Strategist",
      color = MuaviyaGold,
      badge = "The Brains",
      description = "Smart, calm, and always armed with a mischief blueprint. Muaviya calculates the perfect angles for whoopee cushions and knows Rehan's snack schedule by heart.",
      funnyQuote = "“Plan simple hai: Hamza distraction dega, Huzaifa prop layega, aur baaki mera kaam! 💡”",
      favoriteProp = "Prank Blueprint & Notepad",
      traits = listOf(
        "Never gets caught (supposedly)",
        "Calculates burger swap trajectories",
        "Keeps stopwatches for sneak timing",
        "Winks before every masterpiece prank"
      )
    ),
    CharacterProfile(
      id = CharacterId.HAMZA,
      name = "Hamza",
      nickname = "The Troublemaker 💥",
      role = "Prank Starter & Comedy Catalyst",
      color = HamzaRed,
      badge = "Chief Mischief",
      description = "The guy who actually presses the button, swaps the sauce, and bursts out laughing two seconds too early! Hamza can't keep a straight face for more than three seconds.",
      funnyQuote = "“Rehan bhai, bas do minute aankhein band karo, ek surprise hai! 🤣”",
      favoriteProp = "Whoopee Cushion & Squeaky Horn",
      traits = listOf(
        "Uncontrollable giggling",
        "Always wears cap backwards",
        "Fastest banana peel dropper",
        "First to run when Rehan looks up"
      )
    ),
    CharacterProfile(
      id = CharacterId.HUZAIFA,
      name = "Huzaifa",
      nickname = "The Accomplice 🪶",
      role = "Prank Enabler & Lookout",
      color = HuzaifaGreen,
      badge = "The Wingman",
      description = "Equally mischievous but stays on high alert. Huzaifa brings the softest feathers, the sneak cushions, and signals the team when Rehan twitches his eyebrow.",
      funnyQuote = "“Main guard pe khada hoon! Rehan hil raha hai, jaldi chhupo! 👀”",
      favoriteProp = "Magic Feather & Confetti Cannon",
      traits = listOf(
        "Expert sofa hider",
        "Feather tickle technician",
        "Always has backup water balloons",
        "Epic high-five partner"
      )
    )
  )
}

data class LevelData(
  val id: Int,
  val title: String,
  val subtitle: String,
  val iconEmoji: String,
  val objective: String,
  val controlsHelp: String,
  val defaultHighScore: Int = 1000
)

object LevelsCatalog {
  val levels = listOf(
    LevelData(
      id = 1,
      title = "Level 1 — Phone Prank",
      subtitle = "Chicken Alarm & Phone Swap!",
      iconEmoji = "📱",
      objective = "Sneak up while Rehan scrolls on his phone. Hide when he looks up! Place the funny chicken alarm sticker on his screen!",
      controlsHelp = "TAP 'Sneak' to crawl forward • TAP 'Hide' when Rehan looks up • DRAG sticker onto phone!"
    ),
    LevelData(
      id = 2,
      title = "Level 2 — Food Prank",
      subtitle = "Mega Spicy Squeak Burger!",
      iconEmoji = "🍔",
      objective = "Rehan is enjoying lunch. When he wipes his face or glances at TV, swap ingredients on his giant burger with funny prank toppings!",
      controlsHelp = "DRAG chili drop & squeak cheese when Rehan looks away • Avoid adding toppings while he watches!"
    ),
    LevelData(
      id = 3,
      title = "Level 3 — Wake Rehan",
      subtitle = "Feather, Mustache & Alarm!",
      iconEmoji = "😴",
      objective = "Rehan is fast asleep. Use Huzaifa's feather to tickle his nose, draw a funny mustache with washable marker, and set off the rooster clock!",
      controlsHelp = "SWIPE feather across nose • TRACE mustache outline • TAP rooster clock to sound alarm!"
    ),
    LevelData(
      id = 4,
      title = "Level 4 — Rehan Chases Us",
      subtitle = "Run for the Clubhouse!",
      iconEmoji = "🏃",
      objective = "Rehan discovered the prank and is chasing Muaviya, Hamza and Huzaifa! Dodge obstacles, drop banana peels, and reach the safe clubhouse!",
      controlsHelp = "TAP JUMP over obstacles • TAP SLIDE under banners • DROP BANANA to slip Rehan!"
    ),
    LevelData(
      id = 5,
      title = "Level 5 — Rehan's Revenge",
      subtitle = "Water Balloon Blitz!",
      iconEmoji = "😈",
      objective = "Rehan strikes back! Take control of Rehan and launch harmless water balloons and confetti at the 3 friends as they pop up from hiding spots!",
      controlsHelp = "TAP & AIM slingshot at popping friends • Soak Hamza, Muaviya, and Huzaifa before time expires!"
    )
  )
}

enum class GameDifficulty(val label: String, val speedMultiplier: Float) {
  EASY("Easy Mode", 0.8f),
  NORMAL("Normal Mode", 1.0f),
  HARD("Hard Mode (Fast!)", 1.35f)
}

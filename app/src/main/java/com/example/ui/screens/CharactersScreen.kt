package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.model.CharacterId
import com.example.model.CharacterMood
import com.example.model.CharactersData
import com.example.ui.theme.ComicBackgroundDark
import com.example.ui.theme.ComicCardDark
import com.example.ui.theme.ComicGreen
import com.example.ui.theme.ComicOrange
import com.example.ui.theme.ComicYellow

@Composable
fun CharactersScreen(
  soundManager: SoundManager,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCharacterId by remember { mutableStateOf(CharacterId.REHAN) }
  var activeMood by remember { mutableStateOf(CharacterMood.IDLE) }

  val character = remember(selectedCharacterId) {
    CharactersData.list.first { it.id == selectedCharacterId }
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
        .padding(16.dp)
    ) {
      // Top Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(ComicCardDark)
            .border(2.dp, ComicYellow, CircleShape)
            .clickable {
              soundManager.playTap()
              onBack()
            }
            .testTag("btn_back_characters"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = ComicYellow,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "MEET THE 4 FRIENDS 👥",
            color = ComicYellow,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
          )
          Text(
            text = "3 Dost vs Rehan — Comic Squad",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Character Selector Tabs
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CharactersData.list.forEach { charItem ->
          val isSelected = charItem.id == selectedCharacterId
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) charItem.color else ComicCardDark)
              .border(
                width = 2.dp,
                color = if (isSelected) ComicYellow else Color(0xFF3B3550),
                shape = RoundedCornerShape(14.dp)
              )
              .clickable {
                selectedCharacterId = charItem.id
                activeMood = CharacterMood.IDLE
                soundManager.playTap()
              }
              .padding(vertical = 8.dp)
              .testTag("tab_character_${charItem.name.lowercase()}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = charItem.name,
              color = if (isSelected && charItem.color == ComicYellow) Color.Black else Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Main Interactive Profile Card
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ComicCardDark),
        modifier = Modifier
          .fillMaxWidth()
          .border(2.5.dp, character.color, RoundedCornerShape(24.dp))
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(character.color.copy(alpha = 0.25f))
              .border(1.5.dp, character.color, RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Text(
              text = character.badge,
              color = ComicYellow,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Big Animated Avatar
          AnimatedCharacterAvatar(
            characterId = character.id,
            mood = activeMood,
            size = 130.dp,
            showBadge = false
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = character.name,
            color = ComicYellow,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
          )
          Text(
            text = character.nickname,
            color = ComicOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = character.role,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Funny Expression Buttons
          Text(
            text = "Test Expressions:",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            val moods = listOf(
              "Default" to CharacterMood.IDLE,
              "Laugh 😂" to CharacterMood.LAUGHING,
              "Shocked 😱" to CharacterMood.SHOCKED,
              "Suspicious 👀" to CharacterMood.SUSPICIOUS
            )
            moods.forEach { (label, moodVal) ->
              val isCurrent = activeMood == moodVal
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isCurrent) character.color else Color(0xFF1B1828))
                  .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                  .clickable {
                    activeMood = moodVal
                    if (moodVal == CharacterMood.LAUGHING) soundManager.playLaugh()
                    else if (moodVal == CharacterMood.SHOCKED) soundManager.playAlert()
                    else soundManager.playTap()
                  }
                  .padding(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Text(
                  text = label,
                  color = if (isCurrent && character.color == ComicYellow) Color.Black else Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Character Voice / Quote Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF1D192E))
              .border(1.dp, Color(0xFF3B3550), RoundedCornerShape(16.dp))
              .padding(14.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "SIGNATURE QUOTE",
                  color = ComicYellow,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
                Box(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(character.color)
                    .clickable {
                      soundManager.playLaugh()
                    }
                    .padding(6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Filled.VolumeUp,
                    contentDescription = "Play Quote Sound",
                    tint = if (character.color == ComicYellow) Color.Black else Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = character.funnyQuote,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Bio Description
          Text(
            text = character.description,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Funny Traits Checklist
          Column(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = "FUNNY TRAITS:",
              color = ComicOrange,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            character.traits.forEach { trait ->
              Row(
                modifier = Modifier.padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("•", color = ComicYellow, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(trait, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Favorite Prank Prop Pill
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF2E2412))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("🎁 Favorite Prop:", color = ComicYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(character.favoriteProp, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Rehan Expressions Sticker Sheet Preview
      if (selectedCharacterId == CharacterId.REHAN) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = ComicCardDark),
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .border(2.dp, ComicYellow, RoundedCornerShape(20.dp))
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            Image(
              painter = painterResource(id = R.drawable.img_rehan_reactions),
              contentDescription = "Rehan Reactions Sheet",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

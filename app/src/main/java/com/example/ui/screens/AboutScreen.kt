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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.model.CharacterId
import com.example.model.CharacterMood
import com.example.ui.theme.ComicBackgroundDark
import com.example.ui.theme.ComicCardDark
import com.example.ui.theme.ComicGreen
import com.example.ui.theme.ComicOrange
import com.example.ui.theme.ComicYellow

@Composable
fun AboutScreen(
  soundManager: SoundManager,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
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
      // Header
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
            .testTag("btn_back_about"),
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
            text = "ABOUT THE GAME ℹ",
            color = ComicYellow,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
          )
          Text(
            text = "“REHAN KO TANG KARO 😂”",
            color = ComicOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Hero Illustration Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ComicCardDark),
        modifier = Modifier
          .fillMaxWidth()
          .height(150.dp)
          .border(2.dp, ComicYellow, RoundedCornerShape(20.dp))
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          Image(
            painter = painterResource(id = R.drawable.img_prank_club),
            contentDescription = "4 Friends Clubhouse",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Story Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ComicCardDark),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.5.dp, Color(0xFF3B3550), RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "🎮 THE STORY",
            color = ComicYellow,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Rehan, Muaviya, Hamza, and Huzaifa are four inseparable best friends who grew up together. Muaviya comes up with the clever masterplans, Hamza is the chaotic prankster who pushes the big red button, Huzaifa provides the funny props, and lovable Rehan is their favorite comic target — until Level 5 when Rehan launches his epic water balloon revenge!",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 13.sp,
            lineHeight = 19.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Wholesome Comedy Safety Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ComicCardDark),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.5.dp, ComicGreen, RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "🤝 HARMLESS FRIENDSHIP COMEDY",
            color = ComicGreen,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "• Zero violence or injury — only slapstick laughs!\n• Safe, wholesome jokes: chicken ringtones, toy squeakers, washable marker mustaches, and water balloons!\n• At the end of every level, all 4 dost share snacks and laugh together.",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Controls Summary Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ComicCardDark),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.5.dp, Color(0xFF3B3550), RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "📱 MOBILE CONTROLS",
            color = ComicOrange,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "• Tap: Buttons, sneak steps, topping drops & alarms\n• Swipe: Feather tickles & dodging\n• Drag: Placing stickers & props\n• Audio & Haptics: Live low-latency cartoon sound effects and tactile vibrations!",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Footer
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Rehan Ko Tang Karo v1.0",
          color = Color.Gray,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Crafted with Kotlin & Jetpack Compose",
          color = Color.Gray,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

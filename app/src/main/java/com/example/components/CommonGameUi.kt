package com.example.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ComicCardDark
import com.example.ui.theme.ComicGreen
import com.example.ui.theme.ComicOrange
import com.example.ui.theme.ComicRed
import com.example.ui.theme.ComicYellow

@Composable
fun GameTopBar(
  levelTitle: String,
  score: Int,
  coins: Int,
  onPause: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
  onHelp: (() -> Unit)? = null
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(ComicCardDark)
          .border(2.dp, ComicYellow.copy(alpha = 0.6f), CircleShape)
          .clickable { onBack() }
          .testTag("btn_top_back"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = ComicYellow,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(
          text = levelTitle,
          color = ComicYellow,
          fontWeight = FontWeight.Black,
          fontSize = 15.sp
        )
        Text(
          text = "Score: $score",
          color = Color.White.copy(alpha = 0.9f),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      // Coins pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF2C2410))
          .border(1.5.dp, ComicYellow, RoundedCornerShape(16.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("🪙", fontSize = 13.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$coins",
            color = ComicYellow,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Optional How To Play Help button
      if (onHelp != null) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(ComicCardDark)
            .border(2.dp, ComicOrange, CircleShape)
            .clickable { onHelp() }
            .testTag("btn_top_help"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "❓",
            fontSize = 17.sp
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
      }

      // Pause button
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(ComicCardDark)
          .border(2.dp, ComicYellow, CircleShape)
          .clickable { onPause() }
          .testTag("btn_top_pause"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Pause,
          contentDescription = "Pause",
          tint = ComicYellow,
          modifier = Modifier.size(22.dp)
        )
      }
    }
  }
}

@Composable
fun SuspicionMeter(
  suspicionPercent: Float, // 0.0f to 1.0f
  isLooking: Boolean,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "suspicion_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = if (isLooking) 1.08f else 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(250, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  val barColor by animateColorAsState(
    targetValue = when {
      suspicionPercent > 0.75f -> ComicRed
      suspicionPercent > 0.45f -> ComicOrange
      else -> ComicGreen
    },
    label = "barColor"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .scale(pulseScale)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = if (isLooking) "👀 REHAN IS LOOKING! HIDE!" else "👁️ Rehan's Suspicion Meter",
          color = if (isLooking) ComicRed else ComicYellow,
          fontWeight = FontWeight.Black,
          fontSize = 13.sp
        )
      }
      Text(
        text = "${(suspicionPercent * 100).toInt()}%",
        color = barColor,
        fontWeight = FontWeight.Black,
        fontSize = 13.sp
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(14.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF1B192A))
        .border(1.5.dp, if (isLooking) ComicRed else Color(0xFF4A4468), RoundedCornerShape(8.dp))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(suspicionPercent.coerceIn(0f, 1f))
          .fillMaxHeight()
          .clip(RoundedCornerShape(8.dp))
          .background(
            Brush.horizontalGradient(
              listOf(
                barColor.copy(alpha = 0.8f),
                barColor
              )
            )
          )
      )
    }
  }
}

@Composable
fun PrankProgressBar(
  progressPercent: Float, // 0.0f to 1.0f
  label: String = "Prank Progress",
  iconEmoji: String = "🎯",
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(iconEmoji, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = label,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
      Text(
        text = "${(progressPercent * 100).toInt()}%",
        color = ComicYellow,
        fontWeight = FontWeight.Black,
        fontSize = 13.sp
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(14.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF1B192A))
        .border(1.5.dp, Color(0xFF4A4468), RoundedCornerShape(8.dp))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(progressPercent.coerceIn(0f, 1f))
          .fillMaxHeight()
          .clip(RoundedCornerShape(8.dp))
          .background(
            Brush.horizontalGradient(
              listOf(ComicOrange, ComicYellow, ComicGreen)
            )
          )
      )
    }
  }
}

@Composable
fun ControlsHelpBanner(
  text: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF28233C).copy(alpha = 0.9f))
      .border(1.dp, ComicYellow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text("💡", fontSize = 14.sp)
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = text,
        color = Color(0xFFFFECB3),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp
      )
    }
  }
}

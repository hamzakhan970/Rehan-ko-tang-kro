package com.example.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CharacterId
import com.example.model.CharacterMood
import com.example.ui.theme.ComicYellow
import com.example.ui.theme.HamzaRed
import com.example.ui.theme.HuzaifaGreen
import com.example.ui.theme.MuaviyaGold
import com.example.ui.theme.RehanBlue

@Composable
fun AnimatedCharacterAvatar(
  characterId: CharacterId,
  mood: CharacterMood = CharacterMood.IDLE,
  size: Dp = 100.dp,
  modifier: Modifier = Modifier,
  showBadge: Boolean = true
) {
  val infiniteTransition = rememberInfiniteTransition(label = "char_idle")
  val bounceY by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = if (mood == CharacterMood.LAUGHING || mood == CharacterMood.RUNNING) -8f else -3f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (mood == CharacterMood.LAUGHING) 220 else 600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bounce"
  )

  val wobbleAngle by infiniteTransition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (mood == CharacterMood.LAUGHING) 150 else 700, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wobble"
  )

  val breathingScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (mood == CharacterMood.SLEEPING) 1.05f else 1.02f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (mood == CharacterMood.SLEEPING) 1400 else 800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breath"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(size)
        .offset(y = bounceY.dp)
        .rotate(wobbleAngle)
        .scale(breathingScale)
    ) {
      // Glow/Outline Aura
      val auraColor = when (characterId) {
        CharacterId.REHAN -> RehanBlue.copy(alpha = 0.35f)
        CharacterId.MUAVIYA -> MuaviyaGold.copy(alpha = 0.35f)
        CharacterId.HAMZA -> HamzaRed.copy(alpha = 0.35f)
        CharacterId.HUZAIFA -> HuzaifaGreen.copy(alpha = 0.35f)
      }
      Box(
        modifier = Modifier
          .size(size)
          .clip(CircleShape)
          .background(auraColor)
      )

      // Character Canvas
      Canvas(
        modifier = Modifier
          .size(size * 0.92f)
          .clip(CircleShape)
          .border(
            width = 3.dp,
            brush = Brush.verticalGradient(
              listOf(Color.White.copy(alpha = 0.8f), auraColor)
            ),
            shape = CircleShape
          )
      ) {
        drawCharacterCanvas(characterId, mood, this)
      }

      // Floating Expression Indicator for Special Moods
      if (mood == CharacterMood.SLEEPING) {
        Text(
          text = "Zzz...",
          color = Color(0xFF64B5F6),
          fontWeight = FontWeight.Black,
          fontSize = 14.sp,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 6.dp, y = (-8).dp)
        )
      } else if (mood == CharacterMood.SHOCKED) {
        Text(
          text = "⁉️",
          fontSize = 18.sp,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 6.dp, y = (-12).dp)
        )
      } else if (mood == CharacterMood.SUSPICIOUS) {
        Text(
          text = "👀",
          fontSize = 18.sp,
          modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = (-4).dp, y = (-10).dp)
        )
      } else if (mood == CharacterMood.LAUGHING) {
        Text(
          text = "😂",
          fontSize = 18.sp,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 6.dp, y = (-8).dp)
        )
      }
    }

    if (showBadge) {
      Spacer(modifier = Modifier.height(4.dp))
      val (name, color) = when (characterId) {
        CharacterId.REHAN -> "Rehan" to RehanBlue
        CharacterId.MUAVIYA -> "Muaviya" to MuaviyaGold
        CharacterId.HAMZA -> "Hamza" to HamzaRed
        CharacterId.HUZAIFA -> "Huzaifa" to HuzaifaGreen
      }
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(color)
          .padding(horizontal = 8.dp, vertical = 2.dp)
      ) {
        Text(
          text = name,
          color = if (color == MuaviyaGold) Color.Black else Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }
    }
  }
}

private fun drawCharacterCanvas(
  characterId: CharacterId,
  mood: CharacterMood,
  scope: DrawScope
) {
  val w = scope.size.width
  val h = scope.size.height

  // Background circle for avatar
  val bgGrad = when (characterId) {
    CharacterId.REHAN -> Brush.radialGradient(listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9)))
    CharacterId.MUAVIYA -> Brush.radialGradient(listOf(Color(0xFFFFF8E1), Color(0xFFFFE082)))
    CharacterId.HAMZA -> Brush.radialGradient(listOf(Color(0xFFFFEBEE), Color(0xFFEF9A9A)))
    CharacterId.HUZAIFA -> Brush.radialGradient(listOf(Color(0xFFE8F5E9), Color(0xFFA5D6A7)))
  }
  scope.drawCircle(brush = bgGrad, radius = w / 2f, center = Offset(w / 2f, h / 2f))

  val skinColor = Color(0xFFFFDCB1)
  val shadowSkin = Color(0xFFF3C491)

  // Torso / Shoulders
  val shirtColor = when (characterId) {
    CharacterId.REHAN -> RehanBlue
    CharacterId.MUAVIYA -> MuaviyaGold
    CharacterId.HAMZA -> HamzaRed
    CharacterId.HUZAIFA -> HuzaifaGreen
  }

  val shirtPath = Path().apply {
    moveTo(w * 0.15f, h)
    lineTo(w * 0.28f, h * 0.72f)
    quadraticBezierTo(w * 0.5f, h * 0.78f, w * 0.72f, h * 0.72f)
    lineTo(w * 0.85f, h)
    close()
  }
  scope.drawPath(shirtPath, shirtColor)

  // Neck
  scope.drawRect(
    color = shadowSkin,
    topLeft = Offset(w * 0.43f, h * 0.62f),
    size = Size(w * 0.14f, h * 0.14f)
  )

  // Head Base
  val headCenter = Offset(w * 0.5f, h * 0.44f)
  val headRadius = w * 0.27f
  scope.drawCircle(color = skinColor, radius = headRadius, center = headCenter)

  // Ears
  scope.drawCircle(color = skinColor, radius = w * 0.065f, center = Offset(w * 0.22f, h * 0.45f))
  scope.drawCircle(color = skinColor, radius = w * 0.065f, center = Offset(w * 0.78f, h * 0.45f))

  when (characterId) {
    CharacterId.REHAN -> drawRehanFeatures(w, h, mood, scope, skinColor)
    CharacterId.MUAVIYA -> drawMuaviyaFeatures(w, h, mood, scope, skinColor)
    CharacterId.HAMZA -> drawHamzaFeatures(w, h, mood, scope, skinColor)
    CharacterId.HUZAIFA -> drawHuzaifaFeatures(w, h, mood, scope, skinColor)
  }

  // Splat overlay if in SPLATTED state
  if (mood == CharacterMood.SPLATTED) {
    scope.drawCircle(Color(0xFF00E5FF).copy(alpha = 0.85f), radius = w * 0.12f, center = Offset(w * 0.35f, h * 0.38f))
    scope.drawCircle(Color(0xFFFF4081).copy(alpha = 0.85f), radius = w * 0.1f, center = Offset(w * 0.65f, h * 0.48f))
    scope.drawCircle(Color(0xFFFFEA00).copy(alpha = 0.85f), radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.55f))
  }
}

// ----------------- REHAN: Round glasses, wavy dark hair, question mark shirt -----------------
private fun drawRehanFeatures(
  w: Float,
  h: Float,
  mood: CharacterMood,
  scope: DrawScope,
  skinColor: Color
) {
  // Curly wavy dark hair
  val hairColor = Color(0xFF2C1A14)
  val hairPath = Path().apply {
    moveTo(w * 0.22f, h * 0.4f)
    cubicTo(w * 0.18f, h * 0.18f, w * 0.4f, h * 0.12f, w * 0.5f, h * 0.16f)
    cubicTo(w * 0.6f, h * 0.12f, w * 0.82f, h * 0.18f, w * 0.78f, h * 0.4f)
    cubicTo(w * 0.74f, h * 0.28f, w * 0.6f, h * 0.25f, w * 0.5f, h * 0.28f)
    cubicTo(w * 0.4f, h * 0.25f, w * 0.26f, h * 0.28f, w * 0.22f, h * 0.4f)
    close()
  }
  scope.drawPath(hairPath, hairColor)
  // Hair curls
  scope.drawCircle(hairColor, radius = w * 0.09f, center = Offset(w * 0.35f, h * 0.22f))
  scope.drawCircle(hairColor, radius = w * 0.10f, center = Offset(w * 0.5f, h * 0.18f))
  scope.drawCircle(hairColor, radius = w * 0.09f, center = Offset(w * 0.65f, h * 0.22f))

  val eyeY = h * 0.42f
  val leftEyeX = w * 0.38f
  val rightEyeX = w * 0.62f

  // Glasses frames
  val glassesColor = Color(0xFF1A237E)
  val glassRadius = w * 0.11f
  scope.drawCircle(Color.White.copy(alpha = 0.8f), radius = glassRadius, center = Offset(leftEyeX, eyeY))
  scope.drawCircle(Color.White.copy(alpha = 0.8f), radius = glassRadius, center = Offset(rightEyeX, eyeY))
  scope.drawCircle(glassesColor, radius = glassRadius, center = Offset(leftEyeX, eyeY), style = Stroke(width = w * 0.024f))
  scope.drawCircle(glassesColor, radius = glassRadius, center = Offset(rightEyeX, eyeY), style = Stroke(width = w * 0.024f))
  // Bridge
  scope.drawLine(
    color = glassesColor,
    start = Offset(leftEyeX + glassRadius, eyeY),
    end = Offset(rightEyeX - glassRadius, eyeY),
    strokeWidth = w * 0.024f,
    cap = StrokeCap.Round
  )

  // Eyes based on mood
  when (mood) {
    CharacterMood.SLEEPING -> {
      // Curved closed eyes
      scope.drawArc(
        color = Color(0xFF333333),
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(leftEyeX - w * 0.05f, eyeY - w * 0.02f),
        size = Size(w * 0.1f, w * 0.05f),
        style = Stroke(width = w * 0.02f, cap = StrokeCap.Round)
      )
      scope.drawArc(
        color = Color(0xFF333333),
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(rightEyeX - w * 0.05f, eyeY - w * 0.02f),
        size = Size(w * 0.1f, w * 0.05f),
        style = Stroke(width = w * 0.02f, cap = StrokeCap.Round)
      )
      // Slight open sleeping mouth
      scope.drawCircle(Color(0xFFBF360C), radius = w * 0.035f, center = Offset(w * 0.5f, h * 0.56f))
    }
    CharacterMood.SHOCKED -> {
      // Tiny shocked pupils inside wide white eyes
      scope.drawCircle(Color.Black, radius = w * 0.035f, center = Offset(leftEyeX, eyeY))
      scope.drawCircle(Color.Black, radius = w * 0.035f, center = Offset(rightEyeX, eyeY))
      // Gaping comic mouth
      scope.drawOval(
        color = Color(0xFF880E4F),
        topLeft = Offset(w * 0.42f, h * 0.52f),
        size = Size(w * 0.16f, h * 0.11f)
      )
    }
    CharacterMood.SUSPICIOUS -> {
      // Squinting side eyes
      scope.drawOval(
        color = Color.Black,
        topLeft = Offset(leftEyeX - w * 0.05f, eyeY - w * 0.015f),
        size = Size(w * 0.08f, w * 0.035f)
      )
      scope.drawOval(
        color = Color.Black,
        topLeft = Offset(rightEyeX - w * 0.05f, eyeY - w * 0.015f),
        size = Size(w * 0.08f, w * 0.035f)
      )
      // One eyebrow cocked high
      scope.drawLine(
        color = Color(0xFF2C1A14),
        start = Offset(leftEyeX - w * 0.06f, eyeY - w * 0.12f),
        end = Offset(leftEyeX + w * 0.06f, eyeY - w * 0.15f),
        strokeWidth = w * 0.022f,
        cap = StrokeCap.Round
      )
      scope.drawLine(
        color = Color(0xFF2C1A14),
        start = Offset(rightEyeX - w * 0.06f, eyeY - w * 0.16f),
        end = Offset(rightEyeX + w * 0.06f, eyeY - w * 0.11f),
        strokeWidth = w * 0.022f,
        cap = StrokeCap.Round
      )
      // Wavy suspicious mouth
      scope.drawLine(
        color = Color(0xFF424242),
        start = Offset(w * 0.43f, h * 0.57f),
        end = Offset(w * 0.57f, h * 0.55f),
        strokeWidth = w * 0.02f,
        cap = StrokeCap.Round
      )
    }
    else -> {
      // Normal friendly Rehan
      scope.drawCircle(Color.Black, radius = w * 0.045f, center = Offset(leftEyeX, eyeY))
      scope.drawCircle(Color.Black, radius = w * 0.045f, center = Offset(rightEyeX, eyeY))
      scope.drawCircle(Color.White, radius = w * 0.015f, center = Offset(leftEyeX - w * 0.015f, eyeY - w * 0.015f))
      scope.drawCircle(Color.White, radius = w * 0.015f, center = Offset(rightEyeX - w * 0.015f, eyeY - w * 0.015f))

      // Smile
      scope.drawArc(
        color = Color(0xFFD32F2F),
        startAngle = 10f,
        sweepAngle = 160f,
        useCenter = false,
        topLeft = Offset(w * 0.44f, h * 0.52f),
        size = Size(w * 0.12f, h * 0.06f),
        style = Stroke(width = w * 0.02f, cap = StrokeCap.Round)
      )
    }
  }

  // Shirt logo: Question mark
  scope.drawCircle(ComicYellow, radius = w * 0.016f, center = Offset(w * 0.5f, h * 0.88f))
}

// ----------------- MUAVIYA: Sleek side hair, smart winking grin, yellow hoodie -----------------
private fun drawMuaviyaFeatures(
  w: Float,
  h: Float,
  mood: CharacterMood,
  scope: DrawScope,
  skinColor: Color
) {
  // Stylish side-swept hair
  val hairColor = Color(0xFF1E140F)
  val hairPath = Path().apply {
    moveTo(w * 0.22f, h * 0.38f)
    cubicTo(w * 0.2f, h * 0.15f, w * 0.55f, h * 0.1f, w * 0.78f, h * 0.22f)
    cubicTo(w * 0.82f, h * 0.32f, w * 0.75f, h * 0.38f, w * 0.72f, h * 0.35f)
    cubicTo(w * 0.6f, h * 0.25f, w * 0.4f, h * 0.24f, w * 0.26f, h * 0.38f)
    close()
  }
  scope.drawPath(hairPath, hairColor)

  val eyeY = h * 0.42f
  val leftEyeX = w * 0.38f
  val rightEyeX = w * 0.62f

  // Smart winking / thinking eyes
  // Left eye: open, smart glint
  scope.drawCircle(Color.Black, radius = w * 0.045f, center = Offset(leftEyeX, eyeY))
  scope.drawCircle(Color.White, radius = w * 0.016f, center = Offset(leftEyeX - w * 0.012f, eyeY - w * 0.012f))

  // Right eye: sly wink
  scope.drawArc(
    color = Color.Black,
    startAngle = 190f,
    sweepAngle = 160f,
    useCenter = false,
    topLeft = Offset(rightEyeX - w * 0.05f, eyeY - w * 0.01f),
    size = Size(w * 0.1f, w * 0.04f),
    style = Stroke(width = w * 0.022f, cap = StrokeCap.Round)
  )

  // Confident tilted eyebrows
  scope.drawLine(
    color = hairColor,
    start = Offset(leftEyeX - w * 0.06f, eyeY - w * 0.09f),
    end = Offset(leftEyeX + w * 0.06f, eyeY - w * 0.07f),
    strokeWidth = w * 0.022f,
    cap = StrokeCap.Round
  )
  scope.drawLine(
    color = hairColor,
    start = Offset(rightEyeX - w * 0.06f, eyeY - w * 0.07f),
    end = Offset(rightEyeX + w * 0.06f, eyeY - w * 0.09f),
    strokeWidth = w * 0.022f,
    cap = StrokeCap.Round
  )

  // Sly sideways smirk
  val smirkPath = Path().apply {
    moveTo(w * 0.44f, h * 0.55f)
    quadraticBezierTo(w * 0.52f, h * 0.58f, w * 0.62f, h * 0.52f)
  }
  scope.drawPath(smirkPath, color = Color(0xFFC2185B), style = Stroke(width = w * 0.022f, cap = StrokeCap.Round))
}

// ----------------- HAMZA: Backwards red cap, laughing mouth, freckles, red hoodie -----------------
private fun drawHamzaFeatures(
  w: Float,
  h: Float,
  mood: CharacterMood,
  scope: DrawScope,
  skinColor: Color
) {
  // Messy tufts sticking out
  val hairColor = Color(0xFF3E2723)
  scope.drawCircle(hairColor, radius = w * 0.07f, center = Offset(w * 0.28f, h * 0.32f))
  scope.drawCircle(hairColor, radius = w * 0.07f, center = Offset(w * 0.72f, h * 0.32f))

  // Backwards Red Cap
  val capColor = HamzaRed
  val capPath = Path().apply {
    moveTo(w * 0.22f, h * 0.32f)
    cubicTo(w * 0.22f, h * 0.12f, w * 0.78f, h * 0.12f, w * 0.78f, h * 0.32f)
    close()
  }
  scope.drawPath(capPath, capColor)
  // Cap brim turned backwards (top visor lip)
  scope.drawRoundRect(
    color = Color(0xFFB71C1C),
    topLeft = Offset(w * 0.35f, h * 0.1f),
    size = Size(w * 0.3f, h * 0.05f),
    cornerRadius = CornerRadius(8f, 8f)
  )

  val eyeY = h * 0.42f
  val leftEyeX = w * 0.37f
  val rightEyeX = w * 0.63f

  // Expressive mischievous eyes
  if (mood == CharacterMood.LAUGHING) {
    // Happy squint eyes (^_^)
    scope.drawArc(
      color = Color.Black,
      startAngle = 190f,
      sweepAngle = 160f,
      useCenter = false,
      topLeft = Offset(leftEyeX - w * 0.05f, eyeY - w * 0.02f),
      size = Size(w * 0.1f, w * 0.05f),
      style = Stroke(width = w * 0.024f, cap = StrokeCap.Round)
    )
    scope.drawArc(
      color = Color.Black,
      startAngle = 190f,
      sweepAngle = 160f,
      useCenter = false,
      topLeft = Offset(rightEyeX - w * 0.05f, eyeY - w * 0.02f),
      size = Size(w * 0.1f, w * 0.05f),
      style = Stroke(width = w * 0.024f, cap = StrokeCap.Round)
    )
  } else {
    scope.drawCircle(Color.Black, radius = w * 0.045f, center = Offset(leftEyeX, eyeY))
    scope.drawCircle(Color.Black, radius = w * 0.045f, center = Offset(rightEyeX, eyeY))
    scope.drawCircle(Color.White, radius = w * 0.015f, center = Offset(leftEyeX - w * 0.012f, eyeY - w * 0.012f))
    scope.drawCircle(Color.White, radius = w * 0.015f, center = Offset(rightEyeX - w * 0.012f, eyeY - w * 0.012f))
  }

  // Freckles
  val freckleColor = Color(0xFFBA7A42)
  scope.drawCircle(freckleColor, radius = w * 0.012f, center = Offset(w * 0.32f, h * 0.48f))
  scope.drawCircle(freckleColor, radius = w * 0.012f, center = Offset(w * 0.36f, h * 0.50f))
  scope.drawCircle(freckleColor, radius = w * 0.012f, center = Offset(w * 0.64f, h * 0.50f))
  scope.drawCircle(freckleColor, radius = w * 0.012f, center = Offset(w * 0.68f, h * 0.48f))

  // Huge wide laughing grin
  val mouthPath = Path().apply {
    moveTo(w * 0.36f, h * 0.54f)
    quadraticBezierTo(w * 0.5f, h * 0.68f, w * 0.64f, h * 0.54f)
    close()
  }
  scope.drawPath(mouthPath, Color(0xFFC2185B))
  // Comic tooth
  scope.drawRect(
    color = Color.White,
    topLeft = Offset(w * 0.46f, h * 0.54f),
    size = Size(w * 0.08f, h * 0.035f)
  )
}

// ----------------- HUZAIFA: Green athletic jacket, curly puffs / beanie, bright grin -----------------
private fun drawHuzaifaFeatures(
  w: Float,
  h: Float,
  mood: CharacterMood,
  scope: DrawScope,
  skinColor: Color
) {
  // Curly afro puff hairstyle
  val hairColor = Color(0xFF1B120C)
  scope.drawCircle(hairColor, radius = w * 0.12f, center = Offset(w * 0.3f, h * 0.24f))
  scope.drawCircle(hairColor, radius = w * 0.13f, center = Offset(w * 0.5f, h * 0.18f))
  scope.drawCircle(hairColor, radius = w * 0.12f, center = Offset(w * 0.7f, h * 0.24f))
  scope.drawCircle(hairColor, radius = w * 0.10f, center = Offset(w * 0.2f, h * 0.35f))
  scope.drawCircle(hairColor, radius = w * 0.10f, center = Offset(w * 0.8f, h * 0.35f))

  val eyeY = h * 0.43f
  val leftEyeX = w * 0.38f
  val rightEyeX = w * 0.62f

  // Friendly alert eyes
  scope.drawCircle(Color.Black, radius = w * 0.045f, center = Offset(leftEyeX, eyeY))
  scope.drawCircle(Color.Black, radius = w * 0.045f, center = Offset(rightEyeX, eyeY))
  scope.drawCircle(Color.White, radius = w * 0.016f, center = Offset(leftEyeX - w * 0.012f, eyeY - w * 0.012f))
  scope.drawCircle(Color.White, radius = w * 0.016f, center = Offset(rightEyeX - w * 0.012f, eyeY - w * 0.012f))

  // Friendly curved eyebrows
  scope.drawArc(
    color = hairColor,
    startAngle = 200f,
    sweepAngle = 140f,
    useCenter = false,
    topLeft = Offset(leftEyeX - w * 0.06f, eyeY - w * 0.11f),
    size = Size(w * 0.12f, w * 0.05f),
    style = Stroke(width = w * 0.02f, cap = StrokeCap.Round)
  )
  scope.drawArc(
    color = hairColor,
    startAngle = 200f,
    sweepAngle = 140f,
    useCenter = false,
    topLeft = Offset(rightEyeX - w * 0.06f, eyeY - w * 0.11f),
    size = Size(w * 0.12f, w * 0.05f),
    style = Stroke(width = w * 0.02f, cap = StrokeCap.Round)
  )

  // Warm big open smile
  val smilePath = Path().apply {
    moveTo(w * 0.38f, h * 0.55f)
    quadraticBezierTo(w * 0.5f, h * 0.65f, w * 0.62f, h * 0.55f)
    close()
  }
  scope.drawPath(smilePath, Color(0xFFD32F2F))
  // White teeth arc
  scope.drawArc(
    color = Color.White,
    startAngle = 0f,
    sweepAngle = 180f,
    useCenter = true,
    topLeft = Offset(w * 0.42f, h * 0.55f),
    size = Size(w * 0.16f, h * 0.035f)
  )
}

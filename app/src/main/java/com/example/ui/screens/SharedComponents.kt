package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Reusable Sports Colors
object SportColors {
    val DeepBlueHeader = Color(0xFF0A1E3C)
    val DeepBlueHeaderEnd = Color(0xFF1A3A6D)
    val GlowBlueAccent = Color(0xFF2563EB)
    val ActiveBlue = Color(0xFF1D4ED8)
    val SportGreen = Color(0xFF10B981)
    val SportGreenDark = Color(0xFF047857)
    val BrightOrange = Color(0xFFF97316)
    val GoldYellow = Color(0xFFF59E0B)
    val DarkBackground = Color(0xFFF7F9FC)
    val SoftCardBg = Color(0xFFFFFFFF)
    val PureWhite = Color(0xFFFFFFFF)

    // Bold Typography Theme Extensions
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF475569)
    val TextInverse = Color(0xFFFFFFFF)
    val CardBorder = Color(0xFFE2E8F0)
}

@Composable
fun SportsHeaderBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SportColors.DeepBlueHeader,
                        SportColors.DeepBlueHeaderEnd,
                        SportColors.DarkBackground
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Draw a decorative background stadium lighting glow
            drawCircle(
                color = SportColors.GlowBlueAccent.copy(alpha = 0.12f),
                radius = size.width / 2,
                center = Offset(size.width, 0f)
            )
            drawCircle(
                color = SportColors.SportGreen.copy(alpha = 0.08f),
                radius = size.width / 3,
                center = Offset(0f, size.height * 0.7f)
            )
        }
        Box(modifier = Modifier.statusBarsPadding()) {
            content()
        }
    }
}

@Composable
fun CustomCricketBatGraphic(
    modifier: Modifier = Modifier,
    woodColor: Color = Color(0xFFE2B07E),
    gripColor: Color = Color(0xFFE11D48)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Handle
        val handleWidth = w * 0.18f
        val handleHeight = h * 0.45f
        val handleLeft = (w - handleWidth) / 2
        
        drawRoundRect(
            color = gripColor,
            topLeft = Offset(handleLeft, 0f),
            size = Size(handleWidth, handleHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )
        // Draw rubber rings on handle grip
        for (i in 1..4) {
            val yOffset = handleHeight * (i / 5f)
            drawLine(
                color = Color.Black.copy(alpha = 0.3f),
                start = Offset(handleLeft, yOffset),
                end = Offset(handleLeft + handleWidth, yOffset),
                strokeWidth = 3f
            )
        }

        // Blade transition (Shoulder)
        val shoulderHeight = h * 0.08f
        val bladeWidth = w * 0.75f
        val bladeLeft = (w - bladeWidth) / 2
        val bladeHeight = h * 0.47f

        val shoulderPath = Path().apply {
            moveTo(handleLeft, handleHeight)
            lineTo(handleLeft + handleWidth, handleHeight)
            lineTo(bladeLeft + bladeWidth, handleHeight + shoulderHeight)
            lineTo(bladeLeft, handleHeight + shoulderHeight)
            close()
        }
        drawPath(path = shoulderPath, color = woodColor)

        // Main Blade
        drawRoundRect(
            color = woodColor,
            topLeft = Offset(bladeLeft, handleHeight + shoulderHeight),
            size = Size(bladeWidth, bladeHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
        )

        // Draw wood grain lines
        for (i in 1..3) {
            val grainX = bladeLeft + (bladeWidth * (i / 4f))
            drawLine(
                color = Color(0xFFB07F50).copy(alpha = 0.5f),
                start = Offset(grainX, handleHeight + shoulderHeight + 10f),
                end = Offset(grainX, h - 15f),
                strokeWidth = 2f
            )
        }

        // Brand red sticker
        drawRect(
            color = Color(0xFF1E3A8A),
            topLeft = Offset(bladeLeft + 5f, handleHeight + shoulderHeight + 12f),
            size = Size(bladeWidth - 10f, bladeHeight * 0.25f)
        )
        // Mini gold logo on sticker
        drawCircle(
            color = SportColors.GoldYellow,
            radius = handleWidth * 0.4f,
            center = Offset(w / 2, handleHeight + shoulderHeight + 12f + (bladeHeight * 0.25f / 2))
        )
    }
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF10B981))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(color = Color.White)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Verified",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CoachAvatarIllustration(
    modifier: Modifier = Modifier,
    coachName: String,
    primaryColor: Color = SportColors.ActiveBlue
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(primaryColor, primaryColor.copy(alpha = 0.6f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Draw a subtle halo
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = radius * 0.85f,
                center = center
            )
        }

        // First letter initials
        val initials = if (coachName.contains(" ")) {
            val parts = coachName.split(" ")
            "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}"
        } else {
            coachName.take(2).uppercase()
        }

        Text(
            text = initials,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        
        // Sweat band strip decor
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(18.dp)
                .background(SportColors.BrightOrange.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Text("PRO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StarRatingBar(rating: Float, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = rating.toString(),
            color = SportColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating Star",
            tint = SportColors.GoldYellow,
            modifier = Modifier.size(13.dp)
        )
    }
}

package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

// Premium dark tech gradient background
val CosmosBackgroundBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF080512), // Deep cosmic black
        Color(0xFF140D2F), // Dark midnight violet
        Color(0xFF0C071A)  // Dark deep violet
    )
)

val GoldenVipBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFFFD700), // Pure Gold
        Color(0xFFFFA500), // Amber Gold
        Color(0xFFFFD700)
    )
)

val PremiumGradientBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF8E2DE2), // Royal Purple
        Color(0xFF4A00E0)  // Deep Violet
    )
)

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color(0x19FFFFFF),
    backgroundColor: Color = Color(0x0CFFFFFF),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius)),
        content = content
    )
}

@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    width: Dp = 120.dp,
    cornerRadius: Dp = 8.dp
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alphaAnim by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(cornerRadius))
            .alpha(alphaAnim)
            .background(Color.White.copy(alpha = 0.15f))
    )
}

// Gorgeous floating gift animation overlay effect
@Composable
fun FloatingHeartEffect(
    trigger: String?,
    modifier: Modifier = Modifier
) {
    if (trigger == null) return

    val hearts = remember(trigger) {
        List(15) {
            HeartState(
                startX = Random.nextFloat(),
                startY = 1f,
                targetX = Random.nextFloat() * 2f - 1f,
                targetY = Random.nextFloat() * -0.8f,
                scale = Random.nextFloat() * 0.8f + 0.6f,
                color = when (Random.nextInt(4)) {
                    0 -> Color(0xFFFF4D4D)
                    1 -> Color(0xFFFFB300)
                    2 -> Color(0xFFE040FB)
                    else -> Color(0xFF00E676)
                }
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        hearts.forEach { heart ->
            var alphaVal by remember { mutableStateOf(1f) }
            val transition = remember { Animatable(0f) }

            LaunchedEffect(heart) {
                transition.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
                )
                alphaVal = 0f
            }

            val t = transition.value
            val currentX = heart.startX + (heart.targetX * t)
            val currentY = heart.startY + (heart.targetY * t)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (t > 0.8f) (1f - t) * 5f else 1f)
                    .scale(heart.scale * (1f + t * 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = heart.color,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(
                            x = (currentX * 150).dp,
                            y = (currentY * 300).dp
                        )
                )
            }
        }
    }
}

private data class HeartState(
    val startX: Float,
    val startY: Float,
    val targetX: Float,
    val targetY: Float,
    val scale: Float,
    val color: Color
)

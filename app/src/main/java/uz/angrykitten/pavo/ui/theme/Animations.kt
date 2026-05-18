package uz.angrykitten.pavo.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Standard animation durations for consistent motion across the app
 */
object AnimationDurations {
    const val FAST = 150
    const val NORMAL = 300
    const val SLOW = 500
    const val VERY_SLOW = 800
}

/**
 * Standard animation specs for consistent easing across the app
 */
object AnimationSpecs {
    fun fastSpec() = tween<Float>(AnimationDurations.FAST, easing = EaseInOutCubic)
    fun normalSpec() = tween<Float>(AnimationDurations.NORMAL, easing = EaseInOutCubic)
    fun slowSpec() = tween<Float>(AnimationDurations.SLOW, easing = EaseInOutCubic)
    fun verySlowSpec() = tween<Float>(AnimationDurations.VERY_SLOW, easing = EaseInOutCubic)

    fun fastSpringSpec() = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )
    fun normalSpringSpec() = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

/**
 * Shimmer loading animation for skeleton screens
 */
@Composable
fun ShimmerAnimation(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true
) {
    if (!isLoading) return

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset(translateAnim - 1000, 0f),
        end = androidx.compose.ui.geometry.Offset(translateAnim, 0f)
    )

    Box(
        modifier = modifier.background(brush)
    )
}

/**
 * Pulsing animation for interactive elements
 */
@Composable
fun PulsingAnimation(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    color: Color = Brand
) {
    if (!isActive) return

    val transition = rememberInfiniteTransition(label = "pulsing")
    val scale by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .size(48.dp)
            .background(
                color = color.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}

/**
 * Floating animation for elements
 */
@Composable
fun FloatingAnimation(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    durationMillis: Int = 2000
) {
    if (!isActive) return

    val transition = rememberInfiniteTransition(label = "floating")
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )

    Box(
        modifier = modifier.graphicsLayer(
            translationY = offsetY
        )
    )
}

/**
 * Rotation animation for loading indicators
 */
@Composable
fun RotationAnimation(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    durationMillis: Int = 1000
) {
    if (!isActive) return

    val transition = rememberInfiniteTransition(label = "rotation")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    Box(
        modifier = modifier.graphicsLayer(
            rotationZ = rotation
        )
    )
}

/**
 * Bounce animation for interactive feedback
 */
fun <T> bounceSpec(): SpringSpec<T> = spring(
    dampingRatio = Spring.DampingRatioHighBouncy,
    stiffness = Spring.StiffnessHigh
)

/**
 * Smooth animation for state changes
 */
fun <T> smoothSpec(): TweenSpec<T> = tween(
    durationMillis = AnimationDurations.NORMAL,
    easing = EaseInOutCubic
)

/**
 * Quick animation for micro-interactions
 */
fun <T> quickSpec(): TweenSpec<T> = tween(
    durationMillis = AnimationDurations.FAST,
    easing = EaseInOutCubic
)

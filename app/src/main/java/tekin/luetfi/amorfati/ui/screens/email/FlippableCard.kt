package tekin.luetfi.amorfati.ui.screens.email
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.utils.DEFAULT_BACK_IMAGE

@Composable
fun FlippableCard(
    card: TarotCard,
    size: Dp = 200.dp,
    animationDuration: Int = 400
) {
    // How many half-turns we've done
    var rotationCount by remember { mutableStateOf(0) }

    // Target rotation = 180° × count => 0°, 180°, 360°, 540°, ...
    val targetRotation = rotationCount * 180f

    // Animate toward that target
    val animatedRotation: Float by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = animationDuration)
    )

    // a painter for placeholder / error
    val placeholderPainter = rememberAsyncImagePainter(DEFAULT_BACK_IMAGE)

    // Determine whether we're showing the front or back:
    // front when near 0° or 360°, back when near 180°
    val normalized = (animatedRotation % 360f + 360f) % 360f
    val showBack = normalized > 90f && normalized < 270f

    // Larger camera distance so the 3D effect is visible
    val cameraDist = 8f * LocalDensity.current.density

    // No ripple
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        Modifier
            .size(size)
            .graphicsLayer {
                rotationY = animatedRotation
                cameraDistance = cameraDist
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                // always spin forward
                rotationCount++
            }
    ) {
        if (!showBack) {
            // Front face
            AsyncImage(
                model = card.imageUrl,
                contentDescription = "${card.name} front",
                modifier = Modifier.matchParentSize()
            )
        } else {
            // Back face (add an extra 180° so it's not mirrored)
            AsyncImage(
                model = card.backsideImageUrl,
                placeholder = placeholderPainter,
                error = placeholderPainter,
                contentDescription = "${card.name} back",
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { rotationY = 180f }
            )
        }
    }
}

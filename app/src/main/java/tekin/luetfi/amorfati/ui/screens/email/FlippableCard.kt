package tekin.luetfi.amorfati.ui.screens.email

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import tekin.luetfi.amorfati.domain.model.TarotCard

@Composable
fun FlippableCard(
    card: TarotCard,
    size: Dp = 200.dp,
    animationDuration: Int = 400
) {
    val interactionSource = remember { MutableInteractionSource() }

    var flipped by remember { mutableStateOf(false) }

    // Animate between 0f and 180f
    val rotationY: Float by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = TweenSpec(durationMillis = animationDuration)
    )

    // Increase camera distance so the 3D effect is visible
    val cameraDistance = 8f * LocalDensity.current.density

    Box(
        Modifier
            .size(size)
            .graphicsLayer {
                this.rotationY = rotationY
                this.cameraDistance = cameraDistance
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { flipped = !flipped }
    ) {
        if (rotationY <= 90f) {
            // FRONT
            AsyncImage(
                model = card.imageUrl,
                contentDescription = "${card.name} front",
                modifier = Modifier.matchParentSize()
            )
        } else {
            // BACK: rotate a further 180° so it reads correctly
            AsyncImage(
                model = card.backsideImageUrl,
                contentDescription = "${card.name} back",
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer(rotationY = 180f)
            )
        }
    }
}

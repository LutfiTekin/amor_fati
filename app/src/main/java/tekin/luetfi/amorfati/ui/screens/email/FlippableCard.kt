package tekin.luetfi.amorfati.ui.screens.email

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.utils.DEFAULT_BACK_IMAGE
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlippableCard(
    card: TarotCard,
    size: Dp = 200.dp,
    singleFlipDuration: Int = 400,
    spinDurationMillis: Int = 4000
) {
    var rotationCount by remember { mutableStateOf(0) }
    var isSpinning by remember { mutableStateOf(false) }

    // 1) single‐tap flips one half turn
    val targetFlip = rotationCount * 180f
    val animatedFlip by animateFloatAsState(
        targetValue = targetFlip,
        animationSpec = tween(singleFlipDuration)
    )

    // 2) continuous spin when long-pressed
    val infinite = rememberInfiniteTransition()
    val animatedSpin by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(spinDurationMillis, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    // choose which animation to apply
    val cardRotationY = if (isSpinning) animatedSpin else animatedFlip

    // decide front/back
    val normalized = (cardRotationY % 360f + 360f) % 360f
    val showBack = normalized in 90f..270f

    // placeholder painter
    val placeholder = rememberAsyncImagePainter(DEFAULT_BACK_IMAGE)

    // a bit of camera distance
    val cameraDist = 8f * LocalDensity.current.density

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                this.rotationY = cardRotationY
                cameraDistance = cameraDist
            }
            // combinedClickable gives us onClick + onLongClick
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (!isSpinning) {
                        rotationCount++
                    }
                },
                onLongClick = {
                    isSpinning = true
                }
            )
            // watch for the finger lift so we can stop spin & reset
            .pointerInput(isSpinning) {
                if (isSpinning) {
                    // run a single gesture scope
                    coroutineScope {
                        // wait for first down, then wait until it's up
                        awaitPointerEventScope {
                            val down = awaitFirstDown()
                            do {
                                val event = awaitPointerEvent()
                            } while (event.changes.any { it.pressed })
                            // finger lifted
                            isSpinning = false
                            rotationCount = 0
                        }
                    }
                }
            }
    ) {
        if (!showBack) {
            AsyncImage(
                model = card.imageUrl,
                contentDescription = "${card.name} front",
                modifier = Modifier.matchParentSize()
            )
        } else {
            AsyncImage(
                model = card.backsideImageUrl,
                placeholder = placeholder,
                error = placeholder,
                contentDescription = "${card.name} back",
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { rotationY = 180f }
            )
        }
    }
}

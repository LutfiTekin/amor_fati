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
    modifier: Modifier = Modifier,
    card: TarotCard,
    size: Dp = 200.dp,
    flippable: Boolean = true,
    startFlipped: Boolean = false,
    singleFlipDuration: Int = 400,
    spinDurationMillis: Int = 4000,
    onTapped: (TarotCard?) -> Unit = {}
) {
    // Initialize rotationCount to 1 if we want to start flipped AND flipping is enabled
    var rotationCount by remember { mutableStateOf(if (flippable && startFlipped) 1 else 0) }
    var isSpinning    by remember { mutableStateOf(false) }

    // 1) single‐tap flip animation
    val animatedFlip by animateFloatAsState(
        targetValue   = if (flippable) rotationCount * 180f else 0f,
        animationSpec = tween(singleFlipDuration)
    )

    // 2) continuous spin when long‐pressed (only if flippable)
    val infinite    = rememberInfiniteTransition()
    val animatedSpin by infinite.animateFloat(
        initialValue   = 0f,
        targetValue    = 360f,
        animationSpec  = infiniteRepeatable(
            tween(spinDurationMillis, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    // choose which rotation to apply
    val cardRotationY = if (isSpinning && flippable) animatedSpin else animatedFlip

    // determine if back is showing
    val normalized = (cardRotationY % 360f + 360f) % 360f
    val showBack   = flippable && normalized in 90f..270f

    val placeholderBack = rememberAsyncImagePainter(DEFAULT_BACK_IMAGE)
    val placeholderFront = rememberAsyncImagePainter(card.thumbnail)
    val cameraDist  = 8f * LocalDensity.current.density

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationY      = cardRotationY
                cameraDistance = cameraDist
            }
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick = {
                    if (flippable){
                        // only invoke callback when front is showing
                        onTapped(if (showBack) card else null)
                    }else {
                        onTapped(card)
                    }

                    // only flip if allowed
                    if (flippable && !isSpinning) {
                        rotationCount++
                    }
                },
                onLongClick = {
                    if (flippable) isSpinning = true
                }
            )
            .pointerInput(isSpinning, flippable) {
                // only handle spin release if flippable
                if (isSpinning && flippable) {
                    coroutineScope {
                        awaitPointerEventScope {
                            awaitFirstDown()
                            do {
                                val event = awaitPointerEvent()
                            } while (event.changes.any { it.pressed })
                            // on release: stop spinning & reset to front
                            isSpinning    = false
                            rotationCount = 0
                        }
                    }
                }
            }
    ) {
        if (!showBack) {
            AsyncImage(
                model              = card.imageUrl,
                placeholder        = placeholderFront,
                contentDescription = "${card.name} front",
                modifier           = Modifier.matchParentSize()
            )
        } else {
            AsyncImage(
                model              = card.backsideImageUrl,
                placeholder        = placeholderBack,
                error              = placeholderBack,
                contentDescription = "${card.name} back",
                modifier           = Modifier
                    .matchParentSize()
                    .graphicsLayer { rotationY = 180f }
            )
        }
    }
}

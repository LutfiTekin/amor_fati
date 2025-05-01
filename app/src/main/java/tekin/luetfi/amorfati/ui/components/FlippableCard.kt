package tekin.luetfi.amorfati.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.utils.Defaults


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlippableCard(
    modifier: Modifier = Modifier,
    card: TarotCard,
    size: Dp = 200.dp,
    isPicked: Boolean = false,
    flippable: Boolean = true,
    startFlipped: Boolean = false,
    singleFlipDuration: Int = 400,
    spinDurationMillis: Int = 4000,
    onTapped: (TarotCard?) -> Unit = {},
    onFlip: (card: TarotCard, isFront: Boolean) -> Unit = { _, _ -> }
) {
    // rotationCount = how many half-turns we've done
    var rotationCount by remember { mutableIntStateOf(if (flippable && startFlipped) 1 else 0) }
    var isSpinning by remember { mutableStateOf(false) }

    // only collect removals after the initial state
    LaunchedEffect(Unit) {
        snapshotFlow { isPicked }
            .drop(1)               // drop the startup value
            .filter { picked -> !picked }  // only care when it goes false
            .collect {
                rotationCount += 1   // bump for a forward flip
            }
    }

    // 1) single‐tap flip animation
    val animatedFlip by animateFloatAsState(
        targetValue = if (flippable) rotationCount * 180f else 0f,
        animationSpec = tween(singleFlipDuration)
    )

    // 2) continuous spin
    val infinite = rememberInfiniteTransition()
    val animatedSpin by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(spinDurationMillis, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    // choose rotation
    val cardRotationY = if (isSpinning && flippable) animatedSpin else animatedFlip

    // compute whether back is showing
    val normalized = (cardRotationY % 360f + 360f) % 360f
    val showingBack = flippable && normalized in 90f..270f
    val isFront = !showingBack

    val placeholderBack = rememberAsyncImagePainter(Defaults.imageHostDir + "back.png")
    val cameraDist = 8f * LocalDensity.current.density

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationY = cardRotationY
                cameraDistance = cameraDist
            }
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    // 1) fire the tapped callback
                    if (flippable) {
                        onTapped(if (showingBack) card else null)
                    } else {
                        onTapped(card)
                    }
                    // 2) perform flip + notify orientation
                    if (flippable && !isSpinning) {
                        val newCount = rotationCount + 1
                        // compute what face we'll get after this flip
                        val newNorm = ((newCount * 180f) % 360f + 360f) % 360f
                        val newIsFront = newNorm !in 90f..270f
                        onFlip(card, newIsFront)
                        rotationCount = newCount
                    }
                },
                onLongClick = {
                    if (flippable) isSpinning = true
                }
            )
            .pointerInput(isSpinning, flippable) {
                if (isSpinning && flippable) {
                    coroutineScope {
                        awaitPointerEventScope {
                            awaitFirstDown()
                            do {
                                val event = awaitPointerEvent()
                            } while (event.changes.any { it.pressed })
                            // on release: stop spin, reset to front, notify
                            isSpinning = false
                            rotationCount = 0
                            onFlip(card, true)
                        }
                    }
                }
            }
    ) {
        if (isFront) {
            AsyncImage(
                model = card.localImageFile,
                contentDescription = "${card.name} front",
                modifier = Modifier.matchParentSize()
            )
        } else {
            AsyncImage(
                model = card.backSideImage,
                placeholder = placeholderBack,
                error = placeholderBack,
                contentDescription = "${card.name} back",
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { rotationY = 180f }
            )
        }
    }
}

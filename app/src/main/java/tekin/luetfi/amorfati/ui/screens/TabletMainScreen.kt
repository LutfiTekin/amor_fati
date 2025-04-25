package tekin.luetfi.amorfati.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.ui.screens.email.CardInfo
import tekin.luetfi.amorfati.ui.screens.email.EmailComposerViewModel
import tekin.luetfi.amorfati.ui.screens.email.FlippableCard
import tekin.luetfi.amorfati.utils.Deck

@Composable
fun TabletMainScreen(
    modifier: Modifier = Modifier,
    viewModel: EmailComposerViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    val pickedCards = remember { mutableStateListOf<TarotCard>() }
    var cardToPreview by remember { mutableStateOf<TarotCard?>(null) }
    // 1) which chip is selected: 0 = Full Deck, 1 = F8, 2 = Regular
    var selectedChip by remember { mutableIntStateOf(0) }
    val chipLabels = listOf("Full Deck", "F8 Cards", "Regular Cards", "Picked Cards")

    // 2) choose which list to display
    val cardsToShow by remember {
        derivedStateOf {
            when (selectedChip) {
                0 -> Deck.fullDeck
                1 -> Deck.f8Cards.shuffled()   // shuffle once on switch to F8
                3 -> pickedCards.sortedByDescending { it.isF8Card }
                else -> Deck.cards.shuffled()   // shuffle once on switch to Regular
            }
        }
    }


    val flippable by remember { derivedStateOf { listOf(1,2).contains(selectedChip) } }

    val (columns, cardSize) = when (selectedChip) {
        1, 3 -> {
            // F8: 4 columns, bigger cards
            4 to 250.dp
        }
        2 -> {
            8 to 120.dp
        }
        else -> {
            5 to 200.dp
        }
    }

    val flipped = selectedChip != 0

    Row(modifier = modifier.fillMaxSize()) {
        // Left pane: chips + grid
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // -- Chip row --
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chipLabels.forEachIndexed { index, label ->
                    FilterChip(
                        selected = index == selectedChip,
                        onClick = { selectedChip = index },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            // -- Grid of cards --
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement   = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = cardsToShow,
                    key = { card ->
                        selectedChip to card.code
                    }
                ) { card ->
                    FlippableCard(
                        modifier = Modifier.padding(4.dp),
                        card = card,
                        size = cardSize,
                        flippable = flippable,
                        startFlipped = if (pickedCards.contains(card)) false else flipped,
                        onTapped = {
                            cardToPreview = it
                        },
                        onFlip = { flippedCard, isFront ->
                            if (pickedCards.contains(flippedCard).not())
                                return@FlippableCard
                            if (isFront.not())
                                return@FlippableCard
                            if (pickedCards.size >= 4)
                                return@FlippableCard
                            pickedCards.add(flippedCard)
                        }
                    )
                }
            }
        }

        // Right pane: card info
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {

            cardToPreview?.let { selectedCard ->
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            // run in IO since we do disk writes
                            scope.launch(Dispatchers.IO) {
                                // 1) load the image as a Bitmap
                                val loader = ImageLoader(context)
                                val req = ImageRequest.Builder(context)
                                    .data(selectedCard.imageUrl)
                                    .build()
                                val result = loader.execute(req)
                                if (result is SuccessResult) {
                                    val drawable = result.drawable as BitmapDrawable
                                    val bitmap = drawable.bitmap

                                    // 2) insert into MediaStore to get a content:// URI
                                    val path = MediaStore.Images.Media.insertImage(
                                        context.contentResolver,
                                        bitmap,
                                        selectedCard.code,
                                        null
                                    )
                                    val uri = android.net.Uri.parse(path)

                                    // 3) copy that URI into the clipboard as an IMAGE
                                    val clip = ClipData.newUri(
                                        context.contentResolver,
                                        "Card Image",
                                        uri
                                    )
                                    clipboard.setPrimaryClip(clip)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Send Card")
                    }

                    // Add to picked list
                    Button(onClick = {
                        selectedCard.let { card ->
                            if (!pickedCards.contains(card)) {
                                pickedCards.removeIf { it.isF8Card && card.isF8Card }
                                pickedCards.add(card)
                            }else pickedCards.remove(card)
                        }
                        //Show Picked cards
                        selectedChip = 3
                    },
                        modifier = Modifier.weight(1f),
                        enabled = pickedCards.size < 4) {
                        Text(if(pickedCards.contains(selectedCard)) "Remove" else "Add")
                    }
                }

                Spacer(Modifier.height(16.dp))

                val scroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scroll)
                ) {
                    CardInfo(selectedCard)
                }

            }?: run {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Please select a card",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        cardToPreview = Deck.fullDeck.random()
                    }) {
                        Text("Pick random card")
                    }
                }
            }



            }


        }
    }


package tekin.luetfi.amorfati.ui.screens.email


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tekin.luetfi.amorfati.data.remote.dto.EmailAddress
import tekin.luetfi.amorfati.domain.model.ReadingProgress
import tekin.luetfi.amorfati.domain.model.compileLogMessage
import tekin.luetfi.amorfati.utils.Deck
import tekin.luetfi.amorfati.utils.recipient
import tekin.luetfi.amorfati.utils.selectedCards
import tekin.luetfi.amorfati.utils.validatedJSON
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.domain.model.addNewProgress
import tekin.luetfi.amorfati.utils.READING_CARDS_AMOUNT


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailComposeScreen(
    modifier: Modifier = Modifier,
    viewModel: EmailComposerViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val activity = (context as? Activity)
    val intentFlow = remember { snapshotFlow { activity?.intent } }
    var selectedCards by remember { mutableStateOf(Deck.fullDeck) }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    var recipient by rememberSaveable {
        mutableStateOf(EmailAddress(email = "", name = null))
    }
    var jsonInput by rememberSaveable { mutableStateOf("") }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    /**
     * Track general progress
     */
    var progress by remember { mutableStateOf<ReadingProgress?>(null) }
    var progressList by remember { mutableStateOf<List<ReadingProgress>>(listOf()) }
    val progressLog by remember { derivedStateOf { progressList.compileLogMessage() } }

    /**
     * Bottom sheet for card preview
     */
    var selectedCard by remember { mutableStateOf<TarotCard?>(null) }
    var sheetOpen   by remember { mutableStateOf(false) }


    if (sheetOpen) {
        selectedCard?.let { card ->
            CardBottomSheet(
                card = card,
                onDismiss = { sheetOpen = false }
            )
        }
    }

    LaunchedEffect(jsonInput) {
        recipient = jsonInput.recipient
        val displayedCards = jsonInput.selectedCards
        if (displayedCards.size > READING_CARDS_AMOUNT)
            selectedCards = displayedCards
        else {
            selectedCards = listOf()
            displayedCards.forEach {
                selectedCards += it
                delay(400)
            }
        }
    }


    LaunchedEffect(intentFlow, activity?.intent) {
        intentFlow.collect { intent ->
            if (intent?.action == Intent.ACTION_SEND) {
                val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                }
                uri?.let { selectedImageUri = it }
                // clear so we don’t re‑handle the same intent
                activity?.intent = Intent()
            }
        }
    }

    // Prepare the image‑picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // 0. Progress bar
        progress?.progress?.let { rp ->
            if (rp == -1){
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                )
            }else if(rp < 100){
                LinearProgressIndicator(
                    progress = { rp / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 1. Card Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(selectedCards) { card ->
                if (selectedCards.size < 5) {
                    // each item owns its own visibility flag
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(card) {
                        // flip to true on first composition
                        visible = true
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = scaleIn(
                            // start from 0% size
                            initialScale = 0f,
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        ) + fadeIn(tween(200))
                    ) {
                        TarotCardItem(card, cardSelected = {
                            selectedCard = it
                            sheetOpen = true
                        })
                    }
                } else TarotCardItem(card, cardSelected = {
                    selectedCard = it
                    sheetOpen = true
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Recipient Email Input
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = recipient.name.orEmpty(),
                onValueChange = { newName ->
                    recipient = recipient.copy(name = newName)
                },
                modifier = Modifier.weight(1f),
                label = { Text("Name") },
                placeholder = { Text("Jane Doe") },
                singleLine = true
            )
            OutlinedTextField(
                value = recipient.email,
                onValueChange = { newEmail ->
                    recipient = recipient.copy(email = newEmail)
                },
                modifier = Modifier.weight(2f),
                label = { Text("Email Address") },
                placeholder = { Text("you@example.com") },
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        // 3. Image Picker
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Select Metaphor Image")
            }
            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. JSON Input or Progress Log
        if (progressList.isNotEmpty() && progress != null) {
            OutlinedTextField(
                value = progressLog.orEmpty(),
                onValueChange = { /* no-op */ },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                label = { Text("Progress Log") },
                maxLines = Int.MAX_VALUE
            )
        } else {
            OutlinedTextField(
                value = jsonInput,
                onValueChange = { /* no-op */ },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                label = { Text("Reading JSON") },
                maxLines = Int.MAX_VALUE
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        //    Buttons for Paste / Clear
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    progress = null
                    progressList = listOf()
                    clipboardManager.getText()?.text?.let { jsonInput = it.validatedJSON }
                },
                enabled = true,
                modifier = Modifier.weight(1f)
            ) {
                Text("Paste JSON")
            }
            Button(
                onClick = { jsonInput = "" },
                enabled = jsonInput.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear JSON")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))




        // 5. Submit Button
        Button(
            onClick = {
                viewModel.onSubmit(
                    jsonInput,
                    selectedImageUri,
                    recipient){
                    progress = it
                    progressList = progressList.addNewProgress(it)
                    if (progress?.progress == 100){
                        // Clear the form
                        recipient = EmailAddress(email = "", name = null)
                        jsonInput = ""
                        selectedImageUri = null
                        coroutineScope.launch {
                            delay(2000)
                            //progress = null
                            snackbarHostState.showSnackbar("Email sent successfully!")
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = recipient.email.isNotBlank()
                    && jsonInput.isNotBlank()
                    && selectedImageUri != null
                    && progress == null
        ) {
            Text("Submit")
        }


    }
}


package tekin.luetfi.amorfati.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tekin.luetfi.amorfati.data.remote.dto.EmailAddress
import tekin.luetfi.amorfati.utils.Deck
import tekin.luetfi.amorfati.utils.selectedCards


@Composable
fun EmailComposeScreen(
    modifier: Modifier = Modifier,
    viewModel: EmailComposerViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState
) {
    var selectedCards by remember { mutableStateOf(Deck.cards) }
    val coroutineScope = rememberCoroutineScope()
    var recipient by rememberSaveable {
        mutableStateOf(EmailAddress(email = "", name = null))
    }
    var jsonInput by rememberSaveable { mutableStateOf("") }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var uploadProgress by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(jsonInput) {
        selectedCards = jsonInput.selectedCards
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
        uploadProgress?.let { progress ->
            if (progress == -1){
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                )
            }else{
                LinearProgressIndicator(
                    progress = { progress / 100f },
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedCards) { card ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(80.dp)
                        .clickable { /* no-op */ }
                ) {
                    AsyncImage(
                        card.imageUrl,
                        contentDescription = card.name,
                        modifier = Modifier
                            .width(80.dp)
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
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

        // 4. JSON Input
        OutlinedTextField(
            value = jsonInput,
            onValueChange = { jsonInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = { Text("Paste JSON here") },
            placeholder = { Text("{ \"cards\": [...], \"notes\": [...] }") },
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(16.dp))




        // 5. Submit Button
        Button(
            onClick = {
                viewModel.onSubmit(
                    jsonInput,
                    selectedImageUri,
                    recipient){ progress ->
                    uploadProgress = progress
                    if (progress == 100){
                        // Clear the form
                        recipient = EmailAddress(email = "", name = null)
                        jsonInput = ""
                        selectedImageUri = null
                        coroutineScope.launch {
                            delay(2000)
                            uploadProgress = null
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
                    && uploadProgress == null
        ) {
            Text("Submit")
        }
    }
}
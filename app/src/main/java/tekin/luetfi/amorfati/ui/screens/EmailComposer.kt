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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tekin.luetfi.amorfati.utils.Deck

@Composable
fun EmailComposeScreen(
    modifier: Modifier = Modifier,
    viewModel: EmailComposerViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var recipientEmail by rememberSaveable { mutableStateOf("") }
    var jsonInput by rememberSaveable { mutableStateOf("") }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var uploadProgress by remember { mutableStateOf<Int?>(null) }

    // Prepare the image‑picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // 0. Progress bar
            uploadProgress?.let { progress ->
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 1. Card Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Deck.cards) { card ->
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
            OutlinedTextField(
                value = recipientEmail,
                onValueChange = { recipientEmail = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Recipient Email") },
                placeholder = { Text("you@example.com") },
                singleLine = true
            )

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
                        recipientEmail){ progress ->
                        uploadProgress = progress
                        if (progress == 100){
                            // Clear the form
                            recipientEmail = ""
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
                enabled = recipientEmail.isNotBlank()
                        && jsonInput.isNotBlank()
                        && selectedImageUri != null
            ) {
                Text("Submit")
            }
        }
    }
}
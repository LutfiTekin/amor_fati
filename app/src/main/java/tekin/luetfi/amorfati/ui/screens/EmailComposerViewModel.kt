package tekin.luetfi.amorfati.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import tekin.luetfi.amorfati.data.remote.dto.EmailAddress
import tekin.luetfi.amorfati.domain.model.ReadingProgress
import tekin.luetfi.amorfati.domain.model.withMessage
import tekin.luetfi.amorfati.domain.use_case.GetLoreUseCase
import tekin.luetfi.amorfati.domain.use_case.SendEmailUseCase
import tekin.luetfi.amorfati.utils.Deck
import tekin.luetfi.amorfati.utils.Defaults
import tekin.luetfi.amorfati.utils.METAPHOR_IMAGE_KEY
import tekin.luetfi.amorfati.utils.READING_TIME_KEY
import tekin.luetfi.amorfati.utils.formattedTarotDateTime
import tekin.luetfi.amorfati.utils.metaphorImageName
import javax.inject.Inject

@HiltViewModel
class EmailComposerViewModel @Inject constructor(
    private val sendEmailUseCase: SendEmailUseCase,
    private val loreUseCase: GetLoreUseCase,
    private val moshi: Moshi
) : ViewModel() {

    init {
        // Fetch and populate your global lore list as soon as VM is created
        viewModelScope.launch {
            try {
                val lore = loreUseCase()
                Defaults.mainLore.apply {
                    clear()
                    addAll(lore.cards)
                }
            } catch (e: Exception) {
                // You might log this or expose an error state
                e.printStackTrace()
            }
        }
    }


    fun onSubmit(
        jsonInput: String,
        selectedImageUri: Uri?,
        recipientEmail: EmailAddress,
        progress: (ReadingProgress) -> Unit
    ) = viewModelScope.launch {
        try {
            progress(-1 withMessage "Processing Json...")
            progress(-1 withMessage "Replacing Card Placeholders")
            if (Defaults.useTestBucket)
                progress(-1 withMessage "Using Test Bucket")
            delay(400)
            val rawJson = processCardInfo(jsonInput, selectedImageUri){
                progress(ReadingProgress(-1, it, "Uploading Image"))
            }
            progress(50 withMessage "Image Uploaded")
            delay(400)
            progress(-1 withMessage "Replacing Metaphor Placeholder")
            //create dynamic template data
            // create a JsonAdapter for Map<String,Any>
            val mapType = Types.newParameterizedType(
                Map::class.java, String::class.java, Any::class.java
            )
            progress(70 withMessage "Creating Template Data...")
            delay(100)
            val mapAdapter = moshi.adapter<Map<String, Any>>(mapType)
            // parse your raw JSON into a Map
            val dynamicData: Map<String, Any> = mapAdapter.fromJson(rawJson)
                ?: error("Invalid JSON")
            progress(90 withMessage "Sending Email")
            delay(400)
            //send email
            if (Defaults.sendEmail) {
                sendEmailUseCase(dynamicData, recipientEmail)
                progress(-1 withMessage "Email Sent to ${recipientEmail.name} - ${recipientEmail.email}")
                progress(-1 withMessage formattedTarotDateTime())
            } else progress(-1 withMessage "Test Mode: Email Send Disabled")
            delay(500)
            progress(100 withMessage "Session Ended Gracefully")
        } catch (e: Exception) {
            println("Error: ${e.message}")
            e.printStackTrace()
            progress(-1 withMessage "Error: ${e.message}")
        }

    }

    private suspend fun processCardInfo(jsonInput: String, selectedImageUri: Uri?, uploadProgress: (Int?) -> Unit): String {
        //Add card images to json
        var rawJson: String = Deck.cards.fold(jsonInput.replace("&","and")) { acc, card ->
            acc.replace("\"" + card.code + "\"", "\"" + card.imageUrl + "\"")
        }
        //add metaphor image to json
        val imageUrl = uploadMetaphorImage(jsonInput, selectedImageUri ?: error("No image selected")){
            uploadProgress(it)
        }
        rawJson = rawJson.replace(METAPHOR_IMAGE_KEY, imageUrl)
        //add time to json
        rawJson = rawJson.replace(READING_TIME_KEY, formattedTarotDateTime())
        return rawJson
    }

    /**
     * Uploads the given image URI to Firebase Storage
     * and returns its public download URL.
     */
    private suspend fun uploadMetaphorImage(jsonInput: String, imageUri: Uri, uploadProgress: (Int?) -> Unit): String {
        val storage = Firebase.storage
        // Create a random filename
        val path = "${Defaults.storageBucketPath}/${jsonInput.metaphorImageName}.jpg"
        val ref = storage.reference.child(path)

        // Upload the file to Firebase
        val uploadTask = ref.putFile(imageUri)

        uploadTask.addOnProgressListener {
            val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
            println("Upload is $progress% done")
            uploadProgress(progress.toInt())
        }

        uploadTask.await()

        // Once uploaded, retrieve the download URL
        return ref.downloadUrl.await().toString()
    }


}
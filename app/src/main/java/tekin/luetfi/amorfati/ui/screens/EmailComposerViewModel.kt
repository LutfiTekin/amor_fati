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
import tekin.luetfi.amorfati.domain.use_case.SendEmailUseCase
import tekin.luetfi.amorfati.utils.Deck
import tekin.luetfi.amorfati.utils.EMAIL_ENABLED
import tekin.luetfi.amorfati.utils.METAPHOR_IMAGE_KEY
import tekin.luetfi.amorfati.utils.READING_TIME_KEY
import tekin.luetfi.amorfati.utils.formattedTarotDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EmailComposerViewModel @Inject constructor(
    private val useCase: SendEmailUseCase,
    private val moshi: Moshi
) : ViewModel() {


    fun onSubmit(
        jsonInput: String,
        selectedImageUri: Uri?,
        recipientEmail: EmailAddress,
        progress: (ReadingProgress) -> Unit
    ) = viewModelScope.launch {
        try {
            progress(-1 withMessage "Processing Json...")
            progress(-1 withMessage "Replacing Card Placeholders")
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
            if (EMAIL_ENABLED)
                useCase(dynamicData, recipientEmail)
            progress(-1 withMessage "Email Sent to ${recipientEmail.name} - ${recipientEmail.email}")
            progress(-1 withMessage formattedTarotDateTime())
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
        val imageUrl = uploadMetaphorImage(selectedImageUri ?: error("No image selected")){
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
    private suspend fun uploadMetaphorImage(imageUri: Uri, uploadProgress: (Int?) -> Unit): String {
        val storage = Firebase.storage
        // Create a random filename
        val path = "metaphors/${UUID.randomUUID()}.jpg"
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
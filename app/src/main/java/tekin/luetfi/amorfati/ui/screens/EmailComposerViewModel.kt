package tekin.luetfi.amorfati.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.moshi.Moshi.*
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import tekin.luetfi.amorfati.domain.use_case.SendEmailUseCase
import tekin.luetfi.amorfati.utils.Deck
import tekin.luetfi.amorfati.utils.METAPHOR_IMAGE_KEY
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EmailComposerViewModel @Inject constructor(private val useCase: SendEmailUseCase): ViewModel() {


    fun onSubmit(jsonInput: String, selectedImageUri: Uri?, recipientEmail: String, progress: (Int) -> Unit) = viewModelScope.launch {
        try {
            progress(1)
            //Add card images to json
            var rawJson = Deck.cards.fold(jsonInput) { acc, card ->
                acc.replace(card.code, card.imageUrl)
            }
            progress(30)
            //add metaphor image to json
            val imageUrl =  uploadMetaphorImage(selectedImageUri ?: error("No image selected"))
            progress(60)
            rawJson = rawJson.replace(METAPHOR_IMAGE_KEY, imageUrl)
            //create dynamic template data
            val moshi = Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            // create a JsonAdapter for Map<String,Any>
            val mapType = Types.newParameterizedType(
                Map::class.java, String::class.java, Any::class.java
            )
            val mapAdapter = moshi.adapter<Map<String, Any>>(mapType)
            progress(80)
            // parse your raw JSON into a Map
            val dynamicData: Map<String, Any> = mapAdapter.fromJson(rawJson)
                ?: error("Invalid JSON")
            progress(90)
            //send email
            useCase(dynamicData, recipientEmail)
            progress(100)
        }catch (e: Exception){
            println("Error: ${e.message}")
            e.printStackTrace()
        }

    }

    /**
     * Uploads the given image URI to Firebase Storage
     * and returns its public download URL.
     */
    private suspend fun uploadMetaphorImage(imageUri: Uri): String {
        val storage = Firebase.storage
        // Create a random filename
        val path = "metaphors/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(path)

        // Upload the file to Firebase
        ref.putFile(imageUri).await()

        // Once uploaded, retrieve the download URL
        return ref.downloadUrl.await().toString()
    }



}
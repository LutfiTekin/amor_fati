package tekin.luetfi.amorfati.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import tekin.luetfi.amorfati.data.remote.dto.EmailAddress
import tekin.luetfi.amorfati.domain.model.CardLore

object Defaults {
    var useTestBucket by mutableStateOf(false)
    var sendEmail by mutableStateOf(true)
    var shouldSendCC by mutableStateOf(true)
    var onlineOnly by mutableStateOf(false)
    var selectedTemplate by mutableStateOf(Template.list.first())
    val sender = EmailAddress("readingby@lutfitek.in", "Lütfi Tekin")
    val cc = EmailAddress("cc@lutfitek.in", "Lütfi Tekin")
    val storageBucketPath = if (useTestBucket) "metaphors" else "testuploads/metaphors"
    val mainLore = mutableListOf<CardLore>()
    var sendgridApiKey = ""
    var imageHostDir = "https://aeterna.lutfitek.in/"
}

const val SEND_GRID_BASE_URL = "https://api.sendgrid.com/"
const val LORE_API_BASE_URL = "https://aeterna.lutfitek.in/"
const val DEFAULT_TIMEOUT = "default_timeout"
const val READING_CARDS_AMOUNT = 5
const val ASSETS_DIR = "file:///android_asset/"

/**
 * JSON placeholders
 */
const val METAPHOR_IMAGE_KEY = "METAPHOR_IMAGE"
const val READING_TIME_KEY = "READING_TIME"

/**
 * Remote config keys
 */
const val IMAGE_HOST_DIR = "image_host_dir"
const val SEND_GRID_API_KEY = "sendgrid_api_key"

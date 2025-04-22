package tekin.luetfi.amorfati.utils

import tekin.luetfi.amorfati.data.remote.dto.EmailAddress
import tekin.luetfi.amorfati.domain.model.CardLore

object Defaults{
    val sender = EmailAddress("readingby@lutfitek.in", "Lütfi Tekin")
    val cc = EmailAddress("cc@lutfitek.in", "Lütfi Tekin")
    val storageBucketPath = if (PROD_READY) "metaphors" else "testuploads/metaphors"
    val mainLore = mutableListOf<CardLore>()
}

const val PROD_READY = false
const val SEND_GRID_BASE_URL = "https://api.sendgrid.com/"
const val LORE_API_BASE_URL = "https://lutfitek.in/"
const val DEFAULT_TIMEOUT = "default_timeout"
const val TEMPLATE_ID = "d-7690a1effdad4c6295cfe1d4c343987f"
const val SEND_GRID_API_KEY = "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"
const val IMAGE_HOST_DIR = "https://lutfitek.in/assets/"

const val METAPHOR_IMAGE_KEY = "METAPHOR_IMAGE"
const val READING_TIME_KEY = "READING_TIME"

/**
 * Don't send emails while testing other components
 */
const val EMAIL_ENABLED = PROD_READY
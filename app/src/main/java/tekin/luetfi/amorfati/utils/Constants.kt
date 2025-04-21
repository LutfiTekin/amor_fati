package tekin.luetfi.amorfati.utils

import tekin.luetfi.amorfati.data.remote.dto.EmailAddress

object Defaults{
    val sender = EmailAddress("readingby@lutfitek.in", "Lütfi Tekin")
    val cc = EmailAddress("cc@lutfitek.in", "Lütfi Tekin")
}

const val SEND_GRID_BASE_URL = "https://api.sendgrid.com/"
const val DEFAULT_TIMEOUT = "default_timeout"
const val TEMPLATE_ID = "d-7690a1effdad4c6295cfe1d4c343987f"
const val SEND_GRID_API_KEY = "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"

const val METAPHOR_IMAGE_KEY = "METAPHOR_IMAGE"
const val READING_TIME_KEY = "READING_TIME"
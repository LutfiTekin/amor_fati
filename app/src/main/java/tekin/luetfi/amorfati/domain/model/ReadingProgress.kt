package tekin.luetfi.amorfati.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val IMAGE_UPLOAD_MESSAGE = "Uploading Image"

@Parcelize
data class ReadingProgress(
    val progress: Int?,
    val imageUploadProgress: Int?,
    val progressText: String
) : Parcelable {
    // Only compare progressText for equality, to dedupe messages
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReadingProgress) return false
        return progress == other.progress
                && imageUploadProgress == other.imageUploadProgress
                && progressText == other.progressText
    }

    override fun hashCode(): Int = progressText.hashCode()
}

infix fun Int?.withMessage(message: String): ReadingProgress {
    return ReadingProgress(this, null, message)
}

/**
 * Compiles a log message from a list of ReadingProgress entries.
 * Deduplicates messages by content and preserves insertion order.
 */
fun List<ReadingProgress>.compileLogMessage(): String? {
    if (isEmpty()) return null

    val uniqueMessages = this
        .mapNotNull { rp ->
            when {
                rp.imageUploadProgress != null -> "Uploading Image ${rp.imageUploadProgress}%"
                rp.progressText.isNotBlank() -> rp.progressText.trim()
                else -> null
            }
        }


    return uniqueMessages.takeIf { it.isNotEmpty() }?.reversed()
        ?.joinToString(separator = "\n").orEmpty()
}

fun List<ReadingProgress>.addNewProgress(progress: ReadingProgress): List<ReadingProgress> {
    val list = this.toMutableList()
    if (progress.imageUploadProgress != null)
        list.removeIf { it.imageUploadProgress != null }
    list.add(progress)
    return list.toList()
}
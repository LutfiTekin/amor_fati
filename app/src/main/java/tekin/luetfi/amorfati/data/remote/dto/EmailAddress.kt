package tekin.luetfi.amorfati.data.remote.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmailAddress(
    val email: String,
    val name: String? = null
) : Parcelable {
    val mapped: Map<String, String>
        get() = if (name == null) mapOf("email" to email) else mapOf("email" to email, "name" to name)
}
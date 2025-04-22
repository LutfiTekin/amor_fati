package tekin.luetfi.amorfati.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EmailTemplate(
    val name: String,
    //Sendgrid Template ID
    val id: String
) : Parcelable

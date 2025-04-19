package tekin.luetfi.amorfati.data.remote.dto

import com.squareup.moshi.Json
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.utils.Defaults
import tekin.luetfi.amorfati.utils.TEMPLATE_ID

data class SendGridMailSendRequest(
    val personalizations: List<Personalization>,
    val from: EmailAddress = Defaults.sender,
    @field:Json(name = "template_id") val templateId: String = TEMPLATE_ID
)

data class Personalization(
    val to: List<EmailAddress>,
    @field:Json(name = "dynamic_template_data")
    val dynamicTemplateData: DynamicTemplateData
)

data class EmailAddress(
    val email: String,
    val name: String? = null
)

data class DynamicTemplateData(
    val subject: String,
    val cards: List<TarotCard>,
    val notes: List<String>,
    @field:Json(name = "metaphorImageUrl")
    val metaphorImageUrl: String
)
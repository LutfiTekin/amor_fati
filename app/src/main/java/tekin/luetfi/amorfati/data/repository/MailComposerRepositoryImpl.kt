package tekin.luetfi.amorfati.data.repository

import tekin.luetfi.amorfati.data.remote.SendGridApi
import tekin.luetfi.amorfati.data.remote.dto.Personalization
import tekin.luetfi.amorfati.data.remote.dto.SendGridMailSendRequest
import tekin.luetfi.amorfati.domain.repository.MailComposerRepository
import tekin.luetfi.amorfati.utils.Defaults
import tekin.luetfi.amorfati.utils.TEMPLATE_ID
import javax.inject.Inject

class MailComposerRepositoryImpl @Inject constructor(private val api: SendGridApi) : MailComposerRepository {


    override suspend fun sendEmail(dynamicData: Map<String, Any>, recipientEmail: String) {
        val envelope: Map<String, Any> = mapOf(
            "personalizations" to listOf(
                mapOf(
                    "to"                    to listOf(
                        mapOf("email" to recipientEmail),
                        mapOf("email" to Defaults.recipient.email)
                    ),
                    "dynamic_template_data" to dynamicData
                )
            ),
            "from"        to mapOf("email" to Defaults.sender.email, "name" to "Lütfi Tekin"),
            "template_id" to TEMPLATE_ID
        )

        val response = api.sendMail(envelope)
        if (response.isSuccessful.not()) error("Failed to send email")
    }
}
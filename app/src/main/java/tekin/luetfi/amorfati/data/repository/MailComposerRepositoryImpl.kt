package tekin.luetfi.amorfati.data.repository

import tekin.luetfi.amorfati.data.remote.SendGridApi
import tekin.luetfi.amorfati.data.remote.dto.EmailAddress
import tekin.luetfi.amorfati.domain.repository.MailComposerRepository
import tekin.luetfi.amorfati.utils.Defaults
import javax.inject.Inject

class MailComposerRepositoryImpl @Inject constructor(private val api: SendGridApi) :
    MailComposerRepository {


    override suspend fun sendEmail(dynamicData: Map<String, Any>, recipientEmail: EmailAddress) {
        val envelope: Map<String, Any> = mapOf(
            "personalizations" to listOf(
                mapOf(
                    "to" to recipientEmail.receivers,
                    "dynamic_template_data" to dynamicData
                )
            ),
            "from" to Defaults.sender.mapped,
            "template_id" to Defaults.selectedTemplate.id,
        )

        val response = api.sendMail(request = envelope)
        if (response.isSuccessful.not()) error("Failed to send email")
    }
}
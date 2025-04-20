package tekin.luetfi.amorfati.data.repository

import tekin.luetfi.amorfati.data.remote.SendGridApi
import tekin.luetfi.amorfati.data.remote.dto.EmailAddress
import tekin.luetfi.amorfati.domain.repository.MailComposerRepository
import tekin.luetfi.amorfati.utils.Defaults
import tekin.luetfi.amorfati.utils.TEMPLATE_ID
import javax.inject.Inject

class MailComposerRepositoryImpl @Inject constructor(private val api: SendGridApi) :
    MailComposerRepository {


    override suspend fun sendEmail(dynamicData: Map<String, Any>, recipientEmail: EmailAddress) {
        val envelope: Map<String, Any> = mapOf(
            "personalizations" to listOf(
                mapOf(
                    "to" to listOf(
                        recipientEmail.mapped,
                        Defaults.cc.mapped
                    ),
                    "dynamic_template_data" to dynamicData
                )
            ),
            "from" to Defaults.sender.mapped,
            "template_id" to TEMPLATE_ID
        )

        val response = api.sendMail(envelope)
        if (response.isSuccessful.not()) error("Failed to send email")
    }
}
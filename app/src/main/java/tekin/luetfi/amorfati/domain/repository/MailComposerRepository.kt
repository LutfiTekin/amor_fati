package tekin.luetfi.amorfati.domain.repository

import tekin.luetfi.amorfati.data.remote.dto.Personalization

interface MailComposerRepository {

    suspend fun sendEmail(dynamicData: Map<String, Any>, recipientEmail: String)
}
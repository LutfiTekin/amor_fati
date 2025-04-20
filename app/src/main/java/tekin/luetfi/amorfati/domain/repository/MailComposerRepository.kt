package tekin.luetfi.amorfati.domain.repository


interface MailComposerRepository {

    suspend fun sendEmail(dynamicData: Map<String, Any>, recipientEmail: String)
}
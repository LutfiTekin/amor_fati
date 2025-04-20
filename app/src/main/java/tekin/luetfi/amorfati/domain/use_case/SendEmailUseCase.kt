package tekin.luetfi.amorfati.domain.use_case

import tekin.luetfi.amorfati.data.remote.dto.EmailAddress
import tekin.luetfi.amorfati.domain.repository.MailComposerRepository
import javax.inject.Inject

class SendEmailUseCase @Inject constructor(private val repository: MailComposerRepository){

    suspend operator fun invoke(dynamicData: Map<String, Any>, recipientEmail: EmailAddress) =
        repository.sendEmail(dynamicData, recipientEmail)


}
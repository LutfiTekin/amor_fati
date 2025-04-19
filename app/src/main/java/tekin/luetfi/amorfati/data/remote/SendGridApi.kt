package tekin.luetfi.amorfati.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import tekin.luetfi.amorfati.data.remote.dto.SendGridMailSendRequest
import tekin.luetfi.amorfati.utils.SEND_GRID_API_KEY

interface SendGridApi {
    @Headers(
        "Authorization: Bearer $SEND_GRID_API_KEY",
        "Content-Type: application/json"
    )
    @POST("v3/mail/send")
    suspend fun sendMail(
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>
}
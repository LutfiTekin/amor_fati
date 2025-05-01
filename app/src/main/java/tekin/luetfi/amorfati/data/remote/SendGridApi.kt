package tekin.luetfi.amorfati.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import tekin.luetfi.amorfati.utils.Defaults

interface SendGridApi {
    @Headers("Content-Type: application/json")
    @POST("v3/mail/send")
    suspend fun sendMail(
        @Header("Authorization") authorization: String = "Bearer ${Defaults.sendgridApiKey}",
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>
}
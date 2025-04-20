package tekin.luetfi.amorfati.data.remote.dto


data class EmailAddress(
    val email: String,
    val name: String? = null
){
    val mapped: Map<String, String>
        get() = if (name == null) mapOf("email" to email) else mapOf("email" to email, "name" to name)
}
data class FetchDeviceIDRequest(
    val deviceId: String? =null,
    val imeiNo1: String?=null,
    val imeiNo2: String?=null,
    val loginId: String?=null,
    val primaryMobileNo: String?=null,
    val pushToken: String?=null,
    val userId: Int?=null,
    val userName: String?=null
)

data class AuthDetails(
    val clientId: String,
    val clientSecret: String,
    val grantType: String,
    val username: String,
    val password: String
)
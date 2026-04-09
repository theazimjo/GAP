package abs.uits.gap.core.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("auth/request-otp")
    suspend fun requestOtp(@Body body: Map<String, String>): Response<OtpResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: Map<String, String>): Response<AuthResponse>
}

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val name: String,
    val phone: String
)

data class OtpResponse(
    val otpCode: String,
    val message: String?
)

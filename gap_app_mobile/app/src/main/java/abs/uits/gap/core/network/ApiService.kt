package abs.uits.gap.core.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("auth/request-otp")
    suspend fun requestOtp(@Body body: Map<String, String>): Response<OtpResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: Map<String, String>): Response<AuthResponse>

    @GET("groups")
    suspend fun getGroups(): Response<List<GroupDto>>

    @GET("groups/{id}")
    suspend fun getGroupDetail(@Path("id") id: Int): Response<GroupDetailDto>

    @POST("groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<Any>
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

data class GroupDto(
    val id: Int,
    val name: String,
    val totalPool: Double,
    val contributionAmount: Double,
    val creator: UserDto,
    val _count: GroupCountDto
)

data class GroupCountDto(
    val members: Int
)

data class CreateGroupRequest(
    val name: String
)

data class GroupDetailDto(
    val id: Int,
    val name: String,
    val totalPool: Double,
    val contributionAmount: Double,
    val creator: UserDto,
    val members: List<MemberDto>
)

data class MemberDto(
    val id: Int,
    val role: String,
    val user: UserDto
)

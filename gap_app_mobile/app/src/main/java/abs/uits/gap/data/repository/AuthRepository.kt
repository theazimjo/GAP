package abs.uits.gap.data.repository

import abs.uits.gap.core.network.ApiService
import abs.uits.gap.core.network.AuthResponse
import abs.uits.gap.core.network.OtpResponse

class AuthRepository(private val apiService: ApiService) {
    suspend fun requestOtp(phone: String): Result<OtpResponse> {
        return try {
            val response = apiService.requestOtp(mapOf("phone" to phone))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to request OTP: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(phone: String, code: String): Result<AuthResponse> {
        return try {
            val response = apiService.verifyOtp(mapOf("phone" to phone, "otpCode" to code))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to verify OTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMe(): Result<abs.uits.gap.core.network.ProfileDto> {
        return try {
            val response = apiService.getMe()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Profil ma'lumotlarini yuklashda xatolik: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(name: String): Result<Unit> {
        return try {
            val response = apiService.updateProfile(abs.uits.gap.core.network.UpdateProfileRequest(name = name))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Profilni yangilashda xatolik: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

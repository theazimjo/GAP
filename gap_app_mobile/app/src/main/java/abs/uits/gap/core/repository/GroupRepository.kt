package abs.uits.gap.core.repository

import abs.uits.gap.core.network.ApiService
import abs.uits.gap.core.network.CreateGroupRequest
import abs.uits.gap.core.network.GroupDto

class GroupRepository(private val apiService: ApiService) {

    suspend fun getGroups(): Result<List<GroupDto>> {
        return try {
            val response = apiService.getGroups()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroupDetail(id: Int): Result<abs.uits.gap.core.network.GroupDetailDto> {
        return try {
            val response = apiService.getGroupDetail(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Group detallari topilmadi"))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createGroup(
        name: String,
        emoji: String? = null,
        description: String? = null,
        isAmountOptional: Boolean = false,
        meetingDays: String? = null,
        selectionMethod: String = "random",
        totalPool: Double = 0.0,
        contributionAmount: Double = 0.0
    ): Result<Unit> {
        return try {
            val response = apiService.createGroup(
                CreateGroupRequest(
                    name = name,
                    emoji = emoji,
                    description = description,
                    isAmountOptional = isAmountOptional,
                    meetingDays = meetingDays,
                    selectionMethod = selectionMethod,
                    totalPool = totalPool,
                    contributionAmount = contributionAmount
                )
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMember(groupId: Int, phone: String): Result<Unit> {
        return try {
            val response = apiService.addMember(
                abs.uits.gap.core.network.AddMemberRequest(groupId, phone)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

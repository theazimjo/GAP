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

    suspend fun createGroup(name: String): Result<Unit> {
        return try {
            val response = apiService.createGroup(
                CreateGroupRequest(name)
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
}

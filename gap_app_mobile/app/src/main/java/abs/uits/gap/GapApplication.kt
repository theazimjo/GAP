package abs.uits.gap

import android.app.Application
import abs.uits.gap.core.network.ApiClient
import abs.uits.gap.core.network.ApiService
import abs.uits.gap.data.TokenStorage
import abs.uits.gap.data.repository.AuthRepository

import abs.uits.gap.core.repository.GroupRepository

class GapApplication : Application() {
    lateinit var tokenStorage: TokenStorage
    lateinit var apiService: ApiService
    lateinit var authRepository: AuthRepository
    lateinit var groupRepository: GroupRepository

    override fun onCreate() {
        super.onCreate()
        tokenStorage = TokenStorage(this)
        ApiClient.initialize(tokenStorage)
        apiService = ApiClient.retrofit.create(ApiService::class.java)
        authRepository = AuthRepository(apiService)
        groupRepository = GroupRepository(apiService)
    }
}

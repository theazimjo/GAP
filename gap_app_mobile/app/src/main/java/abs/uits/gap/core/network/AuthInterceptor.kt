package abs.uits.gap.core.network

import okhttp3.Interceptor
import okhttp3.Response
import abs.uits.gap.data.TokenStorage

class AuthInterceptor(private val tokenStorage: TokenStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStorage.getToken()
        val request = chain.request().newBuilder()
        if (token != null) {
            request.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(request.build())
    }
}

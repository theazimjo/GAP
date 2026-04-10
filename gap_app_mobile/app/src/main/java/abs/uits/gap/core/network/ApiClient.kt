package abs.uits.gap.core.network

import abs.uits.gap.data.TokenStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // DigitalOcean Production Server
    private const val BASE_URL = "http://104.248.43.194:4000/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private lateinit var okHttpClient: OkHttpClient
    lateinit var retrofit: Retrofit

    fun initialize(tokenStorage: TokenStorage) {
        val authInterceptor = AuthInterceptor(tokenStorage)
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
            
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

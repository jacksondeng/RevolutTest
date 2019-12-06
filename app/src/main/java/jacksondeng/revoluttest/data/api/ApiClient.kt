package jacksondeng.revoluttest.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ApiWorker {
    private const val TIMEOUT = 60L
    private val client: OkHttpClient

    init {
        val interceptor = HttpLoggingInterceptor()

        client = OkHttpClient
            .Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }
}
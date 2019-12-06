package jacksondeng.revoluttest.data.api

import jacksondeng.revoluttest.util.TIMEOUT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ApiWorker {
    val client: OkHttpClient = OkHttpClient
        .Builder()
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor())
        .build()

}
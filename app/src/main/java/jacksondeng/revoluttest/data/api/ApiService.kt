package jacksondeng.revoluttest.data.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import jacksondeng.revoluttest.util.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiService {
    val retrofitInstance: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(ApiWorker.client)
            .build()
}
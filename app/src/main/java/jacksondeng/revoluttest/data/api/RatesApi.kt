package jacksondeng.revoluttest.data.api

import jacksondeng.revoluttest.model.dto.RatesDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesApi {
    @GET("latest?")
    suspend fun getRates(@Query("base") base: String): RatesDTO
}
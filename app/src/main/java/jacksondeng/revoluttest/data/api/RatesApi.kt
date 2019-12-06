package jacksondeng.revoluttest.data.api

import jacksondeng.revoluttest.model.dto.RatesDTO
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesApi {
    @GET("latest?")
    fun getRates(@Query("base") base: String): Deferred<RatesDTO>
}
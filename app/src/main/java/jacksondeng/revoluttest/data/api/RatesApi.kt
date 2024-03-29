package jacksondeng.revoluttest.data.api

import io.reactivex.Flowable
import jacksondeng.revoluttest.model.dto.RatesDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesApi {
    @GET("latest?")
    fun pollRates(@Query("base") base: String): Flowable<RatesDTO>
}
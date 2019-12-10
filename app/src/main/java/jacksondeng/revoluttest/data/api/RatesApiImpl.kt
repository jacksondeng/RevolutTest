package jacksondeng.revoluttest.data.api

import io.reactivex.Observable
import jacksondeng.revoluttest.model.dto.RatesDTO
import retrofit2.Retrofit
import javax.inject.Inject

class RatesApiImpl @Inject constructor(private val retrofit: Retrofit) : RatesApi {
    override fun pollRates(base: String): Observable<RatesDTO> {
        return retrofit
            .create(RatesApi::class.java)
            .pollRates(base)
    }
}
package jacksondeng.revoluttest.data.api

import jacksondeng.revoluttest.model.dto.RatesDTO
import kotlinx.coroutines.Deferred

class RatesApiImpl : RatesApi {
    override fun getRates(base: String): Deferred<RatesDTO> {
        return ApiService().retrofitInstance
            .create(RatesApi::class.java)
            .getRates(base)
    }
}
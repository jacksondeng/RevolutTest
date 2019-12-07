package jacksondeng.revoluttest.data.api

import jacksondeng.revoluttest.model.dto.RatesDTO

class RatesApiImpl : RatesApi {
    override suspend fun getRates(base: String): RatesDTO {
        return ApiService().retrofitInstance
            .create(RatesApi::class.java)
            .getRates(base)
    }
}
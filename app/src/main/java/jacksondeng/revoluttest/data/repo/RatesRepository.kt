package jacksondeng.revoluttest.data.repo

import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.cache.CachedRates
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.model.entity.Currency
import jacksondeng.revoluttest.model.entity.Rates

interface RatesRepository {
    suspend fun getRates(base: String): Rates?
}

class RatesRepositoryImpl(private val api: RatesApi, private val cachedRates: CachedRates) :
    RatesRepository {
    override suspend fun getRates(base: String): Rates? {
        return try {
            val dto = api.getRates(base).await()
            mapToModel(dto)
        } catch (exception: Exception) {
            cachedRates.getCachedRates(base)
        }
    }

    private fun mapToModel(dto: RatesDTO): Rates {
        return Rates(dto.base, dto.rates.map {
            Currency(name = it.key, rate = it.value)
        })
    }
}
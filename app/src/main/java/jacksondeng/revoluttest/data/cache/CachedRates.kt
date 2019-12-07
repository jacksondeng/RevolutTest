package jacksondeng.revoluttest.data.cache

import jacksondeng.revoluttest.model.entity.Rates

class CachedRates() {
    fun getCachedRates(base: String): Rates? {
        // TODO: implement cache
        return Rates("EUR", listOf())
    }
}
package jacksondeng.revoluttest.di.module

import dagger.Module
import dagger.Provides
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.cache.CachedRates
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.data.repo.RatesRepositoryImpl

@Module
class RatesRepositoryModule {
    @Provides
    fun providesCachedRates(): CachedRates {
        return CachedRates()
    }

    @Provides
    fun providesRatesRepo(api: RatesApi, cachedRates: CachedRates): RatesRepository {
        return RatesRepositoryImpl(api, cachedRates)
    }
}
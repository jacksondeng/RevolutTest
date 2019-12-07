package jacksondeng.revoluttest.di.module

import dagger.Module
import dagger.Provides
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.viewmodel.RatesViewModel

@Module
class RatesViewModelModule {
    @Provides
    fun providesRatesViewModel(repository: RatesRepository): RatesViewModel {
        return RatesViewModel(repository)
    }
}
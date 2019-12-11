package jacksondeng.revoluttest.di.module

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.viewmodel.RatesViewModel

@Module
class RatesViewModelModule {
    @Provides
    fun providesRatesViewModel(
        repository: RatesRepository,
        sharePref: SharedPreferences
    ): RatesViewModel {
        return RatesViewModel(repository, sharePref)
    }
}
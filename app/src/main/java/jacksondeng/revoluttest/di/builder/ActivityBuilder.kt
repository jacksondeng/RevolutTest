package jacksondeng.revoluttest.di.builder

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jacksondeng.revoluttest.di.module.NetworkModule
import jacksondeng.revoluttest.di.module.RatesRepositoryModule
import jacksondeng.revoluttest.di.module.RatesViewModelModule
import jacksondeng.revoluttest.di.module.SharePrefModule
import jacksondeng.revoluttest.view.MainActivity

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(
        modules = [
            NetworkModule::class,
            RatesViewModelModule::class,
            RatesRepositoryModule::class,
            SharePrefModule::class
        ]
    )
    abstract fun contributeMainActivity(): MainActivity
}
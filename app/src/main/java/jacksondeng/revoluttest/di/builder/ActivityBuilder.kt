package jacksondeng.revoluttest.di.builder

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jacksondeng.revoluttest.di.module.NetworkModule
import jacksondeng.revoluttest.di.module.RatesRepositoryModule
import jacksondeng.revoluttest.di.module.SharePrefModule
import jacksondeng.revoluttest.util.ViewModelModule
import jacksondeng.revoluttest.view.MainActivity

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(
        modules = [
            NetworkModule::class,
            RatesRepositoryModule::class,
            SharePrefModule::class,
            ViewModelModule::class
        ]
    )
    abstract fun contributeMainActivity(): MainActivity
}
package jacksondeng.revoluttest.di.builder

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jacksondeng.revoluttest.data.cache.db.RatesDb
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.di.module.NetworkModule
import jacksondeng.revoluttest.di.module.RatesRepositoryModule
import jacksondeng.revoluttest.di.module.RatesViewModelModule
import jacksondeng.revoluttest.view.MainActivity

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [NetworkModule::class, RatesViewModelModule::class, RatesRepositoryModule::class])
    abstract fun contributeMainActivity(): MainActivity
}
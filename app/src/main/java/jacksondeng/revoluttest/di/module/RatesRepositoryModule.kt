package jacksondeng.revoluttest.di.module

import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import jacksondeng.revoluttest.RevolutApplication
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.cache.dao.RatesDao
import jacksondeng.revoluttest.data.cache.db.RatesDb
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.data.repo.RatesRepositoryImpl
import jacksondeng.revoluttest.di.scope.ActivityScope
import jacksondeng.revoluttest.util.DB_NAME
import javax.inject.Singleton

@Module
class RatesRepositoryModule {

    @Provides
    @Singleton
    fun providesRoomDatabase(application: RevolutApplication): RatesDb {
        return Room.databaseBuilder(application, RatesDb::class.java, DB_NAME).build()
    }

    @Provides
    @ActivityScope
    fun providesProductDao(ratesDb: RatesDb): RatesDao {
        return ratesDb.ratesDao()
    }

    @Provides
    @ActivityScope
    fun providesRatesRepo(
        api: RatesApi,
        ratesDao: RatesDao,
        sharePref: SharedPreferences
    ): RatesRepository {
        return RatesRepositoryImpl(api, ratesDao, sharePref)
    }
}
package jacksondeng.revoluttest

import androidx.room.EmptyResultSetException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import jacksondeng.revoluttest.data.cache.dao.RatesDao
import jacksondeng.revoluttest.data.cache.db.RatesDb
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.util.TrampolineSchedulerProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: RatesDb
    private lateinit var ratesDao: RatesDao

    @Before
    internal fun setUp() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<RevolutApplication>(),
            RatesDb::class.java
        )
            .allowMainThreadQueries()
            .build()
        ratesDao = db.ratesDao()
    }

    @After
    fun teardown() {
        RxJavaPlugins.reset()
        db.close()
    }


    @Test
    internal fun insert_test() {
        val dummyResponse = RatesDTO(
            base = "EUR",
            date = "2018-09-12",
            rates = mapOf(
                "USD" to 123.4,
                "TWD" to 321.0
            )
        )

        ratesDao.updateCache(dummyResponse).blockingAwait()

        val observer = ratesDao.getCachedRates("EUR")
            .subscribeOn(TrampolineSchedulerProvider().io())
            .test()
            .assertValue { it == dummyResponse }

        observer.dispose()
    }


    @Test
    internal fun invalid_query_test() {
        val observer = ratesDao.getCachedRates("123")
            .subscribeOn(TrampolineSchedulerProvider().io())
            .test()
            .assertError {
                it is EmptyResultSetException
            }
            .assertTerminated()

        observer.dispose()
    }
}
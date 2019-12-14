package jacksondeng.revoluttest

import android.accounts.NetworkErrorException
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.cache.dao.RatesDao
import jacksondeng.revoluttest.data.repo.RatesRepositoryImpl
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.BASE_THUMBNAIL_URL
import jacksondeng.revoluttest.util.TAG_LAST_CACHED_TIME
import jacksondeng.revoluttest.util.TrampolineSchedulerProvider
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class RepositoryTest {

    private val api = mockk<RatesApi>()

    private val ratesDao = mockk<RatesDao>(relaxed = true)

    private val sharePref = mockk<SharedPreferences>(relaxed = true)

    lateinit var repo: RatesRepositoryImpl

    @Before
    internal fun setUp() {
        repo = RatesRepositoryImpl(api, ratesDao, sharePref)
        RxJavaPlugins.reset()
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @After
    fun teardown() {
        RxJavaPlugins.reset()
    }

    @Test
    internal fun `generate currencies list test`() {
        val dummyData = mapOf(
            Pair("EUR", 123.123),
            Pair("USD", 123.004),
            Pair("SGD", 0.0123),
            Pair("ABC", -120.1),
            Pair("TWD", 100.0)
        )

        val resultList = listOf(
            CurrencyModel(
                Currency.getInstance("EUR"),
                1.0,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/eur.png"
            ),
            CurrencyModel(
                Currency.getInstance("EUR"),
                123.123,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/eur.png"
            ),
            CurrencyModel(
                Currency.getInstance("USD"),
                123.004,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/usd.png"
            ),
            CurrencyModel(
                Currency.getInstance("SGD"),
                0.0123,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/sgd.png"
            ),
            CurrencyModel(
                Currency.getInstance("TWD"),
                100.0,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/twd.png"
            )
        )

        val result = repo.generateCurrencies("EUR", 1.0, dummyData)
        println("$result")
        Assert.assertNotNull(result)
        Assert.assertTrue(result.size == resultList.size && result.containsAll(resultList))
    }

    @Test
    internal fun `generate currencies with multiplier test`() {
        val dummyData = mapOf(
            Pair("EUR", 123.123),
            Pair("USD", 123.004),
            Pair("SGD", 0.0123),
            Pair("ABC", -120.1),
            Pair("TWD", 100.0)
        )

        val resultList = listOf(
            CurrencyModel(
                Currency.getInstance("EUR"),
                1.12,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/eur.png"
            ),
            CurrencyModel(
                Currency.getInstance("EUR"),
                123.123 * 1.12,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/eur.png"
            ),
            CurrencyModel(
                Currency.getInstance("USD"),
                123.004 * 1.12,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/usd.png"
            ),
            CurrencyModel(
                Currency.getInstance("SGD"),
                0.0123 * 1.12,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/sgd.png"
            ),
            CurrencyModel(
                Currency.getInstance("TWD"),
                100.0 * 1.12,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/twd.png"
            )
        )

        val result = repo.generateCurrencies("EUR", 1.12, dummyData)
        println("$result")
        Assert.assertNotNull(result)
        Assert.assertTrue(result.size == resultList.size && result.containsAll(resultList))
    }

    @Test
    internal fun `generate currencies list with negative value test`() {
        val dummyData = mapOf(
            Pair("EUR", 123.123),
            Pair("SGD", -120.1),
            Pair("TWD", 100.0)
        )

        val resultList = listOf(
            CurrencyModel(
                Currency.getInstance("EUR"),
                1.0,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/eur.png"
            ),
            CurrencyModel(
                Currency.getInstance("EUR"),
                123.123,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/eur.png"
            ),
            CurrencyModel(
                Currency.getInstance("TWD"),
                100.0,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/twd.png"
            )
        )

        val result = repo.generateCurrencies("EUR", 1.0, dummyData)
        println("$result")
        Assert.assertNotNull(result)
        Assert.assertTrue(result.size == resultList.size && result.containsAll(resultList))
    }

    @Test
    internal fun `generate currencies with empty list test`() {
        val dummyData = mapOf<String, Double>()
        val resultList = listOf(
            CurrencyModel(
                Currency.getInstance("EUR"),
                1000000.0,
                "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/eur.png"
            )
        )

        val result = repo.generateCurrencies("EUR", 1000000.0, dummyData)
        println("$result")
        Assert.assertNotNull(result)
        Assert.assertTrue(result.size == resultList.size && result.containsAll(resultList))
    }

    @Test
    internal fun `exchange rate calculation check overflow test`() {
        Assert.assertEquals(true, repo.checkForOverflow(Double.MAX_VALUE, Double.MAX_VALUE))
    }

    @Test
    internal fun `exchange rate calculation doesn't overflow test`() {
        Assert.assertEquals(false, repo.checkForOverflow(0.9, Double.MAX_VALUE * -1))
    }

    @Test
    internal fun `calculate exchange rate test (overflowed)`() {
        Assert.assertEquals(
            Double.POSITIVE_INFINITY,
            repo.getCalculatedExchangeRate(Double.MAX_VALUE, 2.0),
            0.000000001
        )
    }

    @Test
    internal fun `calculate exchange rate test (doens't overflow)`() {
        Assert.assertNotEquals(
            Double.POSITIVE_INFINITY,
            repo.getCalculatedExchangeRate(100000000000000000000000000.120480, 2.0),
            0.000000001
        )

        Assert.assertEquals(
            100000000000000000000000000.120480 * 2.0,
            repo.getCalculatedExchangeRate(100000000000000000000000000.120480, 2.0),
            0.000000001
        )
    }


    @Test
    internal fun `api success test `() {
        every { api.pollRates("EUR") } returns Flowable.just(
            RatesDTO(
                base = "EUR",
                date = "2018-09-12",
                rates = mapOf(
                    "USD" to 203.4,
                    "TWD" to 110.0
                )
            )
        )

        every { sharePref.getLong(TAG_LAST_CACHED_TIME, -1) } returns 1234

        every { ratesDao.getCachedRates("EUR") } returns Single.just(
            RatesDTO(
                base = "EUR",
                date = "2018-09-12",
                rates = mapOf(
                    "USD" to 123.4,
                    "TWD" to 321.0
                )
            )
        )

        val subscriber = repo.pollRates("EUR", 1.0, TrampolineSchedulerProvider())
            .test()
            .assertValue(
                Rates(
                    base = "EUR",
                    rates = listOf(
                        CurrencyModel(
                            currency = Currency.getInstance("EUR"),
                            rate = 1.0,
                            imageUrl = "${BASE_THUMBNAIL_URL}eur.png"
                        ),
                        CurrencyModel(
                            currency = Currency.getInstance("USD"),
                            rate = 203.4,
                            imageUrl = "${BASE_THUMBNAIL_URL}usd.png"
                        ),
                        CurrencyModel(
                            currency = Currency.getInstance("TWD"),
                            rate = 110.0,
                            imageUrl = "${BASE_THUMBNAIL_URL}twd.png"
                        )
                    )
                )
            )

        subscriber.dispose()
    }

    @Test
    internal fun `api failed cache has value test `() {
        every { api.pollRates("EUR") } returns Flowable.error(NetworkErrorException())

        every { ratesDao.getCachedRates("EUR") } returns Single.just(
            RatesDTO(
                base = "EUR",
                date = "2018-09-12",
                rates = mapOf(
                    "USD" to 123.4,
                    "TWD" to 321.0
                )
            )
        )

        val subscriber = repo.pollRates("EUR", 1.0, TrampolineSchedulerProvider())
            .test()
            .assertValue(
                Rates(
                    base = "EUR",
                    rates = listOf(
                        CurrencyModel(
                            currency = Currency.getInstance("EUR"),
                            rate = 1.0,
                            imageUrl = "${BASE_THUMBNAIL_URL}eur.png"
                        ),
                        CurrencyModel(
                            currency = Currency.getInstance("USD"),
                            rate = 123.4,
                            imageUrl = "${BASE_THUMBNAIL_URL}usd.png"
                        ),
                        CurrencyModel(
                            currency = Currency.getInstance("TWD"),
                            rate = 321.0,
                            imageUrl = "${BASE_THUMBNAIL_URL}twd.png"
                        )
                    )
                )
            )

        subscriber.dispose()
    }

    @Test
    internal fun `api failed cache doesn't have value test `() {
        every { api.pollRates("EUR") } returns Flowable.error(NetworkErrorException())

        every { sharePref.getLong(TAG_LAST_CACHED_TIME, -1) } returns 1012

        every { ratesDao.getCachedRates("EUR") } returns Single.error(RuntimeException())

        val subscriber = repo.pollRates("EUR", 1.0, TrampolineSchedulerProvider())
            .test()
            .assertError {
                println(it)
                it is RuntimeException
            }
            .assertNoValues()
            .assertTerminated()

        subscriber.dispose()
    }

    @Test
    internal fun `api scheduling test `() {

        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        every { api.pollRates("EUR") } returns Flowable.just(
            RatesDTO(
                base = "EUR",
                date = "2018-09-12",
                rates = mapOf(
                    "USD" to 123.4,
                    "TWD" to 321.0
                )
            )
        )

        val subscriber = repo.pollRates("EUR", 1.0, TrampolineSchedulerProvider())
            .subscribeOn(testScheduler)
            .test()

        subscriber
            .assertNoErrors()
            .assertNotTerminated()

        // First api call will be without delay, hence after 1 second the valueCount should be 2
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        subscriber.assertValueCount(2)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        subscriber.assertValueCount(3)
        subscriber.dispose()
    }
}
package jacksondeng.revoluttest

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.cache.CachedRates
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.data.repo.RatesRepositoryImpl
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.model.entity.Rates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import java.util.*


@ExperimentalCoroutinesApi
class RepositoryTest {

    private val api = mockk<RatesApi>()

    private val cachedRates = mockk<CachedRates>()

    private val validResponse = RatesDTO(
        "EUR",
        "2019-01-12",
        mapOf(Pair("AUD", 2104935.0), Pair("SGD", 1208402.11))
    )

    private val validCache = Rates(
        base = "EUR",
        rates = listOf(
            CurrencyModel(Currency.getInstance("USD"), 2104935.0, ""),
            CurrencyModel(Currency.getInstance("SGD"), 1208402.11, "")
        )
    )

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    lateinit var repo: RatesRepositoryImpl

    @Before
    internal fun setUp() {
        MockitoAnnotations.initMocks(RatesRepository::class)
        Dispatchers.setMain(testDispatcher)
        repo = RatesRepositoryImpl(api, cachedRates)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }

    @Test
    internal fun `api success`() {
        coEvery { api.getRates("EUR") } returns validResponse

        runBlocking {
            val result = repo.getRates("EUR")
            println("$result")
            Assert.assertNotNull(result)
        }
    }

    @Test
    internal fun `api failed and cached rates not null`() {
        coEvery { api.getRates("EUR") }
        every { cachedRates.getCachedRates("EUR") } returns validCache
        runBlocking {
            val result = repo.getRates("EUR")
            println("$result")
            Assert.assertNotNull(result)
        }
    }

    @Test
    internal fun `api failed and cached rates is null`() {
        coEvery { api.getRates("EUR") }
        every { cachedRates.getCachedRates("EUR") } returns null
        runBlocking {
            val result = repo.getRates("EUR")
            println("$result")
            Assert.assertNull(result)
        }
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
            CurrencyModel(Currency.getInstance("EUR"), 0.0, "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/eur.png"),
            CurrencyModel(Currency.getInstance("EUR"), 123.123, "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/eur.png"),
            CurrencyModel(Currency.getInstance("USD"), 123.004, "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/usd.png"),
            CurrencyModel(Currency.getInstance("SGD"), 0.0123, "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/sgd.png"),
            CurrencyModel(Currency.getInstance("TWD"), 100.0, "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/twd.png")
        )

        val result = repo.generateCurrencies("EUR",dummyData)
        println("$result")
        Assert.assertNotNull(result)
        Assert.assertTrue(result.size == resultList.size && result.containsAll(resultList))
    }
}
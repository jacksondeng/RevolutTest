package jacksondeng.revoluttest

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.cache.CachedRates
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.data.repo.RatesRepositoryImpl
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.model.entity.Currency
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


@ExperimentalCoroutinesApi
class RepositoryTest {

    private val api = mockk<RatesApi>()

    private val cachedRates = mockk<CachedRates>()

    private val validResponse = RatesDTO(
        "abc",
        "2019-01-12",
        mapOf(Pair("AUD", 2104935.0), Pair("SGD", 1208402.11))
    )

    private val validCache = Rates(
        base = "EUR",
        rates = listOf(
            Currency("AUD_CACHED",2104935.0),
            Currency("SGD_CACHED",1208402.11)
        )
    )

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    lateinit var repo: RatesRepository

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
}
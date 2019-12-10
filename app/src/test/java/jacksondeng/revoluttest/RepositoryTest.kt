package jacksondeng.revoluttest

import io.mockk.mockk
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.repo.RatesRepositoryImpl
import jacksondeng.revoluttest.model.entity.CurrencyModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*


@ExperimentalCoroutinesApi
class RepositoryTest {

    private val api = mockk<RatesApi>()

    private val cachedRates = mockk<CachedRates>()

    lateinit var repo: RatesRepositoryImpl

    @Before
    internal fun setUp() {
        repo = RatesRepositoryImpl(api, cachedRates)
    }

    @After
    fun teardown() {

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
                0.0,
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
    internal fun `poll rates with multiplier test`() {
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
                0.0,
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
                0.0,
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
                0.0,
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
}
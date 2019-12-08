package jacksondeng.revoluttest

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.SpyK
import io.mockk.mockk
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.api.RatesApiImpl
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
import retrofit2.HttpException
import retrofit2.Retrofit

@ExperimentalCoroutinesApi
class NetworkCallTest {

    @SpyK
    lateinit var api: RatesApiImpl

    var retrofit = mockk<Retrofit>()

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    internal fun setUp() {
        MockKAnnotations.init(RatesApi::class)
        Dispatchers.setMain(testDispatcher)
        api = RatesApiImpl(retrofit)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }


    @Test
    internal fun `api success and response not null`() {
        runBlocking {
            try {
                val result = api.getRates("EUR")
                println("$result")
                Assert.assertNotNull(result)
            } catch (exception: Exception) {
                Assert.fail()
            }
        }
    }

    @Test
    internal fun `api call with invalid query string`() {
        runBlocking {
            try {
                api.getRates("30urefjefjhp9")
                Assert.fail()
            } catch (exception: Exception) {
                println("$exception")
                Assert.assertTrue(exception is HttpException)
                Assert.assertTrue(exception.message?.contains("HTTP 422") ?: true)
            }
        }
    }

    @Test
    internal fun `api call with empty query string`() {
        runBlocking {
            try {
                api.getRates("")
                Assert.fail()
            } catch (exception: Exception) {
                println("$exception")
                Assert.assertTrue(exception is HttpException)
                Assert.assertTrue(exception.message?.contains("HTTP 422") ?: true)
            }
        }
    }
}
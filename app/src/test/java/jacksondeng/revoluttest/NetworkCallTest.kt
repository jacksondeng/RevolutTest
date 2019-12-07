package jacksondeng.revoluttest

import jacksondeng.revoluttest.data.api.RatesApiImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import retrofit2.HttpException

class NetworkCallTest {
    @InjectMocks
    lateinit var api: RatesApiImpl

    @Before
    internal fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    internal fun `api success and response not null`() {
        runBlocking {
            try {
                val result = api.getRates("EUR").await()
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
                api.getRates("30urefjefjhp9").await()
                Assert.fail()
            } catch (exception: Exception) {
                Assert.assertTrue(exception is HttpException)
                Assert.assertTrue(exception.message?.contains("HTTP 422") ?: true)
            }
        }
    }

    @Test
    internal fun `api call with empty query string`() {
        runBlocking {
            try {
                api.getRates("").await()
                Assert.fail()
            } catch (exception: Exception) {
                println("$exception")
                Assert.assertTrue(exception is HttpException)
                Assert.assertTrue(exception.message?.contains("HTTP 422") ?: true)
            }
        }
    }
}
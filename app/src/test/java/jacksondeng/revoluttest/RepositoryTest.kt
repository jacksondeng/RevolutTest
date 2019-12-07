package jacksondeng.revoluttest

import jacksondeng.revoluttest.data.api.RatesApiImpl
import jacksondeng.revoluttest.data.cache.CachedRates
import jacksondeng.revoluttest.data.repo.RatesRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations


class RepositoryTest {

    @InjectMocks
    lateinit var api: RatesApiImpl
    lateinit var repo: RatesRepositoryImpl

    @Before
    internal fun setUp() {
        MockitoAnnotations.initMocks(this)
        repo = RatesRepositoryImpl(api, CachedRates())

    }


    @Test
    internal fun `api success`() {
        runBlocking {
            val result = repo.getRates("EUR")
            Assert.assertNotNull(result)
        }
    }

    @Test
    internal fun `api failed and cached rates not null`() {
        runBlocking {
            val result = repo.getRates("EUR")
            Assert.assertNotNull(result)
        }
    }

    // TODO: cached rates implementation
   /* @Test
    internal fun `api failed and cached rates is null`() {
        runBlocking {
            val result = repo.getRates("EUR")
            Assert.assertNull(result)
        }
    }*/


}
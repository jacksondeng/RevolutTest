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

    @Before
    internal fun setUp() {
        MockKAnnotations.init(RatesApi::class)
        api = RatesApiImpl(retrofit)
    }

    @After
    fun teardown() {

    }
}
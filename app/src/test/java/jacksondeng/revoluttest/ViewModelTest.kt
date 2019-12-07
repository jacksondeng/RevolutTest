package jacksondeng.revoluttest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.State
import jacksondeng.revoluttest.viewmodel.RatesViewModel
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repo = mockk<RatesRepository>()

    private val dummyData = mockk<Rates>()

    lateinit var viewModel: RatesViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setUp() {
        MockKAnnotations.init(RatesViewModel::class)
        Dispatchers.setMain(testDispatcher)
        viewModel = RatesViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        testScope.cleanupTestCoroutines()
    }

    @Test
    internal fun `viewmodel get rates success`() {
        coEvery { repo.getRates("EUR") } returns dummyData
        runBlocking {
            viewModel.getRates("EUR")
        }
        coVerify(exactly = 1) { repo.getRates("EUR") }
        assertEquals(State.RefreshList(dummyData), viewModel.state.value)
    }

    @Test
    internal fun `viewmodel get rates with empty query string`() {
        coEvery { repo.getRates("") } returns null
        runBlocking {
            viewModel.getRates("")
        }
        coVerify(exactly = 1) { repo.getRates("") }
        assertTrue(viewModel.state.value is State.ShowEmptyScreen)
    }

    @Test
    internal fun `viewmodel get rates with invalid query string`() {
        coEvery { repo.getRates("sdfjpiej") } returns null
        runBlocking {
            viewModel.getRates("sdfjpiej")
        }
        coVerify(exactly = 1) { repo.getRates("sdfjpiej") }
        assertTrue(viewModel.state.value is State.ShowEmptyScreen)
    }
}
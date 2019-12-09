package jacksondeng.revoluttest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.mockk
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.viewmodel.RatesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
class ViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repo = mockk<RatesRepository>()

    private val dummyData = mockk<Rates>()

    lateinit var viewModel: RatesViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(RatesViewModel::class)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }
}
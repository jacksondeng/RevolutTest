package jacksondeng.revoluttest

import android.accounts.NetworkErrorException
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.EmptyResultSetException
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import io.reactivex.Single
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.State
import jacksondeng.revoluttest.util.TrampolineSchedulerProvider
import jacksondeng.revoluttest.viewmodel.RatesViewModel
import org.junit.*
import java.util.*


class ViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val repo = mockk<RatesRepository>(relaxed = true)

    private val sharedPreferences = mockk<SharedPreferences>(relaxed = true)

    lateinit var viewModel: RatesViewModel

    val dummyResponse = Rates(
        base = "EUR",
        rates = listOf(
            CurrencyModel(
                currency = Currency.getInstance("EUR"),
                rate = 1.0,
                imageUrl = ""
            ),

            CurrencyModel(
                currency = Currency.getInstance("TWD"),
                rate = 123.0,
                imageUrl = ""
            ),

            CurrencyModel(
                currency = Currency.getInstance("GBP"),
                rate = 202.0,
                imageUrl = ""
            )
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(RatesViewModel::class)
        viewModel = RatesViewModel(repo, sharedPreferences)
    }

    @After
    fun tearDown() {

    }

    @Test
    internal fun `poll rates success test`() {
        val schedulerProvider = TrampolineSchedulerProvider()
        every { repo.pollRates("EUR", 1.0, schedulerProvider) } returns Flowable.just(dummyResponse)

        viewModel.pollRates("EUR", 1.0, schedulerProvider)
        Assert.assertTrue(viewModel.state.getOrAwaitValue() == State.Loaded(dummyResponse.rates))
    }

    @Test
    internal fun `poll rates failed test`() {
        val schedulerProvider = TrampolineSchedulerProvider()

        every { repo.pollRates("EUR", 1.0, schedulerProvider) } returns Flowable.error(
            NetworkErrorException()
        )

        viewModel.pollRates("EUR", 1.0, schedulerProvider)
        Assert.assertTrue(viewModel.state.getOrAwaitValue() == State.Error())
    }

    @Test
    internal fun `selected currency changed, poll rates success test`() {
        val schedulerProvider = TrampolineSchedulerProvider()
        every { repo.pollRates("EUR", 1.0, schedulerProvider) } returns Flowable.just(dummyResponse)

        viewModel.setBaseChange(true)
        viewModel.pollRates("EUR", 1.0, schedulerProvider)

        Assert.assertTrue(viewModel.state.getOrAwaitValue() == State.RefreshList(dummyResponse.rates))
    }

    @Test
    internal fun `getCachedRates(), cache exists test`() {
        val schedulerProvider = TrampolineSchedulerProvider()
        every {
            repo.getCachedRates(
                "EUR",
                1.0,
                schedulerProvider
            )
        } returns Single.just(
            dummyResponse
        )

        viewModel.getCachedRates("EUR", 1.0, false, schedulerProvider)
        Assert.assertTrue(viewModel.state.getOrAwaitValue() == State.Calculated(dummyResponse.rates))
    }

    @Test
    internal fun `getCachedRates(), cache doesn't exists test`() {
        val schedulerProvider = TrampolineSchedulerProvider()
        every {
            repo.getCachedRates(
                "EUR",
                1.0,
                schedulerProvider
            )
        } returns Single.error(EmptyResultSetException("Empty"))

        viewModel.getCachedRates("EUR", 1.0, false, schedulerProvider)
        Assert.assertTrue(viewModel.state.getOrAwaitValue() == State.Loading())
    }

    @Test
    internal fun `getCachedRates(), selected curency changed, cache exists test`() {
        val schedulerProvider = TrampolineSchedulerProvider()

        every {
            repo.getCachedRates(
                "EUR",
                1.0,
                schedulerProvider
            )
        } returns Single.just(dummyResponse)

        viewModel.getCachedRates("EUR", 1.0, true, schedulerProvider)
        Assert.assertTrue(viewModel.state.getOrAwaitValue() == State.RefreshList(dummyResponse.rates))
    }

    @Test
    internal fun `getCachedRates(), selected curency changed, cache doesn't exists test`() {
        val schedulerProvider = TrampolineSchedulerProvider()

        every {
            repo.getCachedRates(
                "EUR",
                1.0,
                schedulerProvider
            )
        } returns Single.error(EmptyResultSetException("Empty"))

        viewModel.getCachedRates("EUR", 1.0, true, schedulerProvider)
        Assert.assertTrue(viewModel.state.getOrAwaitValue() == State.Loading())
    }
}
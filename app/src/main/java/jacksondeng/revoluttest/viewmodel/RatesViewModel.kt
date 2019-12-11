package jacksondeng.revoluttest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.State
import javax.inject.Inject

class RatesViewModel @Inject constructor(private val repo: RatesRepository) : ViewModel() {

    private var baseRate: Rates? = null

    private var _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private var multiplier: Double = 1.0

    private var compositeDisposable = CompositeDisposable()

    fun pollRates(base: String = "EUR", multiplier: Double = 1.0) {
        compositeDisposable.add(
            repo.pollRates(base, multiplier)
                .subscribe({ rates ->
                    onSuccess(rates)
                }, {
                    State.ShowEmptyScreen("Please try again later")
                })
        )
    }

    fun getCachedRates(base: String, multiplier: Double) {
        compositeDisposable.add(
            repo.getCachedRates(base, multiplier)
                .subscribe({ rates ->
                    onSuccess(rates)
                }, {
                    State.ShowEmptyScreen("Please try again later")
                })
        )
    }

    private fun onSuccess(rates: Rates?) {
        baseRate = rates
        rates?.let {
            _state.value = State.RefreshList(it.rates)
        } ?: run {
            State.ShowEmptyScreen("Please try again later")
        }
    }

    fun calculateRate(multiplier: Double) {
        baseRate?.rates?.let { rates ->
            val result = rates.map {
                CurrencyModel(it.currency, it.rate * multiplier, it.imageUrl)
            }
            _state.postValue(State.RefreshList(result))
        }
    }

    fun stopPolling() = compositeDisposable.dispose()

    fun pausePolling() = compositeDisposable.clear()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
package jacksondeng.revoluttest.viewmodel

import androidx.lifecycle.*
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.Result
import jacksondeng.revoluttest.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RatesViewModel @Inject constructor(private val repo: RatesRepository) : ViewModel() {

    private var baseRate: Rates? = null

    private var _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private var multiplier: Double = 1.0

    val viewState: LiveData<State> = Transformations.map(repo.getRatesToObserve()) {
        when (it) {
            is Result.Success -> {
                baseRate = it.base
                State.RefreshList(it.value.rates)
            }

            is Result.Failure -> {
                State.ShowEmptyScreen("Please try again later")
            }
        }
    }

    fun getRates(base: String = "EUR") {
        viewModelScope.launch(Dispatchers.IO) {
            baseRate = repo.getRates(base)
            withContext(Dispatchers.Main) {
                baseRate?.let {
                    _state.value = State.RefreshList(it.rates)
                } ?: run {
                    _state.value = State.ShowEmptyScreen("Please try again later")
                }
            }
        }
    }

    fun pollRates(base: String = "EUR", multiplier: Double = 1.0) = repo.pollRates(base, multiplier)

    fun calculateRate(multiplier: Double) {
        baseRate?.rates?.let { rates ->
            val result = rates.map {
                CurrencyModel(it.currency, it.rate * multiplier, it.imageUrl)
            }
            _state.postValue(State.RefreshList(result))
        }
    }

    fun stopPolling() = repo.stopPolling()

    fun pausePolling() = repo.pausePolling()
}
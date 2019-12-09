package jacksondeng.revoluttest.viewmodel

import androidx.lifecycle.*
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.Result
import jacksondeng.revoluttest.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RatesViewModel @Inject constructor(private val repo: RatesRepository) : ViewModel() {

    private var rates: Rates? = null

    private var _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private var multiplier: Double = 1.0

    val viewState: LiveData<State> = Transformations.map(repo.getRatesToObserve()) {
        when (it) {
            is Result.Success -> {
                State.RefreshList(it.value)
            }

            is Result.Failure -> {
                State.ShowEmptyScreen("Please try again later")
            }
        }
    }

    fun getRates(base: String = "EUR") {
        viewModelScope.launch(Dispatchers.IO) {
            rates = repo.getRates(base)
            withContext(Dispatchers.Main) {
                rates?.let {
                    _state.value = State.RefreshList(it)
                } ?: run {
                    _state.value = State.ShowEmptyScreen("Please try again later")
                }
            }
        }
    }

    fun pollRates(base: String = "EUR", multiplier: Double = 1.0) = repo.pollRates(base, multiplier)

    fun stopPolling() = repo.stopPolling()

    fun pausePolling() = repo.pausePolling()

    fun setMultiplier(multiplier: Double) {
        this.multiplier = multiplier
    }
}
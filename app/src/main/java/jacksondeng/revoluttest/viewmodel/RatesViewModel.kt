package jacksondeng.revoluttest.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.State
import jacksondeng.revoluttest.util.getSelectedBase
import javax.inject.Inject

class RatesViewModel @Inject constructor(
    private val repo: RatesRepository,
    private val sharePref: SharedPreferences
) : ViewModel() {

    private var baseRate: Rates? = null

    private var _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private var multiplier: Double = 1.0

    private var compositeDisposable = CompositeDisposable()

    fun pollRates(
        base: String = sharePref.getSelectedBase()
        , multiplier: Double = this.multiplier
    ) {
        this.multiplier = multiplier
        compositeDisposable.add(
            repo.pollRates(base, multiplier)
                .subscribe({ rates ->
                    onSuccess(rates)
                }, {
                    State.ShowEmptyScreen("Please try again later")
                })
        )
    }

    fun getCachedRates(
        base: String = sharePref.getSelectedBase(),
        multiplier: Double = this.multiplier
    ) {
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
        rates?.let {
            baseRate = it
            _state.value = State.RefreshList(it.rates)
        } ?: run {
            State.ShowEmptyScreen("Please try again later")
        }
    }

    fun calculateRate(multiplier: Double) {
        this.multiplier = multiplier
        baseRate?.rates?.let { rates ->
            val result = rates.mapIndexed { index, it ->
                CurrencyModel(
                    it.currency, if (index != 0) {
                        it.rate * multiplier
                    } else {
                        it.rate
                    }, it.imageUrl
                )
            }
            _state.postValue(State.RefreshList(result))
        }
    }

    fun pausePolling() = compositeDisposable.clear()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
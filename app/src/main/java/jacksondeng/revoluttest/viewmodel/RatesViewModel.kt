package jacksondeng.revoluttest.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import jacksondeng.revoluttest.data.repo.RatesRepository
import jacksondeng.revoluttest.util.State
import jacksondeng.revoluttest.util.getSelectedBase
import javax.inject.Inject

class RatesViewModel @Inject constructor(
    private val repo: RatesRepository,
    private val sharePref: SharedPreferences
) : ViewModel() {

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
                    rates?.let {
                        _state.value = State.Loaded(it.rates)
                    } ?: run {
                        _state.value = State.Error()
                    }
                }, {
                    _state.value = State.Error()
                })
        )
    }

    fun getCachedRates(
        base: String = sharePref.getSelectedBase(),
        multiplier: Double = this.multiplier,
        baseChanged: Boolean = false
    ) {
        this.multiplier = multiplier
        compositeDisposable.add(
            repo.getCachedRates(base, multiplier)
                .subscribe({ rates ->
                    rates?.let {
                        if (baseChanged) {
                            _state.value = State.RefreshList(it.rates)
                        } else {
                            _state.value = State.Calculated(it.rates)
                        }
                    } ?: run {
                        _state.value = State.Loading()
                    }
                }, {
                    _state.value = State.Loading()
                })
        )
    }

    fun pausePolling() = compositeDisposable.clear()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}